package com.app.realtimechatapp.ultilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Ultil {
    public static Bitmap getBitmapFromBase64Text(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
