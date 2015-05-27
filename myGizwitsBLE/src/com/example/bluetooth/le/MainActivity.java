package com.example.bluetooth.le;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Android中进行蓝牙开发需要使用到的类的执行过程是：
 *

 * 检查手机是否开启蓝牙

 1、使用BluetoothAdapter.startLeScan来扫描低功耗蓝牙设备

 2、在扫描到设备的回调函数中会得到BluetoothDevice对象，并使用BluetoothAdapter.stopLeScan停止扫描

 3、使用BluetoothDevice.connectGatt来获取到BluetoothGatt对象

 4、执行BluetoothGatt.discoverServices，这个方法是异步操作，在回调函数onServicesDiscovered中得到status，
    通过判断status是否等于BluetoothGatt.GATT_SUCCESS来判断查找Service是否成功

 5、如果成功了，则通过BluetoothGatt.getService来获取BluetoothGattService

 6、接着通过BluetoothGattService.getCharacteristic获取BluetoothGattCharacteristic

 7、然后通过BluetoothGattCharacteristic.getDescriptor获取BluetoothGattDescriptor
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void OnOpen(View view) {
        startActivity(new Intent(this, DeviceScanActivity.class));
    }
}
