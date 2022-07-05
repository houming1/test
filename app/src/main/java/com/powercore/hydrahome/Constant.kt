package com.powercore.hydrahome

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2021/12/01
 *  desc   :
 *
 */
object Constant {
   // const val WEB_SOCKET_URL = "wss://cloud.cnpowercore.com:8091/platform/%s_pcApp"
    const val WEB_SOCKET_URL = "wss://170s2247n7.51mypc.cn/platform/%s_HydraHome"

    const val LOGIN_RESULT = "LOGIN_RESULT"
    const val EMAIL = "EMAIL"
    const val TOKEN = "TOKEN"
    const val REFRESH_TOKEN = "REFRESH_TOKEN"
    const val LAST_HOME_PK = "LAST_HOME_PK"
    const val LAST_HOME_NANE = "LAST_HOME_NANE"
    const val IS_FIRST_INSTALL = "IS_FIRST_INSTALL"
    const val ONLY_LOAD_NET_IMG_BY_WIFI = "ONLY_LOAD_NET_IMG_BY_WIFI"
    const val WIFI_NAME_KEY = "WIFI_NAME_KEY"
    const val REMEMBER_WIFI_NAME = "REMEMBER_WIFI_NAME"
    const val CHARGE_MAX_POWER = "CHARGE_MAX_POWER"
}

object LiveDataBusKeys {
    const val MSG_ARRIVED = "MSG_ARRIVED"//收到消息
    const val MSG_ERROR = "MSG_ERROR"
    const val RELOGIN = "RELOGIN"

    const val NAVIGATION_TO_CHARGING = "NAVIGATION_TO_CHARGING"
    const val NAVIGATION_TO_RECORD = "NAVIGATION_TO_RECORD"

    //
    const val CONNECT_WIFI = "CONNECT_WIFI"

    const val TO_CONNECT_WIFI = "TO_CONNECT_WIFI"

    //统计家庭选择
    const val HOME_STATS_CHECKED = "HOME_STATS_CHECKED"

    //统计家庭选择
    const val HOME_RECORD_CHECKED = "HOME_RECORD_CHECKED"

    //修改家庭名称
    const val UPDATE_HOME_NAME = "UPDATE_HOME_NAME"

    //刷新充电桩
    const val REFRESH_CHARGE_BOX = "REFRESH_CHARGE_BOX"

    //刷新充电中界面
    const val REFRESH_CHARGING = "REFRESH_CHARGING"

    //配网结果 需要 返回 的状态
    const val BLU_CONFIG_WIFI_STATUS = "BLU_CONFIG_WIFI_STATUS"

    //蓝牙断开
    const val BLU_DISCONNECT = "BLU_DISCONNECT"

    //跳转至主页且选择home
//    const val NAVIGATION_TO_MAIN="NAVIGATION_TO_MAIN"
    const val FEEDBACK_READ = "FEEDBACK_READ"

    //充电请求接口调用
    const val CHARGE_REQUEST_SUCCESS = "CHARGE_REQUEST_SUCCESS"

    //刷新充电记录
    const val REFRESH_CHARGE_RECORD = "REFRESH_CHARGE_RECORD"
}