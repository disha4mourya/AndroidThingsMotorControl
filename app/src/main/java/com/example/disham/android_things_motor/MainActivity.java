package com.example.disham.android_things_motor;

import android.app.Activity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import java.util.Locale;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button Forward,Backward,Left,Right,Stop,show_ip,ip_close;
    private TextView ip;
    public MotorsControl motor;

    private String current_action="no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        motor=new MotorsControl();
        motor.motorInit();




        HttpServer server = new HttpServer(8090, this,
                new HttpServer.CommandListener() {


                    @Override
                    public void onCommand(String command) {



                        if (command.equals("F") && !current_action.equals("F") ) {
                            Log.d("Command From Http", "Command received ["+command+"]");
                            motor.forward();

                        }
                        else if (command.equals("B") && !current_action.equals("B") ) {
                            Log.d("Command From Http", "Command received ["+command+"]");
                            motor.backward();
                        }
                        else if (command.equals("S") && !current_action.equals("S") ) {
                            Log.d("Command From Http", "Command received ["+command+"]");
                            motor.stop();
                        }
                        else if (command.equals("L") && !current_action.equals("F")) {
                            Log.d("Command From Http", "Command received ["+command+"]");
                            motor.left();
                        }
                        else if (command.equals("R") && !current_action.equals("F") ) {
                            Log.d("Command From Http", "Command received ["+command+"]");
                            motor.right();
                        }

                        current_action=command;
                    }


                });


        Forward = findViewById(R.id.Forward);
        Backward = findViewById(R.id.Backward);
        Left=findViewById(R.id.Left);
        Right=findViewById(R.id.Right);
        Stop=findViewById(R.id.Stop);


        show_ip=findViewById(R.id.show_ip);

        ip = findViewById(R.id.ip);
        ip_close=findViewById(R.id.ip_close);



        show_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String final_ip="http://"+getIP()+":8090";
                Log.i("IP IS:",final_ip);
                ip.setText(final_ip);

                Forward.setVisibility(View.GONE);
                Backward.setVisibility(View.GONE);
                Left.setVisibility(View.GONE);
                Right.setVisibility(View.GONE);
                Stop.setVisibility(View.GONE);
                show_ip.setVisibility(View.GONE);

                ip.setVisibility(View.VISIBLE);
                ip_close.setVisibility(View.VISIBLE);
            }
        });

        ip_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Forward.setVisibility(View.VISIBLE);
                Backward.setVisibility(View.VISIBLE);
                Left.setVisibility(View.VISIBLE);
                Right.setVisibility(View.VISIBLE);
                Stop.setVisibility(View.VISIBLE);
                show_ip.setVisibility(View.VISIBLE);

                ip.setVisibility(View.GONE);
                ip_close.setVisibility(View.GONE);
            }
        });

        Forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                motor.forward();
            }
        });

        Backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                motor.backward();
            }
        });

        Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                motor.left();
            }
        });

        Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                motor.right();
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                motor.stop();
            }
        });
    }


    private String getIP() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            Log.e("IP ERROR", ex.getMessage());
            return null;
        }
    }



}
