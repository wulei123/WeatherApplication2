package com.example.administrator.weatherapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

    private TextView welcomeName;
    private ImageView background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeName = (TextView)findViewById(R.id.name_welcome);
        background = (ImageView)findViewById(R.id.welcome_background);
        //在界面上显示一个提示框
//        Toast.makeText(WelcomeActivity.this, "更多精彩内容，请使用app", Toast.LENGTH_LONG).show();



        Animation anim = AnimationUtils.loadAnimation(this,R.anim.welcome_show);
        welcomeName.setAnimation(anim);
        Animation animBg = AnimationUtils.loadAnimation(this,R.anim.backgroung_anim);
        background.setAnimation(animBg);





        final Runnable callback=new Runnable() {
            //一段被运行的代码
            @Override
            public void run() {
                //跳转到新的页面
                Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);

                //增加一个页面跳转的动画
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                //销毁当前的activity
                finish();
            }
        };
        //实例化消息传递者handler
        final Handler handler=new Handler();
        //在界面停留5秒钟
        Thread thread=new Thread() {
            //当线程运行的时候，执行的操作
            @Override
            public void run() {
                //在子线程里停留五秒钟
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    //把异常信息打印在控制台上
                    e.printStackTrace();
                }
                handler.post(callback);
            }
        };
        //开启新的线程
        thread.start();
    }
}
