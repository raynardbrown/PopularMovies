# Popular Movies

## Project Overview

Popular Movies is an Android application that allows users to discover the most popular movies
playing.

## API Key

This project makes use of The Movie Database (TMDb) to retrieve movie information.
Per the terms of the TMDb, this API key must not be must be protected. This project
 does not include an API key. The user of this project shall use their own API key
 as provided by TMDb for this project.

The user must complete the following step to get a successful build.

1. Create a new class called "PopularMoviesConstants" within the the directory, 
"app/src/main/java/com.example.android.popularmovies/utils".

2. This class should have the following contents:
~~~~
package com.example.android.popularmovies.utils;

/**
 * Internal constants that are not part of version control
 */
public class PopularMoviesConstants
{
  private PopularMoviesConstants()
  {

  }

  public static String getTheMovieDatabaseApiKey()
  {
    return "YOUR_API_KEY";
  }
}
~~~~
3. Note, you must replace "YOUR_API_KEY" in the getTheMovieDatabaseApiKey function
with your actual API key from TMDb.