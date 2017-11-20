package com.example.user.kalevcv;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

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
        drawPaint.setStrokeWidth(60);
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

    public void setColor(String newColor , int s) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        if(s > 0){
            drawPaint.setStrokeWidth(10);
        }else{
            drawPaint.setStrokeWidth(60);
        }
    }

    public long getShape() {
        Bitmap canvastmp = canvasBitmap;
        long lados = 0;
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
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 0);
        Imgproc.threshold(tmp, tmp, 90, 255, Imgproc.THRESH_BINARY);


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(tmp, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint op : contours) {
            MatOfPoint2f res = new MatOfPoint2f();
            MatOfPoint2f op2 = new MatOfPoint2f(op.toArray());
            Double con = Imgproc.arcLength(op2, true);
            Imgproc.approxPolyDP(op2, res, con * 0.01, true);
            lados = res.total();
        }
        Utils.matToBitmap(tmp, canvasBitmap);
        return lados;

    }

    public int getNumber() {
        /*
        int[] puntos = getNumberBitmap(canvasBitmap);
        Bitmap b = resizeBitmap(1400,1400,Bitmap.createBitmap(canvasBitmap,puntos[0],puntos[1]
                ,puntos[2],puntos[3]));
        Mat tmp = new Mat(b.getWidth(), b.getHeight(), CvType.CV_8S);
        Utils.bitmapToMat(b, tmp);
        Utils.matToBitmap(tmp, canvasBitmap);
        return 1;*/

        int[] puntos = getNumberBitmap(canvasBitmap);

        Bitmap canvasBitmapCopy = Bitmap.createBitmap(canvasBitmap,puntos[0],puntos[1]
                ,puntos[2],puntos[3]);
        Bitmap canvastmp = resizeBitmap(28, 28, canvasBitmapCopy);
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
        Mat tmp = new Mat(canvastmp.getWidth(), canvastmp.getHeight(), CvType.CV_8S);
        Utils.bitmapToMat(canvastmp, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 0);
        Imgproc.threshold(tmp, tmp, 90, 255, Imgproc.THRESH_BINARY);
        MatOfFloat matOfFloat = new MatOfFloat();
        Size sz = new Size(28, 28);
        HOGDescriptor hog = new HOGDescriptor
                (sz, new Size(14, 14), new Size(7, 7), new Size(7, 7), 9);
        hog.compute(tmp, matOfFloat);
        float[] ar = matOfFloat.toArray();
        NumberPrediction pr = new NumberPrediction();
        int num = pr.getNumber(matOfFloat.toArray());
        this.nuevoDibujo();
        return num;
    }

    public int[] getNumberBitmap(Bitmap canvastmp) {
        Bitmap numberBitmap;
        int dif = 0;
        int con = 0;
        int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
        for (int y = 0; y < canvastmp.getHeight(); y++) {
            for (int x = 0; x < canvastmp.getWidth(); x++) {
                if (canvastmp.getPixel(x, y) == Color.parseColor("#004C00")) {
                    con++;
                    if (con == 1) {
                        min_y = y;
                    }
                    max_y = y;
                }
            }
        }
        con = 0;
        for (int x = 0; x < canvastmp.getWidth(); x++) {
            for (int y = 0; y < canvastmp.getHeight(); y++) {
                if (canvastmp.getPixel(x, y) == Color.parseColor("#004C00")) {
                    con++;
                    if (con == 1) {
                        min_x = x;
                    }
                    max_x = x;
                }
            }
        }

        Log.d("TAG", "POS MIN :  (" + min_x + "," + min_y + ")");
        Log.d("TAG", "POS MAX :  (" + max_x + "," + max_y + ")");
        Log.d("TAG", "ANCHO :  " + (max_x - min_x));
        Log.d("TAG", "ALTO :  " + (max_y - min_y));
        int[] r ={min_x,min_y,(max_x - min_x),(max_y - min_y)};
        return  r;
    }

    public Bitmap resizeBitmap(int x, int y, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) x / width);
        float scaleHeight = ((float) y / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap bitmapNew = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return bitmapNew;
    }
/*
Log.d("TAG", "ANCHO " + canvastmp.getWidth());
Log.d("TAG", "ALTO " + canvastmp.getHeight());
Log.d("TAG", "descriptores :  " + matOfFloat.toArray().length);
*/
}
