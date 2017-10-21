package com.narwal.parvesh.slapifytrump;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Parvesh on 04-Jun-17.
 */

public class GameOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Resources res = getResources();

        int no_of_slaps = intent.getIntExtra("score", 0);

        String text = String.format(res.getString(R.string.game_over_text), Integer.toString(no_of_slaps));

        setContentView(R.layout.game_over);
        TextView scoreLine = (TextView) findViewById(R.id.tvGameOverText);

        scoreLine.setText(text);

        setTitle("Game Over");
    }

    public void tryAgain(View view){
        Intent game_over = new Intent(GameOver.this, SlapingArea.class);
        game_over.putExtra("isTryAgain", 1);
        startActivity(game_over);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
