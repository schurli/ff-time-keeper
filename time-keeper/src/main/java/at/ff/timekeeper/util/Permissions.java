package at.ff.timekeeper.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    public static final String[] BLE;
    public static final String[] CAMERA;
    public static final String[] STORAGE;
    public static final String[] ACTIVITY_RECOGNITION;

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            BLE = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT, // api level > 30
                    Manifest.permission.BLUETOOTH_SCAN, // api level > 30
            };
            CAMERA = new String[]{
                    Manifest.permission.CAMERA,
            };
            STORAGE = new String[0];
            ACTIVITY_RECOGNITION = new String[]{
                    Manifest.permission.ACTIVITY_RECOGNITION, // api level > 28
            };
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            BLE = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            CAMERA = new String[]{
                    Manifest.permission.CAMERA,
            };
            STORAGE = new String[0];
            ACTIVITY_RECOGNITION = new String[]{
                    Manifest.permission.ACTIVITY_RECOGNITION, // api level > 28
            };
        } else {
            BLE = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            CAMERA = new String[]{
                    Manifest.permission.CAMERA,
            };
            STORAGE = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            ACTIVITY_RECOGNITION = new String[0];
        }
    };

    public static final Permissions INSTANCE = new Permissions();

    private Permissions() {}

    public String[] missing(Context context, String[] permissions) {

        List<String> missingPermissions = new ArrayList<>();

        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions.toArray(new String[0]);
    }

}
