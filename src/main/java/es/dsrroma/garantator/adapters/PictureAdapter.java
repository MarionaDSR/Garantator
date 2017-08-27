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

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.ImageUtils;

public class PictureAdapter extends ArrayAdapter {
    private Context context;
    private Warranty warranty;
    private boolean editable;

    public PictureAdapter(Context context, Warranty warranty) {
        this(context, warranty, true);
    }

    public PictureAdapter(Context context, Warranty warranty, boolean editable) {
        super(context, R.layout.fragment_warranty_picture, warranty.getPictures());
        this.context = context;
        this.warranty = warranty;
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
        } else {
            holder.iv.setImageResource(R.drawable.ic_picture_not_found);
            Crashlytics.log("Image not found " + picture.getFilename());
        }

        convertView.setOnClickListener(pictureOnClickListener(picture));
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
                    dialogBuilder.setPositiveButton(R.string.photo_delete, deleteOnClickListener(picture));
                }
                dialogBuilder.setNegativeButton(R.string.photo_cancel, negativeOnClickListener()).show();
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
                                .setPositiveButton(R.string.photo_delete, confirmDeleteOnClickListener(picture))
                                .setNegativeButton(R.string.photo_delete_cancel, negativeOnClickListener())
                                .show();
                        }
            };
    }

    @NonNull
    private DialogInterface.OnClickListener confirmDeleteOnClickListener(final Picture picture) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                warranty.removePicture(picture);
                remove(picture);
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

    private class ViewHolder {
        ImageView iv;
    }
}
