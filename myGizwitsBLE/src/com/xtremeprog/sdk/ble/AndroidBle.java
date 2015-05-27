package com.xtremeprog.sdk.ble;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;
import org.apache.commons.codec.binary.Hex;

import java.util.*;

/**
 * 安卓原生ble 2015/5/18 19:36
 */
@SuppressLint("NewApi")
public class AndroidBle implements IBle, IBleRequestHandler {

	protected static final String TAG = "blelib";

	private BleService mService; // ble服务
	private BluetoothAdapter mBtAdapter; // 蓝牙适配器
	private Map<String, BluetoothGatt> mBluetoothGatts;
	// private BTQuery btQuery;

    /**
     * 蓝牙扫描回调 2015/5/18 19:38
     */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			mService.bleDeviceFound(device, rssi, scanRecord,
					BleService.DEVICE_SOURCE_SCAN);
		}
	};

    /**
     * 通道监听器 2015/5/18 19:43
     */
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * 状态更改 2015/5/18 19:44
         * @param gatt 通道
         * @param status 状态
         * @param newState 新状态
         */
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onConnectionStateChange " + address + " status "
					+ status + " newState " + newState);
			if (status != BluetoothGatt.GATT_SUCCESS) {
				disconnect(address);
				mService.bleGattDisConnected(address);
				return;
			}

			if (newState == BluetoothProfile.STATE_CONNECTED) { // 状态为正在连接
				mService.bleGattConnected(gatt.getDevice());
				mService.addBleRequest(new BleRequest(
						RequestType.DISCOVER_SERVICE, address));
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 状态为断开连接
				mService.bleGattDisConnected(address);
				disconnect(address);
			}
		}

        /**
         * 发现服务器 2015/5/18 19:46
         * @param gatt 通道
         * @param status 状态
         */
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onServicesDiscovered " + address + " status " + status);
			if (status != BluetoothGatt.GATT_SUCCESS) {
				mService.requestProcessed(address,
						RequestType.DISCOVER_SERVICE, false);
				return;
			}
			mService.bleServiceDiscovered(gatt.getDevice().getAddress());
		}

        /**
         * 通道读取 2015/5/18 19:47
         * @param gatt 通道
         * @param characteristic 读取的蓝牙
         * @param status 状态
         */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onCharacteristicRead " + address + " status " + status);
			if (status != BluetoothGatt.GATT_SUCCESS) {
				mService.requestProcessed(address,
						RequestType.READ_CHARACTERISTIC, false);
				return;
			}

			// Log.d(TAG, "data " + characteristic.getStringValue(0));
			mService.bleCharacteristicRead(gatt.getDevice().getAddress(),
					characteristic.getUuid().toString(), status,
					characteristic.getValue());
		}

        /**
         * 通道改变，发出通知 2015/5/18 19:48
         * @param gatt 通道
         * @param characteristic 发送通知的蓝牙
         */
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onCharacteristicChanged " + address);
			Log.d(TAG, new String(Hex.encodeHex(characteristic.getValue())));
			mService.bleCharacteristicChanged(address, characteristic.getUuid()
					.toString(), characteristic.getValue());
		}

        /**
         * 通道写入 2015/5/18 19:49
         * @param gatt 通道
         * @param characteristic 写入的蓝牙
         * @param status 状态
         */
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onCharacteristicWrite " + address + " status " + status);
			if (status != BluetoothGatt.GATT_SUCCESS) {
				mService.requestProcessed(address,
						RequestType.WRITE_CHARACTERISTIC, false);
				return;
			}
			mService.bleCharacteristicWrite(gatt.getDevice().getAddress(),
					characteristic.getUuid().toString(), status);
		};

		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			String address = gatt.getDevice().getAddress();
			Log.d(TAG, "onDescriptorWrite " + address + " status " + status);
			BleRequest request = mService.getCurrentRequest();
			if (request.type == RequestType.CHARACTERISTIC_NOTIFICATION
					|| request.type == RequestType.CHARACTERISTIC_INDICATION
					|| request.type == RequestType.CHARACTERISTIC_STOP_NOTIFICATION) {
				if (status != BluetoothGatt.GATT_SUCCESS) {
					mService.requestProcessed(address,
							RequestType.CHARACTERISTIC_NOTIFICATION, false);
					return;
				}
				if (request.type == RequestType.CHARACTERISTIC_NOTIFICATION) {
					mService.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), true,
							status);
				} else if (request.type == RequestType.CHARACTERISTIC_INDICATION) {
					mService.bleCharacteristicIndication(address, descriptor
							.getCharacteristic().getUuid().toString(), status);
				} else {
					mService.bleCharacteristicNotification(address, descriptor
							.getCharacteristic().getUuid().toString(), false,
							status);
				}
				return;
			}
		};
	};

    /**
     * 构造器 2015/5/18 19:50
     * @param service ble服务
     */
	public AndroidBle(BleService service) {
		mService = service;
		// btQuery = BTQuery.getInstance();
		if (!mService.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			mService.bleNotSupported();
			return;
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) mService
				.getSystemService(Context.BLUETOOTH_SERVICE);

		mBtAdapter = bluetoothManager.getAdapter(); // 获取蓝牙适配器 2015/5/18 19:50
        if (mBtAdapter == null) { // 判断是否找到蓝牙适配器
			mService.bleNoBtAdapter();
		}

		mBluetoothGatts = new HashMap<String, BluetoothGatt>();
	}

    /**
     * 开始扫描 2015/5/18 19:51
     */
	@Override
	public void startScan() {
		mBtAdapter.startLeScan(mLeScanCallback);
	}

    /**
     * 停止扫描 2015/5/18 19:51
     */
	@Override
	public void stopScan() {
		mBtAdapter.stopLeScan(mLeScanCallback);
	}

    /**
     * 判断适配器是否启动
     * @return 是否启动
     */
	@Override
	public boolean adapterEnabled() {
		if (mBtAdapter != null) {
			return mBtAdapter.isEnabled();
		}

		return false;
	}

    /**
     * 连接 2015/5/18 19:46
     * @param address mac地址
     * @return 是否连接成功
     */
	@Override
	public boolean connect(String address) {
        System.out.println("===========================111");
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address); // 要连接的设备
		BluetoothGatt gatt = device.connectGatt(mService, false, mGattCallback);
		if (gatt == null) { // 连接失败
			mBluetoothGatts.remove(address);
			return false;
		} else { // 连接成功
			// TODO: if state is 141, it can be connected again after about 15
			// seconds
			mBluetoothGatts.put(address, gatt);
			return true;
		}
	}

    /**
     * 断开设备连接 2015/5/18 19:52
     * @param address mac地址
     */
	@Override
	public void disconnect(String address) {
		if (mBluetoothGatts.containsKey(address)) {
			BluetoothGatt gatt = mBluetoothGatts.remove(address);
			if (gatt != null) {
				gatt.disconnect();
				gatt.close();
			}
		}
	}

    /**
     * 根据mac地址，获取ble通道服务器列表 2015/5/18 19:52
     * @param address
     * @return
     */
	@Override
	public ArrayList<BleGattService> getServices(String address) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null) {
			return null;
		}

		ArrayList<BleGattService> list = new ArrayList<BleGattService>();
		List<BluetoothGattService> services = gatt.getServices();
		for (BluetoothGattService s : services) {
			BleGattService service = new BleGattService(s);
			// service.setInfo(btQuery.getGattServiceInfo(s.getUuid()));
			list.add(service);
		}

		return list;
	}

    /**
     * 请求读取蓝牙通道 2015/5/18 19:53
     * @param address mac地址
     * @param characteristic 蓝牙通道
     * @return 是否添加ble请求读取
     */
	@Override
	public boolean requestReadCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		mService.addBleRequest(new BleRequest(RequestType.READ_CHARACTERISTIC,
				gatt.getDevice().getAddress(), characteristic));

		return true;
	}

    /**
     * 读取蓝牙通道 2015/5/18 19:56
     * @param address mac地址
     * @param characteristic 蓝牙通道
     * @return 是否读取成功
     */
	public boolean readCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null) {
			return false;
		}

		return gatt.readCharacteristic(characteristic.getGattCharacteristicA());
	}

    /**
     * 检测是否连接服务器成功 2015/5/18 19:58
     * @param address mac地址
     * @return
     */
	@Override
	public boolean discoverServices(String address) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null) {
			return false;
		}

		boolean ret = gatt.discoverServices();
		if (!ret) {
			disconnect(address);
		}

		return ret;
	}

    /**
     * 获取服务通道 2015/5/18 20:14
     * @param address mac地址
     * @param uuid 连接通道的uuid
     * @return 返回服务通道
     */
	@Override
	public BleGattService getService(String address, UUID uuid) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null) {
			return null;
		}

		BluetoothGattService service = gatt.getService(uuid);
		if (service == null) {
			return null;
		} else {
			return new BleGattService(service);
		}
	}

    /**
     * 请求通道通知 2015/5/18 20:17
     * @param address mac地址
     * @param characteristic 通道
     * @return 添加通知请求是否成功
     */
	@Override
	public boolean requestCharacteristicNotification(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		mService.addBleRequest(new BleRequest(
				RequestType.CHARACTERISTIC_NOTIFICATION, gatt.getDevice()
						.getAddress(), characteristic));
		return true;
	}

    /**
     * 通道通知 2015/5/18 20:17
     * @param address mac地址
     * @param characteristic 通道
     * @return 是否通知成功
     */
	@Override
	public boolean characteristicNotification(String address,
			BleGattCharacteristic characteristic) {
		BleRequest request = mService.getCurrentRequest();
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		boolean enable = true;
		if (request.type == RequestType.CHARACTERISTIC_STOP_NOTIFICATION) {
			enable = false;
		}

		BluetoothGattCharacteristic c = characteristic.getGattCharacteristicA();
		if (!gatt.setCharacteristicNotification(c, enable)) { // 判断是否开启了通知
			return false;
		}

		BluetoothGattDescriptor descriptor = c
				.getDescriptor(BleService.DESC_CCC);
		if (descriptor == null) {
			return false;
		}

        // 获取通知的value
		byte[] val_set = null;
		if (request.type == RequestType.CHARACTERISTIC_NOTIFICATION) {
			val_set = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
		} else if (request.type == RequestType.CHARACTERISTIC_INDICATION) {
			val_set = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
		} else {
			val_set = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
		}

		if (!descriptor.setValue(val_set)) {
			return false;
		}

		return gatt.writeDescriptor(descriptor); // 写入通知
	}

    /**
     * 请求通道写入 2015/5/18 20:21
     * @param address mac地址
     * @param characteristic 通道
     * @param remark
     * @return 返回添加通道写入请求是否成功
     */
	@Override
	public boolean requestWriteCharacteristic(String address,
			BleGattCharacteristic characteristic, String remark) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		mService.addBleRequest(new BleRequest(RequestType.WRITE_CHARACTERISTIC,
				gatt.getDevice().getAddress(), characteristic, remark));
		return true;
	}

    /**
     * 通道写入 2015/5/18 20:26
     * @param address mac地址
     * @param characteristic 通道
     * @return 通道写入是否成功
     */
	@Override
	public boolean writeCharacteristic(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null) {
			return false;
		}

		Log.d("blelib", new String(Hex.encodeHex(characteristic.getGattCharacteristicA().getValue())));
		return gatt
				.writeCharacteristic(characteristic.getGattCharacteristicA());
	}

    /**
     * 请求连接 2015/5/18 20:27
     * @param address mac地址
     * @return 请求连接是否添加成功
     */
	@Override
	public boolean requestConnect(String address) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt != null && gatt.getServices().size() == 0) {
			return false;
		}

		mService.addBleRequest(new BleRequest(RequestType.CONNECT_GATT, address));
		return true;
	}

	@Override
	public String getBTAdapterMacAddr() {
		if (mBtAdapter != null) {
			return mBtAdapter.getAddress();
		}

		return null;
	}

	@Override
	public boolean requestIndication(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		mService.addBleRequest(new BleRequest(
				RequestType.CHARACTERISTIC_INDICATION, gatt.getDevice()
						.getAddress(), characteristic));
		return true;
	}

    /**
     * 请求停止通道通知
     * @param address mac地址
     * @param characteristic 通道
     * @return 请求停止通道通知是否添加成功
     */
	@Override
	public boolean requestStopNotification(String address,
			BleGattCharacteristic characteristic) {
		BluetoothGatt gatt = mBluetoothGatts.get(address);
		if (gatt == null || characteristic == null) {
			return false;
		}

		mService.addBleRequest(new BleRequest(
				RequestType.CHARACTERISTIC_NOTIFICATION, gatt.getDevice()
						.getAddress(), characteristic));
		return true;
	}
}
