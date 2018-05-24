package Models;

/**
 * Created by Davin12x on 16-06-25.
 */
public class CreateUser {
    String userName;
    String imageUrl;
    String activityName;
    String location;

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getLocatin() {
        return location;
    }

    public CreateUser(String userName, String imageUrl, String activityName, String location) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.activityName = activityName;
        this.location = location;

    }

}
