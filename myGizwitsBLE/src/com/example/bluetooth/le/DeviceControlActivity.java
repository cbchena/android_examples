package com.example.bluetooth.le;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 设备控制中心 2015/5/18 19:09
 */
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME"; // 设备名
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"; // 设备的mac地址

	private TextView mConnectionState; // 显示连接状态
	private String mDeviceName; // 显示设备名
	private String mDeviceAddress; // 显示设备mac地址
	private ExpandableListView mGattServicesList;
	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>(); // 存放特征列表
	private boolean mConnected = false; // 是否已经连接

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	protected IBle mBle; // ble控制器

    /**
     * 连接监听器 2015/5/18 19:21
     */
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			if (!mDeviceAddress.equals(extras.getString(BleService.EXTRA_ADDR))) {
				return;
			}

			String action = intent.getAction();
			if (BleService.BLE_GATT_CONNECTED.equals(action)) { // 连接成功
				mConnected = true;
				updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) { // 设备断开连接
				onDeviceDisconnected();
			} else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) { // 显示服务器的所有uuid通道
				displayGattServices(mBle.getServices(mDeviceAddress));
			}
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Log.d(TAG, "onChildClick " + groupPosition + " " + childPosition);
			if (mGattCharacteristics != null) {
				final BleGattCharacteristic characteristic = mGattCharacteristics
						.get(groupPosition).get(childPosition);
				Intent intent = new Intent(DeviceControlActivity.this,
						CharacteristicActivity.class);
				intent.putExtra("address", mDeviceAddress);
				Log.d(TAG, "service size " + mBle.getServices(mDeviceAddress).size());
				intent.putExtra("service", mBle.getServices(mDeviceAddress)
						.get(groupPosition).getUuid().toString());
				intent.putExtra("characteristic", characteristic.getUuid()
						.toString().toUpperCase());
				startActivity(intent);
				return true;
			}
			return false;
		}
	};

	private void clearUI() {
		mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_services_characteristics);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME); // 设备名
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS); // 设备mac地址

		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
		mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
		mGattServicesList.setOnChildClickListener(servicesListClickListner);
		mConnectionState = (TextView) findViewById(R.id.connection_state);

		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle(); // 获取ble控制器
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mBleReceiver, BleService.getIntentFilter()); // 注册监听器
		ArrayList<BleGattService> services = mBle.getServices(mDeviceAddress); // 获取服务器列表
        if (services == null || services.size() == 0) {
            onDeviceDisconnected();
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBleReceiver); // 销毁监听器
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBle != null) {
			mBle.disconnect(mDeviceAddress); // 断开连接
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected) {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_connect:
			mBle.requestConnect(mDeviceAddress); // 请求连接
			return true;
		case R.id.menu_disconnect:
			mBle.disconnect(mDeviceAddress); // 断开连接
			onDeviceDisconnected();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * 更改连接状态 2015/5/18 19:28
     */
	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
			}
		});
	}

    /**
     * 显示服务器的所有uuid通道 2015/5/18 19:30
     * @param gattServices
     */
	private void displayGattServices(List<BleGattService> gattServices) {
		if (gattServices == null)
			return;

		String uuid = null;
		String unknownServiceString = getResources().getString(
				R.string.unknown_service);
		String unknownCharaString = getResources().getString(
				R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BleGattService gattService : gattServices) { // 遍历服务器
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString().toUpperCase();

			currentServiceData.put(LIST_NAME, Utils.BLE_SERVICES
					.containsKey(uuid) ? Utils.BLE_SERVICES.get(uuid)
					: unknownServiceString);
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BleGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BleGattCharacteristic> charas = new ArrayList<BleGattCharacteristic>();

			// Loops through available Characteristics.
			for (BleGattCharacteristic gattCharacteristic : gattCharacteristics) { // 遍历服务器的所有通道
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString().toUpperCase();
                System.out.println("=====================    " + uuid);
                currentCharaData
						.put(LIST_NAME,
                                Utils.BLE_CHARACTERISTICS.containsKey(uuid) ? Utils.BLE_CHARACTERISTICS
                                        .get(uuid) : unknownCharaString);
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData); // 添加在数据列表，用于显示在适配器列表中
		}

        // 数据适配器
		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
				this, gattServiceData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
						android.R.id.text2 }, gattCharacteristicData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		mGattServicesList.setAdapter(gattServiceAdapter); // 设置适配器
	}

    /**
     * 设备断开连接 2015/5/18 19:32
     */
	private void onDeviceDisconnected() {
		mConnected = false;
		updateConnectionState(R.string.disconnected);
		invalidateOptionsMenu();
		clearUI();
	}
}
