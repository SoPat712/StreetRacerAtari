package com.jp12.streetraceratari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
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
    boolean moveLeft = false;
    boolean moveRight = false;
    int carPos = 475;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if(y<0){
            moveLeft = true;
            moveRight = false;
        }
        else {
            moveRight = true;
            moveLeft = false;
        }
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
            bg = BitmapFactory.decodeResource(getResources(), R.drawable.atari);
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            paintProperty = new Paint();


        }

        @Override
        public void run() {
            while (running == true) {
                if (holder.getSurface().isValid() == false)
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(255, 0, 0);
                canvas.drawBitmap(bg,25,30,null);
                if(carPos > 100 && moveLeft == true && moveRight ==false){
                    carPos -= 5;
                } else if (carPos > 100 && moveLeft == true && moveRight ==false){
                    carPos += 5;
                }
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