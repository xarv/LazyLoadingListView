package com.iwa.harshit.houzify;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class FlickrContentProvider extends ContentProvider {
    private DbHelper dbHelper;
    private String authorities= "com.iwa.harshit.houzify";



    public FlickrContentProvider() {

//        getImages();

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.

        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        long id = 0;
        id = sqlDB.insert(DbHelper.IMG_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse("IMG_TABLE" + "/" + id);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return true;
      }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        long tStart = System.currentTimeMillis();


//        while(!imagesRecieved && System.currentTimeMillis() <= tStart+10000);
//        if(imagesRecieved) {
            Cursor cursor = dbHelper.getImages();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
//        }
//        return null;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        return 0;
    }


    public class DbHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "storage.db";


        //IMAGE TABLE
        public static final String ID = "_id";
        public static final String IMG_TABLE = "IMG_TABLE";
        public static final String IMG_ID = "IMG_ID";
        public static final String IMG_TIMESTAMP = "IMG_TIMESTAMP";
        public static final String IMG_URL = "IMG_URL";

        public final String[] IMG_COL = {"_id", "IMG_ID", "IMG_TIMESTAMP", "IMG_URL"};

        private static final String IMAGE_TABLE_CREATE =
                "CREATE TABLE " + IMG_TABLE + " ( " +
                        ID + " TEXT NOT NULL, " +
                        IMG_ID + " TEXT NOT NULL, " +
                        IMG_TIMESTAMP + " TEXT NOT NULL, " +
                        IMG_URL + " TEXT NOT NULL " +

                        " );";


        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IMAGE_TABLE_CREATE);


        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // upgrade
        }

        public boolean insertImgTable(String id, String imgId,String imgTimeStamp, String imgUrl) {
            try {

                SQLiteDatabase database = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ID, id);
                values.put(IMG_ID, imgId);
                values.put(IMG_URL, imgUrl);
                values.put(IMG_TIMESTAMP, imgTimeStamp);

                Long rowID = database.insertWithOnConflict(IMG_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                if (rowID < 0)
                    return false;
                else
                    return true;
            } catch (Exception e) {
                return false;

            }

        }

        public Cursor getImages() {
            Cursor cursor = getReadableDatabase().rawQuery("select * from IMG_TABLE", null);
            return cursor;
        }

    }
}
