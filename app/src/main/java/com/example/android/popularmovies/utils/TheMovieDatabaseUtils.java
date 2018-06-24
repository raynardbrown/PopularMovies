package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.model.MovieReviewResultObject;
import com.example.android.popularmovies.model.MovieVideoResultObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TheMovieDatabaseUtils
{
  public static Uri getPopularMoviesUri(Context context, String apiKey, int page)
  {
    return getMoviesUriHelper(context, apiKey, page, context.getString(R.string.tmdb_movies_api_get_popular_path));
  }

  public static Uri getTopRatedMoviesUri(Context context, String apiKey, int page)
  {
    return getMoviesUriHelper(context, apiKey, page, context.getString(R.string.tmdb_movies_api_get_top_rated_path));
  }

  public static Uri getVideosMoviesUri(Context context, String apiKey, int page, int id)
  {
    return getMoviesUriWithIdHelper(context, apiKey, page, id, context.getString(R.string.tmdb_movies_api_get_videos_path));
  }

  public static Uri getReviewsMoviesUri(Context context, String apiKey, int page, int id)
  {
    return getMoviesUriWithIdHelper(context, apiKey, page, id, context.getString(R.string.tmdb_movies_api_get_reviews_path));
  }

  private static Uri getMoviesUriHelper(Context context, String apiKey, int page, String path)
  {
    String pageToString = Integer.toString(page);

    return Uri.parse(context.getString(R.string.tmdb_movies_api_base_url)).buildUpon()
            .appendPath(path)
            .appendQueryParameter(context.getString(R.string.tmdb_api_key_url_parameter), apiKey)
            .appendQueryParameter(context.getString(R.string.tmdb_page_url_parameter), pageToString)
            .build();
  }

  private static Uri getMoviesUriWithIdHelper(Context context, String apiKey, int page, int id, String path)
  {
    String idToString = Integer.toString(id);
    String pageToString = Integer.toString(page);

    return Uri.parse(context.getString(R.string.tmdb_movies_api_base_url)).buildUpon()
            .appendPath(idToString)
            .appendPath(path)
            .appendQueryParameter(context.getString(R.string.tmdb_api_key_url_parameter), apiKey)
            .appendQueryParameter(context.getString(R.string.tmdb_page_url_parameter), pageToString)
            .build();
  }

  public static Response queryTheMovieDatabase(Uri queryUri) throws IOException
  {
    OkHttpClient okHttpClient = new OkHttpClient();

    Request request = new Request.Builder()
            .url(queryUri.toString())
            .build();

    return okHttpClient.newCall(request).execute();
  }

  public static Uri getMoviePosterUriFromPath(Context context, String posterPath)
  {
    return Uri.parse(context.getString(R.string.tmdb_configuration_base_url))
            .buildUpon()
            .appendPath(context.getString(R.string.tmdb_configuration_image_size))
            .appendEncodedPath(posterPath) // because the poster path has a "/" that we don't want encoded
            .build();
  }

  public static List<MovieListResultObject> movieJsonStringToMovieResultList(Context context, String json)
  {
    try
    {
      JSONObject rootJsonObject = new JSONObject(json);

      JSONArray resultsArray = rootJsonObject.getJSONArray(context.getString(R.string.tmdb_json_movie_results_object_results_array));

      List<MovieListResultObject> movieListResultObjectList = new ArrayList<MovieListResultObject>();

      for(int i = 0; i < resultsArray.length(); ++i)
      {
        JSONObject resultsObject = resultsArray.getJSONObject(i);

        String posterPath = resultsObject.getString(context.getString(R.string.tmdb_json_movie_results_object_poster_path_string));

        String plotSynopsis = resultsObject.getString(context.getString(R.string.tmdb_json_movie_results_object_overview_string));

        String releaseDate = resultsObject.getString(context.getString(R.string.tmdb_json_movie_results_object_release_date_string));

        String originalTitle = resultsObject.getString(context.getString(R.string.tmdb_json_movie_results_object_original_title_string));

        String userRating = resultsObject.getString(context.getString(R.string.tmdb_json_movie_results_object_vote_average_floating_point));

        int id = resultsObject.getInt(context.getString(R.string.tmdb_json_movie_results_object_id_int));

        movieListResultObjectList.add(new MovieListResultObject(posterPath, plotSynopsis, releaseDate, originalTitle, userRating, id));
      }

      return movieListResultObjectList;
    }
    catch(JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static List<MovieVideoResultObject> movieVideoJsonStringToMovieVideoResultList(Context context, String json, int id)
  {
    try
    {
      JSONObject rootJsonObject = new JSONObject(json);

      JSONArray resultsArray = rootJsonObject.getJSONArray(context.getString(R.string.tmdb_json_movie_videos_results_array));

      List<MovieVideoResultObject> movieVideoResultObjectList = new ArrayList<MovieVideoResultObject>();

      for(int i = 0; i < resultsArray.length(); ++i)
      {
        JSONObject resultsObject = resultsArray.getJSONObject(i);

        String videoClipName = resultsObject.getString(context.getString(R.string.tmdb_json_movie_videos_name_string));

        String key = resultsObject.getString(context.getString(R.string.tmdb_json_movie_videos_key_string));

        movieVideoResultObjectList.add(new MovieVideoResultObject(id, videoClipName, key));
      }

      return movieVideoResultObjectList;
    }
    catch(JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static List<MovieReviewResultObject> movieReviewJsonStringToMovieReviewResultList(Context context, String json, int id)
  {
    try
    {
      JSONObject rootJsonObject = new JSONObject(json);

      JSONArray resultsArray = rootJsonObject.getJSONArray(context.getString(R.string.tmdb_json_movie_reviews_results_array));

      List<MovieReviewResultObject> movieReviewResultObjectList = new ArrayList<MovieReviewResultObject>();

      for(int i = 0; i < resultsArray.length(); ++i)
      {
        JSONObject resultsObject = resultsArray.getJSONObject(i);

        String author = resultsObject.getString(context.getString(R.string.tmdb_json_movie_reviews_author_string));

        String content = resultsObject.getString(context.getString(R.string.tmdb_json_movie_reviews_content_string));

        movieReviewResultObjectList.add(new MovieReviewResultObject(id, author, content));
      }

      return movieReviewResultObjectList;
    }
    catch(JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
