package es.dsrroma.garantator.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.data.model.Picture;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.ImageUtils;

public class PictureAdapter extends ArrayAdapter {
    private Context context;
    private Warranty warranty;

    public PictureAdapter(Context context, Warranty warranty) {
        super(context, R.layout.fragment_warranty_picture, warranty.getPictures());
        this.context = context;
        this.warranty = warranty;
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
            // TODO holder.iv.setImageResource(R.drawable.ic_notes_picture_not_found);
            Crashlytics.log("Image not found " + picture.getFilename());
            Toast.makeText(getContext(), "Image not found " + picture.getFilename(), Toast.LENGTH_LONG).show();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView dialogImage = new ImageView(context);
                Bitmap image = ImageUtils.getScaledBitmapImage(picture.getFilename(), 0, 0);
                dialogImage.setImageBitmap(image);
                new AlertDialog.Builder(context)
                        .setView(dialogImage)
                        .setPositiveButton(R.string.photo_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(context)
                                        .setMessage(R.string.photo_delete_confirm)
                                        .setPositiveButton(R.string.photo_delete, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                warranty.removePicture(picture);
                                                remove(picture);
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton(R.string.photo_delete_cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // nothing to do
                                            }
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton(R.string.photo_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing to do
                            }
                        })
                        .show();
            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView iv;
    }
}
