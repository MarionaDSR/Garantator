package es.dsrroma.garantator.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.utils.ImageUtils;

public class PictureAdapter extends ArrayAdapter {
    private Context context;
    private boolean editable;

    public PictureAdapter(Context context, List pictures) {
        this(context, pictures, true);
    }

    public PictureAdapter(Context context, List pictures, boolean editable) {
        super(context, R.layout.fragment_warranty_picture, pictures);
        this.context = context;
        this.editable = editable;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Check if this convertView is an instance of our ViewHolder
        // This allow us to reuse the convertView for each row and only call
        // to findViewById method once.
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_warranty_picture, null);

            holder = new ViewHolder();

            holder.iv = (ImageView) convertView.findViewById(R.id.warranty_picture_image);
            // Save views in convertView tag
            convertView.setTag(holder);
        } else {
            // Load the views from convertView tag
            holder = (ViewHolder) convertView.getTag();
        }

        // Get actual item
        final Picture picture = (Picture) getItem(position);

        // Set values
        int imageSize = (int) getContext().getResources().getDimension(R.dimen.picture_size);
        Bitmap image = ImageUtils.getScaledBitmapImage(picture.getFilename(), imageSize, imageSize);
        if (image != null) {
            holder.iv.setImageBitmap(image);
            convertView.setOnClickListener(pictureOnClickListener(picture));
        } else {
            holder.iv.setImageResource(R.drawable.ic_picture_not_found);
            Crashlytics.log("Image not found " + picture.getFilename());
            picture.removeFromWarranty();
        }
        return convertView;
    }

    @NonNull
    private View.OnClickListener pictureOnClickListener(final Picture picture) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView dialogImage = new ImageView(context);
                Bitmap image = ImageUtils.getScaledBitmapImage(picture.getFilename(), 0, 0);
                dialogImage.setImageBitmap(image);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                        .setView(dialogImage);
                if (editable) {
                    dialogBuilder.setPositiveButton(R.string.dialog_delete, deleteOnClickListener(picture));
                }
                dialogBuilder.setNegativeButton(R.string.dialog_cancel, negativeOnClickListener()).show();
            }
        };
    }

    @NonNull
    private DialogInterface.OnClickListener deleteOnClickListener(final Picture picture) {
        return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(context)
                                .setMessage(R.string.photo_delete_confirm)
                                .setPositiveButton(R.string.dialog_delete, confirmDeleteOnClickListener(picture))
                                .setNegativeButton(R.string.dialog_cancel, negativeOnClickListener())
                                .show();
                        }
            };
    }

    @NonNull
    private DialogInterface.OnClickListener confirmDeleteOnClickListener(final Picture picture) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                picture.removeFromWarranty();
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    private DialogInterface.OnClickListener negativeOnClickListener() {
        return new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do
                    }
                };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void resetPictures(List pictures) {
        clear();
        addAll(pictures);
    }

    private class ViewHolder {
        ImageView iv;
    }
}
