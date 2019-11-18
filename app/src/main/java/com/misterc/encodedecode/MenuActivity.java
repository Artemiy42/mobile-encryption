package com.misterc.encodedecode;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MenuActivity extends AppCompatActivity {

    public static final int READ_REQUEST_CODE = 10;
    public static final int SAVE_REQUEST_CODE = 20;
    public static final String KEY_SECRET_KEY_INDEX = "SAVED_SECRET_KEY_INDEX";

    private int savedSecretKeyIndex;
    private String[] listItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listItem = getResources().getStringArray(R.array.key_values);
    }

    @Override
    protected void onPause() {
        super.onPause();

        PreferencesUtil.savePreferences(getApplicationContext(), KEY_SECRET_KEY_INDEX, savedSecretKeyIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();

        savedSecretKeyIndex = PreferencesUtil.loadPreferences(getApplicationContext(), KEY_SECRET_KEY_INDEX);
    }

    protected String getSecretKeyItem() {
        return listItem[savedSecretKeyIndex];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Нет разрешения на доступ к хранилищу. Включите его в настройках", Toast.LENGTH_SHORT).show();
        }

        switch (item.getItemId()) {
            case R.id.mn_open_file:
                performFileSearch();
                break;
            case R.id.mn_save_file:
                performSaveFile();
                break;
            case R.id.mn_security:
                showDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
