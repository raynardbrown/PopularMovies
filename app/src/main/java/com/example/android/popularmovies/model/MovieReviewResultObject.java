package com.example.android.popularmovies.model;

/**
 * MovieReviewResultObject is a class that models a review that is associated with a specific movie.
 */
public class MovieReviewResultObject
{
  private int id;

  private String author;

  /**
   * The review contents.
   */
  private String reviewContent;

  public MovieReviewResultObject()
  {
  }

  public MovieReviewResultObject(int id,
                                 String author,
                                 String reviewContent)
  {
    this.id = id;
    this.author = author;
    this.reviewContent = reviewContent;
  }

  /**
   * Returns an identifier that uniquely identifies the movie associated with the review.
   *
   * @return an identifier that uniquely identifies the movie associated with the review.
   */
  public int getId()
  {
    return id;
  }

  /**
   * Returns the author that wrote the review.
   *
   * @return the author that wrote the review.
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * Returns the review contents.
   *
   * @return the review contents.
   */
  public String getReviewContent()
  {
    return reviewContent;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public void setReviewContent(String reviewContent)
  {
    this.reviewContent = reviewContent;
  }
}
