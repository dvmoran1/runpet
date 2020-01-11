package com.example.aplicacionmoviles;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ShakeService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAcceloremeter;
    private float mAccel, mAccelCurrent, mAcceLast;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        mAcceloremeter = mSensorManager
                .getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        mSensorManager.registerListener(this, mAcceloremeter,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAcceLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt( (double) ( x * x + y * y + z * z ) );
        float delta = mAccelCurrent - mAcceLast;
        mAccel = mAccel * 0.9f + delta;

        if( mAccel > 11 ){

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
