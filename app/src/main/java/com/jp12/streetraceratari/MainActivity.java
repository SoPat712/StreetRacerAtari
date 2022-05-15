package com.jp12.streetraceratari;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Code from this program has been used from Beginning Android Games
    //Review SurfaceView, Canvas, continue

    GameSurface gameSurface;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    int carPos = 220;
    int enemyCarPos = 0;
    int changeY = 2;
    int enemyCarX = 500;
    int collisionTime = 100;
    boolean collision = false;
    int seconds = 60;
    int points = 0;
    MediaPlayer soundPlayer;
    boolean gameEnd = false;
    SoundPool pool;
    int soundEffect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        soundPlayer = new MediaPlayer();
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        soundPlayer = MediaPlayer.create(this, R.raw.songofstorms);
        soundPlayer.start();
        pool = new SoundPool.Builder().build();

        soundEffect = pool.load(this, R.raw.carcrash, 1);
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
        if(enemyCarPos >= 1800){
            if(collision){
                collision = false;
            } else{
                points++;
            }


            enemyCarPos = -100;
            int rando = (int)(Math.random()*8+1);
            if(rando == 1) enemyCarX = 200;
            else if (rando == 2) enemyCarX = 300;
            else if (rando == 3) enemyCarX = 400;
            else if (rando == 4) enemyCarX = 500;
            else if (rando == 5) enemyCarX = 600;
            else if (rando == 6) enemyCarX = 700;
            else if (rando == 7) enemyCarX = 800;
            else if (rando == 8) enemyCarX = 900;
        }
        enemyCarPos += changeY;
        if(carPos > 900) carPos = 900;
        if(carPos < 195) carPos = 195;
        if(x >= 7 && carPos - 20 >= 195) {carPos -= 20; return;}
        if(x >= 5 && carPos - 10 >= 195) {carPos -= 10; return;}
        if(x >= 3 && carPos - 8 >= 195) {carPos -= 8; return;}
        if(x >= 2 && carPos - 5 >= 195) {carPos -= 5; return;}
        if(x >= 0.2 && carPos - 3 >= 195) {carPos -= 3;} else if (x >= 0.2 && carPos - 3 <= 195){ carPos -= 1; return;}
        if(x <= -7 && carPos + 20 <= 900) {carPos += 20; return;}
        if(x <= -5 && carPos + 10 <= 900) {carPos += 10; return;}
        if(x <= -3 && carPos + 8 <= 900) {carPos += 8; return;}
        if(x <= -2 && carPos + 5 <= 900) {carPos += 5; return;}
        if(x <= -0.2 && carPos + 3 <= 900) {carPos += 3;} else if (x <= -0.2 && carPos + 3 >= 900){ carPos += 1; return;}
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
        Bitmap enemyCar;
        Bitmap bg;
        Paint paintProperty;

        int screenWidth;
        int screenHeight;

        public GameSurface(Context context) {
            super(context);
            holder = getHolder();
            car = BitmapFactory.decodeResource(getResources(), R.drawable.car);
            enemyCar = BitmapFactory.decodeResource(getResources(), R.drawable.enemycar);
            bg = BitmapFactory.decodeResource(getResources(), R.drawable.atari);
            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            paintProperty = new Paint();


        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(changeY == 2) {changeY = 4;}
            else changeY = 2;
            return super.onTouchEvent(event);
        }

        @Override
        public void run() {
            while (running) {
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                if(!gameEnd){
                    canvas.drawRGB(255, 0, 0);
                    canvas.drawBitmap(bg,0,0,null);
                    System.out.println("collTiem: "+ collisionTime);
                    System.out.println("secs: "+seconds);
                    if(collisionTime < seconds + 4){
                        car = BitmapFactory.decodeResource(getResources(), R.drawable.crashedcar);
                    } else{
                        car = BitmapFactory.decodeResource(getResources(), R.drawable.car);
                    }
                    canvas.drawBitmap(car, carPos, 1550, null);
                    canvas.drawBitmap(enemyCar, enemyCarX, enemyCarPos, null);
                    Rect myCarRect = new Rect(carPos+8, 1550+8, carPos + 130 - 3, 1550 + 93 - 3);
                    Rect enemyCarRect = new Rect(enemyCarX+8, enemyCarPos+8, enemyCarX + 130 - 3, enemyCarPos + 93 - 3);
                    paintProperty.setTextSize(100f);
                    Typeface plain = ResourcesCompat.getFont(MainActivity.this, R.font.rb);
                    paintProperty.setTypeface(plain);
                    canvas.drawText(points + " points", 325f, 100f, paintProperty);
                    canvas.drawText( seconds + " seconds left", 200f, 300f, paintProperty);
                    if(Rect.intersects(myCarRect,enemyCarRect)){
                        pool.play(soundEffect,0.2f,0.2f,1,0,1f);
                        collision = true;
                        collisionTime = seconds;
                    }
                } else{
                    canvas.drawRGB(255,255,255);
                    paintProperty.setTextSize(100f);
                    canvas.drawText("Game over",300f,500f, paintProperty);
                    canvas.drawText("You got " + points + " points", 200f, 900f, paintProperty);
                }

                holder.unlockCanvasAndPost(canvas);

            }
        }

        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (seconds != 0){
                            seconds --;
                        } else{
                            soundPlayer.stop();
                            gameEnd = true;
                            mSensorManager.unregisterListener(MainActivity.this,mAccelerometer);
                        }
                    });
                }
            }, 0, 1000);
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