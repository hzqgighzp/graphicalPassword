package com.example.graphicalpasswordapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class PasswordFactory {

    protected static String getImgB64(Bitmap btm){

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        btm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        //btm.recycle();
        byte[] byteArray = bao.toByteArray();
        String imgEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imgEncoded;
    }

    protected static Bitmap decodeFromBase64(String str){
        byte[] decoded = Base64.decode(str, Base64.DEFAULT);
        Bitmap decodedPassword = BitmapFactory.decodeByteArray(decoded,0,decoded.length);

        return decodedPassword;
    }

    protected static Bitmap takeScreenShot(PaintView view){
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.buildDrawingCache();
        if(view.getDrawingCache() == null) return null;
        Bitmap imgSaved = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return imgSaved;
    }
}
