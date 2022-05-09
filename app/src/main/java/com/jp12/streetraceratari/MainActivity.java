package com.jp12.streetraceratari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Code from this program has been used from Beginning Android Games
    //Review SurfaceView, Canvas, continue

    GameSurface gameSurface;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    int carPos = 220;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        setContentView(gameSurface);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = (int) event.values[0];
        if(carPos > 900) carPos = 900;
        if(carPos < 195) carPos = 195;
        if(x >= 7) {carPos -= 20; return;}
        if(x >= 5) {carPos -= 10; return;}
        if(x >= 3) {carPos -= 8; return;}
        if(x >= 2) {carPos -= 5; return;}
        if(x >= 1) {carPos -= 3; return;}
        if(x <= -7) {carPos += 20; return;}
        if(x <= -5) {carPos += 10; return;}
        if(x <= -3) {carPos += 8; return;}
        if(x <= -2) {carPos += 5; return;}
        if(x <= -1) carPos += 3;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable {
        //https://developer.android.com/reference/android/view/SurfaceView

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap car;
        Bitmap bg;
        Paint paintProperty;

        int screenWidth;
        int screenHeight;

        public GameSurface(Context context) {
            super(context);
            holder = getHolder();

            car = BitmapFactory.decodeResource(getResources(), R.drawable.car);
            bg = BitmapFactory.decodeResource(getResources(), R.drawable.atari);
            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            paintProperty = new Paint();


        }

        @Override
        public void run() {
            while (running) {
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(255, 0, 0);
                Rect myCar = new Rect();
                Rect enemyCar = new Rect();
                canvas.drawBitmap(bg,0,0,null);
                canvas.drawBitmap(car, carPos, 1550, null);
                holder.unlockCanvasAndPost(canvas);

            }
        }

        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }


    }
}