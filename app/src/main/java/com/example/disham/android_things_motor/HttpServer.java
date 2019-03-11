package com.example.disham.android_things_motor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer  extends NanoHTTPD {



        private String TAG = getClass().getName();
        private Context context;
        private CommandListener listener;

        public HttpServer(int port, Context context, CommandListener listener) {

            super(port);
            this.context = context;
            this.listener = listener;
            Log.d(TAG, "Starting Server");
            try {
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
            Map<String, String> params = session.getParms();

//            String control = params.get("control");
            String action = params.get("btn");

//            Log.d(TAG, "Serve - Control ["+control+"] - Action ["+action+"]");


            if (action != null && !"".equals(action))
                listener.onCommand(action);

            return newFixedLengthResponse(readHTMLFile().toString());
        }



        private StringBuffer readHTMLFile() {
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            try {
                reader = new BufferedReader(
                        new InputStreamReader
                                (context.getAssets().open("controller.html"), "UTF-8"));


                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    buffer.append(mLine);
                }
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
            finally {
                if (reader != null)
                    try {
                        reader.close();
                    }
                    catch (IOException ioe) {}
            }

            return buffer;
        }


        public static interface CommandListener {
            public void onCommand(String command);
        }
}
