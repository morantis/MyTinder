package Models;

/**
 * Created by Davin12x on 16-07-09.
 */
public class BookmarkModel {
    private String name;
    private String address;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BookmarkModel() {


    }
}
