package com.example.zhengjx.myapplication;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button button;

    //方法3
    final String LOG_TAG="METHOD3";
    RelativeLayout activity_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        activity_main=(RelativeLayout) findViewById(R.id.activity_main);
        activity_main.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "View touched",
                        Toast.LENGTH_LONG
                );
                toast.show();

                return true;
            }
        });
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                test3();
                //模拟后台运行

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            Log.i("ZJX","TEST");
                            Log.i("ZJX",sendEvent());
//                            KeyEvent e1=new KeyEvent( KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A );
//                            KeyEvent e2=new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A );
//
//                            Instrumentation m_Instrumentation = new Instrumentation();
//                            long downTime = SystemClock.uptimeMillis();
////                            long eventTime = SystemClock.uptimeMillis() + 100;
////                            long eventTime2 = SystemClock.uptimeMillis() + 200;
//                            float x=200.0f;
//                            float y=280.0f;
//                            m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                    downTime,MotionEvent.ACTION_DOWN,x, y, 0));
//                            for(int i=1;i<=50;i++){
//                                m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                        SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE,x+i, y, 0));
//                            }
//                            for(int i=1;i<=50;i++){
//                                m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                        SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE,x+50, y+i, 0));
//                            }
//                            for(int i=50;i>=1;i--){
//                                m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                        SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE,x+i, y+50, 0));
//                            }
//                            for(int i=50;i>=1;i--){
//                                m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                        SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE,x, y+i, 0));
//                            }
//                            m_Instrumentation.sendPointerSync(MotionEvent.obtain(downTime,
//                                    eventTime2,MotionEvent.ACTION_UP,550, 550, 0));
//                            m_Instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
//                                    SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,550.f, 550.0f, 0));

                        } catch (Exception e) {
                            Log.e("ZJX",e.toString());
                        }
                    }
                }).start();
//                Intent intent=new Intent(MainActivity.this,Main2Activity.class);

//                startActivity(intent);
            }
        });
    }

    private void test3(){
//        twoPointTouch(240.0f, 400.0f, 240.0f, 20.0f, 241.0f, 401.0f, 241.0f, 600.0f, 40);
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );

        activity_main.dispatchTouchEvent(motionEvent);
    }
    private void twoPointTouch(float xOneStart, float yOneStart, float xOneEnd, float yOneEnd,
                               float xTwoStart, float yTwoStart, float xTwoEnd, float yTwoEnd, int stepCount) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent.PointerCoords pointerCoordsOneStart = new MotionEvent.PointerCoords();
        pointerCoordsOneStart.x = xOneStart;
        pointerCoordsOneStart.y = yOneStart;

        MotionEvent.PointerCoords pointerCoordsTwoStart = new MotionEvent.PointerCoords();
        pointerCoordsTwoStart.x = xTwoStart;
        pointerCoordsTwoStart.y = yTwoStart;

        MotionEvent.PointerCoords pointerCoordsOneEnd = new MotionEvent.PointerCoords();
        pointerCoordsOneEnd.x = xOneEnd;
        pointerCoordsOneEnd.y = yOneEnd;

        MotionEvent.PointerCoords pointerCoordsTwoEnd = new MotionEvent.PointerCoords();
        pointerCoordsTwoEnd.x = xTwoEnd;
        pointerCoordsTwoEnd.y = yTwoEnd;

        float xOneStep = (xOneEnd - xOneStart) / stepCount;
        float yOneStep = (yOneEnd - yOneStart) / stepCount;

        float xTwoStep = (xTwoEnd - xTwoStart) / stepCount;
        float yTwoStep = (yTwoEnd - xTwoStart) / stepCount;

        MotionEvent.PointerCoords pointerCoordsOneStep = new MotionEvent.PointerCoords();
        pointerCoordsOneStep.x = xOneStart;
        pointerCoordsOneStep.y = yOneStart;

        MotionEvent.PointerCoords pointerCoordsTwoStep = new MotionEvent.PointerCoords();
        pointerCoordsTwoStep.x = xTwoStart;
        pointerCoordsTwoStep.y = yTwoStart;

        MotionEvent event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                2, new int[] {0, 1}, new MotionEvent.PointerCoords[] {pointerCoordsOneStart, pointerCoordsTwoStart}, 0, 0.0f,
                0.0f, 0, 0, 0, 0);
        Log.e(LOG_TAG, "this event has " + event.getPointerCount() + "action is " + event.getAction());
        sendPointerTest(event);
        for (int i = 0; i < stepCount; ++i) {
            pointerCoordsOneStep.x += xOneStep;
            pointerCoordsOneStep.y += yOneStep;

            pointerCoordsTwoStep.x += xTwoStep;
            pointerCoordsTwoStep.y += yTwoStep;

            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
                    2, new int[] {0, 1}, new MotionEvent.PointerCoords[] {pointerCoordsOneStep, pointerCoordsTwoStep}, 0, 0.0f,
                    0.0f, 0, 0, 0, 0);
            Log.i(LOG_TAG, "pointerCoordsTwoStep x is " + pointerCoordsTwoStep.x);
            Log.i(LOG_TAG, "pointerCoordsTwoStep y is " + pointerCoordsTwoStep.y);
            sendPointerTest(event);
        }

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                2, new int[] {0, 1}, new MotionEvent.PointerCoords[] {pointerCoordsOneEnd, pointerCoordsTwoEnd}, 0, 0.0f,
                0.0f, 0, 0, 0, 0);

        sendPointerTest(event);

    }
    public void sendPointerTest(MotionEvent event) {
        try {
            activity_main.dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    native public String  sendEvent();
    static {
        System.loadLibrary("nativeSend");
    }




}
