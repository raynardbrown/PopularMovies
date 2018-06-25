package com.example.android.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.db.MovieFavoriteContract;
import com.example.android.popularmovies.db.MovieFavoriteDbHelper;

public class MovieFavoriteContentProvider extends ContentProvider
{
  private MovieFavoriteDbHelper movieFavoriteDbHelper;

  public static final int MOVIE_FAVORITE = 100;
  public static final int MOVIE_FAVORITE_WITH_ID = 101;

  public static final int MOVIE_TRAILER = 200;
  public static final int MOVIE_TRAILER_WITH_ID = 201;

  public static final int MOVIE_REVIEW = 300;
  public static final int MOVIE_REVIEW_WITH_ID = 301;

  private static final UriMatcher uriMatcher = buildUriMatcher();

  private static UriMatcher buildUriMatcher()
  {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Add a matcher for the movie favorites
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieFavorites.TABLE_NAME, MovieFavoriteContentProvider.MOVIE_FAVORITE);

    // Single row in the movie favorites
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieFavorites.TABLE_NAME + "/#", MovieFavoriteContentProvider.MOVIE_FAVORITE_WITH_ID);

    // Add a matcher for the movie trailers
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieTrailers.TABLE_NAME, MovieFavoriteContentProvider.MOVIE_TRAILER);

    // Single row in the movie trailers
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieTrailers.TABLE_NAME + "/#", MovieFavoriteContentProvider.MOVIE_TRAILER_WITH_ID);

    // Add a matcher for the movie reviews
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieReviews.TABLE_NAME, MovieFavoriteContentProvider.MOVIE_REVIEW);

    // Single row in the movie reviews
    uriMatcher.addURI(MovieFavoriteContract.URI_CONTENT_AUTHORITY, MovieFavoriteContract.MovieReviews.TABLE_NAME + "/#", MovieFavoriteContentProvider.MOVIE_REVIEW_WITH_ID);

    return uriMatcher;
  }

  @Override
  public boolean onCreate()
  {
    Context context = getContext();

    movieFavoriteDbHelper = new MovieFavoriteDbHelper(context);

    return true;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
  {
    final SQLiteDatabase db = movieFavoriteDbHelper.getWritableDatabase();

    int match = uriMatcher.match(uri);

    Uri returnUri = null;

    switch(match)
    {
      case MovieFavoriteContentProvider.MOVIE_FAVORITE:
      {
        // insert values into the movie_favorite table
        long id = db.insert(MovieFavoriteContract.MovieFavorites.TABLE_NAME, null, values);

        if(id > 0)
        {
          returnUri = ContentUris.withAppendedId(MovieFavoriteContract.MovieFavorites.CONTENT_URI, id);
        }
        else
        {
          throw new SQLiteException("Failed to insert row into " + uri);
        }

        break;
      }

      case MovieFavoriteContentProvider.MOVIE_TRAILER:
      {
        long id = db.insert(MovieFavoriteContract.MovieTrailers.TABLE_NAME, null, values);

        if(id > 0)
        {
          returnUri = ContentUris.withAppendedId(MovieFavoriteContract.MovieTrailers.CONTENT_URI, id);
        }
        else
        {
          throw new SQLiteException("Failed to insert row into " + uri);
        }

        break;
      }

      case MovieFavoriteContentProvider.MOVIE_REVIEW:
      {
        long id = db.insert(MovieFavoriteContract.MovieReviews.TABLE_NAME, null, values);

        if(id > 0)
        {
          returnUri = ContentUris.withAppendedId(MovieFavoriteContract.MovieReviews.CONTENT_URI, id);
        }
        else
        {
          throw new SQLiteException("Failed to insert row into " + uri);
        }

        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    // notify the content resolver that the uri has changed
    if(getContext() != null)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return returnUri;
  }


  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                      @Nullable String[] selectionArgs, @Nullable String sortOrder)
  {
    final SQLiteDatabase db = movieFavoriteDbHelper.getReadableDatabase();

    int match = uriMatcher.match(uri);

    Cursor returnCursor = null;

    switch(match)
    {
      case MovieFavoriteContentProvider.MOVIE_FAVORITE:
      {
        // return all the rows in the database.
        returnCursor = db.query(MovieFavoriteContract.MovieFavorites.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_FAVORITE_WITH_ID:
      {
        // index 0, is the movie_favorite portion of the URI
        // index 1, is the segment next to movie_favorite
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        returnCursor = db.query(MovieFavoriteContract.MovieFavorites.TABLE_NAME,
                projection,
                tempSelection,
                tempSelectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_TRAILER:
      {
        // return all the rows in the database.
        returnCursor = db.query(MovieFavoriteContract.MovieTrailers.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_TRAILER_WITH_ID:
      {
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        returnCursor = db.query(MovieFavoriteContract.MovieTrailers.TABLE_NAME,
                projection,
                tempSelection,
                tempSelectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_REVIEW:
      {
        // return all the rows in the database.
        returnCursor = db.query(MovieFavoriteContract.MovieReviews.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_REVIEW_WITH_ID:
      {
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        returnCursor = db.query(MovieFavoriteContract.MovieReviews.TABLE_NAME,
                projection,
                tempSelection,
                tempSelectionArgs,
                null,
                null,
                sortOrder);
        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    if(getContext() != null)
    {
      // tell the cursor what content uri the cursor was created for.
      returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
    }

    return returnCursor;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
  {
    final SQLiteDatabase db = movieFavoriteDbHelper.getWritableDatabase();

    int match = uriMatcher.match(uri);

    int numberDeleted = 0;

    switch(match)
    {
      case MovieFavoriteContentProvider.MOVIE_FAVORITE:
      {
        // delete all rows. We must pass "1" in the where clause in order to get a valid value for
        // numberDeleted otherwise 0 is returned.
        numberDeleted = db.delete(MovieFavoriteContract.MovieFavorites.TABLE_NAME, "1", null);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_FAVORITE_WITH_ID:
      {
        // index 0, is the movie_favorite portion of the URI
        // index 1, is the segment next to movie_favorite
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        numberDeleted = db.delete(MovieFavoriteContract.MovieFavorites.TABLE_NAME, tempSelection, tempSelectionArgs);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_TRAILER:
      {
        // delete all rows. We must pass "1" in the where clause in order to get a valid value for
        // numberDeleted otherwise 0 is returned.
        numberDeleted = db.delete(MovieFavoriteContract.MovieTrailers.TABLE_NAME, "1", null);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_TRAILER_WITH_ID:
      {
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        numberDeleted = db.delete(MovieFavoriteContract.MovieTrailers.TABLE_NAME, tempSelection, tempSelectionArgs);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_REVIEW:
      {
        // delete all rows. We must pass "1" in the where clause in order to get a valid value for
        // numberDeleted otherwise 0 is returned.
        numberDeleted = db.delete(MovieFavoriteContract.MovieReviews.TABLE_NAME, "1", null);
        break;
      }

      case MovieFavoriteContentProvider.MOVIE_REVIEW_WITH_ID:
      {
        String id = uri.getPathSegments().get(1);

        String tempSelection = "_id=?"; // column name
        String[] tempSelectionArgs = new String[]{id}; // column value

        numberDeleted = db.delete(MovieFavoriteContract.MovieReviews.TABLE_NAME, tempSelection, tempSelectionArgs);
        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    // notify the content resolver that the uri has changed
    if(getContext() != null && numberDeleted > 0)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return numberDeleted;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                    @Nullable String[] selectionArgs)
  {
    // We are not using update in this app
    throw new UnsupportedOperationException("Unknown uri: " + uri);
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri)
  {
    // We are not using getType in this app
    throw new UnsupportedOperationException("Unknown uri: " + uri);
  }
}
