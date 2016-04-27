package com.example.moamen.moviesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Moamen on 4/24/2016.
 */
public class FavouriteContract {
    public abstract class FavouriteMovies implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_MOVIE_ID = "movieid";
    }
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_MOVIES =
            "CREATE TABLE " + FavouriteMovies.TABLE_NAME + " (" + FavouriteMovies._ID + " INTEGER PRIMARY KEY," + FavouriteMovies.COLUMN_NAME_MOVIE_ID + TEXT_TYPE + " )";

    private static final String SQL_DELETE_MOVIES =
            "DROP TABLE IF EXISTS " + FavouriteMovies.TABLE_NAME;

    public class FavouriteDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FavouriteDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_MOVIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_MOVIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
