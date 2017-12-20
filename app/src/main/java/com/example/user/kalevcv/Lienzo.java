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
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
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
            drawPaint.setStrokeWidth(50);
        }
        drawPaint.setColor(paintColor);
    }

    public String[] voidImagen() {

        String figura;
        int number;
        Bitmap canvastmp_shape = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap canvastmp_digit = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
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

        // CREAR MAT DE OPENCV
        Mat tmp = new Mat(canvastmp.getWidth(), canvastmp.getHeight(), CvType.CV_8S);

        // CONVERTIR MAPA DE BIT EN MAT
        Utils.bitmapToMat(canvastmp, tmp);

        //EXTRAER EL COLOR VERDE DEL  NUMERO
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2BGR);
        Core.inRange(tmp, new Scalar(0, 76, 0), new Scalar(0, 76, 0), tmp);
        Imgproc.threshold(tmp, tmp, 70, 255, Imgproc.THRESH_BINARY_INV);
        Utils.matToBitmap(tmp, canvastmp);

        //CALCULAR EL CONTORNO DEL NUMERO
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(tmp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f;
        Log.d("TAG", "Contornos : " + contours.size());

        // CALCULAR CUANDRADO ENVOLVENTE DEL DIGITO
        if (contours.size() > 1) {

            // SELECCIONAR EL CONTORNO MAS AMPLIO
            Rect rect;
            int lado;
            ArrayList<Integer> lados = new ArrayList<>();
            ArrayList<Rect> rectangulos = new ArrayList<>();
            for (MatOfPoint c : contours) {
                contour2f = new MatOfPoint2f(c.toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                rect = Imgproc.boundingRect(points);
                lado = ((rect.width > rect.height) ? rect.width : rect.height);
                lados.add(lado);
                Log.d("TAG", "LADO : " + lado);
                rectangulos.add(rect);
            }

            // GENERAR EL BITMAP DEL RECTANGULO SELECCIONADO
            lado = changeToMultiple(lados.get(1), 28) + 224;
            rect = rectangulos.get(1);
            Bitmap canvasBitmapRectangule = Bitmap.createBitmap(canvastmp, rect.x - 112, rect.y - 112, lado, lado);
            Log.d("TAG", "LADO FINAL : " + lado);

            //GENEAR MAT
            Mat tmp_rect = new Mat(lado, lado, CvType.CV_8U);
            Utils.bitmapToMat(canvasBitmapRectangule, tmp_rect);

            //CAMBIAR LA MASCARA DE LA IMAGEN
            Imgproc.cvtColor(tmp_rect, tmp_rect, Imgproc.COLOR_RGB2BGR);

            // APLICAR FILTRO GAUSSIANO
            Imgproc.GaussianBlur(tmp_rect, tmp_rect, new Size(5, 5), 0);

            //REAJUSTAR IMAGEN
            Imgproc.resize(tmp_rect, tmp_rect, new Size(28, 28));

            imprimirNumero(tmp_rect);

            //EXTRAER HOG
            MatOfFloat matOfFloat = new MatOfFloat();
            HOGDescriptor hog = new HOGDescriptor
                    (new Size(28, 28), new Size(14, 14), new Size(7, 7),
                            new Size(7, 7), 9);
            hog.compute(tmp_rect, matOfFloat);

            //CALCULAR NUMERO
            NumberPrediction pr = new NumberPrediction();
            Log.d("TAG", "HOG : " + matOfFloat.toArray().length);
            num = pr.getNumber(matOfFloat.toArray());

            //REAJUSTAR EL TAMAÑO DEL MAT
            Imgproc.resize(tmp_rect, tmp_rect, new Size(1400, 1400));

            //MOSTRAR EL MAP COMO BITMAP
            Utils.matToBitmap(tmp_rect, canvasBitmap);

            exePca();


        }
        return num;
    }


    //CONVERTIR UN NUMERO EN SU MULTIPLO DETERMINA MAS CERCANO
    private int changeToMultiple(int number, int multiple) {
        while (number % multiple != 0) {
            number++;
        }
        return number;
    }

    public void imprimirNumero(Mat tmp_rect){
        /*Bitmap x = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp_rect, x);

        //IMPRIMIR IMAGEN
        String cadena = "";
        for (int i = 0; i < x.getWidth(); i++) {
            cadena = cadena + "\n";
            for (int j = 0; j < x.getHeight(); j++) {
                if (x.getPixel(j, i) != -1) {
                    cadena = cadena + "1" + " ";
                } else {
                    cadena = cadena + "0" + " ";
                }

            }
        }
        Log.d("TAG", "CADENA : " + cadena);*/
        Mat res = new Mat();
        Core.divide(tmp_rect, new Scalar(255.0 ,255.0 , 255.0) , res);

        String cadena = "";
        for (int row = 0; row < res.rows(); row++) {
            cadena = cadena + "\n";
            for (int col = 0; col < res.cols(); col++) {
                double[] d = res.get(row, col);
                Log.d("PCA", "vectors " + Arrays.toString(d));
                /*if (d[0] != 255) {
                    cadena = cadena + "1" + " ";
                } else {
                    cadena = cadena + "0" + " ";
                }*/
            }
        }
        Log.d("MAT", "MAT " + cadena);

    }

    public void exePca() {
        // test data
        Mat data = new Mat(3, 6, CvType.CV_32F) {
            {
                put(0, 0, new double[]{0.5, 0.8, 0.9, 0.01, 0.255, 0.004});
                put(1, 0, new double[]{0.04, 0.08, 0.52, 0.54, 0.84, 0.048});
                put(2, 0, new double[]{0.07, 0.014, 0.583, 0.5856, 0.556, 0.112});
            }
        };
        // calculate mean and vectors
        Mat mean = new Mat();
        Mat eigenvectors = new Mat();
        Core.PCACompute(data, mean, eigenvectors ,3);
        // project data into pc space
        Mat result = new Mat();
        Core.PCAProject(data, mean, eigenvectors, result);
        for (int row = 0; row < mean.rows(); row++) {
            for (int col = 0; col < mean.cols(); col++) {
                double[] d = mean.get(row, col);
                Log.d("PCA", "mean " + d[0]);
            }
        }
        // vectors should be ?, ?, 0.2, 0.4, 0.4, 0.8
        for (int row = 0; row < eigenvectors.rows(); row++) {
            for (int col = 0; col < eigenvectors.cols(); col++) {
                double[] d = eigenvectors.get(row, col);
                Log.d("PCA", "eigenvectors " + Arrays.toString(d));
            }
        }
        for (int row = 0; row < result.rows(); row++) {
            for (int col = 0; col < result.cols(); col++) {
                double[] d = result.get(row, col);
                Log.d("PCA", "result " + Arrays.toString(d));
            }
        }

    }


}
