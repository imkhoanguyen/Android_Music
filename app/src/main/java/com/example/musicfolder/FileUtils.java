package com.example.musicfolder;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static void copyRawFilesToMusicDirectory(Context context, int[] rawFileIds) {
        Resources resources = context.getResources();
        File musicDirectory = new File(context.getExternalFilesDir(null), "Music");

        if (!musicDirectory.exists()) {
            musicDirectory.mkdirs();
        }

        for (int rawFileId : rawFileIds) {
            String fileName = resources.getResourceEntryName(rawFileId) + ".mp3";
            File destinationFile = new File(musicDirectory, fileName);

            try (InputStream inputStream = resources.openRawResource(rawFileId);
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                Log.d("FileUtils", "File copied to: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Log the list of files in the music directory
        File[] files = musicDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                Log.d("FileUtils", "File in music directory: " + file.getAbsolutePath());
            }
        } else {
            Log.d("FileUtils", "No files in the music directory.");
        }
    }

}
