package com.misterc.encodedecode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    public final int PERMISSION_REQUEST_READ = 10;
    public final int PERMISSION_REQUEST_SAVE = 20;
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
        switch (item.getItemId()) {
            case R.id.mn_open_file:
                PermissionUtil.requestPerms(this, PermissionUtil.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_READ);
                break;
            case R.id.mn_save_file:
                PermissionUtil.requestPerms(this, PermissionUtil.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_SAVE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_REQUEST_READ:
                    performFileSearch();
                    break;
                case PERMISSION_REQUEST_SAVE:
                    performSaveFile();
                    break;
                default:
                    break;
            }
        } else {
            PermissionUtil.showNoStoragePermissionSnackbar(this);
        }
    }
}
