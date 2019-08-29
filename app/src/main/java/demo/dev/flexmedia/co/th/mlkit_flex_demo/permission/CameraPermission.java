package demo.dev.flexmedia.co.th.mlkit_flex_demo.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class CameraPermission {

    public static final int REQUEST_STORAGE = 002;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static boolean verify(Activity activity){
        int permission_write_external_storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read_external_storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission_write_external_storage != PackageManager.PERMISSION_GRANTED || permission_read_external_storage != PackageManager.PERMISSION_GRANTED||permission_camera!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_STORAGE
            );
            return true;
        }
        return false;
    }

}
