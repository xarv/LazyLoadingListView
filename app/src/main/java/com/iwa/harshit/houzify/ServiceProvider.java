package com.iwa.harshit.houzify;

/**
 * Created by harshit on 6/22/15.
 */

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
public interface ServiceProvider {

    @GET("/getimages")
    void getImages(Callback<ArrayList<Image>> callback);
}
