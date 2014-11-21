package com.example.app_controller;

import java.io.IOException;
import java.net.Socket;

import com.example.socket.Socket_util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Sensor aSensor;
	private TextView textviewX;
	private TextView textviewY;
	private TextView textviewZ;
	private TextView textviewD;
	private TextView textviewD_seted;
	private Button button_connect;
	private Button button_reset;
	private Button button_a;
	private Button button_b;

	private static Socket socket;

	float[] accelerometerValues = new float[3];
	float[] magneticFieldValues = new float[3];
	float[] values = new float[3];
	float[] rotate = new float[9];

	private Socket_util socket_util;

	private boolean state_left;
	private boolean state_right;
	private boolean state_forward;
	private boolean state_back;

	private boolean connect_state;
	
	private int set_direct;
	private int orientation_offset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// no recommanded
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		textviewX = (TextView) findViewById(R.id.textView1);
		textviewY = (TextView) findViewById(R.id.textView2);
		textviewZ = (TextView) findViewById(R.id.textView3);
		textviewD = (TextView) findViewById(R.id.textView7);
		textviewD_seted = (TextView) findViewById(R.id.textView10);
		button_connect = (Button) findViewById(R.id.button3);
		button_reset = (Button) findViewById(R.id.button4);
		button_a = (Button) findViewById(R.id.button1);
		button_b = (Button) findViewById(R.id.button2);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (null == mSensorManager) {
			Log.d(TAG, "deveice not support SensorManager");
		}

		state_left = false;
		state_right = false;
		state_forward = false;
		state_back = false;

		connect_state = false;
		
		// util
		socket_util = new Socket_util();

		button_connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				socket = socket_util.connectSocket(socket);
				
				connect_state = true;
				 
				//String tmp = (String) textviewD.getText();
				set_direct =(int)Float.valueOf((String) textviewD.getText()).floatValue();
				//set_direct =30;
				textviewD_seted.setText(""+set_direct);

			}
		});

		// button_a
		button_a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					socket_util.sendMessage(socket, "A");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// button_b
		button_b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					socket_util.sendMessage(socket, "a");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// button_reset
		button_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				set_direct =(int)Float.valueOf((String) textviewD.getText()).floatValue();
				textviewD_seted.setText(""+set_direct);

			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorManager.unregisterListener(myListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(myListener, aSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(myListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	final SensorEventListener myListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerValues = event.values;
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magneticFieldValues = event.values;
			}

			SensorManager.getRotationMatrix(rotate, null, accelerometerValues,
					magneticFieldValues);
			SensorManager.getOrientation(rotate, values);
			// 经过SensorManager.getOrientation(rotate, values);得到的values值为弧度
			// 转换为角度
			values[0] = (float) Math.toDegrees(values[0]);
			textviewD.setText("" + values[0]);
			textviewX.setText("" + accelerometerValues[0]);
			textviewY.setText("" + accelerometerValues[1]);
			textviewZ.setText("" + accelerometerValues[2]);

			// /Util

			if (connect_state != false) {

				orientation_offset = set_direct - (int) values[0];
				// left
				if (state_left == false && orientation_offset > 10) {
					state_left = true;
					try {
						socket_util.sendMessage(socket, "A");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// right
				else if (state_right == false && orientation_offset < -10) {
					state_right = true;
					try {
						socket_util.sendMessage(socket, "D");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// origin
				else {
					state_right = false;
					state_left = false;
				}

				// UP
				if (state_forward == false && (int) accelerometerValues[2] > 2) {
					state_forward = true;
					try {
						socket_util.sendMessage(socket, "W");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// back
				else if (state_back == false
						&& (int) accelerometerValues[2] < -2) {
					state_back = true;
					try {
						socket_util.sendMessage(socket, "S");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// origin
				else {
					state_forward = false;
					state_back = false;
				}

			}// end null socket
		}
	};

}
