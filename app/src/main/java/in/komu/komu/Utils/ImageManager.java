package in.komu.komu.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {

    private static final String TAG = "ImageManager";
    public static final int IMAGE_SAVE_QUALITY = 90;
    public static String mAppend = "file:/";

    public static Bitmap getBitmapFromImageURL(String imgUrl){

        String str = imgUrl.replace("file:/", "");
        File imageFile = new File(str);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try{
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
//              bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

//            bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap: ArithmeticException: " + e.getMessage() );
        }finally {
            try{
                fis.close();
            }catch (IOException e){
                Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "getBitmapFromImageURL: Null Pointer Exception" + e.getMessage() );
            }
        }
        return bitmap;
    }

    /**
     * return byte array from a bitmap
     * quality is greater than 0 but less than 100
     * @param bm
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        return stream.toByteArray();
    }
}






















