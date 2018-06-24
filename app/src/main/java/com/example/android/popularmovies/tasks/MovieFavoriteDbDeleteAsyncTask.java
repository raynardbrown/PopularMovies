package com.example.android.popularmovies.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.db.MovieFavoriteContract;

import java.lang.ref.WeakReference;

public class MovieFavoriteDbDeleteAsyncTask extends AsyncTask<MovieFavoriteDbDeleteAsyncTask.Param, Void, MovieFavoriteDbDeleteAsyncTask.Result>
{
  private WeakReference<Context> context;
  private IAsyncTaskCompleteListener<Result> listener;

  public static class Param
  {
    /**
     * The id of the movie to delete
     */
    private int movieId;

    public Param(int movieId)
    {
      this.movieId = movieId;
    }
  }

  public class Result
  {
    public int numberRowsDeleted;

    Result(int numberRowsDeleted)
    {
      this.numberRowsDeleted = numberRowsDeleted;
    }
  }

  public MovieFavoriteDbDeleteAsyncTask(Context context, IAsyncTaskCompleteListener<Result> listener)
  {
    this.context = new WeakReference<Context>(context);
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
          // delete one row

          // Make sure to use the correct URI
          Uri uri = MovieFavoriteContract.MovieFavorites.CONTENT_URI;
          uri = uri.buildUpon().appendPath(Integer.toString(params[0].movieId)).build();

          return new Result(weakContext.getContentResolver().delete(uri,
                  null,
                  null));
        }
        else
        {
          // delete everything
          return new Result(weakContext.getContentResolver().delete(MovieFavoriteContract.MovieFavorites.CONTENT_URI,
                  null,
                  null));
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
}
