package com.narwal.parvesh.slapifytrump;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

import java.io.IOException;

/**
 * Created by Parvesh on 21-May-17.
 */

public class Menu_Page extends Activity implements View.OnClickListener {

    Button s, r, c;

    private static final int PICK_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.menu);

        AdView adView = (AdView) findViewById(R.id.adView_menu);

        AdRequest adRequest = new   AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        adView.loadAd(adRequest);

        s = (Button) findViewById(R.id.start);
        r = (Button) findViewById(R.id.rate);
        c = (Button) findViewById(R.id.change);

        s.setOnClickListener(this);
        r.setOnClickListener(this);
        c.setOnClickListener(this);

        StartSmartAnimation.startAnimation(s, AnimationType.Pulse, 1000, 0, false);
        StartSmartAnimation.startAnimation(r, AnimationType.Pulse, 1000, 0, false);
        StartSmartAnimation.startAnimation(c, AnimationType.Pulse, 1000, 0, false);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                Intent start = new Intent(Menu_Page.this, SlapingArea.class);
                startActivity(start);
                break;

            case R.id.rate:
                OpenPlayStore();
                break;
            
            case R.id.change:
                Intent start_gallery = new Intent(Menu_Page.this, SlapingArea.class);
                start_gallery.putExtra("start_gallery", true);
                startActivity(start_gallery);
                break;
        }
    }

    private void OpenPlayStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
