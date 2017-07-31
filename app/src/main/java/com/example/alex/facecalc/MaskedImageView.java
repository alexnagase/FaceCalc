package com.example.alex.facecalc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

import static android.R.attr.bitmap;

public class MaskedImageView extends android.support.v7.widget.AppCompatImageView {

    private enum MaskType {
        NOMASK, FIRST, SECOND
    }
    private static final float ID_TEXT_SIZE = 50.0f;
    private SparseArray<Face> mFaces = null;
    private MaskType maskType = MaskType.NOMASK;
    Paint mPaint = new Paint();
    Paint mPaint2 = new Paint();
    private Bitmap mBitmap;
    private volatile Face mFace;

    public MaskedImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        if(mBitmap == null){
            return;
        }
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        drawBitmap(canvas, scale);

        switch (maskType){
            case FIRST:
                drawFirstMaskOnCanvas(canvas, scale);
                break;
            case SECOND:
                drawSecondMaskOnCanvas(canvas, scale);
                break;
        }
    }

    protected void drawFirstMask(SparseArray<Face> faces){
        this.mFaces = faces;
        this.maskType = MaskType.FIRST;
        this.invalidate();
    }

    protected void drawSecondMask(SparseArray<Face> faces){
        this.mFaces = faces;
        this.maskType = MaskType.SECOND;
        this.invalidate();
    }

    private void drawBitmap(Canvas canvas, double scale) {
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
    }

    private void drawFirstMaskOnCanvas(Canvas canvas, double scale) {

        mPaint.setColor(Color.GREEN);
        mPaint2.setColor(Color.RED);
        mPaint2.setTextSize(ID_TEXT_SIZE);

        for (int i = 0; i < mFaces.size(); i++){
            Face face = mFaces.valueAt(i);
            List<Landmark> landmarks = face.getLandmarks();

            for (Landmark landmark : face.getLandmarks()) {
                switch (landmark.getType()) {
                    case Landmark.LEFT_MOUTH:
                        int cx = (int) (landmark.getPosition().x * scale);
                        int cy = (int) (landmark.getPosition().y * scale);

                        canvas.drawCircle(cx, cy, 10, mPaint);
                }
                switch (landmark.getType()) {
                    case Landmark.RIGHT_MOUTH:
                        int cx = (int) (landmark.getPosition().x * scale);
                        int cy = (int) (landmark.getPosition().y * scale);

                        canvas.drawCircle(cx, cy, 10, mPaint);

                }

                canvas.drawText("123456", face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight(), mPaint2);
            }
        }
    }

    private void drawSecondMaskOnCanvas( Canvas canvas, double scale ) {

        mPaint.setColor(Color.GREEN);
        mPaint2.setColor(Color.RED);
        mPaint2.setTextSize(ID_TEXT_SIZE);
        mPaint2.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < mFaces.size(); i++){
            Face face = mFaces.valueAt(i);
            List<Landmark> landmarks = face.getLandmarks();
            float lex = 0;
            float ley = 0;
            float rex = 0;
            float rey = 0;
            for (Landmark landmark : face.getLandmarks()) {

                switch (landmark.getType()) {
                    case Landmark.LEFT_EYE:
                        int cx = (int) (landmark.getPosition().x * scale);
                        int cy = (int) (landmark.getPosition().y * scale);
                        lex = landmark.getPosition().x;
                        ley = landmark.getPosition().y;
                        canvas.drawCircle(cx, cy, 10, mPaint);
                }
                switch (landmark.getType()) {
                    case Landmark.RIGHT_EYE:
                        int cx = (int) (landmark.getPosition().x * scale);
                        int cy = (int) (landmark.getPosition().y * scale);
                        rex = landmark.getPosition().x;
                        rey = landmark.getPosition().y;
                        canvas.drawCircle(cx, cy, 10, mPaint);
                }
                float width = face.getWidth();

            }
            double eyeDistance = (Math.sqrt(Math.pow(rex-lex, 2)+ Math.pow(rey-ley,2))*scale);
            double ratio = Math.round(eyeDistance/face.getWidth() * 10000D) / 10000D;

            canvas.drawText(Double.toString(ratio),((float)scale)*((face.getPosition().x + face.getWidth())/2),((float)scale)*(face.getPosition().y), mPaint2);
        }
    }

    public void noFaces() {
        mFaces = null;
    }

    public void reset() {
        mFaces = null;
        setImageBitmap(null);
    }
}
