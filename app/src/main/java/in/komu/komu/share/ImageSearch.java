package in.komu.komu.share;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImageSearch {

    private String mSelectedImage;
    private ArrayList<String> listOfAllImages;


    /**
     * Getting All Images Path.
     *
     * @param activity
     *            the activity
     * @return ArrayList with images Path
     */
    public ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
//            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

//            String[] projection = { MediaStore.MediaColumns.DATA,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

//        uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        uri = MediaStore.Files.getContentUri("external");

        String orderBy = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        CursorLoader cursorLoader = new CursorLoader(
                activity,
                uri,
                projection,
                selection,
                null, // Selection args (none).
                orderBy
        );


        cursor = cursorLoader.loadInBackground();



        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

        //column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

//            column_index_folder_name = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        //        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";


//        cursor = activity.getContentResolver().query(uri, projection, null,
//                null, orderBy);


        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }


        return listOfAllImages;

    }


}
