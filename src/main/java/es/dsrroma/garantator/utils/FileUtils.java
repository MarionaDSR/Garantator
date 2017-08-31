package es.dsrroma.garantator.utils;

import android.os.Environment;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import es.dsrroma.garantator.Garantator;
import es.dsrroma.garantator.R;

public class FileUtils {
    public static final String APP_PREFIX = "/garantator";
    public static final String WARRANTY_PREFIX = "/warranty";
    public static final String PICTURE_PREFIX = "/pic_";
    public static final String IMAGE_EXT = ".jpg";

    public static void copyFile(String inputFile, File outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File(outputFile.getParent());
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new RuntimeException("no puedo crear los directorios " + dir.getAbsolutePath());
                }
            }
            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Crashlytics.logException(fnfe1);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static String getUserPath() {
        File externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = externalStorage.getAbsolutePath() + APP_PREFIX;
        return path;
    }

    public static String getPicturesPath() {
        String path = getUserPath() + WARRANTY_PREFIX;
        return path;
    }

    public static String getPictureFileName() {
        return PICTURE_PREFIX + Long.toString(System.currentTimeMillis()) + IMAGE_EXT;
    }

    public static boolean deleteFile(String filename) {
        File file = new File(filename);
        boolean deleted = file.delete();
        if (!deleted) {
            Crashlytics.log(Garantator.getContext().getString(R.string.error_file_not_deleted, filename));
        }
        return deleted;
    }

    public static boolean deleteFiles() {
        return deleteFiles(new File(getUserPath()));
    }

    private static boolean deleteFiles(File dir) {
        boolean ok = true;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    ok &= deleteFiles(file);
                } else {
                    ok &= file.delete();
                }
            }
        }
        ok &= dir.delete();
        return ok;
    }
}
