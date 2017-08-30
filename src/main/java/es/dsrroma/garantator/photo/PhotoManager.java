package es.dsrroma.garantator.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.IOException;

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.utils.FileUtils;

import static es.dsrroma.garantator.utils.Constants.CAPTURE_IMAGE_REQUEST_CODE;
import static es.dsrroma.garantator.utils.Constants.LOAD_IMAGE_REQUEST_CODE;

/**
 * Class to manage all interaction with camera and gallery. Allow to the user
 * to choose one option: Camera or Gallery and scale the selected photo to save
 * memory.
 */
public class PhotoManager {

    /**
     * A pointer to the current callbacks instance (the Activity)
     */
    private Activity activity;

    private String currentPhotoPath;

    /**
     * Constructor
     *
     * @param activity A pointer to the current callbacks instance (the Activity)
     */
    public PhotoManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Comprueba si el intent se puede realizar antes de lanzarlo
     */
    private static boolean intentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);

        return ((packageManager != null) && (packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)).size() > 0);
    }

    public void getPhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            activity.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
        } catch (IOException ex) {
            this.showMessage(activity.getString(R.string.error_camera_missing));
        }
    }

    public void getPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
                intentAvailable(activity, MediaStore.ACTION_IMAGE_CAPTURE)) {
            activity.startActivityForResult(intent, LOAD_IMAGE_REQUEST_CODE);
        } else {
            this.showMessage(activity.getString(R.string.error_insert_sd_card));
        }
    }

    /**
     * Call this method from the activity to manage the result of the onActivityResult
     * method.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return Bitmap image
     */
    public String onActivityResult(int requestCode, int resultCode, Intent data) {
        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_REQUEST_CODE value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();

                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = activity.getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                // At the end remember to close the cursor or you will end with the RuntimeException!
                cursor.close();

                File file = createImageFile();

                FileUtils.copyFile(imagePath, file);

                return file.getAbsolutePath();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Save Image To Gallery
            return currentPhotoPath;
        }
        return null;
    }

    /**
     * You may wish also to save the path in a member variable for later use.
     * Here's an example solution in a method that returns a unique file
     * name for a new photo using a date-time stamp.
     *
     * @return File of image
     * @throws java.io.IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = new File(FileUtils.getPicturesPath());
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                // qué pasa??
                Crashlytics.log("Unable to create " + storageDir);
            }
        }
        File image = new File(storageDir, FileUtils.getPictureFileName());

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Show toast message to the user with a sort duration
     *
     * @param message to show
     */
    private void showMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
