package es.dsrroma.garantator.photo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import es.dsrroma.garantator.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Fragment used for managing options of getting a photo.
 */
public class PhotoSourceDialog extends DialogFragment {
    /**
     * The dialog fragment identifier.
     */
    public static final String PHOTO_SOURCE_DIALOG = "PHOTO_SOURCE_DIALOG";

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 300;
    public static final int REQUEST_CAMERA_PERMISSION = 400;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private PhotoSourceDialogListener mDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mDialogListener = (PhotoSourceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set dialog title
        builder.setTitle(getResources().getString(R.string.photo_source_dialog_title));

        //Set options and listeners
        builder.setItems(R.array.photo_source_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Gallery option selected
                        openGallery();
                        break;

                    case 1:
                        // Camera option selected
                        openCamera();
                        break;
                    default:
                        break;
                }
            }

            private void openCamera() {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                    requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION);
                } else {
                    mDialogListener.onCameraClick();
                }
            }

            private void openGallery() {
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PERMISSION_GRANTED) {
//                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
//                } else {
                    mDialogListener.onGalleryClick();
//                }
            }

            private void requestPermission(final String permission, final int code) {
                final FragmentActivity activity = getActivity();
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Snackbar.make(activity.getCurrentFocus(), "Necesito tu permiso ;)", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(activity, new String[]{permission}, code);

                                }
                            })
                .show();
                } else {
                    requestPermissions(new String[]{permission}, code);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onDetach() {
        mDialogListener = null;
        super.onDetach();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface PhotoSourceDialogListener {
        /**
         * Called when a galery item in the dialog is selected.
         */
        void onGalleryClick();

        /**
         * Called when a camera item in the dialog is selected.
         */
        void onCameraClick();
    }
}
