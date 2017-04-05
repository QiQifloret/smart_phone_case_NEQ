package com.led_on_off.led;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.widget.*;

import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Character.FORMAT;


public class ledControl extends Fragment implements SensorEventListener  {
    private TextView text1;
    private TextView textviewX;
    private TextView textviewY;
    private TextView textviewZ;
    private TextView textviewP;
    private ImageView circle;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double pitch;
    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_led_control, container, false);
        textviewX = (TextView) view.findViewById(R.id.x_value);
        textviewY = (TextView) view.findViewById(R.id.y_value);
        textviewZ = (TextView) view.findViewById(R.id.z_value);
        textviewP = (TextView) view.findViewById(R.id.p_value);
        text1 = (TextView) view.findViewById(R.id.timer);
        circle = (ImageView)view.findViewById(R.id.circle);

        mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_ACCELEROMETER
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME

        new CountDownTimer(1800000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                float radius = Float.valueOf(200-millisUntilFinished/9000);
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStyle(Style.FILL);

                Bitmap bmp = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                canvas.drawCircle(bmp.getWidth()/2,bmp.getHeight()/2,radius,paint);

                circle.setImageBitmap(bmp);

                text1.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                text1.setText("done!");
            }
        }.start();


        return view;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x= (int) event.values[0];
            int y= (int) event.values[1];
            int z= (int) event.values[2];
            double x_Buff = (float)x;
            double y_Buff = (float)y;
            double z_Buff = (float)z;

            // pitch = Math.atan2((- x_Buff) , Math.sqrt(y_Buff * y_Buff + z_Buff * z_Buff)) * 57.3;
            pitch = Math.atan2(y_Buff , z_Buff) * 57.3;// angle


            textviewX.setText(String.valueOf(x));
            textviewY.setText(String.valueOf(y));
            textviewZ.setText(String.valueOf(z));
            textviewP.setText(String.valueOf(pitch));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
