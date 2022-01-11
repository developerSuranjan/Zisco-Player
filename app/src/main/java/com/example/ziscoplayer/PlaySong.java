package com.example.ziscoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    TextView songname;
    ImageView play, previous, next;
    SeekBar seekbar;
    ArrayList<File> songs; //All intents coming from mainactivity.java will be taken here
    MediaPlayer mediaPlayer;
    String textContent; //will save the name of the song
    int position;
    Thread updateSeek;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        songname = findViewById(R.id.songname);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekbar = findViewById(R.id.seekbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs =(ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songname.setText(textContent);
        songname.setSelected(true);

        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());  //uri of the song is stored here
        mediaPlayer = MediaPlayer.create(this, uri);
        seekbar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekbar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position != 0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());  //uri of the song is stored here
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekbar.setMax(mediaPlayer.getDuration());
                seekbar.setProgress(0);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                songname.setText(songs.get(position).getName());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position != songs.size() - 1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());  //uri of the song is stored here
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                play.setImageResource(R.drawable.pause);
                seekbar.setMax(mediaPlayer.getDuration());
                seekbar.setProgress(0);
                mediaPlayer.start();
                songname.setText(songs.get(position).getName());
            }
        });
    }
}