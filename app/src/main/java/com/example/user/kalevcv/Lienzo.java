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
import org.opencv.core.Core;
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

    public void limpiar() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setFunction(int function) { // function :  1 = shape  2 = draw
        invalidate();

        if (function == 1) {
            paintColor = Color.parseColor("#FF0000");//#FF0000 //000000
            drawPaint.setStrokeWidth(50);
        } else {
            paintColor = Color.parseColor("#004C00");//#004C00 //C0C0C0
            drawPaint.setStrokeWidth(30);
        }
        drawPaint.setColor(paintColor);
    }

    public String[] voidImagen() {

        String figura;
        int number;
        Bitmap canvastmp_shape = canvasBitmap;
        Bitmap canvastmp_digit = canvasBitmap;
        int shape = getFigura(canvastmp_shape);
        switch (shape) {
            case 0:
                figura = "NO DETERMINADO";
                break;
            case 3:
                figura = "TRIANGULO";
                break;
            case 4:
                figura = "CUADRADO";
                break;
            case 5:
                figura = "PENTAGONO";
                break;
            default:
                figura = "CIRCULO";

        }
        Log.d("TAG", "Figura : " + figura);

        number = getNumero(canvastmp_digit);
        String[] s_n = {figura, number + ""};
        return s_n;

    }

    public int getFigura(Bitmap canvastmp) {
        long lados = 0;
        Mat tmp = new Mat(canvastmp.getWidth(), canvastmp.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(canvastmp, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2BGR);
        Core.inRange(tmp, new Scalar(0, 0, 255), new Scalar(0, 0, 255), tmp);
        Imgproc.threshold(tmp, tmp, 70, 255, Imgproc.THRESH_BINARY_INV);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(tmp, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        if (contours.size() > 1) {
            for (MatOfPoint c : contours) {
                MatOfPoint2f res = new MatOfPoint2f();
                MatOfPoint2f op2 = new MatOfPoint2f(c.toArray());
                Double con = Imgproc.arcLength(op2, true);
                Imgproc.approxPolyDP(op2, res, con * 0.02, true);
                lados = res.total();
            }
        }
        //Utils.matToBitmap(tmp, canvasBitmap);
        return (int) lados;
    }


    public int getNumero(Bitmap canvastmp) {
        int num = -1;
        Mat tmp = new Mat(canvastmp.getWidth(), canvastmp.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(canvastmp, tmp);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2BGR);
        Core.inRange(tmp, new Scalar(0, 76, 0), new Scalar(0, 76, 0), tmp);
        Imgproc.threshold(tmp, tmp, 70, 255, Imgproc.THRESH_BINARY_INV);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f = null;
        Log.d("TAG", "Contornos : " + contours.size());
        // VALIDAR QUE contours.size() > 1 , digito fuera del marco
        Rect rect = null;
        if (contours.size() > 1) {
            // SELECCIONAR EL CONTORNO MAS AMPLIO
            for (MatOfPoint c : contours) {
                contour2f = new MatOfPoint2f(c.toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                rect = Imgproc.boundingRect(points);
            }

            int lado = 0;
            if (rect.width > rect.height) {
                lado = rect.width;
            } else {
                lado = rect.height;
            }
            int lado_a = lado;
            while (lado_a % 28 != 0) {
                lado_a++;

            }
            lado = lado_a;
            Bitmap canvasBitmapRectangule = Bitmap.createBitmap(canvasBitmap, rect.x-20, rect.y-20, lado+40, lado+40);
            Mat tmp_rect = new Mat(lado, lado, CvType.CV_8UC3);
            Utils.bitmapToMat(canvasBitmapRectangule, tmp_rect);
            Imgproc.resize(tmp_rect, tmp_rect, new Size(1400, 1400));
            Utils.matToBitmap(tmp_rect, canvasBitmap);

            /*
            MatOfFloat matOfFloat = new MatOfFloat();
            HOGDescriptor hog = new HOGDescriptor
                    (new Size(28, 28), new Size(14, 14), new Size(7, 7), new Size(7, 7), 9);
            Bitmap b = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
            Mat mat_final = new Mat(b.getWidth(), b.getHeight(), CvType.CV_8UC3);
            Utils.bitmapToMat(b, mat_final);
            Imgproc.cvtColor(mat_final, mat_final, Imgproc.COLOR_RGB2GRAY);
            Imgproc.GaussianBlur(mat_final, mat_final, new Size(5, 5), 0);
            Imgproc.threshold(mat_final, mat_final, 90, 255, Imgproc.THRESH_BINARY);
            hog.compute(mat_final, matOfFloat);
            float[] ar = matOfFloat.toArray();
            NumberPrediction pr = new NumberPrediction();
            num = pr.getNumber(matOfFloat.toArray());
            */
        }
        return num;
    }

}
