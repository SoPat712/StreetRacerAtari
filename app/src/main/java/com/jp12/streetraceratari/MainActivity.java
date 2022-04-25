package com.jp12.streetraceratari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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

public class MainActivity extends AppCompatActivity {
    //Code from this program has been used from Beginning Android Games
    //Review SurfaceView, Canvas, continue

    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
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
                // https://developer.android.com/reference/android/graphics/Canvas
                Canvas canvas = holder.lockCanvas();
                Timer t = new Timer();

                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        AtomicInteger quarterSeconds = new AtomicInteger(240);
                        if (quarterSeconds.get() != 0) {
                            quarterSeconds.decrementAndGet();
                        }
                        runOnUiThread(() -> {
                            if (quarterSeconds.get() % 4 == 0) {
                                canvas.drawRGB(255, 0, 0);
                                canvas.drawBitmap(bg,25,30,null);
                                canvas.drawBitmap(car, 475, 1550, null);
                                canvas.drawBitmap(cary, x, x, null);

                                System.out.println(String.valueOf(quarterSeconds.get() / 4));
                            }
                        });
                    }

                }, 0, 250);


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