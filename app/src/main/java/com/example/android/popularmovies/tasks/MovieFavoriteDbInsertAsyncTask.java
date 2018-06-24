package com.example.android.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.db.MovieFavoriteContract;
import com.example.android.popularmovies.model.MovieFavoriteObject;
import com.example.android.popularmovies.model.MovieReviewResultObject;
import com.example.android.popularmovies.model.MovieVideoResultObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class MovieFavoriteDbInsertAsyncTask extends AsyncTask<MovieFavoriteObject, Void, MovieFavoriteDbInsertAsyncTask.Result>
{
  private WeakReference<Context> context;
  private IAsyncTaskCompleteListener<MovieFavoriteDbInsertAsyncTask.Result> listener;

  public class Result
  {
    public Uri movieFavoriteUri;
    public Uri movieTrailerUri;
    public Uri movieReviewUri;

    Result(Uri movieFavoriteUri, Uri movieTrailerUri, Uri movieReviewUri)
    {
      this.movieFavoriteUri = movieFavoriteUri;
      this.movieTrailerUri = movieTrailerUri;
      this.movieReviewUri = movieReviewUri;
    }
  }

  public MovieFavoriteDbInsertAsyncTask(Context context, IAsyncTaskCompleteListener<MovieFavoriteDbInsertAsyncTask.Result> listener)
  {
    this.context = new WeakReference<Context>(context);
    this.listener = listener;
  }

  @Override
  protected Result doInBackground(MovieFavoriteObject... movieFavoriteObjects)
  {
    try
    {
      Context weakContext = context.get();

      if(weakContext != null)
      {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieFavoriteContract.MovieFavorites._ID, movieFavoriteObjects[0].getMovieListResultObject().getId());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_TITLE, movieFavoriteObjects[0].getMovieListResultObject().getOriginalTitle());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_PLOT_SYNOPSIS, movieFavoriteObjects[0].getMovieListResultObject().getPlotSynopsis());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_URL, movieFavoriteObjects[0].getMovieListResultObject().getPosterPath());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_RELEASE_DATE, movieFavoriteObjects[0].getMovieListResultObject().getReleaseDate());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_USER_RATING, movieFavoriteObjects[0].getMovieListResultObject().getUserRating());
        contentValues.put(MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_IMAGE_DATA, movieFavoriteObjects[0].getMoviePosterImageData());

        Uri movieFavoriteUri = weakContext.getContentResolver().insert(MovieFavoriteContract.MovieFavorites.CONTENT_URI, contentValues);

        // Insert trailers
        List<MovieVideoResultObject> movieVideoResultObjectList = movieFavoriteObjects[0].getMovieVideoResultObjectList();

        Uri movieTrailerUri = null;

        for(int i = 0; i < movieVideoResultObjectList.size(); ++i)
        {
          ContentValues contentValuesTemp = new ContentValues();

          contentValuesTemp.put(MovieFavoriteContract.MovieTrailers.MOVIE_FAVORITE_FOREIGN_KEY, movieVideoResultObjectList.get(i).getId());
          contentValuesTemp.put(MovieFavoriteContract.MovieTrailers.TRAILER_CLIP_TITLE, movieVideoResultObjectList.get(i).getVideoClipName());
          contentValuesTemp.put(MovieFavoriteContract.MovieTrailers.TRAILER_YOUTUBE_KEY, movieVideoResultObjectList.get(i).getKey());

          movieTrailerUri = weakContext.getContentResolver().insert(MovieFavoriteContract.MovieTrailers.CONTENT_URI, contentValuesTemp);
        }

        // Insert reviews
        List<MovieReviewResultObject> movieReviewResultObjectList = movieFavoriteObjects[0].getMovieReviewResultObjectList();

        Uri movieReviewUri = null;

        for(int i = 0; i < movieReviewResultObjectList.size(); ++i)
        {
          ContentValues contentValuesTemp = new ContentValues();
          contentValuesTemp.put(MovieFavoriteContract.MovieReviews.MOVIE_FAVORITE_FOREIGN_KEY, movieReviewResultObjectList.get(i).getId());
          contentValuesTemp.put(MovieFavoriteContract.MovieReviews.REVIEW_AUTHOR, movieReviewResultObjectList.get(i).getAuthor());
          contentValuesTemp.put(MovieFavoriteContract.MovieReviews.REVIEW_CONTENT, movieReviewResultObjectList.get(i).getReviewContent());

          movieReviewUri = weakContext.getContentResolver().insert(MovieFavoriteContract.MovieReviews.CONTENT_URI, contentValuesTemp);
        }

        return new Result(movieFavoriteUri, movieTrailerUri, movieReviewUri);
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
