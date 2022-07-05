package com.powercore.hydrahome.ws

import android.app.*
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.powercore.hydrahome.Constant
import com.powercore.hydrahome.LiveDataBusKeys
import com.powercore.hydrahome.R
import com.powercore.hydrahome.ws.entity.BaseWsResponse
import com.fdf.base.ext.log
import com.fdf.base.utils.mmkv
import com.gk.net.utils.MoshiUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.powercore.hydrahome.ws.entity.WsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.TimeUnit


/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/01
 *  desc   : WebSocket 服务
 *
 */
class WsService : Service() {
    private var wsClient: WebSocketClient? = null
    private val mBinder = WsClientBinder()

    private val NOTIFICATION_CHANNEL_NAME = "ws_notification"
    private var notificationManager: NotificationManager? = null
    private var isCreateChannel = false

    companion object {
        var isStop = false
    }

    override fun onCreate() {
        super.onCreate()
        "onCreate".log()
        startForeground(1, buildNotification())
        stopForeground(true)

        initWebSocket()

        GlobalScope.launch(Dispatchers.IO) {
            while (!isStop) {
                if (wsClient != null) {
                    if (wsClient!!.isClosed)
                        reconnectWs()
                } else {
                    initWebSocket()
                    reconnectWs()
                }
                delay(10 * 1000L)
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        "onStartCommand".log()
        startForeground(1, buildNotification())
        stopForeground(true)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initWebSocket() {
        val email = mmkv.getString(Constant.EMAIL, "")
        val wsUrl = String.format(Constant.WEB_SOCKET_URL, email)
        "ws url->${wsUrl}".log()
        wsClient = object : WebSocketClient(URI(wsUrl)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                "ws onOpen".log()
            }

            override fun onMessage(message: String?) {
                "ws onMessage->${message}".log()
                if (isJsonStr(message.toString())) {
                    if (message?.isNotEmpty() == true) {
                        val response = MoshiUtils.fromJson<BaseWsResponse<WsData>>(message)
                        response?.let {
                            if (response.success) {
                                //正常消息
                                LiveEventBus.get<BaseWsResponse<WsData>>(LiveDataBusKeys.MSG_ARRIVED)
                                    .postOrderly(response)
                            } else {
                                //错误消息
                                LiveEventBus.get<String>(LiveDataBusKeys.MSG_ERROR)
                                    .postOrderly(response.data?.message)
                            }
                        }
                    }
                }

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                "ws onClose->code=${code},reason->${reason}".log()
                Thread { reconnect() }.start()
            }

            override fun onError(ex: Exception?) {
                "ws onError->${ex?.message}".log()
            }
        }
        connect()
    }

    private fun isJsonStr(rawString: String): Boolean {
        return try {
            JSONObject(rawString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onDestroy() {
        closeConnect()
        super.onDestroy()
    }

    /**
     * 发送消息
     */
    fun sendMsg(msg: String) {
        if (null != wsClient) {
            "发送的消息：$msg".log()
            try {
                wsClient?.send(msg)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * TODO 连接ws
     *
     */
    fun connect() {
        try {
            wsClient?.connectBlocking(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * TODO 断开连接
     */
    private fun closeConnect() {
        try {
            wsClient?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * TODO 重连
     */
    private fun reconnectWs() {
        try {
            //重连
            wsClient?.reconnectBlocking()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    //用于Activity和service通讯
    internal inner class WsClientBinder : Binder() {
        val service: WsService
            get() = this@WsService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        closeConnect()
    }


    private fun buildNotification(): Notification {
        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager =
                    application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            }
            val channelId: String = application.packageName
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true) //是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(applicationContext, channelId)
        } else {
            builder = Notification.Builder(applicationContext)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("正在后台运行")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    this.packageManager.getLaunchIntentForPackage(this.packageName),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setWhen(System.currentTimeMillis())
        notification = builder.build()
        return notification
    }
}