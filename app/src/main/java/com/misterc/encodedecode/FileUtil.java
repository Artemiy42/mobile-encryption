package com.misterc.encodedecode;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {
    private static boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveFile(@Nullable Intent data, String text) {
        if (!isExternalStorageWritable()) {
            return;
        }

        try {
            File textFile = new File(Environment.getExternalStorageDirectory(), getDirection(data));
            FileOutputStream fos = new FileOutputStream(textFile);
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(@Nullable Intent data) {
        StringBuilder text = new StringBuilder();

        try {
            File file = new File(Environment.getExternalStorageDirectory(), getDirection(data));
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    private static String getDirection(@Nullable Intent data) throws IOException {
        if (data == null) {
            throw new IOException();
        }

        Uri uri = data.getData();
        String path = uri.getPath();
        path = path.substring(path.indexOf(":") + 1);
        if (path.contains("emulated")) {
            path = path.substring(path.indexOf("0") + 1);
        }

        return path;
    }
}
