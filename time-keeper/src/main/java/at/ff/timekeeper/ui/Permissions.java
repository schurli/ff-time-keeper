package at.ff.timekeeper.ui;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

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
