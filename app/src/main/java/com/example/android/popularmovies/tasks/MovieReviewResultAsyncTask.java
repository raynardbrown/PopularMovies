package com.example.android.popularmovies.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.model.MovieReviewResultObject;
import com.example.android.popularmovies.utils.PopularMoviesConstants;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class MovieReviewResultAsyncTask extends AsyncTask<Integer, Void, MovieReviewResultAsyncTask.Result>
{
  private static final String TAG = MovieReviewResultAsyncTask.class.getSimpleName();

  private WeakReference<Context> context;
  private IAsyncTaskCompleteListener<Result> listener;

  public class Result
  {
    public List<MovieReviewResultObject> movieReviewResultObjectList;

    Result(List<MovieReviewResultObject> movieReviewResultObjectList)
    {
      this.movieReviewResultObjectList = movieReviewResultObjectList;
    }
  }

  public MovieReviewResultAsyncTask(Context context, IAsyncTaskCompleteListener<Result> listener)
  {
    this.context = new WeakReference<Context>(context);
    this.listener = listener;
  }

  @Override
  protected Result doInBackground(Integer... movieIds)
  {
    Log.i(MovieReviewResultAsyncTask.TAG, "executing the trailer background task on movie id: " + movieIds[0]);

    String theMovieDatabaseApiKey = PopularMoviesConstants.getTheMovieDatabaseApiKey();
    int page = 1; // TODO: Only get the first page of results for now.

    List<MovieReviewResultObject> movieReviewResultObjectList = null;

    Context weakContext = context.get();
    if(weakContext != null)
    {
      Log.i(MovieReviewResultAsyncTask.TAG, "The context is valid before getting the videos URI");
      Uri uri = TheMovieDatabaseUtils.getReviewsMoviesUri(weakContext, theMovieDatabaseApiKey, page, movieIds[0]);

      Log.i(MovieReviewResultAsyncTask.TAG, "URI: " + uri.toString());

      try
      {
        Response response = TheMovieDatabaseUtils.queryTheMovieDatabase(uri);

        if(response.code() == 200) // the movie database returns this code on success
        {
          Log.i(MovieReviewResultAsyncTask.TAG, "The response is good");
          ResponseBody responseBody = response.body();

          if(responseBody != null)
          {
            Log.i(MovieReviewResultAsyncTask.TAG, "The response body is good");
            String responseBodyToString = responseBody.string();

            Context weakContextJson = context.get();
            if(weakContextJson != null)
            {
              Log.i(MovieReviewResultAsyncTask.TAG, "The context is valid before parsing the reviews json");
              Log.i(MovieReviewResultAsyncTask.TAG, "JSON: \n" + responseBodyToString);
              movieReviewResultObjectList = TheMovieDatabaseUtils.movieReviewJsonStringToMovieReviewResultList(weakContext, responseBodyToString, movieIds[0]);

              Log.i(MovieReviewResultAsyncTask.TAG, movieReviewResultObjectList != null ? "got a valid result list" : "result list is null");
            }
            else
            {
              return null;
            }
          }
        }
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    return new MovieReviewResultAsyncTask.Result(movieReviewResultObjectList);
  }

  @Override
  protected void onPostExecute(Result result)
  {
    super.onPostExecute(result);
    listener.onTaskComplete(result);
  }
}
