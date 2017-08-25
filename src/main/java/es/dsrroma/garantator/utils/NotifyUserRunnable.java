package es.dsrroma.garantator.utils;

import android.content.Context;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

public class NotifyUserRunnable implements Runnable {

    // TODO manage and translate errors to userfriendly messages

    private Context context;
    private String message;

    public NotifyUserRunnable(Context context, Throwable problem) {
        this.context = context;
        this.message = "ERROR: " + problem.getMessage();
        Crashlytics.log(message);
    }

    public NotifyUserRunnable(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    @Override
    public void run() {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
