package uz.ismoilov.muhriddin.neomemo;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by ismoi on 8/3/2017.
 */

public class ListViewItem  implements Serializable{
    private Drawable memoImage;
    private String LocalPath;
    private String FirebasePath;
    private String LastEdited;
    private String isUpdated;
    private  String ImageName;
    private ImageUpload img;
    private String memoDate;
    private String memoText;
    private int id;

    public ImageUpload getImg() {
        return img;
    }

    public void setImg(ImageUpload img) {
        this.img = img;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ListViewItem() {
    }

    public ListViewItem(Drawable memoImage, String memoDate, String memoText, int id) {
        this.memoImage = memoImage;
        this.memoDate = memoDate;
        this.memoText = memoText;
        this.id=id;
    }


    public Drawable getMemoImage() {
        return memoImage;
    }

    public void setMemoImage(Drawable memoImage) {
        this.memoImage = memoImage;
    }

    public String getMemoDate() {
        return memoDate;
    }

    public void setMemoDate(String memoDate) {
        this.memoDate = memoDate;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public String getLocalPath() {
        return LocalPath;
    }

    public void setLocalPath(String localPath) {
        LocalPath = localPath;
    }

    public String getFirebasePath() {
        return FirebasePath;
    }

    public void setFirebasePath(String firebasePath) {
        FirebasePath = firebasePath;
    }

    public String getLastEdited() {
        return LastEdited;
    }

    public void setLastEdited(String lastEdited) {
        LastEdited = lastEdited;
    }

    public String getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(String isUpdated) {
        this.isUpdated = isUpdated;
    }
}
