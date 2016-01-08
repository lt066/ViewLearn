package com.example.administrator.viewlearn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.viewlearn.view.MyCompoundButton;
import com.example.administrator.viewlearn.view.SwitchButton;

public class MainActivity extends AppCompatActivity {
    private TextView text1;
    private View li1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        li1 = findViewById(R.id.li1);
        text1 = (TextView) findViewById(R.id.text1);
//        text1.setText(" 免费 ");
//        text1.setTextColor(Color.RED);
//        text1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        text1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        text1.getPaint().setAntiAlias(true);// 抗锯齿
        li1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        li1.setBackgroundResource(R.color.text1);
                        Toast.makeText(getApplicationContext(), motionEvent.getY() + "" + "另一个" + view.getTop(), Toast.LENGTH_SHORT).show();

                        break;
                    case MotionEvent.ACTION_UP:

                        li1.setBackgroundResource(R.color.text1_default);
                        Toast tm = Toast.makeText(getApplicationContext(), motionEvent.getY() + "" + "另一个" + view.getBottom(), Toast.LENGTH_SHORT);

                        tm.setGravity(Gravity.CENTER, 0, 0);
                        tm.show();
                        if (motionEvent.getY() >= 0 && motionEvent.getY() <= (view.getBottom() - view.getTop()))
                            startActivity(new Intent(MainActivity.this, Activity2.class));

                        return false;
                    case MotionEvent.ACTION_MOVE:

                        if (motionEvent.getY() >= 0 && motionEvent.getY() <= (view.getBottom() - view.getTop()))
                            li1.setBackgroundResource(R.color.text1);
                        else
                            li1.setBackgroundResource(R.color.text1_default);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Toast.makeText(getApplicationContext(), motionEvent.getY() + "" + "另一个" + view.getTop(), Toast.LENGTH_SHORT).show();
                        break;

                }

                return true;
            }
        });
        SwitchButton myCompoundButton = (SwitchButton) findViewById(R.id.switchbutton);
        myCompoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Toast.makeText(getApplicationContext(),"开启",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"关闭",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
