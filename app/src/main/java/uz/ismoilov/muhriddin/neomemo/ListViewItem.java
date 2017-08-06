package uz.ismoilov.muhriddin.neomemo;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by ismoi on 8/3/2017.
 */


public class ListViewItem  implements Serializable{
    private Drawable memoImage;
    private String memoDate;
    private String memoText;
    private int id;

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

}
