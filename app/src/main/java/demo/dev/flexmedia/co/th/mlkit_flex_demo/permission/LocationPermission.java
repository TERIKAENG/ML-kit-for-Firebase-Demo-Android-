package demo.dev.flexmedia.co.th.mlkit_flex_demo.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class LocationPermission {

    public static final int REQUEST_LOCATION = 001;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static boolean verify(Activity activity){
        int permission_fine_location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse_location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission_fine_location != PackageManager.PERMISSION_GRANTED || permission_coarse_location != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION
            );
            return true;
        }
        return false;
    }

    public static boolean checkAllowPermissionLocation(Activity activity){
        int permission_fine_location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse_location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission_fine_location != PackageManager.PERMISSION_GRANTED || permission_coarse_location != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


}
