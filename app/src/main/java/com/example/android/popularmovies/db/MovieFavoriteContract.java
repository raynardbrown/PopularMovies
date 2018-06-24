package com.example.android.popularmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract between the movie favorite provider and applications.
 */
public final class MovieFavoriteContract
{
  private static final String URI_SCHEME = "content://";

  /**
   * The authority for the movie favorite provider.
   */
  public static final String URI_CONTENT_AUTHORITY = "com.example.android.popularmovies";

  private static final Uri BASE_URI = Uri.parse(MovieFavoriteContract.URI_SCHEME + MovieFavoriteContract.URI_CONTENT_AUTHORITY);

  private MovieFavoriteContract()
  {

  }

  /**
   * MovieFavorites defines the table columns.
   */
  public static class MovieFavorites implements BaseColumns
  {
    public static final String TABLE_NAME = "movie_favorite";

    public static final String MOVIE_TITLE = "movie_title";

    public static final String MOVIE_POSTER_URL = "movie_poster_url";

    public static final String MOVIE_PLOT_SYNOPSIS = "movie_plot_synopsis";

    public static final String MOVIE_RELEASE_DATE = "movie_release_date";

    public static final String MOVIE_USER_RATING = "movie_user_rating";

    public static final String MOVIE_POSTER_IMAGE_DATA = "movie_poster_image_data";

    /**
     * The content:// style URI for this table.
     */
    public static final Uri CONTENT_URI = MovieFavoriteContract.BASE_URI.buildUpon().appendPath(MovieFavorites.TABLE_NAME).build();
  }

  public static class MovieTrailers implements BaseColumns
  {
    public static final String TABLE_NAME = "movie_trailer";

    public static final String TRAILER_CLIP_TITLE = "trailer_clip_title";

    public static final String TRAILER_YOUTUBE_KEY = "trailer_youtube_key";

    public static final String MOVIE_FAVORITE_FOREIGN_KEY = "movie_favorite_id";

    /**
     * The content:// style URI for this table.
     */
    public static final Uri CONTENT_URI = MovieFavoriteContract.BASE_URI.buildUpon().appendPath(MovieTrailers.TABLE_NAME).build();
  }

  public static class MovieReviews implements BaseColumns
  {
    public static final String TABLE_NAME = "movie_review";

    public static final String REVIEW_AUTHOR = "review_author";

    public static final String REVIEW_CONTENT = "review_content";

    public static final String MOVIE_FAVORITE_FOREIGN_KEY = "movie_favorite_id";

    /**
     * The content:// style URI for this table.
     */
    public static final Uri CONTENT_URI = MovieFavoriteContract.BASE_URI.buildUpon().appendPath(MovieReviews.TABLE_NAME).build();
  }
}
