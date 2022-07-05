package com.powercore.hydrahome.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * author : JiangKun
 * e-mail : jiangkuikui001@gmail.com
 * time   : 2022/04/20
 * desc   :
 */
public class BlueUtils {

    //获取已绑定设备信息和连接状态
    public static ArrayList<String> showBondedDevice(BluetoothAdapter bluetoothAdapter) {
        ArrayList<String> connectAddrList=new ArrayList<>();
        Set<BluetoothDevice> deviceList = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : deviceList) {
            Log.d("Jason", "Name:" + device.getName() + "   Mac:" + device.getAddress());

            try {
                //使用反射调用获取设备连接状态方法
                Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                Log.d("Jason", "isConnected：" + isConnected);
                if (isConnected)
                connectAddrList.add(device.getAddress());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return connectAddrList;
    }
}
