package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.MovieListResultObject;

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

  private static Uri getMoviesUriHelper(Context context, String apiKey, int page, String path)
  {
    String pageToString = Integer.toString(page);

    return Uri.parse(context.getString(R.string.tmdb_movies_api_base_url)).buildUpon()
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

        movieListResultObjectList.add(new MovieListResultObject(posterPath, plotSynopsis, releaseDate, originalTitle, userRating));
      }

      return movieListResultObjectList;
    }
    catch(JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
