package com.collinbarnwell.bold1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            // First make sure the file exists
            //the following statement will create the file if not exists, and will leave if file exists:
            // Do not use Mode_private because that will overwrite the file.
            OutputStream io = context.openFileOutput(inputFileName,Context.MODE_APPEND);
            io.close();
            // Now the file should be created
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

    public JSONObject loadJSONFromFile(Context context, String inputFileName){
        try {
            String saved_user_info_string = loadJSONStringFromFile(context, inputFileName);
            JSONObject saved_user_info;
            // If the file doesn't have anything in the file, create a new JSON object with empty
            // strings in it, so that there will be no exception thrown when we read the data.
            if (saved_user_info_string.isEmpty()) {
                JSONObject user_info = new JSONObject();
                try {
                    for (int i=0; i<UserInfoStrings.length;i++) {
                        user_info.put(UserInfoStrings[i], "");
                    }
                    saved_user_info_string = user_info.toString();
                } catch (Exception e) {
                    Toast.makeText(context,"Some error occurred when loading JSON from file.",Toast.LENGTH_LONG).show();
                }
                saved_user_info = new JSONObject(saved_user_info_string);
            } else {
                saved_user_info = new JSONObject(saved_user_info_string);
        }
            return saved_user_info;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
