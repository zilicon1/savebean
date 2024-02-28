package com.example.savebean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;


import java.util.Random;


public class GameView extends View {

    int dWidth, dHeight;
    Bitmap trash, hand, bean;
    Handler handler;
    Runnable runnable;
    long UPDATE_MILLIS = 30;
    int handX, handY;
    int beanX, beanY;
    Random random;
    boolean beanAnimation = false;
    int points = 0;
    float TEXT_SIZE = 120;
    Paint textPaint;
    Paint healthPaint;
    int life = 3;
    Context context;
    int handSpeed;
    int trashX, trashY;
    MediaPlayer mpPoints, mpWhoosh, mpPop;


    public GameView(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        trash = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        bean = BitmapFactory.decodeResource(getResources(), R.drawable.bean);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        random = new Random();
        handX = dWidth + random.nextInt(300);
        handY = random.nextInt(600);
        beanX = handX;
        beanY = handY + hand.getHeight() - 30;
        textPaint = new Paint();
        textPaint.setColor(Color.rgb(255,0,0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint = new Paint();
        healthPaint.setColor(Color.GREEN);
        handSpeed = 21 + random.nextInt(30);
        trashX = dWidth/2 - trash.getWidth()/2;
        trashY = dHeight - trash.getHeight();
        mpPoints = MediaPlayer.create(context, R.raw.point);
        mpWhoosh = MediaPlayer.create(context, R.raw.whoosh);
        mpPop = MediaPlayer.create(context, R.raw.pop);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (beanAnimation == false){
            handX -= handSpeed;
            beanX -= handSpeed;
        }
        if (handX <= -hand.getWidth()){
            if (mpWhoosh != null){
                mpWhoosh.start();
            }
            handX = dWidth + random.nextInt(300);
            beanX = handX;
            handY = random.nextInt(600);
            beanY = handY + hand.getHeight() - 30;
            handSpeed = 21 + random.nextInt(30);
            trashX = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            life--;
            if(life == 0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points", points);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        }
        if (beanAnimation){
            beanY += 40;
        }
        if (beanAnimation
        && (beanX + bean.getWidth() >= trashX)
        && (beanX <= trashX + trash.getWidth())
        && (beanY + bean.getHeight() >= (dHeight-trash.getHeight()))
        && beanY <= dHeight){
            if(mpPoints != null){
                mpPoints.start();
            }
            handX = dWidth + random.nextInt(300);
            beanX = handX;
            handY = random.nextInt(600);
            beanY = handY + hand.getHeight() - 30;
            handSpeed = 21 + random.nextInt(30);
            points++;
            trashX = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            beanAnimation = false;
        }
        if (beanAnimation && (beanY + bean.getHeight()) >= dHeight){
            if (mpPop != null){
                mpPop.start();
            }
            life--;
            if (life == 0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points", points);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            handX = dWidth + random.nextInt(300);
            beanX = handX;
            handY = random.nextInt(600);
            beanY = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            beanAnimation = false;
        }
        canvas.drawBitmap(trash, trashX, trashY, null);
        canvas.drawBitmap(hand, handX, handY, null);
        canvas.drawBitmap(bean, beanX, beanY, null);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        if (life == 2)
            healthPaint.setColor(Color.YELLOW);
        else if (life == 1)
            healthPaint.setColor(Color.RED);
        canvas.drawRect(dWidth-200, 30, dHeight-200+60*life, 80, healthPaint);
        if (life != 0)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (beanAnimation == false &&
                    (touchX >= handX && touchX <= (handX + hand.getWidth())
                    && touchY >= handY && touchY <= (handY + hand.getHeight()))){
                beanAnimation = true;
            }
        }
        return true;
    }
}
