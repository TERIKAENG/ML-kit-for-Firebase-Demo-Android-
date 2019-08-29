package demo.dev.flexmedia.co.th.mlkit_flex_demo.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class CallPermission {

    public static final int REQUEST_PHONE = 003;
    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.CALL_PHONE
    };

    public static boolean verify(Activity activity){
        int permission_call_phone = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

        if (permission_call_phone != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_PHONE,
                    REQUEST_PHONE
            );
            return true;
        }
        return false;
    }

}
