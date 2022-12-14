package com.projectmonterey.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.projectmonterey.R;
import com.projectmonterey.backend.LoginActivity;

import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    public List<LinearLayout> linearLayoutList;
    public SeekBar volume;
    public LinearLayout mainLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mainLL = findViewById(R.id.settingsLL);
        //Should add the customised linearlayout views
        LinearLayout linearLayout = new LinearLayout(SettingsActivity.this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        volume = findViewById(R.id.seekBar);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if(!audioManager.isVolumeFixed()){
                            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            int min = Build.VERSION.SDK_INT>Build.VERSION_CODES.P ? audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) : 0;
                            int desired = Math.round((max-min)*i/100+min);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,desired,AudioManager.FLAG_SHOW_UI);
                        }else{
                            Toast.makeText(getApplicationContext(),"Volume is fixed by device",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        try{
                            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            int min = 0;
                            int desired = Math.round((max-min)*i/100+min);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,desired,AudioManager.FLAG_SHOW_UI);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    public void logout(View view){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Toast.makeText(getApplicationContext(),"Successfully signed out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        SettingsActivity.this.finish();
    }

}