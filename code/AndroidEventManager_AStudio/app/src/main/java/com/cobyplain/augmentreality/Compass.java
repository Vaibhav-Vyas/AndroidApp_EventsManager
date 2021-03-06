package com.cobyplain.augmentreality;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.socialapp.eventmanager.R;

/*
 * Portions (c) 2009 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Coby Plain coby.plain@gmail.com, Ali Muzaffar ali@muzaffar.me
 */

public class Compass extends Activity {

	private static final String TAG = "Compass";
	private static boolean DEBUG = false;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private DrawSurfaceView mDrawView;
	LocationManager locMgr;

	private final SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (DEBUG)
				Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
			if (mDrawView != null) {
				mDrawView.setOffset(event.values[0]);
				mDrawView.invalidate();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		setContentView(R.layout.activity_augmented_reality);
		
		mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);
		
		locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE); // <2>
		LocationProvider high = locMgr.getProvider(locMgr.getBestProvider(
				LocationUtils.createFineCriteria(), true));

		// using high accuracy provider... to listen for updates
		locMgr.requestLocationUpdates(high.getName(), 0, 0f,
				new LocationListener() {
					public void onLocationChanged(Location location) {
						// do something here to save this new location
						Log.d(TAG, "Location Changed");
						mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
						mDrawView.invalidate();
					}

					public void onStatusChanged(String s, int i, Bundle bundle) {

					}

					public void onProviderEnabled(String s) {
						// try switching to a different provider
					}

					public void onProviderDisabled(String s) {
						// try switching to a different provider
					}
				});

	}

	@Override
	protected void onResume() {
		if (DEBUG)
			Log.d(TAG, "onResume");
		super.onResume();

		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		if (DEBUG)
			Log.d(TAG, "onStop");
		mSensorManager.unregisterListener(mListener);
		super.onStop();
	}
}
