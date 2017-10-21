package com.narwal.parvesh.slapifytrump;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Parvesh on 20-May-17.
 */

public class SlapingArea extends Activity implements View.OnClickListener {

    TextView seconds, slapsCount;
    RelativeLayout rLayout;
    ImageView trumpFace;
    String slap_counter_text;

    private boolean isPhotoFromGallery = false;
    private boolean isCounterInteruppt = false;

    private static final String TAG = SlapingArea.class.getSimpleName();

    private static final int RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM = 3;
    private static int PICK_IMAGE_REQUEST = 5;

    //private ArrayList<Bitmap> facesBitmap;

    private static final int MAX_FACE = 10;

    private InterstitialAd mInterstitialAd;

    SoundPool sPool;
    int slap = 0;
    final int COUNTER_TIME = 5000;
    boolean isCounterStarted = false;
    int no_of_slaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slaping_area);
        init_app();

        Intent intent = getIntent();
        if (intent.hasExtra("isTryAgain")) {
            int isTryAgain = intent.getIntExtra("isTryAgain", 0);

            if (isTryAgain == 1) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet. Try again");
                }
            }
        }

        else if (intent.getBooleanExtra("start_gallery", false)) {
            // start proccess to get Image
            Toast.makeText(this, "Please select an image with a single face.", Toast.LENGTH_SHORT).show();
            getFaceFromGallery();
        }


    }

    private void getFaceFromGallery() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            requestWriteExternalPermission();
        }
    }

    private void requestWriteExternalPermission() {

        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_WRITE_EXTERNAL_STORAGE_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Write External permission granted");
            // we have permission
            getImage();
        }
    }

    private void getImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException i) {
            Toast.makeText(SlapingArea.this, "Your Device can not select image from gallery.", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            Bitmap bitmap = ImageUtils.getBitmap(ImageUtils.getRealPathFromURI(this, uri), 2048, 1232);
            if (bitmap != null)
                detectFace(bitmap);
                //setBitmap(bitmap);
            else
                Toast.makeText(this, "Cann't open this image.", Toast.LENGTH_LONG).show();
        }
    }

    private void setBitmap(Bitmap bitmap) {
        trumpFace.setImageBitmap(bitmap);
        isPhotoFromGallery = true;
    }

    private void detectFace(Bitmap bitmap) {

        resetData();

        android.media.FaceDetector fdet_ = new android.media.FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACE);

        android.media.FaceDetector.Face[] fullResults = new android.media.FaceDetector.Face[MAX_FACE];
        fdet_.findFaces(bitmap, fullResults);

        ArrayList<FaceResult> faces_ = new ArrayList<>();


        for (int i = 0; i < MAX_FACE; i++) {
            if (fullResults[i] != null) {
                PointF mid = new PointF();
                fullResults[i].getMidPoint(mid);

                float eyesDis = fullResults[i].eyesDistance();
                float confidence = fullResults[i].confidence();
                float pose = fullResults[i].pose(android.media.FaceDetector.Face.EULER_Y);

                Rect rect = new Rect(
                        (int) (mid.x - eyesDis * 1.20f),
                        (int) (mid.y - eyesDis * 0.55f),
                        (int) (mid.x + eyesDis * 1.20f),
                        (int) (mid.y + eyesDis * 1.85f));

                /**
                 * Only detect face size > 100x100
                 */
                if (rect.height() * rect.width() > 100 * 100) {
                    FaceResult faceResult = new FaceResult();
                    faceResult.setFace(0, mid, eyesDis, confidence, pose, System.currentTimeMillis());
                    faces_.add(faceResult);

                    //
                    // Crop Face to display in RecylerView
                    //
                    Bitmap cropedFace = ImageUtils.cropFace(faceResult, bitmap, 0);
                    if (cropedFace != null) {
                        trumpFace.setImageBitmap(cropedFace);
                        isPhotoFromGallery = true;
                    }

                }
            }
        }

        if(!isPhotoFromGallery){
            setBitmap(bitmap);
            Toast.makeText(this, "Sorry! We could not detect any face in selected picture.", Toast.LENGTH_LONG).show();
        }
    }

    private void resetData() {
    }


    private void init_app() {
        seconds = (TextView) findViewById(R.id.seconds);
        rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        slapsCount = (TextView) findViewById(R.id.no_of_slaps);
        trumpFace = (ImageView) findViewById(R.id.ivTrump);

        AdView adView = (AdView) findViewById(R.id.adView_slapping_area);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("8BE8614F2298107266F01F0E41BEDE27")
                .build();

        adView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4327820221556313/9239174984");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        rLayout.setOnClickListener(this);
        trumpFace.setOnClickListener(this);


        no_of_slaps = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            sPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        slap = sPool.load(this, R.raw.slap_sound, 1);

    }

    @Override
    public void onClick(View view) {

        if (!isCounterStarted) {
            no_of_slaps++;
            startCounter();
        } else if (view.getId() == R.id.ivTrump) {
            final Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
            trumpFace.startAnimation(bounceAnimation);
            count_slaps();
        }

    }

    private void count_slaps() {
        no_of_slaps++;

        sPool.play(slap, 1, 1, 1, 0, 1);

        slap_counter_text = "Slaps: " + String.format(Locale.US, "%02d", no_of_slaps -1);

        slapsCount.setText(slap_counter_text);

        if (no_of_slaps > 10 && !isPhotoFromGallery) trumpFace.setImageResource(R.drawable.trump_yelling);
    }

    private void startCounter() {

        isCounterStarted = true;

        new CountDownTimer(COUNTER_TIME, 1) {

            @Override
            public void onTick(long l) {
                String text = "Time: " + String.format(Locale.US, "%04d", l);
                seconds.setText(text);
            }

            @Override
            public void onFinish() {

                if(!isCounterInteruppt){
                    String text = "Time: " + String.format(Locale.US, "%04d", 0);
                    seconds.setText(text);
                    trumpFace.setEnabled(false);
                    Intent game_over = new Intent(SlapingArea.this, GameOver.class);
                    game_over.putExtra("score", no_of_slaps - 1);
                    startActivity(game_over);
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        if(isCounterStarted){
            isCounterInteruppt = true;
        }

        Intent menu = new Intent(SlapingArea.this, Menu_Page.class);
        finish();
        startActivity(menu);


    }
}
