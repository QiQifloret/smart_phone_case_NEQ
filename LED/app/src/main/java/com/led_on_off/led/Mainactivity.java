package com.led_on_off.led;

/**
 * Created by qiqi on 2017/3/27.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

import static com.led_on_off.led.R.drawable.about;


public class Mainactivity extends ActionBarActivity implements View.OnClickListener,SensorEventListener {

    private ledControl fg_1;
    private progress fg_2;
    private About fg_3;
    private FragmentTransaction fTransaction;
    private FragmentManager fManager;
    private FrameLayout ly_content;

    private double pitch;

    Switch mySwitch;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    ImageButton  Discnt;
    Button Abt,Inf,Home,line_1,line_2,line_3;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean status = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //view of activity_index
        setContentView(R.layout.activity_index);
        fManager = getFragmentManager();
        bindViews();//stimulate one click, the first fragment
        onClick(Home);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        Discnt = (ImageButton)findViewById(R.id.discnt);
        //commands to be sent to bluetooth
        Discnt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
        new ConnectBT().execute(); //Call the class to connect

        mySwitch = (Switch) findViewById(R.id.swi);
        //set the switch to OFF
        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    turnOnLed();
                    mySwitch.setTextOff("ON");
                }else{
                    turnOffLed();
                    mySwitch.setTextOn("OFF");
                }

            }
        });



        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_ACCELEROMETER

        mSensorManager.registerListener( this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME
    }

    public void bindViews()
    {
        Abt = (Button)findViewById(R.id.abt);
        Inf =(Button)findViewById(R.id.inf);
        Home = (Button)findViewById(R.id.home);
        line_1 = (Button)findViewById(R.id.h);
        line_2 = (Button)findViewById(R.id.i);
        line_3 = (Button)findViewById(R.id.a);
        ly_content = (FrameLayout) findViewById(R.id.content);


        Inf.setOnClickListener(this);
        Home.setOnClickListener(this);
        Abt.setOnClickListener(this);
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void onClick(View view) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (view.getId()) {
            case R.id.home:
                clearof();
                Home.setSelected(true);
               line_1.setSelected(true);
                // 如果fg1为空，则创建一个并添加到界面上
                if (fg_1 == null) {
                    fg_1 = new ledControl();
                    fTransaction.add(R.id.content, fg_1);
                } else {
                    // if it is not null,shown
                    fTransaction.show(fg_1);
                }

                break;
            case R.id.inf:
                clearof();
               Inf.setSelected(true);
               line_2.setSelected(true);
                if (fg_2 == null) {
                    fg_2 = new progress();
                    fTransaction.add(R.id.content, fg_2);
                } else {
                    fTransaction.show(fg_2);
                }

                break;
            case R.id.abt:
                clearof();
                Abt.setSelected(true);
                line_3.setSelected(true);
                if (fg_3 == null) {
                    fg_3 = new About();
                    fTransaction.add(R.id.content, fg_3);
                } else {
                    fTransaction.show(fg_3);
                }

                break;
        }
        fTransaction.commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg_1 != null)fragmentTransaction.hide(fg_1);
        if(fg_2 != null)fragmentTransaction.hide(fg_2);
        if(fg_3 != null)fragmentTransaction.hide(fg_3);
    }

    private void clearof() {
        Home.setSelected(false);
        Inf.setSelected(false);
        Abt.setSelected(false);
        line_1.setSelected(false);
        line_2.setSelected(false);
        line_3.setSelected(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Mainactivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
     protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
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

            if(status) {
                if (pitch >= 0 && pitch < 30) {
                    red();
                }
                if (pitch >= 30 && pitch < 60) {
                    yellow();
                }
                if (pitch >= 60 && pitch < 90) {
                    green();
                }
            }

        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void turnOffLed()
    {
        status= false;
        if (btSocket!=null)
        {
            try
            {

                btSocket.getOutputStream().write("0".toString().getBytes());


            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOnLed()
    {
        status = true;
        if (btSocket!=null)
        {
            try
            {

                btSocket.getOutputStream().write("1".toString().getBytes());

            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void red()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("2".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void yellow()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("3".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void green()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("4".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

}
