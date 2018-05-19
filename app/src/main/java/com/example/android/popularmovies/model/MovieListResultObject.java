package com.example.android.popularmovies.model;

/**
 * MovieListResultObject is a class that models the data that is returned from the movie database
 * "top rated" and "popular" movie queries.
 */
public class MovieListResultObject
{
  private String posterPath;
  private String plotSynopsis;
  private String releaseDate;
  private String originalTitle;
  private String userRating;

  public MovieListResultObject()
  {

  }

  public MovieListResultObject(String posterPath,
                               String plotSynopsis,
                               String releaseDate,
                               String originalTitle,
                               String userRating)
  {
    this.posterPath = posterPath;
    this.plotSynopsis = plotSynopsis;
    this.releaseDate = releaseDate;
    this.originalTitle = originalTitle;
    this.userRating = userRating;
  }

  public String getPosterPath()
  {
    return posterPath;
  }

  public String getPlotSynopsis()
  {
    return plotSynopsis;
  }

  public String getReleaseDate()
  {
    return releaseDate;
  }

  public String getOriginalTitle()
  {
    return originalTitle;
  }

  public String getUserRating()
  {
    return userRating;
  }
}
