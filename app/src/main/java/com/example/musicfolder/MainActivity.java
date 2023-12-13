package com.example.musicfolder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    private ListView folderListView;
    private ArrayAdapter<String> adapter;
    private String currentFolderPath;
    private TextView folderPathTextView;
    private List<Song> songs;
    private MediaPlayer mediaPlayer;

    private int currentSongIndex = 0;
    int[] rawFileIds = {
            R.raw.anhluonnhuvay_bray_11853369,
            R.raw.chiucachminhnoithua_rhydercoolkidban_12449134,
            R.raw.emconnhoanhkhong_hoangtonkoo_6055903,
            R.raw.mienman_minhhuy_7561811,
            R.raw.tellthekidsilovethem_obitoshikii_11836730
    };

    // Thêm biến boolean để kiểm tra xem có phải là thư mục gốc không
    private boolean isRootFolder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folderListView = findViewById(R.id.folderListView);
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        buttonLayout.setVisibility(View.VISIBLE);
        FileUtils.copyRawFilesToMusicDirectory(this, rawFileIds);
        ImageButton playAllButton = findViewById(R.id.imageButton7);
        ImageButton quitButton = findViewById(R.id.imageButton8);
        ImageButton upButton = findViewById(R.id.imageButton9);

        folderPathTextView = findViewById(R.id.folderPathTextView);

        currentFolderPath = Environment.getExternalStorageDirectory().getPath();
        songs = getListOfSongs();

        List<String> folders = getListOfFolders(currentFolderPath);
        adapter = new ArrayAdapter<String>(this, R.layout.item_listview, R.id.textViewItem, folders) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ImageView imageViewItem = view.findViewById(R.id.imageViewItem);
                TextView textViewItem = view.findViewById(R.id.textViewItem);

                String itemName = getItem(position);

                if (isDirectory(new File(currentFolderPath, itemName).getPath())) {
                    imageViewItem.setImageResource(R.drawable.folder);
                } else {
                    imageViewItem.setImageResource(songs.get(position).getAlbumCoverResourceId());
                }

                textViewItem.setText(itemName);

                return view;
            }
        };

        folderListView.setAdapter(adapter);
        folderPathTextView.setText(currentFolderPath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkStoragePermission();
        }

        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                String selectedPath = new File(currentFolderPath, selectedItem).getPath();

                if (isDirectory(selectedPath)) {
                    currentFolderPath = selectedPath;
                    updateFolderList();
                } else {
                    currentSongIndex = position;
                    openMusicActivity(songs.get(currentSongIndex).getResourceId());
                    buttonLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Choose folder button clicked");
                playAllSongsInFolder();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parentFolder = new File(currentFolderPath).getParentFile();
                if (parentFolder != null) {
                    currentFolderPath = parentFolder.getPath();
                    updateFolderList();
                }
            }
        });
    }

    private void openMusicActivity(int songResourceId) {
        Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
        intent.putExtra("SONG_RESOURCE_ID", songResourceId);
        intent.putExtra("SONG_INDEX", currentSongIndex);
        intent.putParcelableArrayListExtra("SONG_LIST", (ArrayList<Song>) songs);
        startActivity(intent);
    }

    private List<Song> getListOfSongs() {
        List<Song> songs = new ArrayList<>();

        songs.add(new Song("Anh Luôn Như Vậy", R.raw.anhluonnhuvay_bray_11853369));
        songs.add(new Song("Chịu Cách Mình Nói Thua", R.raw.chiucachminhnoithua_rhydercoolkidban_12449134));
        songs.add(new Song("Em Còn Nhớ Anh Không", R.raw.emconnhoanhkhong_hoangtonkoo_6055903));
        songs.add(new Song("Miên Man", R.raw.mienman_minhhuy_7561811));
        songs.add(new Song("Tell the Kids I Love Them", R.raw.tellthekidsilovethem_obitoshikii_11836730));
        for (Song song : songs) {
            int resourceId = song.getResourceId();
            int albumCoverResourceId = getAlbumCoverResourceId(resourceId);
            song.setAlbumCoverResourceId(albumCoverResourceId);
        }

        return songs;
    }

    private int getAlbumCoverResourceId(int songResourceId) {
        Map<Integer, Integer> albumCovers = getAlbumCovers();

        return albumCovers.get(songResourceId) != null ? albumCovers.get(songResourceId) : R.drawable.file_chung;
    }

    private Map<Integer, Integer> getAlbumCovers() {
        Map<Integer, Integer> albumCovers = new HashMap<>();

        albumCovers.put(R.raw.anhluonnhuvay_bray_11853369, R.drawable.bray);
        albumCovers.put(R.raw.chiucachminhnoithua_rhydercoolkidban_12449134, R.drawable.chiucachnoiminhthua1);
        albumCovers.put(R.raw.emconnhoanhkhong_hoangtonkoo_6055903, R.drawable.hoangton);
        albumCovers.put(R.raw.mienman_minhhuy_7561811, R.drawable.mienman);
        albumCovers.put(R.raw.tellthekidsilovethem_obitoshikii_11836730, R.drawable.opito);

        return albumCovers;
    }

    private void updateFolderList() {
        if (currentFolderPath.equals(Environment.getExternalStorageDirectory().getPath())) {
            isRootFolder = true;
            List<String> folders = getListOfFolders(currentFolderPath);
            folderPathTextView.setText(currentFolderPath);
            adapter.clear();
            adapter.addAll(folders);
        } else if (currentFolderPath.contains("Music")) {
            isRootFolder = false;
            List<String> songTitles = new ArrayList<>();
            for (Song song : songs) {
                songTitles.add(song.getTitle());
            }
            folderPathTextView.setText(currentFolderPath);
            adapter.clear();
            adapter.addAll(songTitles);
        } else {
            isRootFolder = false;
            List<String> folders = getListOfFolders(currentFolderPath);
            folderPathTextView.setText(currentFolderPath);
            adapter.clear();
            adapter.addAll(folders);
        }

        // Cập nhật trạng thái của nút "Quay lại" khi danh sách thư mục được cập nhật
        updateUpButtonVisibility();
    }

    // Thêm hàm này để cập nhật trạng thái của nút "Quay lại" khi danh sách thư mục được cập nhật
    private void updateUpButtonVisibility() {
        ImageButton upButton = findViewById(R.id.imageButton9);
        upButton.setVisibility(isRootFolder ? View.GONE : View.VISIBLE);
    }


    private List<String> getListOfFolders(String path) {
        List<String> folders = new ArrayList<>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] contents = directory.listFiles();

            if (contents != null) {
                for (File file : contents) {
                    if (file.isDirectory()) {
                        folders.add(file.getName());
                    }
                }
            }
        }

        return folders;
    }

    private boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                updateFolderList();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playAllSongsInFolder() {
        if (currentFolderPath.contains("Music")) {
            if (songs.isEmpty()) {
                Toast.makeText(this, "Không có bài hát trong danh sách", Toast.LENGTH_SHORT).show();
                return;
            }

            currentSongIndex = 0;
            openMusicActivity(songs.get(currentSongIndex).getResourceId());
        } else {
            Toast.makeText(this, "Không có bài hát nào để phát", Toast.LENGTH_SHORT).show();
        }
    }
}
