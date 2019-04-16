package com.example.android.popularmovies.tasks;

public interface IAsyncTaskCompleteListener<T>
{
  /**
   * Called by the async task where this IAsyncTaskCompleteListener is registered. This called is
   * executed on the UI thread.
   *
   * @param result the result of the return by the async task after its execution has completed.
   */
  public void onTaskComplete(T result);
}
