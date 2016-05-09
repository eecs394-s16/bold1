package com.collinbarnwell.bold1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jerry on 5/9/2016.
 * This is the class for
 */
public final class UtilClass{


    public static final String UserInfoFile = "User_Info_File";
    public static final String[] UserInfoStrings= {"user_ID","user_email","user_institution_affiliation","user_respective_physician","user_first_name"
            ,"user_last_name","user_age","user_dob"};
    public static final int[] UserInfoIds={R.id.user_id,R.id.user_email,R.id.user_institution_affiliation,R.id.user_respective_physician,
            R.id.user_first_name,R.id.user_last_name,R.id.user_age,R.id.user_dob};

    // Must have a context. otherwise just openFileInput does not work
    public String loadJSONStringFromFile(Context context, String inputFileName) {
        String json = null;
        try {
            InputStream is = context.openFileInput(inputFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
