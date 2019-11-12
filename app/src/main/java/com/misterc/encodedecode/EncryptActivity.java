package com.misterc.encodedecode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class EncryptActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final int READ_REQUEST_CODE = 10;
    private static final int SAVE_REQUEST_CODE = 20;
    private static final String TAG = "MyLogs";

    private TextView tv_output;
    private TextView tv_encode;
    private EditText et_fileName;
    private EditText et_password;
    private String input;

    private SharedPreferences sharedPreferences;
    private String[] listItems;

    public final String APP_PREFERENCES = "AppSettings";
    final String KEY_SECRET_KEY_INDEX = "SAVED_SECRET_KEY_INDEX";
    private int savedSecretKeyIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        Log.d(TAG, "onCreat: Начало роботы програмы");

        tv_output = findViewById(R.id.tv_output);
        tv_encode = findViewById(R.id.tv_encode);
        et_password = findViewById(R.id.et_password);

        listItems = getResources().getStringArray(R.array.key_values);

        LoadPreferences();

        if (ContextCompat.checkSelfPermission(EncryptActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(EncryptActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SavePreferences(KEY_SECRET_KEY_INDEX, savedSecretKeyIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (ContextCompat.checkSelfPermission(EncryptActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Нет разрешения на доступ к хранилищу. Включите его в настройках", Toast.LENGTH_SHORT).show();
        }

        switch (item.getItemId()) {
            case R.id.mn_open_file:
                performFileSearch();
                break;
            case R.id.mn_save_file:
                if (tv_output.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Зашифруйте або дешифруйте файл", Toast.LENGTH_SHORT).show();
                } else {
                    performSaveFile();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void onClickEncoding(View view) {
        String encMsq = "";
        String password = et_password.getText().toString();

        if (password.isEmpty()) {
            Toast.makeText(EncryptActivity.this, "Для шифрування та дешифрування треба ввести пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (view.getId()) {
            case R.id.btn_encode:
                try {
                    encMsq = SimpleCrypto.encrypt(password, input, listItems[savedSecretKeyIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_decode:
                try {
                    encMsq = SimpleCrypto.decrypt(password, input, listItems[savedSecretKeyIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        tv_encode.setText(encMsq);
    }

    public void openPermissions() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void performFileSearch() {
        Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent, READ_REQUEST_CODE);
    }

    private void performSaveFile() {
        Intent fileIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent, SAVE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = uri.getPath();
                        Log.d(TAG, "onActivityResult.path until: " + path);
                        path = path.substring(path.indexOf(":") + 1);
                        if (path.contains("emulated")) {
                            path = path.substring(path.indexOf("0") + 1);
                        }
                        Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
                        input = readText(path);
                        tv_output.setText(input);
                    }
                }
                break;
            case SAVE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = uri.getPath();
                        Log.d(TAG, "onActivityResult.path until: " + path);
                        path = path.substring(path.indexOf(":") + 1);
                        if (path.contains("emulated")) {
                            path = path.substring(path.indexOf("0") + 1);
                        }
                        Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
                        saveFile(path);
                    }
                }
                break;
        }
    }

    private String readText(String input) {
        File file = new File(Environment.getExternalStorageDirectory(), input);
        Log.d(TAG, "readText.getExternalStorageDirectory: " + Environment.getExternalStorageDirectory());
        StringBuilder text = new StringBuilder();
        try {
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

    private void saveFile(String input) {
        if (!isExternalStorageWritable()) {
            Toast.makeText(EncryptActivity.this, "Не могу записать на устройство", Toast.LENGTH_SHORT).show();
            return;
        }

        File textFile = new File(Environment.getExternalStorageDirectory(), input);

        try {
            FileOutputStream fos = new FileOutputStream(textFile);
            fos.write(tv_encode.getText().toString().getBytes());
            fos.close();
            Log.d(TAG, "writeFile: Файл сохранён");
            Toast.makeText(this, "Файл збережено", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d(TAG, "Да, в него можно писать");
            return true;
        } else {
            Log.d(TAG, "Нет, в него нельзя писать");
            return false;
        }
    }

    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        Log.d(TAG, "Настройка сохранена: " + KEY_SECRET_KEY_INDEX);
    }

    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                APP_PREFERENCES, MODE_PRIVATE);
        savedSecretKeyIndex = sharedPreferences.getInt(KEY_SECRET_KEY_INDEX, 0);
        Log.d(TAG, "Загрузка настроек: " + KEY_SECRET_KEY_INDEX);
    }

    public void showDialog(MenuItem item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EncryptActivity.this);

        builder.setTitle("Виберіть шифрування повідомлення")
                .setSingleChoiceItems(R.array.key_values, savedSecretKeyIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        savedSecretKeyIndex = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                    }
                })
                .setNegativeButton("Скасувати", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        builder.create();
        builder.show();
    }
}