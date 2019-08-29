package demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageConstant.*;


public class ImageProvider {

    private static final String TAG = ImageProvider.class.getSimpleName();
    public static String mCurrentPhotoPath;
    public static File photoFile;
    public static Uri outputUri;

    public static void dispatchTakePictureIntent(Context context, Activity activity) {

        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(context, "Failed to create image file.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, Log.getStackTraceString(ex));
                    }

                    if (photoFile != null) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                            Uri photoURI = FileProvider.getUriForFile(context,
                                    context.getPackageName() + ".provider",
                                    new File(photoFile.getAbsolutePath()));
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        } else {
                            takePictureIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            Uri uri = Uri.parse("file://" + photoFile.getAbsolutePath());
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        }

                        activity.startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
                    } else {
                        Toast.makeText(context, "Cannot create photo file.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }
    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(PICTURE_TIMESTAMP_FORMAT).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File storageDir = getAlbumDir();
        File image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        //ApplicationManager.setPassiveActivityPhotoPath(mCurrentPhotoPath);
        Log.e(TAG, "CurrentPhotoPath = " + mCurrentPhotoPath);
        return image;
    }
    public static File getAlbumDir() {
        File storageDir = null;

        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                storageDir = getAlbumStorageDir(PHOTO_ALBUM_NAME);

                if (storageDir != null) {
                    if (!storageDir.mkdirs()) {
                        if (!storageDir.exists()) {
                            return null;
                        }
                    }
                }
            } else {
                Log.v(TAG, "External storage is not mounted READ/WRITE.");
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return storageDir;
    }
    public static File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
    public static Uri galleryAddPic(Activity activity) {

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Bitmap imageBitmap = null;
            int orientation = 0;

            File f = new File(mCurrentPhotoPath);
            Uri contentUri = MyContentProvider.getPhotoUri(f);

            if (contentUri == null) {
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                mCurrentPhotoPath = findLatestImage(activity);
                imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            } else {
                activity.getContentResolver().notifyChange(contentUri, null);
                ContentResolver contentResolver = activity.getContentResolver();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentUri);
                    mCurrentPhotoPath = contentUri.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            orientation = getOrientation(mCurrentPhotoPath);
            if (imageBitmap.getWidth() > 0 && imageBitmap.getHeight() > 0) {
                if (orientation > 1) {
                    Bitmap matrix = rotateBitmap(mCurrentPhotoPath, imageBitmap, orientation,activity);
                }
            }

            File file = new File(mCurrentPhotoPath);
            if (file.length()!=0) {
                mediaScanIntent.setData(contentUri);
                activity.sendBroadcast(mediaScanIntent);
            }else{
//                DialogProvider.OKDialog(activity, activity.getString(R.string.Alert), "ไม่พบข้อมูลรูปภาพ");
            }

            return contentUri;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }
    public static String findLatestImage(Activity activity) {
        long imageId = 0l;
        long thumbnailImageId = 0l;
        String thumbnailPath = "";

        String[] largeFileProjection = {MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA};

        String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
        Cursor myCursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, null, largeFileSort);
        String largeImagePath = "";
        Uri imageCaptureUri;

        try {
            myCursor.moveToFirst();
            largeImagePath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            imageCaptureUri = Uri.fromFile(new File(largeImagePath));

        } finally {
            myCursor.close();
        }
        return largeImagePath;

    }
    public static int getOrientation(String imagePath) {
        ExifInterface exif = null;
        int orientation = 0;
        try {
            exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return orientation;
    }
    public static Bitmap rotateBitmap(String imagePath, Bitmap bitmap, int orientation,Activity activity) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
//        Bitmap cc = null;
//        try {
        Bitmap cc = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        }catch (OutOfMemoryError e){
//            Toast.makeText(activity,R.string.error,Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        cc.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try {
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap.recycle();
        return cc;
    }
    public static void setPhotoSelect(Activity activity, Uri contentUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), contentUri);
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static String imageEncoded(String tempPath) {
        Bitmap image = BitmapFactory.decodeFile(tempPath);
       // image = getResizeImage(image,tempPath);
        String encoded = null;
        if (tempPath != null && !tempPath.isEmpty()) {
            File sdf = new File(tempPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            String mimeType = getMimeType(sdf.getAbsolutePath());

            encoded = "data:" + mimeType + ";base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return encoded;
    }
    public static Bitmap getResizeImage(Bitmap bitmap, String imagePath) {
        Bitmap resized = resizeImage(imagePath);
        return resized;
    }
    public static Bitmap resizeImage( String ImagePath) {

        Bitmap original = BitmapFactory.decodeFile(ImagePath);
        int newHeight = 200;
        int newWidth = 200;

//        if (original.getWidth() > 1028) {
//            float aspectRatio = original.getWidth() /
//                    (float) original.getHeight();
//            int width = 1028;
//            int height = Math.round(width / aspectRatio);
//
//
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);
//            if (original != scaledBitmap) {
//                original.recycle();
//                original = null;
//            }
//            original = scaledBitmap;
//            Bitmap imageQuality = ImageQuality(ImagePath, original);
//            return imageQuality;
//        }

        if(original.getWidth() > 200||original.getHeight()>200){
            int width = original.getWidth();
            int height = original.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);
            // RECREATE THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, false);
            original = resizedBitmap;
        }

        return original;
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void selectFile(Context context, Activity activity){
        photoFile = null;
        try {
            photoFile = createImageFile();
            if(photoFile != null) {
                Intent intentPhotoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPhotoGallery.setType("image/*");
                if (intentPhotoGallery.resolveActivity(context.getPackageManager()) != null) {
                    activity.startActivityForResult(intentPhotoGallery, REQUEST_CODE_SELECT_FILE);
                }
            }
        } catch (IOException ex) {

        }
    }
}
