package Models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Davin12x on 16-07-07.
 */
public class SaveUserId {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Context context;

    public SaveUserId(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void saveUserId(String id) {
        editor.putString("id",id).commit();
    }

    public String getUserId() {

        String userId = sharedpreferences.getString("id",null);
        return  userId;
    }

    public int getUserDistance() {

        int distance = sharedpreferences.getInt("distance",0);
        return  distance;
    }

    public void saveDistance(int id) {
        editor.putInt("distance",id).commit();
    }






}
