package com.example.android.popularmovies.model;


public class MovieVideoResultObject
{
  private int id;

  private String videoClipName;

  private String key;

  public MovieVideoResultObject()
  {

  }

  public MovieVideoResultObject(int id,
                                String videoClipName,
                                String key)
  {

    this.id = id;
    this.videoClipName = videoClipName;
    this.key = key;
  }
  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public String getVideoClipName()
  {
    return videoClipName;
  }

  public void setVideoClipName(String videoClipName)
  {
    this.videoClipName = videoClipName;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }
}
