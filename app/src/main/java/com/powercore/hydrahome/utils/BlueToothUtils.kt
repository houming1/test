package com.powercore.hydrahome.utils

import android.content.Context
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.beacon.Beacon
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.inuker.bluetooth.library.utils.BluetoothLog


/**
 *    Created by Administrator on 2021/12/2.
 *    Desc :
 */
object BlueToothUtils {

    private var mClient: BluetoothClient? = null

    fun init(context: Context) {
        if (mClient == null)
            mClient = BluetoothClient(context)
    }

    fun search() {
        val request = SearchRequest.Builder()
            .searchBluetoothLeDevice(3000, 3) // 先扫BLE设备3次，每次3s
            .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
            .searchBluetoothLeDevice(2000) // 再扫BLE设备2s
            .build()

        mClient?.search(request, object : SearchResponse {
            override fun onSearchStarted() {}
            override fun onDeviceFounded(device: SearchResult) {
                val beacon = Beacon(device.scanRecord)
                BluetoothLog.v(String.format("beacon for %s\n%s", device.address, beacon.toString()))
            }

            override fun onSearchStopped() {}
            override fun onSearchCanceled() {}
        })
    }

    fun stopSearch() {
        mClient?.stopSearch()
    }

    fun isOpen(): Boolean = mClient?.isBluetoothOpened == true

    fun changeStatus(isOpen: Boolean, stateCallback: (Boolean) -> Unit = {}) {
        mClient?.registerBluetoothStateListener(object : BluetoothStateListener() {
            override fun onBluetoothStateChanged(openOrClosed: Boolean) {
                stateCallback.invoke(openOrClosed)
            }
        })
        if (isOpen)
            mClient?.openBluetooth()
        else
            mClient?.closeBluetooth()
    }

}