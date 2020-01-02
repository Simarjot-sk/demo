package com.simarjot.bookwala.helpers;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import com.simarjot.bookwala.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdderUtility {
    private static final String TAG = "imageAdder";
    private List<ImageView> bookViews;
    private List<Uri> bookImageUris;
    private int currentIndex = -1;

    public ImageAdderUtility(List<ImageView> imageViews) {
        bookViews = imageViews;
        bookImageUris = new ArrayList<Uri>();
    }

    public void addImage(Uri imageUri){
        currentIndex++;
        updatePlus();
        bookViews.get(currentIndex).setImageURI(imageUri);
        bookViews.get(currentIndex).setScaleType(ImageView.ScaleType.CENTER_CROP);
        bookImageUris.add(imageUri);
        log();
    }

    private void updatePlus(){
        if(currentIndex < (bookViews.size() - 1)){
            int plusIndex = currentIndex+1;
            bookViews.get(plusIndex).setImageResource(R.drawable.chnage_img);
            bookViews.get(plusIndex).setScaleType(ImageView.ScaleType.CENTER);

            //setting all the images unclickable
            for(ImageView iv: bookViews){
                iv.setClickable(false);
            }
            //setting just the image with plus clickable
            bookViews.get(plusIndex).setClickable(true);
        }else {
            for(ImageView iv: bookViews){
                iv.setClickable(false);
            }
        }
    }

    private void log(){
        if(currentIndex==-1){
            Log.d(TAG, "no images added");
        }else if(currentIndex==0){
            Log.d(TAG, "1 image added");
        }else {
            Log.d(TAG, (currentIndex + 1) + " images added");
        }
    }

    public int getCurrentIndex(){
        return currentIndex;
    }
    public List<Uri> getBookImageUris(){
        return bookImageUris;
    }
}
