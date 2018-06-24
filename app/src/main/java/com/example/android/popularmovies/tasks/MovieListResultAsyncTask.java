package com.example.android.popularmovies.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.model.PopularMoviesSettings;
import com.example.android.popularmovies.utils.PopularMoviesConstants;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * MovieListResultAsyncTask is an AsyncTask that is used for retrieving a "page" of results from
 * the movie database.
 */
public class MovieListResultAsyncTask extends AsyncTask<PopularMoviesSettings, Void, MovieListResultAsyncTask.Results>
{
  private WeakReference<Context> context;

  private IAsyncTaskCompleteListener<Results> listener;

  public class Results
  {
    /**
     * The page we retrieved.
     */
    public int page;

    public List<MovieListResultObject> movieListResultObjectList;

    Results(int page, List<MovieListResultObject> movieListResultObjectList)
    {
      this.page = page;
      this.movieListResultObjectList = movieListResultObjectList;
    }
  }

  public MovieListResultAsyncTask(Context context, IAsyncTaskCompleteListener<Results> listener)
  {
    this.context = new WeakReference<Context>(context);
    this.listener = listener;
  }

  @Override
  protected MovieListResultAsyncTask.Results doInBackground(PopularMoviesSettings... popularMoviesSettings)
  {
    Uri uri = null;
    String theMovieDatabaseApiKey = PopularMoviesConstants.getTheMovieDatabaseApiKey();
    int page = 1; // TODO: Only get the first page of results for now.

    if(popularMoviesSettings[0].getSortSetting() == PopularMoviesSettings.MOST_POPULAR)
    {
      Context weakContext = context.get();
      if(weakContext != null)
      {
        uri = TheMovieDatabaseUtils.getPopularMoviesUri(weakContext, theMovieDatabaseApiKey, page);
      }
      else
      {
        // the context is invalid
        return null;
      }
    }
    else
    {
      Context weakContext = context.get();
      if(weakContext != null)
      {
        uri = TheMovieDatabaseUtils.getTopRatedMoviesUri(weakContext, theMovieDatabaseApiKey, page);
      }
      else
      {
        // the context is invalid
        return null;
      }
    }

    List<MovieListResultObject> movieListResultObjectList = null;

    try
    {
      Response response = TheMovieDatabaseUtils.queryTheMovieDatabase(uri);

      if(response.code() == 200) // the movie database returns this code on success
      {
        ResponseBody responseBody = response.body();
        if(responseBody != null)
        {
          String responseBodyToString = responseBody.string();

          Context weakContext = context.get();
          if(weakContext != null)
          {
            movieListResultObjectList = TheMovieDatabaseUtils.movieJsonStringToMovieResultList(weakContext, responseBodyToString);
          }
          else
          {
            // the context is invalid
            return null;
          }
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return new MovieListResultAsyncTask.Results(page, movieListResultObjectList);
  }

  @Override
  protected void onPostExecute(MovieListResultAsyncTask.Results results)
  {
    super.onPostExecute(results);
    listener.onTaskComplete(results);
  }
}
