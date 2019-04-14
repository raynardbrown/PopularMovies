package com.example.android.popularmovies.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.db.MovieFavoriteContract;

import java.lang.ref.WeakReference;

public class MovieFavoriteDbQueryAsyncTask extends AsyncTask<MovieFavoriteDbQueryAsyncTask.Param, Void, MovieFavoriteDbQueryAsyncTask.Result>
{
  private WeakReference<Context> context;
  private IAsyncTaskCompleteListener<Result> listener;

  public static class Param
  {
    /**
     * The id of the movie to query.
     */
    private Integer movieId;

    private Object extraData;

    /**
     * Parameter object for the MovieFavoriteDbQueryAsyncTask class. If the specified movieId is
     * null then all of the rows in the favorite database are returned. Otherwise only row with the
     * specified movieId are returned.
     *
     * @param movieId all rows specified by this id.
     *
     * @param extraData extra data that is passed along to the results object.
     */
    public Param(Integer movieId, Object extraData)
    {
      this.movieId = movieId;
      this.extraData = extraData;
    }
  }

  public class Result
  {
    public Cursor movieFavoriteCursor;
    public Cursor movieTrailerCursor;
    public Cursor movieReviewCursor;

    public Object extraData;

    Result(Cursor movieFavoriteCursor,
           Cursor movieTrailerCursor,
           Cursor movieReviewCursor,
           Object extraData)
    {
      this.movieFavoriteCursor = movieFavoriteCursor;
      this.movieTrailerCursor = movieTrailerCursor;
      this.movieReviewCursor = movieReviewCursor;
      this.extraData = extraData;
    }
  }

  public MovieFavoriteDbQueryAsyncTask(Context context, IAsyncTaskCompleteListener<Result> listener)
  {
    this.context = new WeakReference<>(context);
    this.listener = listener;
  }

  @Override
  protected Result doInBackground(Param... params)
  {
    try
    {
      Context weakContext = context.get();

      if(weakContext != null)
      {
        if(params.length > 0)
        {
          String movieId = null;

          if(params[0].movieId != null)
          {
            // query one item
            movieId = Integer.toString(params[0].movieId);
          }

          // Get the movie favorite cursor
          Cursor movieFavoriteCursor = getCursor(weakContext,
                  MovieFavoriteContract.MovieFavorites.CONTENT_URI,
                  movieId);

          // Get the movie favorite trailer cursor
          Cursor movieTrailerCursor = getCursor(weakContext,
                  MovieFavoriteContract.MovieTrailers.CONTENT_URI,
                  movieId);

          // Get the movie favorite review cursor
          Cursor movieReviewCursor = getCursor(weakContext,
                  MovieFavoriteContract.MovieReviews.CONTENT_URI,
                  movieId);

          return new Result(movieFavoriteCursor,
                  movieTrailerCursor,
                  movieReviewCursor,
                  params[0].extraData);
        }
        else
        {
          // should throw exception since params are required
          return null;
        }
      }
      else
      {
        return null;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void onPostExecute(Result result)
  {
    super.onPostExecute(result);
    listener.onTaskComplete(result);
  }

  private Cursor getCursor(Context context, Uri contentUri, String movieId)
  {
    if(movieId != null)
    {
      contentUri = contentUri.buildUpon().appendPath(movieId).build();
    }

    return context.getContentResolver().query(contentUri,
            null,
            null,
            null,
            null);
  }
}
