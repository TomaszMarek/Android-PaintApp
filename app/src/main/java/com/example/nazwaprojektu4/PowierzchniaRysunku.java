package com.example.nazwaprojektu4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class PowierzchniaRysunku extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mPojemnik;
    private Thread mWatekRysujacy;
    private boolean mWatekPracuje = false;
    private final Object mBlokada = new Object();
    private Paint mFarba;
    private Path mSciezka;
    private Bitmap mBitmapa = null;
    private Canvas mKanwa = null;
    private int mKolorRysowania = Color.RED;

    private List<ColoredPath> sciezki = new ArrayList<>();

    public PowierzchniaRysunku(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPojemnik = getHolder();
        mPojemnik.addCallback(this);
        mFarba = new Paint();
        mFarba.setStrokeWidth(5);
        mFarba.setStyle(Paint.Style.STROKE);
        mFarba.setColor(mKolorRysowania);
        mSciezka = new Path();
    }

    public void setKolorRysowania(int kolor) {
        synchronized (mBlokada) {
            mKolorRysowania = kolor;
            mFarba.setColor(kolor);
            sciezki.add(new ColoredPath(new Path(mSciezka), mKolorRysowania));
            mSciezka.reset();
        }
    }

    public void wyczyscRysunek() {
        synchronized (mBlokada) {
            mBitmapa = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mKanwa = new Canvas(mBitmapa);
            mKanwa.drawARGB(255, 255, 255, 255);
            sciezki.clear();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        synchronized (mBlokada) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSciezka.moveTo(event.getX(), event.getY());
                    mKanwa.drawCircle(event.getX(),event.getY(), 5, mFarba);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mSciezka.lineTo(event.getX(), event.getY());
                    mKanwa.drawPath(mSciezka, mFarba);
                    break;
                case MotionEvent.ACTION_UP:
                    mKanwa.drawPath(mSciezka, mFarba);
                    mKanwa.drawCircle(event.getX(),event.getY(), 5, mFarba);
                    mSciezka.reset();
                    break;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (mWatekPracuje) {
            Canvas kanwa = null;
            try {
                synchronized (mPojemnik) {
                    if (!mPojemnik.getSurface().isValid())
                        continue;
                    kanwa = mPojemnik.lockCanvas(null); //pobiera obiekt na ktorym mozna rysowac.
                    synchronized (mBlokada) {
                        if (mBitmapa != null) {
                            kanwa.drawBitmap(mBitmapa, 0, 0, null);
                            for (ColoredPath cp : sciezki) {
                                mFarba.setColor(cp.color);
                                kanwa.drawPath(cp.path, mFarba);
                            }
                            mFarba.setColor(mKolorRysowania);
                            kanwa.drawPath(mSciezka, mFarba);
                        }
                    }
                }
            } finally {
                if (kanwa != null) {
                    mPojemnik.unlockCanvasAndPost(kanwa);
                }
            }
            try {
                Thread.sleep(1000 / 25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    public ArrayList<Parcelable> getPaths() {
//        //zawiera liste narysowaanych sciezek
//        ArrayList<Parcelable> paths = new ArrayList<>();
//        for (ColoredPath path : sciezki) {
//            paths.add(path);
//        }
//        return paths;
//    }

//    public void restorePaths(ArrayList<Parcelable> paths) {
//
//        sciezki.clear();
//        for (Parcelable p : paths) {
//            if (p instanceof ColoredPath) {
//                sciezki.add((ColoredPath) p);
//            }
//        }
//        invalidate();
//    }

    private static class ColoredPath implements Parcelable {
        //Parcelable, co umożliwia jej łatwe przekazywanie między komponentami Androida.
        Path path;
        int color;

        ColoredPath(Path path, int color) {
            this.path = path;
            this.color = color;
        }

        protected ColoredPath(Parcel in) {
            path = new Path();
            float[] points = in.createFloatArray();
            for (int i = 0; i < points.length; i += 2) {
                if (i == 0) {
                    path.moveTo(points[i], points[i + 1]);
                } else {
                    path.lineTo(points[i], points[i + 1]);
                }
            }
            color = in.readInt();
        }

        public static final Creator<ColoredPath> CREATOR = new Creator<ColoredPath>() {
            @Override
            public ColoredPath createFromParcel(Parcel in) {
                return new ColoredPath(in);
            }

            @Override
            public ColoredPath[] newArray(int size) {
                return new ColoredPath[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            List<Float> pointsList = new ArrayList<>();
            PathMeasure pathMeasure = new PathMeasure(path, false);
            float[] coords = new float[2];
            do {
                pathMeasure.getPosTan(pathMeasure.getLength(), coords, null);
                pointsList.add(coords[0]);
                pointsList.add(coords[1]);
            } while (pathMeasure.nextContour());
            float[] points = new float[pointsList.size()];
            for (int i = 0; i < points.length; i++) {
                points[i] = pointsList.get(i);
            }
            dest.writeFloatArray(points);
            dest.writeInt(color);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (mBlokada) {
            mBitmapa = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mKanwa = new Canvas(mBitmapa);
            mKanwa.drawARGB(255, 255, 255, 255);
        }
        wznowRysowanie();
    }

    public void wznowRysowanie() {
        mWatekRysujacy = new Thread(this);
        mWatekPracuje = true;
        mWatekRysujacy.start();
    }

    public void pauzujRysowanie() {
        mWatekPracuje = false;
        try {
            mWatekRysujacy.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pauzujRysowanie();
    }

    public Bitmap getmBitmapa(){
        return mBitmapa;
    }
}
