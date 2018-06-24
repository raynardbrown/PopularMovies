package com.example.android.popularmovies.model;

public class PopularMoviesSettings
{
  public static final int TOP_RATED = 0;
  public static final int MOST_POPULAR = 1;
  public static final int FAVORITES = 2;

  private int sortSetting;

  public PopularMoviesSettings(int sortSetting)
  {
    this.sortSetting = sortSetting;
  }

  public int getSortSetting()
  {
    return this.sortSetting;
  }

  public void setSortSetting(int sortSetting)
  {
    this.sortSetting = sortSetting;
  }
}
