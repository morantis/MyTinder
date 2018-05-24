package Models;

/**
 * Created by Davin12x on 16-06-26.
 */

import android.graphics.Bitmap;

import android.graphics.Bitmap;

/**
 * Created by Davin12x on 16-05-22.
 */
public class GridItem {

    private String image;
    private String name;
    private String location;
    private String userId;
    private String activity;


    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
