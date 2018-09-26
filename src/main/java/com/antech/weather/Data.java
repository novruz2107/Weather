package com.antech.weather;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Novruz Engineer on 9/13/2018.
 */

public class Data {

    String today = String.valueOf(Calendar.getInstance().getTime()).split(" ")[0];

    public void processData(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            double cnt = (jsonObject.getDouble("cnt"));
            for(int i = 0; i < cnt; i++){
                JSONObject jsonObject0 = jsonObject.getJSONArray("list").getJSONObject(i);
                Date date = new Date((long) jsonObject0.getDouble("dt") *1000);
                if(today.equals(String.valueOf(date).split(" ")[0])){
                    continue;
                }else{

                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
