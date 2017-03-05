package com.example.ty.hackutd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private static String address = "34:02:86:BA:E8:E2";

    TextView out;
    Button btn_quit;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your server's MAC address
    //private static String address = "00:10:60:AA:B9:B2";
    String left_click = "left_click\n";
    String right_click = "right_click\n";
    String release = "release\n";
    String degrees_str = "\n";

    String quit = "quit\n";

    final byte[] left_click_buf = left_click.getBytes();
    final byte[] right_click_buf = right_click.getBytes();
    final byte[] release_buf = release.getBytes();

    final byte[] quit_buf = quit.getBytes();


    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Joystick joystick = (Joystick) findViewById(R.id.joystick);
        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                try {
                    outStream.write(("d " + degrees + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUp() {
                try {
                    outStream.write(release_buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;
        out = (TextView) findViewById(R.id.out);


        out.append("\n...In onCreate()...");

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();
        CheckBTState();
    }

    public void onStart() {
        super.onStart();
        while(mSensor == null) {
            out.append("mSensor is null\n");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        }
        out.append("\n...In onStart()...");
    }


    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        out.append("\n...In onResume...\n...Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        out.append("1 ");
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        out.append("1 ");

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();

        }
        out.append(btSocket.getRemoteDevice().getName());

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        try {
            btSocket.connect();
            out.append(btSocket.getRemoteDevice().getName());

            out.append("\n...Connection established and data link opened...");
        } catch (IOException e) {
            e.printStackTrace();

            try {
                btSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();

            }
        }

        // Create a data stream so we can talk to server.
        out.append("\n...Sending message to server...");
        String message = "Hello from Android.\n";
        out.append("\n\n...The message that we will send to the server is: "+message);

        try {
            out.append("...Attempting to get Outstream\n");
            outStream = btSocket.getOutputStream();
            out.append("Got Outstream\n");
        } catch (IOException e) {
            e.printStackTrace();

        }

        try {
            out.append("...Attempting to write to outStream\n");
            outStream.write(left_click_buf);
            out.append("Written to outStream\n");
            outStream.flush();


        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

        }

        btn_quit = (Button) findViewById(R.id.quit);
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    out.append("quit\n");
                    outStream.write(quit_buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // Handle volume button left click and right click
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                try {
                    outStream.write(left_click_buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                try {
                    outStream.write(right_click_buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);

        InputStream inStream;
        try {
            inStream = btSocket.getInputStream();
            BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
            String lineRead=bReader.readLine();
            out.append("\n..."+lineRead+"\n");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        out.append("\n...In onPause()...");



        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    public void onStop() {
        super.onStop();
        out.append("\n...In onStop()...");
    }

    public void onDestroy() {
        super.onDestroy();
        out.append("\n...In onDestroy()...");
    }

    private void CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
        } else {
            if (btAdapter.isEnabled()) {
                out.append("\n...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }




    // Sensor Stuff

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    boolean dirty1 = false;
    boolean dirty2 = false;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
            dirty1 = true;
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
            dirty2 = true;
        }

        if (dirty1 && dirty2) {
            updateOrientationAngles();
        }
    }

    int x = 1;
    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(mRotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);

        for (int i = 0; i < 9; ++i) {
            mRotationMatrix[i] = adjustedRotationMatrix[i];
        }
        // "mRotationMatrix" now has up-to-date information.
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
        double azimuth = (mOrientationAngles[0] + (Math.PI / 2.0));
        double roll = (mOrientationAngles[2] + (Math.PI / 2.0));

        double y;
        double x;

        double upper_bound;

        if (roll >= 1 && roll <= Math.PI)
            y = 1;
        else if (roll <= -1 || roll > Math.PI)
            y = -1;
        else
            y = roll;

        if (azimuth >= 1 && azimuth <= Math.PI)
            x = 1;
        else if (azimuth <= -1 || azimuth > Math.PI)
            x = -1;
        else
            x = azimuth;

        String mouse = "mouse " + x + " " + y + "\n";
        final byte[] mouse_buf = mouse.getBytes();
        try {
            outStream.write(mouse_buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
