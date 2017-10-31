package com.example.user.kalevcv;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class Lienzo extends View {
    private Path drawPath;
    private static Paint drawPaint;
    private Paint canvasPaint;
    private static int paintColor = 0xFFFF0000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;


    public Lienzo(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public void setupDrawing() {

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void nuevoDibujo() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();

    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void getShape() {
        Bitmap canvastmp = canvasBitmap;
        int[] allpixels = new int[canvastmp.getHeight() * canvastmp.getWidth()];
        canvastmp.getPixels(allpixels, 0,
                canvastmp.getWidth(), 0, 0,
                canvastmp.getWidth(), canvastmp.getHeight());
        for (int i = 0; i < allpixels.length; i++) {
            if (allpixels[i] != Color.parseColor("#ff0000")) {
                allpixels[i] = Color.WHITE;
            }
        }
        canvastmp.setPixels(allpixels, 0, canvastmp.getWidth(),
                0, 0, canvastmp.getWidth(), canvastmp.getHeight());
        canvasBitmap = canvastmp;

        Mat tmp = new Mat(canvasBitmap.getWidth(), canvasBitmap.getHeight(), CvType.CV_8S);
        Utils.bitmapToMat(canvasBitmap, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp,new Size(5,5), 0);
        Imgproc.threshold(tmp,tmp,90,255,Imgproc.THRESH_BINARY);
        Utils.matToBitmap(tmp, canvasBitmap);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(tmp, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        //Double con = Imgproc.arcLength(MatOfPoint2f. contours,true);



    }

    public void getNumber() {
        Bitmap canvastmp = canvasBitmap;
        int[] allpixels = new int[canvastmp.getHeight() * canvastmp.getWidth()];
        canvastmp.getPixels(allpixels, 0,
                canvastmp.getWidth(), 0, 0,
                canvastmp.getWidth(), canvastmp.getHeight());
        for (int i = 0; i < allpixels.length; i++) {
            if (allpixels[i] != Color.parseColor("#004C00")) {
                allpixels[i] = Color.WHITE;
            }
        }
        canvastmp.setPixels(allpixels, 0, canvastmp.getWidth(),
                0, 0, canvastmp.getWidth(), canvastmp.getHeight());
        canvasBitmap = canvastmp;
        Mat tmp = new Mat(canvasBitmap.getWidth(), canvasBitmap.getHeight(), CvType.CV_8S);
        Utils.bitmapToMat(canvasBitmap, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp,new Size(5,5), 0);
        Imgproc.threshold(tmp,tmp,90,255,Imgproc.THRESH_BINARY);
        Utils.matToBitmap(tmp, canvasBitmap);
    }
}
        /*Mat tmp = new Mat(canvasBitmap.getWidth(), canvasBitmap.getHeight(), CvType.CV_8S);
        Mat mask= new Mat();
        Mat dst= new Mat();
        Utils.bitmapToMat(canvasBitmap, tmp);
        Core.inRange(tmp, new Scalar(0, 0, 70), new Scalar(97, 100, 255), mask);
        Core.bitwise_and(tmp,tmp, mask, dst);
        Utils.matToBitmap(mask, canvasBitmap);
        */