package com.bricenangue.insyconn.ki_ki;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;


/**
 * Created by bricenangue on 12/01/2017.
 */

public class MyImageRotator {
    private static final String TAG = MyImageRotator.class.getSimpleName();

    private MyImageRotator() {
    }

    public static int getRotation(Context context, Uri imageUri, boolean fromCamera) {
        int rotation;
        if(fromCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }

        Log.i(TAG, "Image rotation: " + rotation);
        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        short rotate = 0;

        try {
            context.getContentResolver().notifyChange(imageFile, (ContentObserver)null);
            ExifInterface e = new ExifInterface(imageFile.getPath());
            int orientation = e.getAttributeInt("Orientation", 1);
            switch(orientation) {
                case 3:
                    rotate = 180;
                    break;
                case 6:
                    rotate = 90;
                    break;
                case 8:
                    rotate = 270;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return rotate;
    }

    private static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = new String[]{"orientation"};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(imageUri, columns, (String)null, (String[])null, (String)null);
            if(cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception var9) {
            ;
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return result;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if(bitmap != null && degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate((float)degrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
