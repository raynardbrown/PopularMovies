package com.example.android.popularmovies.model;

import java.util.List;

public class MovieFavoriteObject
{
  private MovieListResultObject movieListResultObject;
  private List<MovieVideoResultObject> movieVideoResultObjectList;
  private List<MovieReviewResultObject> movieReviewResultObjectList;
  private byte[] moviePosterImageData;

  public MovieFavoriteObject(MovieListResultObject movieListResultObject,
                             List<MovieVideoResultObject> movieVideoResultObjectList,
                             List<MovieReviewResultObject> movieReviewResultObjectList,
                             byte[] moviePosterImageData)
  {
    this.movieListResultObject = movieListResultObject;
    this.movieVideoResultObjectList = movieVideoResultObjectList;
    this.movieReviewResultObjectList = movieReviewResultObjectList;
    this.moviePosterImageData = moviePosterImageData;
  }

  public MovieListResultObject getMovieListResultObject()
  {
    return movieListResultObject;
  }

  public List<MovieVideoResultObject> getMovieVideoResultObjectList()
  {
    return movieVideoResultObjectList;
  }

  public List<MovieReviewResultObject> getMovieReviewResultObjectList()
  {
    return movieReviewResultObjectList;
  }

  public byte[] getMoviePosterImageData()
  {
    return moviePosterImageData;
  }
}
