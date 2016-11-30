package com.example.saeteam.saeclock;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tiger on 2016/11/1.
 */
public class PlayAlarmAty extends Activity {

    private MediaPlayer mp;
    Button btnAlarmPause;
    Button btnAlarmReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_player_aty);

        mp = MediaPlayer.create(this,R.raw.music);
        mp.start();

        btnAlarmPause = (Button)findViewById(R.id.btnAlarmReset);
        btnAlarmPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        mp.stop();
        mp.release();
    }
}