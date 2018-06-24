package com.example.android.popularmovies.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.model.MovieVideoResultObject;
import com.example.android.popularmovies.utils.PopularMoviesConstants;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class MovieTrailerResultAsyncTask extends AsyncTask<Integer, Void, MovieTrailerResultAsyncTask.Result>
{
  private WeakReference<Context> context;
  private IAsyncTaskCompleteListener<Result> listener;

  public class Result
  {
    public List<MovieVideoResultObject> movieVideoResultObjectList;

    Result(List<MovieVideoResultObject> movieVideoResultObjectList)
    {
      this.movieVideoResultObjectList = movieVideoResultObjectList;
    }
  }

  public MovieTrailerResultAsyncTask(Context context, IAsyncTaskCompleteListener<Result> listener)
  {
    this.context = new WeakReference<Context>(context);
    this.listener = listener;
  }

  @Override
  protected Result doInBackground(Integer... movieIds)
  {
    String theMovieDatabaseApiKey = PopularMoviesConstants.getTheMovieDatabaseApiKey();
    int page = 1; // TODO: Only get the first page of results for now.

    List<MovieVideoResultObject> movieVideoResultObjectList = null;

    Context weakContext = context.get();
    if(weakContext != null)
    {
      Uri uri = TheMovieDatabaseUtils.getVideosMoviesUri(weakContext, theMovieDatabaseApiKey, page, movieIds[0]);

      try
      {
        Response response = TheMovieDatabaseUtils.queryTheMovieDatabase(uri);

        if(response.code() == 200) // the movie database returns this code on success
        {
          ResponseBody responseBody = response.body();

          if(responseBody != null)
          {
            String responseBodyToString = responseBody.string();

            Context weakContextJson = context.get();
            if(weakContextJson != null)
            {
              movieVideoResultObjectList = TheMovieDatabaseUtils.movieVideoJsonStringToMovieVideoResultList(weakContextJson, responseBodyToString, movieIds[0]);
            }
            else
            {
              return null;
            }
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    return new MovieTrailerResultAsyncTask.Result(movieVideoResultObjectList);
  }

  @Override
  protected void onPostExecute(Result result)
  {
    super.onPostExecute(result);
    listener.onTaskComplete(result);
  }
}
