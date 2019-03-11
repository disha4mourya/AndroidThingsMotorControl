package com.example.disham.android_things_motor;
import android.util.Log;
import com.google.android.things.pio.Gpio;

import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

class MotorsControl {

    private Gpio LeftMotor1,LeftMotor2,RightMotor1,RightMotor2;
    private PeripheralManager manager;

    private String currentDirection="forward";
    private String state="stop";


    private static final String ECHO_PIN_NAME = "GPIO2_IO07";
    private static final String TRIGGER_PIN_NAME = "GPIO6_IO15";


    private Gpio mEcho;
    private Gpio mTrigger;
    long time1, time2;
    int keepBusy;


     void motorInit()
    {

        manager = PeripheralManager.getInstance();
        Log.d("ROBo", "Available GPIOs: " + manager.getGpioList());
        try
        {
            Log.i("ROBo", "Configuring GPIO pins");

            LeftMotor1 = manager.openGpio("GPIO2_IO03");
            LeftMotor2 = manager.openGpio("GPIO1_IO10");

            RightMotor1 = manager.openGpio("GPIO2_IO00");
            RightMotor2 = manager.openGpio("GPIO2_IO05");

            LeftMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            LeftMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            RightMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            RightMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);





            // Create GPIO connection.
            mEcho = manager.openGpio(ECHO_PIN_NAME);
            // Configure as an input.
            mEcho.setDirection(Gpio.DIRECTION_IN);
            // Enable edge trigger events.
            mEcho.setEdgeTriggerType(Gpio.EDGE_BOTH);
            // Set Active type to HIGH, then the HIGH events will be considered as TRUE
            mEcho.setActiveType(Gpio.ACTIVE_HIGH);


            // Create GPIO connection.
            mTrigger = manager.openGpio(TRIGGER_PIN_NAME);
            // Configure as an output with default LOW (false) value.
            mTrigger.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        }
        catch (IOException e)
        {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    while (true) {
                        readDistanceSync();
                        Thread.sleep(200);
                    }
                } catch (IOException e) {
                    Log.e("USS Error", "->", e);
                } catch (InterruptedException e) {
                    Log.e("USS Error", "->", e);
                }
            }
        }.start();
    }

    void forward()
    {
        state="start";
        try {
            Log.e("Direction", "Forward");
            LeftMotor1.setValue(false);
            LeftMotor2.setValue(true);

            RightMotor1.setValue(false);
            RightMotor2.setValue(true);
            currentDirection="forward";
        } catch (IOException e) {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }
    }



    void backward()
    {
        state="start";
        try {
            Log.e("Direction", "Backward");
            LeftMotor1.setValue(true);
            LeftMotor2.setValue(false);

            RightMotor1.setValue(true);
            RightMotor2.setValue(false);
            currentDirection="backward";

        } catch (IOException e) {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }
    }


    void left()
    {

        try {
            Log.e("Direction", "left");
            LeftMotor1.setValue(true);
            LeftMotor2.setValue(false);

            RightMotor1.setValue(false);
            RightMotor2.setValue(true);

            moveToCurrentDirection();

        } catch (IOException e) {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }
    }


    void right()
    {
        try {
            Log.e("Direction", "right");
            LeftMotor1.setValue(false);
            LeftMotor2.setValue(true);

            RightMotor1.setValue(true);
            RightMotor2.setValue(false);

            moveToCurrentDirection();
        } catch (IOException e) {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }
    }




    void stop()
    {
        state="stop";
        try {
            Log.e("Direction", "Stop");
            LeftMotor1.setValue(false);
            LeftMotor2.setValue(false);

            RightMotor1.setValue(false);
            RightMotor2.setValue(false);
        } catch (IOException e) {
            Log.e("ROBo", "Error configuring GPIO pins", e);
        }

    }

    private void moveToCurrentDirection()
    {
        try {
            Thread.sleep(200);
            if(currentDirection.equals("backward"))
            {
                Log.d("MOVE---->>>>","BACKWARD......");
                backward();
            }
            else{
                Log.d("MOVE---->>>>","FORWARD......");
                forward();
            }
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }




    }


    private void readDistanceSync() throws IOException, InterruptedException {
        // Just to be sure, set the trigger first to false
        mTrigger.setValue(false);
        Thread.sleep(0,2000);

        // Hold the trigger pin HIGH for at least 10 us
        mTrigger.setValue(true);
        Thread.sleep(0,10000); //10 microsec

        // Reset the trigger pin
        mTrigger.setValue(false);

        // Wait for pulse on ECHO pin
        while (!mEcho.getValue()) {
            //long t1 = System.nanoTime();
            Log.d("ECO", "Echo has not arrived...");

            // keep the while loop busy
            keepBusy = 0;
            mTrigger.setValue(false);
            Thread.sleep(0,2000);

            // Hold the trigger pin HIGH for at least 10 us
            mTrigger.setValue(true);
            Thread.sleep(0,10000); //10 microsec

            // Reset the trigger pin
            mTrigger.setValue(false);
            Thread.sleep(0,2000);
            //long t2 = System.nanoTime();
            //Log.d(TAG, "diff 1: " + (t2-t1));
        }
        time1 = System.nanoTime();
        Log.i("USS", "Echo ARRIVED!");

        // Wait for the end of the pulse on the ECHO pin
        while (mEcho.getValue()) {
            //long t1 = System.nanoTime();
            Log.d("ECO", "Echo is still coming...");

            // keep the while loop busy
            keepBusy = 1;

            //long t2 = System.nanoTime();
            //Log.d(TAG, "diff 2: " + (t2-t1));
        }
        time2 = System.nanoTime();
        Log.i("USS", "Echo ENDED!");

        // Measure how long the echo pin was held high (pulse width)
        long pulseWidth = time2 - time1;

        // Calculate distance in centimeters. The constants
        // are coming from the datasheet, and calculated from the assumed speed
        // of sound in air at sea level (~340 m/s).
        double distance = (pulseWidth / 1000.0 ) / 58.23 ; //cm

        // or we could calculate it withe the speed of the sound:
        //double distance = (pulseWidth / 1000000000.0) * 340.0 / 2.0 * 100.0;

        Log.i("USS", "distance: " + distance + " cm");
        if(distance<20)
        {
            Log.d("stop trig","--->");
            if(state.equals("start") && currentDirection.equals("forward")) {
                left();
            }
        }
    }

}
