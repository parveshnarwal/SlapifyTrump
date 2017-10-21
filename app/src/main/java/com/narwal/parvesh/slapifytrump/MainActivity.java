package com.narwal.parvesh.slapifytrump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.SmartAnimation;
import com.podcopic.animationlib.library.StartSmartAnimation;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ImageView splashIcon = (ImageView) findViewById(R.id.ivSplash);

        StartSmartAnimation.startAnimation(splashIcon, AnimationType.RotateOut, 3000, 0, true);


        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                finally{
                    Intent start = new Intent(MainActivity.this, Menu_Page.class);
                    startActivity(start);
                }
            }

        };

        timer.start();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}
