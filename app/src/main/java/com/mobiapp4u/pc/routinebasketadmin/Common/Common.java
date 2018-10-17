package com.mobiapp4u.pc.routinebasketadmin.Common;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.mobiapp4u.pc.routinebasketadmin.Modal.Request;
import com.mobiapp4u.pc.routinebasketadmin.Modal.User;
import com.mobiapp4u.pc.routinebasketadmin.Remote.APIService;
import com.mobiapp4u.pc.routinebasketadmin.Remote.FCMRetrofitClient;
import com.mobiapp4u.pc.routinebasketadmin.Remote.IGeoCoordinates;
import com.mobiapp4u.pc.routinebasketadmin.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static String PHONE_TEXT = "userPhone";
    public static String TOPIC_NEWS = "News";
    public static Request currentRequest;
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String DETAILS = "details";
    public static final String baseUrl = "https://maps.googleapis.com";
    private static final String FCCM_URL = "https://fcm.googleapis.com/";
    public static String SHIPPER_TABLE = "Shippers";
    public static String ORDER_NEED_SHIPPER = "OrdersNeedShipper";

    public static APIService getFCMService(){
        return FCMRetrofitClient.getClient(FCCM_URL).create(APIService.class);
    }

    public static String converCodeToStatus(String status){
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On My way";
        else if(status.equals("2"))
            return "Shipping";
        else
            return "Shipped";

    }
    public static IGeoCoordinates getGeoCodeServices(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatric = new Matrix();
        scaleMatric.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatric);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
    public static String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd:MM:yyyy HH:mm",calendar).toString());
        return date.toString();
    }

}
