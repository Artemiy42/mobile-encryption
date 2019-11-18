package com.misterc.encodedecode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public final class PermissionUtil {

    private static final String MNC = "MNC";

    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static boolean isMNC() {
        return MNC.equals(Build.VERSION.CODENAME);
    }

    private static boolean hasPermission(Context context, String permission) {
        if (isMNC()) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    public static void requestPerms(Activity activity, String permissions, int requestCode) {
//        if (!hasPermission(activity, permissions)) {
            activity.requestPermissions(new String[] {permissions}, requestCode);
    }

    public static void showNoStoragePermissionSnackbar(final Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "Дозвіл на доступ до сховища не надан", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(activity)
                .setTitle("Необхідний дозвіл")
                .setMessage("Немає дозволу на доступ до сховища. Увімкніть його в налаштуваннях")
                .setPositiveButton("Надати", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openApplicationSettings(activity);
                    }
                })
                .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
        }
    }

    private static void openApplicationSettings(Activity activity) {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(appSettingsIntent);
    }

    private static void requestPermissionWithRationale(final Activity activity, final String permissions, final int requestCode) {
        if (activity.shouldShowRequestPermissionRationale(permissions)) {
            new AlertDialog.Builder(activity)
                .setTitle("Необхідний дозвіл")
                .setMessage("Дозвіл потрібен для відкиття та збереження файлів")
                .setPositiveButton("Надати", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPerms(activity, permissions, requestCode);
                    }
                })
                .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
        } else {
            requestPerms(activity, permissions, requestCode);
        }
    }
}
