import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.bluetooth.*;
import javax.microedition.io.*;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SimpleSPPServer implements Runnable{

    Robot r;
    
    
    long lastBufferTime = System.currentTimeMillis();
    int bufferTime = 150;
    int prevX;
    int curX = 0;
    int prevY;
    int curY = 0;
    boolean runMe = true;
    //start server
    private void startServer() throws Exception{

        r = new Robot();
        
        Thread t = new Thread(this);
        t.start();
        
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //UUID uuid = new UUID("76d3a401-9cdc-4d11-a01b-6699163b4e7a1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";

        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );

        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection=streamConnNotifier.acceptAndOpen();

        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: "+dev.getBluetoothAddress());
        System.out.println("Remote device name: "+dev.getFriendlyName(true));

        //read string from spp client
        InputStream inStream=connection.openInputStream();
        BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        
        prevX = width / 2;
        prevY = height / 2;
        curX = width / 2;
        curY = height / 2;
        
        System.out.println("Waiting for next input");
        
        // Buffer to smooth out mouse movement
        //int bufferTime = 150; // Milliseconds
        int bufferMax = 10;
        int bufferSize = 0;
        ArrayList<Integer> xbuffer = new ArrayList<Integer>();
        ArrayList<Integer> ybuffer = new ArrayList<Integer>();
        lastBufferTime = System.currentTimeMillis();

        while (true) {
            String lineRead=bReader.readLine();
//          System.out.println(lineRead);
            if(lineRead == null)
                break; 
            if(lineRead.charAt(0) == 'm') {
                String array[] = lineRead.split(" "); 
                int move_width = (int)(width *(Float.parseFloat(array[1]) + 1.0f) / 2.0f);
                int move_height = (int)(height * (-Float.parseFloat(array[2]) + 1.0f)/ 2.0f);

                xbuffer.add(move_width);
                ybuffer.add(move_height);
                //bufferSize++;
                
                if (System.currentTimeMillis() - lastBufferTime >= bufferTime) {
                    
                    Collections.sort(xbuffer);
                    Collections.sort(ybuffer);
                    
                    // Reset
                    int x_median = xbuffer.get(xbuffer.size()/2);
                    int y_median = ybuffer.get(ybuffer.size()/2);
                    
                    //bufferSize = 0;
                    xbuffer.clear();
                    ybuffer.clear();
                    
                    lastBufferTime = System.currentTimeMillis();
                    
                    System.out.println("prevX " + (prevX - curX));
                    if(((prevX + 3 > x_median)) && ((prevX - 3) < x_median)) {
                        curX = prevX;
                        curY = prevY;
                        continue;
                    }
                        
                    if(((prevY + 3 > y_median)) && ((prevY - 3) < y_median)) {
                        curX = prevX;
                        curY = prevY;
                        continue;
                    }
                    
                    
                    prevX = curX;
                    prevY = curY;
                    
                    curX = x_median;
                    curY = y_median;
                    
                    //r.mouseMove(x_median, y_median);
                    
                }
                
                //System.out.println("width: " + move_width + "   height: " + move_height);
                
            }
            else if(lineRead.charAt(0) == 'd'){
                String degreeArray[] = lineRead.split(" ");
                int degrees = (int)Double.parseDouble(degreeArray[1]);
                
                if(degrees <= 150 && degrees >= 30) {
                    r.keyPress(KeyEvent.VK_W);
                } else {
                    r.keyRelease(KeyEvent.VK_W);
                }
                if(degrees <= 60 && degrees >= -60) {
                    r.keyPress(KeyEvent.VK_D);
                } else {
                    r.keyRelease(KeyEvent.VK_D);
                }
                if(degrees >= -150 && degrees <= -30) {
                    r.keyPress(KeyEvent.VK_S);
                } else {
                    r.keyRelease(KeyEvent.VK_S);
                }
                if(degrees >= 120 || degrees <= -120) {
                    r.keyPress(KeyEvent.VK_A);
                } else {
                    r.keyRelease(KeyEvent.VK_A);
                }
            }
            else if(lineRead.equals("release")){
                r.keyRelease(KeyEvent.VK_A);
                r.keyRelease(KeyEvent.VK_S);
                r.keyRelease(KeyEvent.VK_W);
                r.keyRelease(KeyEvent.VK_D);
            }
            else if (lineRead.equals("left_click")) {
                r.mousePress(InputEvent.BUTTON1_MASK);
                r.mouseRelease(InputEvent.BUTTON1_MASK);
            }
            else if (lineRead.equals("right_click")) {
                r.mousePress(InputEvent.BUTTON3_MASK);
                r.mouseRelease(InputEvent.BUTTON3_MASK);
            }
            else if (lineRead.equals("quit")) {
                break;
            } else if (lineRead.equals("reload")) {
                r.keyPress(KeyEvent.VK_R);
                r.keyRelease(KeyEvent.VK_R);
            }
        }
        
        runMe = false;
        System.out.println("Done Looping");
        
        
        //send response to spp client
        // IMPLEMENT LATER
       //  OutputStream outStream=connection.openOutputStream();
       //  BufferedWriter bWriter=new BufferedWriter(new OutputStreamWriter(outStream));
       //  bWriter.write("Response String from SPP Server\r\n");

        /*PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write("Response String from SPP Server\r\n");
        pWriter.flush();
        pWriter.close();*/

        streamConnNotifier.close();

    }


    public static void main(String[] args) throws Exception {

        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());

        SimpleSPPServer sampleSPPServer=new SimpleSPPServer();
        sampleSPPServer.startServer();
        
        

    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (runMe) {
            long timeElapsed = (System.currentTimeMillis() - lastBufferTime);
            double factor = timeElapsed / (bufferTime*1.0);
            //System.out.printf("Time elapsed: %d \n", timeElapsed);
            factor *= 1.2; //Make it a little faster
            if (factor > 1.0) factor = 1.0;
            //System.out.println("factor " + factor);
            //System.out.println("curX - prevX" + (curX - prevX *1.0)/(1.0 *bufferTime));

            r.mouseMove(
                    (int)(prevX + factor * (curX - prevX * 1.0)/*/(1.0*bufferTime)*/), 
                    (int)(prevY + factor * (curY - prevY  * 1.0)/*/(1.0*bufferTime)*/));
            
        }
        
    }
}