package com.example.android.popularmovies.tasks;

public interface IAsyncTaskCompleteListener<T>
{
  public void onTaskComplete(T result);
}
