package com.example.bluetooth.le;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import java.util.ArrayList;

/**
 * 设备扫描 2015/5/18 16:40
 */
public class DeviceScanActivity extends ListActivity {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private boolean mScanning; // 是否正在扫描中
	private Handler mHandler;
	private IBle mBle; // ble控制器

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

    /**
     * 注册监听 2015/5/18 16:38
     */
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.BLE_NOT_SUPPORTED.equals(action)) { // 不支持ble
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceScanActivity.this,
								"Ble not support", Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) { // 找到设备
				// device found
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras
						.getParcelable(BleService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLeDeviceListAdapter.addDevice(device);
						mLeDeviceListAdapter.notifyDataSetChanged();
					}
				});
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) { // 没有蓝牙可以适配
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceScanActivity.this,
								"No bluetooth adapter", Toast.LENGTH_SHORT)
								.show();
						finish();
					}
				});
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.title_devices);
		mHandler = new Handler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.actionbar_indeterminate_progress);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scan:
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (mBle != null && !mBle.adapterEnabled()) { // 开启蓝牙 2015/5/18 16:38
            Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true); // 开始扫描
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBleReceiver); // 销毁注册
		scanLeDevice(false); // 停止扫描
		mLeDeviceListAdapter.clear();
	}

    /**
     * 点击设备 2015/5/18 16:36
     * @param l
     * @param v
     * @param position
     * @param id
     */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if (device == null)
			return;
		final Intent intent = new Intent(this, DeviceControlActivity.class);
		intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,
				device.getName()); // 设备名称
		intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,
				device.getAddress()); // 设备mac地址
		if (mBle != null) {
			mBle.stopScan();
		}

		startActivity(intent); // 进入设备控制心中
	}

    /**
     * 扫描设备 2015/5/18 16:34
     * @param enable
     */
	private void scanLeDevice(final boolean enable) {
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle == null) {
			return;
		}
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					if (mBle != null) {
						mBle.stopScan();
					}
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			if (mBle != null) {
				mBle.startScan();
			}
		} else {
			mScanning = false;
			if (mBle != null) {
				mBle.stopScan();
			}
		}
		invalidateOptionsMenu();
	}

    /**
     * 列表适配器 2015/5/18 16:40
     */
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
}