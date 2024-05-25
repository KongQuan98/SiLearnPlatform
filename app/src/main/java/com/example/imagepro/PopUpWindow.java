package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class PopUpWindow extends AppCompatActivity {

    GifImageView gifPartTwo;
    TextView textPartTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        String checkSign = "";

        gifPartTwo = findViewById(R.id.gifSecondPart);
        textPartTwo = findViewById(R.id.textSecondPart);

        Intent intent = getIntent();
        if (intent.getStringExtra("keys") != null && !intent.getStringExtra("keys").equals("")) {
            checkSign = intent.getStringExtra("keys");
            textPartTwo.setText("Tips 2: Please refer to the gif above to perform the sign of " + checkSign);
            if (checkSign.equals("A")){
                gifPartTwo.setImageResource(R.drawable.a);
            }else if (checkSign.equals("B")){
                gifPartTwo.setImageResource(R.drawable.b);
            }else if (checkSign.equals("C")){
                gifPartTwo.setImageResource(R.drawable.c);
            }else if (checkSign.equals("D")){
                gifPartTwo.setImageResource(R.drawable.d);
            }else if (checkSign.equals("E")){
                gifPartTwo.setImageResource(R.drawable.e);
            }else if (checkSign.equals("F")){
                gifPartTwo.setImageResource(R.drawable.f);
            }else if (checkSign.equals("G")){
                gifPartTwo.setImageResource(R.drawable.g);
            }else if (checkSign.equals("H")){
                gifPartTwo.setImageResource(R.drawable.h);
            }else if (checkSign.equals("I")){
                gifPartTwo.setImageResource(R.drawable.i);
            }else if (checkSign.equals("J")){
                gifPartTwo.setImageResource(R.drawable.j);
            }else if (checkSign.equals("K")){
                gifPartTwo.setImageResource(R.drawable.k);
            }else if (checkSign.equals("L")){
                gifPartTwo.setImageResource(R.drawable.l);
            }else if (checkSign.equals("M")){
                gifPartTwo.setImageResource(R.drawable.m);
            }else if (checkSign.equals("N")){
                gifPartTwo.setImageResource(R.drawable.n);
            }else if (checkSign.equals("O")){
                gifPartTwo.setImageResource(R.drawable.o);
            }else if (checkSign.equals("P")){
                gifPartTwo.setImageResource(R.drawable.p);
            }else if (checkSign.equals("Q")){
                gifPartTwo.setImageResource(R.drawable.q);
            }else if (checkSign.equals("R")){
                gifPartTwo.setImageResource(R.drawable.r);
            }else if (checkSign.equals("S")){
                gifPartTwo.setImageResource(R.drawable.s);
            }else if (checkSign.equals("T")){
                gifPartTwo.setImageResource(R.drawable.t);
            }else if (checkSign.equals("U")){
                gifPartTwo.setImageResource(R.drawable.u);
            }else if (checkSign.equals("V")){
                gifPartTwo.setImageResource(R.drawable.v);
            }else if (checkSign.equals("W")){
                gifPartTwo.setImageResource(R.drawable.w);
            }else if (checkSign.equals("X")){
                gifPartTwo.setImageResource(R.drawable.x);
            }else if (checkSign.equals("Y")){
                gifPartTwo.setImageResource(R.drawable.y);
            }else if (checkSign.equals("Z")){
                gifPartTwo.setImageResource(R.drawable.z);
            }else if (checkSign.equals("1")){
                gifPartTwo.setImageResource(R.drawable.num1);
            }else if (checkSign.equals("2")){
                gifPartTwo.setImageResource(R.drawable.num2);
            }else if (checkSign.equals("3")){
                gifPartTwo.setImageResource(R.drawable.num3);
            }else if (checkSign.equals("4")){
                gifPartTwo.setImageResource(R.drawable.num4);
            }else if (checkSign.equals("5")){
                gifPartTwo.setImageResource(R.drawable.num5);
            }else if (checkSign.equals("6")){
                gifPartTwo.setImageResource(R.drawable.num6);
            }else if (checkSign.equals("7")){
                gifPartTwo.setImageResource(R.drawable.num7);
            }else if (checkSign.equals("8")){
                gifPartTwo.setImageResource(R.drawable.num8);
            }else if (checkSign.equals("9")){
                gifPartTwo.setImageResource(R.drawable.num9);
            }else if (checkSign.equals("10")){
                gifPartTwo.setImageResource(R.drawable.num10);
            }else if (checkSign.equals("Hello")){
                gifPartTwo.setImageResource(R.drawable.hello);
            }else if (checkSign.equals("Good Bye")){
                gifPartTwo.setImageResource(R.drawable.goodbye);
            }else if (checkSign.equals("Thank You")){
                gifPartTwo.setImageResource(R.drawable.thankyou);
            }else if (checkSign.equals("Please")){
                gifPartTwo.setImageResource(R.drawable.please);
            }else if (checkSign.equals("Sorry")){
                gifPartTwo.setImageResource(R.drawable.sorry);
            }else{
                gifPartTwo.setImageResource(R.drawable.space);
            }
        }
    }
}