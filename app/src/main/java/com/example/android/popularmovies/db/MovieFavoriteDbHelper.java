package com.example.android.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieFavoriteDbHelper extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "movie_favorite.db";

  private static final int DATABASE_VERSION = 1;

  private static final String SQL_CREATE_MOVIE_FAVORITE_TABLE = "CREATE TABLE " +
          MovieFavoriteContract.MovieFavorites.TABLE_NAME + " (" +
          MovieFavoriteContract.MovieFavorites._ID + " INTEGER PRIMARY KEY," +
          MovieFavoriteContract.MovieFavorites.MOVIE_TITLE + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_URL + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieFavorites.MOVIE_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieFavorites.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieFavorites.MOVIE_USER_RATING + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_IMAGE_DATA + " BLOB NOT NULL" +
          ")";

  private static final String SQL_CREATE_MOVIE_TRAILER_TABLE = "CREATE TABLE " +
          MovieFavoriteContract.MovieTrailers.TABLE_NAME + " (" +
          MovieFavoriteContract.MovieTrailers._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          MovieFavoriteContract.MovieTrailers.TRAILER_CLIP_TITLE  + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieTrailers.TRAILER_YOUTUBE_KEY + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieTrailers.MOVIE_FAVORITE_FOREIGN_KEY  + " INTEGER, " +
          "FOREIGN KEY(" + MovieFavoriteContract.MovieTrailers.MOVIE_FAVORITE_FOREIGN_KEY  + ")" +
          " REFERENCES " + MovieFavoriteContract.MovieFavorites.TABLE_NAME + "(" + MovieFavoriteContract.MovieFavorites._ID + ")" +
          " ON DELETE CASCADE" +
          ")";

  private static final String SQL_CREATE_MOVIE_REVIEW_TABLE = "CREATE TABLE " +
          MovieFavoriteContract.MovieReviews.TABLE_NAME  + " (" +
          MovieFavoriteContract.MovieReviews._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
          MovieFavoriteContract.MovieReviews.REVIEW_AUTHOR  + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieReviews.REVIEW_CONTENT  + " TEXT NOT NULL, " +
          MovieFavoriteContract.MovieReviews.MOVIE_FAVORITE_FOREIGN_KEY  + " INTEGER, " +
          "FOREIGN KEY(" + MovieFavoriteContract.MovieReviews.MOVIE_FAVORITE_FOREIGN_KEY  + ")" +
          " REFERENCES " + MovieFavoriteContract.MovieFavorites.TABLE_NAME + "(" + MovieFavoriteContract.MovieFavorites._ID + ")" +
          " ON DELETE CASCADE" +
          ")";


  public MovieFavoriteDbHelper(Context context)
  {
    super(context, MovieFavoriteDbHelper.DATABASE_NAME, null, MovieFavoriteDbHelper.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    // Create all of the tables
    db.execSQL(MovieFavoriteDbHelper.SQL_CREATE_MOVIE_FAVORITE_TABLE);
    db.execSQL(MovieFavoriteDbHelper.SQL_CREATE_MOVIE_TRAILER_TABLE);
    db.execSQL(MovieFavoriteDbHelper.SQL_CREATE_MOVIE_REVIEW_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    // TODO: really implement upgrade once we have a version number larger than 1 for now we will just drop the database and recreate it.

    db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteContract.MovieFavorites.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteContract.MovieTrailers.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteContract.MovieReviews.TABLE_NAME);
    onCreate(db);
  }
}
