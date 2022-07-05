package com.powercore.hydrahome.utils

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.core.location.LocationManagerCompat
import blufi.espressif.BlufiCallback
import blufi.espressif.BlufiClient
import blufi.espressif.params.BlufiConfigureParams
import blufi.espressif.params.BlufiParameter
import blufi.espressif.response.BlufiScanResult
import blufi.espressif.response.BlufiStatusResponse
import blufi.espressif.response.BlufiVersionResponse
import com.fdf.base.app.appContext
import com.fdf.base.ext.getString
import com.fdf.base.ext.log
import com.fdf.base.ext.toast
import com.powercore.hydrahome.R
import java.util.*

/**
 *
 *  author : JiangKun
 *  e-mail : jiangkuikui001@gmail.com
 *  time   : 2022/01/19
 *  desc   :
 *
 */
object BlufiUtils {
    const val DEFAULT_MTU_LENGTH = 128
    const val ConnectWIFICallBack = "ConnectWIFICallBack"
    const val ConnectWIFI = "ConnectWIFI"
    private var mBlufiClient: BlufiClient? = null


    private fun init(context: Context, device: BluetoothDevice): BlufiClient {
        return BlufiClient(context, device)
    }

    /**
     * Try to connect device
     */
    fun connect(device: BluetoothDevice, onGattConnected: () -> Unit = {}, onGattDisconnected: () -> Unit = {}) {
        if (mBlufiClient != null) {
            mBlufiClient?.requestCloseConnection()
            mBlufiClient?.close()
            mBlufiClient = null
        }
        mBlufiClient = init(appContext, device)
        mBlufiClient?.setGattCallback(object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                "onConnectionStateChange".log()
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            onGattConnected.invoke()
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            onGattDisconnected.invoke()
                            updateMessage(String.format("Disconnected device"), false)
                        }
                    }
                } else {
                    onGattDisconnected.invoke()
                    updateMessage(String.format("Disconnected device"), false)
                }
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
                super.onMtuChanged(gatt, mtu, status)
                "onMtuChanged".log()
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    updateMessage(String.format(Locale.ENGLISH, "Set mtu complete, mtu=%d ", mtu), false)
                } else {
                    mBlufiClient?.setPostPackageLengthLimit(20)
                    updateMessage(String.format(Locale.ENGLISH, "Set mtu failed, mtu=%d, status=%d", mtu, status), false)
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                "onServicesDiscovered".log()
                "BluetoothGatt.GATT_SUCCESS=${BluetoothGatt.GATT_SUCCESS}".log()
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    updateMessage(String.format(Locale.ENGLISH, "Discover services error status %d", status), false)
                }
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                "onCharacteristicWrite".log()
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    updateMessage(String.format(Locale.ENGLISH, "WriteChar error status %d", status), false)
                }
            }
        })
        mBlufiClient?.setBlufiCallback(object : BlufiCallback() {
            override fun onGattPrepared(
                client: BlufiClient?,
                gatt: BluetoothGatt?,
                service: BluetoothGattService?,
                writeChar: BluetoothGattCharacteristic?,
                notifyChar: BluetoothGattCharacteristic?
            ) {
                super.onGattPrepared(client, gatt, service, writeChar, notifyChar)
                "client=null?${client == null},gatt=null?${gatt == null},service=null?${service == null},writeChar=null?${writeChar == null},notifyChar=null?${notifyChar == null}".log()
                if (service == null) {
                    gatt?.disconnect()
                    updateMessage("Discover service failed", true)
                    return
                }
                if (writeChar == null) {
                    gatt?.disconnect()
                    updateMessage("Get write characteristic failed", false)
                    return
                }
                if (notifyChar == null) {
                    gatt?.disconnect()
                    updateMessage("Get notification characteristic failed", false)
                    return
                }
                updateMessage("Discover service and characteristics success", false)
                var mtu: Int = DEFAULT_MTU_LENGTH
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                    && Build.MANUFACTURER.lowercase(Locale.getDefault()).startsWith("samsung")
                ) {
                    mtu = 23
                }
                val requestMtu = gatt?.requestMtu(mtu)
                if (!requestMtu!!) {
                    client?.setPostPackageLengthLimit(20)
                    updateMessage(String.format(Locale.ENGLISH, "Request mtu %d failed", mtu), false)
                }
            }

            override fun onNegotiateSecurityResult(client: BlufiClient?, status: Int) {
                super.onNegotiateSecurityResult(client, status)
                if (status == STATUS_SUCCESS) {
                    updateMessage("Negotiate security complete", false)
                } else {
                    updateMessage("Negotiate security failed， code=$status", false)
                }
            }

            override fun onConfigureResult(client: BlufiClient?, status: Int) {
                super.onConfigureResult(client, status)
                if (status == STATUS_SUCCESS) {
                    updateMessage("Post configure params complete", false)
                } else {
                    updateMessage("Post configure params failed, code=$status", false)
                }
            }


            override fun onDeviceScanResult(client: BlufiClient?, status: Int, results: MutableList<BlufiScanResult>?) {
                super.onDeviceScanResult(client, status, results)
                if (status == STATUS_SUCCESS) {
                    val msg = StringBuilder()
                    msg.append("Receive device scan result:\n")
                    if (results != null) {
                        for (scanResult in results) {
                            msg.append("SSID:${scanResult.ssid},RSSI:${scanResult.rssi}").append("\n")
                        }
                    }
                    updateMessage(msg.toString(), true)
                } else {
                    updateMessage("Device scan result error, code=$status", false)
                }
            }

            override fun onDeviceVersionResponse(client: BlufiClient?, status: Int, response: BlufiVersionResponse?) {
                super.onDeviceVersionResponse(client, status, response)
                if (status == STATUS_SUCCESS) {
                    updateMessage(String.format("Receive device version: %s", response?.versionString), false)
                } else {
                    updateMessage("Device version error, code=$status", false)
                }
            }

            override fun onPostCustomDataResult(client: BlufiClient?, status: Int, data: ByteArray?) {
                super.onPostCustomDataResult(client, status, data)
                val dataStr = data?.let { String(it) }
                val format = "Post data %s %s"
                if (status == STATUS_SUCCESS) {
                    updateMessage(String.format(format, dataStr, "complete"), false)
                } else {
                    updateMessage(String.format(format, dataStr, "failed"), false)
                }
            }

            override fun onReceiveCustomData(client: BlufiClient?, status: Int, data: ByteArray?) {
                super.onReceiveCustomData(client, status, data)
                if (status == STATUS_SUCCESS) {
                    val customStr = data?.let { String(it) }
                    updateMessage(String.format("Receive custom data:\n%s", customStr), false)
                } else {
                    updateMessage("Receive custom data error, code=$status", false)
                }
            }

            override fun onError(client: BlufiClient?, errCode: Int) {
                super.onError(client, errCode)
                updateMessage(String.format(Locale.ENGLISH, "Receive error code %d", errCode), false)
            }
        })
        mBlufiClient?.connect()
    }

    fun configure(ssid: String, pwd: String, success: () -> Unit, error: () -> Unit = {}) {
        if (mBlufiClient == null)
            try {
                error.invoke()
                throw Exception("please initialize first")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        val params = BlufiConfigureParams()
        // 设置需要配置的模式：1 为 Station 模式，2 为 SoftAP 模式，3 为 Station 和 SoftAP 共存模式
        params.opMode = BlufiParameter.OP_MODE_STA
        params.staSSIDBytes = ssid.toByteArray() // 设置 Wi-Fi SSID
        params.staPassword = pwd // 设置 Wi-Fi 密码，若是开放 Wi-Fi 则不设或设空
        "blue wifi-> na:${ssid},p:${pwd}".log()
        mBlufiClient?.configure(params)
        mBlufiClient?.setBlufiCallback(object : BlufiCallback() {
            override fun onConfigureResult(client: BlufiClient?, status: Int) {
                super.onConfigureResult(client, status)
                if (status == STATUS_SUCCESS) {
                    success.invoke()
//                    requestDevStatus()
                    updateMessage("Post configure params complete", true)
                } else {
                    error.invoke()
                    updateMessage("Post configure params failed, code=$status", true)
                }
            }
        })
    }

    fun requestDevStatus(success: () -> Unit = {}, error: () -> Unit = {}) {
        if (mBlufiClient == null)
            try {
                throw Exception("please initialize first")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        mBlufiClient?.requestDeviceStatus()
        mBlufiClient?.setBlufiCallback(object : BlufiCallback() {
            override fun onDeviceStatusResponse(client: BlufiClient?, status: Int, response: BlufiStatusResponse?) {
                super.onDeviceStatusResponse(client, status, response)
                when (status) {
                    STATUS_SUCCESS -> {
                        val conn = response?.staConnectionStatus // 获取 Device 当前连接状态：0 表示有 Wi-Fi 连接，否则没有 Wi-Fi 连接
                        val ssid = response?.staSSID // 获取 Device 当前连接的 Wi-Fi 的 SSID
                        val bssid = response?.staBSSID // 获取 Device 当前连接的 Wi-Fi 的 BSSID
                        val password = response?.staPassword // 获取 Device 当前连接的 Wi-Fi 密码
                        "conn=${conn},ssid=${ssid},bssid=${bssid},password=${password},".toast()
                        if (conn == 0) {
                            success.invoke()
//                            LiveEventBus.get<EventBean>(LiveDataBusKeys.CONNECT_WIFI).post(EventBean(ConnectWIFICallBack, ConnectWIFI, ""))
                            updateMessage("Station connect Wi-Fi now", true)
                        } else {
                            error.invoke()
                            updateMessage("There are some problems with the connect, please check if you have entered the correct ssid and password", true)
//                            LiveEventBus.get<EventBean>(LiveDataBusKeys.CONNECT_WIFI).post(EventBean(ConnectWIFICallBack, "", ""))
                        }
                    }
                    else -> {
                        error.invoke()
                        updateMessage("Device status response error, $status", true)
//                        LiveEventBus.get<EventBean>(LiveDataBusKeys.CONNECT_WIFI).post(EventBean(ConnectWIFICallBack, "", ""))
                    }
                }
            }
        })
    }

    /**
     * 关闭客户端
     */
    fun closeClient() {
        if (mBlufiClient != null) {
            mBlufiClient?.setGattCallback(null)
            mBlufiClient?.setBlufiCallback(null)
            mBlufiClient?.close()
            mBlufiClient = null
        }
    }

    fun scan(locationManager: LocationManager?, onLeScan: (result: ScanResult) -> Unit, scanFinish: () -> Unit, scanFailed: () -> Unit = {}) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = adapter.bluetoothLeScanner
        if (!adapter.isEnabled || scanner == null) {
            getString(R.string.main_bt_disable_msg).toast()
            scanFailed.invoke()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationEnable = locationManager != null && LocationManagerCompat.isLocationEnabled(
                locationManager
            )
            if (!locationEnable) {
                getString(R.string.main_location_disable_msg).toast()
                scanFailed.invoke()
                return
            }
        }
        "Start scan ble".log()
        scanner.startScan(
            null, ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    result?.let { onLeScan.invoke(it) }

                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    "errorCode ${errorCode}".log()
                    scanFailed.invoke()
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)
                    if (results != null) {
                        for (result in results) {
                            onLeScan.invoke(result)
                        }
                    }
                }
            }
        )
    }

    /**
     * 停止扫描
     */
    fun stopScan(onLeScan: (result: ScanResult) -> Unit, scanFailed: () -> Unit = {}) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = adapter.bluetoothLeScanner
        scanner?.stopScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                result?.let { onLeScan.invoke(it) }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                scanFailed.invoke()
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                if (results != null) {
                    for (result in results) {
                        onLeScan.invoke(result)
                    }
                }
            }
        })
    }

    var old = ""
    var time = 0L
    fun updateMessage(message: String, isNotificaiton: Boolean) {
        try {
            if (isNotificaiton && this != null) {
                if (message != old) {
                    message.toast()
                    time = System.currentTimeMillis()
                    old = message
                } else {
                    if (System.currentTimeMillis() - time >= 2500) {
                        message.toast()
                        time = System.currentTimeMillis()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getConnectedDevices(context: Context, devices: (List<BluetoothDevice>?) -> Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val flag = adapter.getProfileConnectionState(BluetoothProfile.GATT)
        adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                val mDevices = proxy?.connectedDevices
                devices.invoke(mDevices)
            }

            override fun onServiceDisconnected(profile: Int) {
            }
        }, flag)

    }
}