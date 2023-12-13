package com.example.musicfolder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayMusicActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView albumImageView;
    private int currentSongIndex;
    private List<Song> songs;
    private ImageButton previousButton;
    private ImageButton playButton;
    private ImageButton forwardButton;
    private ImageButton backButton;
    private TextView textViewDuration;
    private TextView textViewRemainingTime;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private Handler handler;
    TextView textViewSongTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);

        textViewSongTitle = findViewById(R.id.textViewSongTitle);
        currentSongIndex = getIntent().getIntExtra("SONG_INDEX", 0);
        songs = getIntent().getParcelableArrayListExtra("SONG_LIST");

        int songResourceId = getIntent().getIntExtra("SONG_RESOURCE_ID", -1);
        backButton = findViewById(R.id.buttonBack);
        previousButton = findViewById(R.id.imageButtonPrevious);
        playButton = findViewById(R.id.imageButtonPlay);
        forwardButton = findViewById(R.id.imageButtonForward);
        albumImageView = findViewById(R.id.imageView);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewRemainingTime = findViewById(R.id.textViewRemainingTime);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacksAndMessages(null);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int progress = (int) (event.getX() / v.getWidth() * mediaPlayer.getDuration());
                        mediaPlayer.seekTo(progress);
                        updateDurationLabels(progress, mediaPlayer.getDuration());
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessage(0);
                        break;
                }
                return true;
            }
        });

        if (songResourceId != -1) {
            mediaPlayer = MediaPlayer.create(this, songResourceId);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });
            isPlaying = true;
            mediaPlayer.start();
            updateAlbumImage(songResourceId);
            updateDurationLabels(0, mediaPlayer.getDuration());
            setupSeekBar();
            updatePlayButtonImage();
            textViewSongTitle.setText(songs.get(currentSongIndex).getTitle());
            isPlaying = true;
        } else {
            Toast.makeText(this, "Invalid song resource ID", Toast.LENGTH_SHORT).show();
        }

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            isPlaying = !isPlaying;
            updatePlayButtonImage();
        }
    }

    private void updatePlayButtonImage() {
        int playButtonImageResource = isPlaying ? R.drawable.pause : R.drawable.play;
        playButton.setImageResource(playButtonImageResource);
    }

    private void setupSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                updateDurationLabels(currentPosition, mediaPlayer.getDuration());
                handler.sendEmptyMessageDelayed(0, 1000); // Update every second
                return true;
            }
        });
        handler.sendEmptyMessage(0);
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            currentSongIndex = songs.size() - 1;
        }
        playSong();
    }

    private void playNextSong() {
        if (currentSongIndex < songs.size() - 1) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0;
        }
        playSong();
    }

    private void playSong() {
        if (currentSongIndex >= 0 && currentSongIndex < songs.size()) {
            Song selectedSong = songs.get(currentSongIndex);
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, selectedSong.getResourceId());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });
            mediaPlayer.start();
            updateAlbumImage(selectedSong.getResourceId());
            updateDurationLabels(0, mediaPlayer.getDuration());
            setupSeekBar();
            updatePlayButtonImage();
            textViewSongTitle.setText(selectedSong.getTitle());
            isPlaying = true;
        }
    }

    private void updateAlbumImage(int songResourceId) {
        Map<Integer, Integer> albumImages = getAlbumImages();
        Integer imageResourceId = albumImages.get(songResourceId);

        if (imageResourceId != null) {
            albumImageView.setImageResource(imageResourceId);
        }
    }

    private Map<Integer, Integer> getAlbumImages() {
        Map<Integer, Integer> albumImages = new HashMap<>();

        albumImages.put(R.raw.anhluonnhuvay_bray_11853369, R.drawable.bray);
        albumImages.put(R.raw.chiucachminhnoithua_rhydercoolkidban_12449134, R.drawable.chiucachnoiminhthua1);
        albumImages.put(R.raw.emconnhoanhkhong_hoangtonkoo_6055903, R.drawable.hoangton);
        albumImages.put(R.raw.mienman_minhhuy_7561811, R.drawable.mienman);
        albumImages.put(R.raw.tellthekidsilovethem_obitoshikii_11836730, R.drawable.opito);

        return albumImages;
    }

    private void updateDurationLabels(int currentPosition, int duration) {
        String durationString = formatDuration(currentPosition);
        String remainingTimeString = formatDuration(duration - currentPosition);

        textViewDuration.setText(durationString);
        textViewRemainingTime.setText("-" + remainingTimeString);
    }

    private String formatDuration(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        handler.removeCallbacksAndMessages(null); // Remove callbacks to prevent memory leaks
    }
}
