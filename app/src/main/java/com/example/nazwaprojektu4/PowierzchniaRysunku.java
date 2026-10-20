package com.example.nazwaprojektu4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PowierzchniaRysunku extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final SurfaceHolder mPojemnik;
    private Thread mWatekRysujacy;
    private volatile boolean mWatekPracuje = false;
    private final Object mBlokada = new Object();

    private final Paint mFarba;
    private final Path mSciezka;
    private Bitmap mBitmapa = null;
    private Canvas mKanwa = null;
    private int mKolorRysowania = Color.RED;

    public PowierzchniaRysunku(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPojemnik = getHolder();
        mPojemnik.addCallback(this);

        mFarba = new Paint();
        mFarba.setAntiAlias(true);
        mFarba.setDither(true);
        mFarba.setStyle(Paint.Style.STROKE);
        mFarba.setStrokeJoin(Paint.Join.ROUND);
        mFarba.setStrokeCap(Paint.Cap.ROUND);
        mFarba.setStrokeWidth(8f);
        mFarba.setColor(mKolorRysowania);

        mSciezka = new Path();
    }

    public void setKolorRysowania(int kolor) {
        synchronized (mBlokada) {
            mKolorRysowania = kolor;
            mFarba.setColor(kolor);
        }
    }

    public void wyczyscRysunek() {
        synchronized (mBlokada) {
            if (mBitmapa != null) {
                mKanwa.drawColor(Color.WHITE);
                mSciezka.reset();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        float x = event.getX();
        float y = event.getY();

        synchronized (mBlokada) {
            if (mKanwa == null) return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSciezka.reset();
                    mSciezka.moveTo(x, y);
                    mKanwa.drawCircle(x, y, 4f, mFarba);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mSciezka.lineTo(x, y);
                    mKanwa.drawPath(mSciezka, mFarba);
                    break;
                case MotionEvent.ACTION_UP:
                    mKanwa.drawPath(mSciezka, mFarba);
                    mSciezka.reset();
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void run() {
        while (mWatekPracuje) {
            if (!mPojemnik.getSurface().isValid()) {
                continue;
            }

            Canvas kanwa = null;
            try {
                kanwa = mPojemnik.lockCanvas();
                if (kanwa != null) {
                    synchronized (mBlokada) {
                        if (mBitmapa != null) {
                            kanwa.drawBitmap(mBitmapa, 0f, 0f, null);
                            if (!mSciezka.isEmpty()) {
                                kanwa.drawPath(mSciezka, mFarba);
                            }
                        }
                    }
                }
            } finally {
                if (kanwa != null) {
                    mPojemnik.unlockCanvasAndPost(kanwa);
                }
            }

            try {
                Thread.sleep(1000 / 30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (mBlokada) {
            if (mBitmapa == null && getWidth() > 0 && getHeight() > 0) {
                mBitmapa = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                mKanwa = new Canvas(mBitmapa);
                mKanwa.drawColor(Color.WHITE);
            }
        }
        wznowRysowanie();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mBlokada) {
            if (mBitmapa == null && width > 0 && height > 0) {
                mBitmapa = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mKanwa = new Canvas(mBitmapa);
                mKanwa.drawColor(Color.WHITE);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pauzujRysowanie();
    }

    public synchronized void wznowRysowanie() {
        if (mWatekRysujacy == null || !mWatekRysujacy.isAlive()) {
            mWatekPracuje = true;
            mWatekRysujacy = new Thread(this);
            mWatekRysujacy.start();
        }
    }

    public synchronized void pauzujRysowanie() {
        mWatekPracuje = false;
        if (mWatekRysujacy != null) {
            try {
                mWatekRysujacy.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            mWatekRysujacy = null;
        }
    }

    public Bitmap getmBitmapa() {
        synchronized (mBlokada) {
            return mBitmapa;
        }
    }
}
