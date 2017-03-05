package com.example.ty.hackutd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private static String address = "34:02:86:BA:E8:E2";

    TextView out;
    Button but;
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

    String quit = "quit\n";

    final byte[] left_click_buf = left_click.getBytes();
    final byte[] right_click_buf = right_click.getBytes();

    final byte[] quit_buf = quit.getBytes();

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        out.append("\n...In onStart()...");
    }
int i = 5;
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(new SensorEventListener() {
            int counter = 0;
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if ( ++counter != 20) {
                    return;

                }
                counter = 0;
                System.out.println("...Trigger onSensorChanged.");
                System.out.println(sensorEvent.sensor.getName());
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    //System.out.println("Azimith: " + sensorEvent.values[0]);
                    //System.out.println("Pitch: " + sensorEvent.values[1]);
                    //System.out.println("Roll: " + sensorEvent.values[2]);
                    System.out.println(sensorEvent.values.toString());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                out.append("...Trigger onAccuracyChanged\n");
            }
        }, mSensor,SensorManager.SENSOR_DELAY_NORMAL);

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

        final byte[] msgBuffer = message.getBytes();


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

        but = (Button) findViewById(R.id.shoot);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    out.append("boom\n");
                    outStream.write(left_click_buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

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

        //out.append("\n...Hello\n");
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


}
    /*BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    private static final String TAG = "MAIN";
    private OutputStream outStream = null;




    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Get a better phone lol", Toast.LENGTH_LONG).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        } else {
            System.out.println("No previously paired devices");
            //boolean began = mBluetoothAdapter.startDiscovery();
            //System.out.println(began);

        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        String message = "Fuck you, Tony Romo";
        byte[] buf = message.getBytes();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to create Socket", Toast.LENGTH_LONG).show();
        }

        System.out.println("btSocket");
        try {
            outStream = btSocket.getOutputStream();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to get btSocket stream", Toast.LENGTH_LONG).show();
        }

        try {
            outStream.write(buf);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to write to buffer", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
           // manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }*/

