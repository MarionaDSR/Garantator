package es.dsrroma.garantator.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageUtils {

    /**
     * You can dramatically reduce the amount of dynamic heap used by expanding the JPEG
     * into a memory array that's already scaled to match the size of the destination view
     *
     * @param imagePath path of the image
     * @return Bitmap scaled image
     */
    public static Bitmap getScaledBitmapImage(String imagePath, int width, int height) {
        File file = new File(imagePath);
        if (file.exists() && file.length() > 0) {
            try {
                FileInputStream instream = new FileInputStream(imagePath);
                BufferedInputStream bif = new BufferedInputStream(instream);
                byte[] byteImage1 = new byte[bif.available()];
                bif.read(byteImage1);
                BitmapFactory.decodeByteArray(byteImage1, 0, byteImage1.length);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor;
            if (width == 0 || height == 0) {
                scaleFactor = 1;
            } else {
                scaleFactor = Math.min(photoW / width, photoH / height);
            }

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            // Return scaled bitmap
            return BitmapFactory.decodeFile(imagePath, bmOptions);
        }
        return null;
    }
}
