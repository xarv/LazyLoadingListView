package com.iwa.harshit.houzify;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final  int  URL_LOADER = 1;
    private String authorities= "com.iwa.harshit.houzify";
    private String[] mProjection = {"_id","IMG_ID","IMG_NAME","IMG_DATA"};
    private Uri imgTable = Uri.parse("content://" + authorities+"/content"  + "/IMG_TABLE");
    private boolean imagesRecieved = false;
    private ListView imgListView;

    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("http://private-b2ea3-houzify.apiary-mock.com/")
            .build();
    ServiceProvider sp = restAdapter.create(ServiceProvider.class);
    MatrixCursor matrixCursor = new MatrixCursor(mProjection);
    ImageAdapter imgAdapter = new ImageAdapter(this,matrixCursor,false);
    private int savedScrollPosition;
    private boolean scrollListenerNotSet = true;
    private int scrollPos;
    private int lastItemCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgListView = (ListView) findViewById(R.id.imglistView);
        getImages();
        getLoaderManager().initLoader(URL_LOADER, null, this);
        if(scrollListenerNotSet) {
            imgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if ((firstVisibleItem + visibleItemCount) >= totalItemCount && totalItemCount != 0 && totalItemCount != lastItemCount) {
                            getImages();
                        lastItemCount = totalItemCount;
                    }
                }
            });
            scrollListenerNotSet = false;
        }


    }
    public void getImages(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        //progressDialog.show();
        this.setProgressBarIndeterminate(true);

        sp.getImages(new Callback<ArrayList<Image>>() {
            @Override
            public void success(ArrayList<Image> images, Response response) {
                Log.wtf("Content Provider", images.get(0).getUrl());

                for (int i = 0; i < images.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(FlickrContentProvider.DbHelper.ID,String.valueOf(i + System.currentTimeMillis() / 60 * 1000));
                    values.put(FlickrContentProvider.DbHelper.IMG_ID,images.get(i).getId());
                    values.put(FlickrContentProvider.DbHelper.IMG_TIMESTAMP,images.get(i).getTimeStamp());
                    values.put(FlickrContentProvider.DbHelper.IMG_URL, images.get(i).getUrl());
                    getContentResolver().insert(imgTable,values);


                }

                imagesRecieved = true;
                //getLoaderManager().getLoader(URL_LOADER).forceLoad();
                MainActivity.this.setProgressBarIndeterminate(false);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.wtf("Content Provider", "error");
                MainActivity.this.setProgressBarIndeterminate(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        MainActivity.this,   // Parent activity context
                        imgTable,        // Table to query
                        mProjection,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

        scrollPos = imgAdapter.getCount() ;
        imgAdapter.swapCursor(data);
        imgListView.setAdapter(imgAdapter);
        imgListView.post(new Runnable() {

                             @Override
                             public void run() {
                                 imgListView.setSelection( scrollPos- 1);

                             }
                         }
            );

        }

        @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }


}