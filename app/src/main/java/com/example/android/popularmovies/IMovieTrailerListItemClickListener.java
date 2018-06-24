package com.example.android.popularmovies;

/**
 * Interface that receives clicks from movie trailer list items.
 */
public interface IMovieTrailerListItemClickListener
{
  /**
   * Called when a movie trailer list item is clicked.
   *
   * @param clickedItemIndex the index of the movie trailer item in the list that was clicked.
   */
  public void onMovieTrailerListItemClick(int clickedItemIndex);
}
