package com.example.user.kalevcv;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    Button btn_new;
    Button btn_next;
    Button btn_number;
    ImageButton btn_shape;
    ImageButton btn_line;
    private static Lienzo lienzo;
    private static final String TAG = "Opencv";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "ERROR OPENCV");
        } else {
            Log.d(TAG, "CORRECTO OPENCV");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_new = (Button) findViewById(R.id.btn_new);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_shape = (ImageButton) findViewById(R.id.btn_shape);
        btn_number = (Button) findViewById(R.id.btn_number);
        btn_line = (ImageButton) findViewById(R.id.btn_line);
        lienzo = (Lienzo) findViewById(R.id.lienzo);


        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lienzo.nuevoDibujo();

            }
        });

        btn_shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lienzo.setColor("#FF0000");
            }
        });


        btn_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lienzo.setColor("#004C00");
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Forma";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                lienzo.getShape();

            }
        });
        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Numero";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                lienzo.getNumber();

            }
        });
    }
}
