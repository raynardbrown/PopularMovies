package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.databinding.ActivityMoviePosterDetailBinding;
import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;
import com.squareup.picasso.Picasso;

/**
 * MoviePosterDetailActivity is an Activity that show detailed information about a movie that a
 * user selected from the main activity in this application.
 */
public class MoviePosterDetailActivity extends AppCompatActivity
{
  private ActivityMoviePosterDetailBinding dataBinding;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_poster_detail);

    setSupportActionBar(dataBinding.tbMoviePosterDetail);

    if(getSupportActionBar() != null)
    {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.movie_poster_detail_name);
    }

    Intent intent = getIntent();

    if(intent != null)
    {
      MovieListResultObject movieListResultObject = new MovieListResultObject(intent.getStringExtra(getString(R.string.movie_detail_poster_path_key)),
              intent.getStringExtra(getString(R.string.movie_detail_plot_synopsis_key)),
              intent.getStringExtra(getString(R.string.movie_detail_release_date_key)),
              intent.getStringExtra(getString(R.string.movie_detail_original_title_key)),
              intent.getStringExtra(getString(R.string.movie_detail_user_rating_key)));

      updateViews(movieListResultObject);
    }
  }

  private void updateViews(MovieListResultObject movieListResultObject)
  {
    dataBinding.tvMovieSynopsis.setText(movieListResultObject.getPlotSynopsis());
    dataBinding.tvReleaseDate.setText(movieListResultObject.getReleaseDate());
    dataBinding.tvOriginalTitle.setText(movieListResultObject.getOriginalTitle());
    dataBinding.tvUserRating.setText(movieListResultObject.getUserRating());

    Uri uri = TheMovieDatabaseUtils.getMoviePosterUriFromPath(getApplicationContext(),
            movieListResultObject.getPosterPath());

    Picasso.with(getApplicationContext())
            .load(uri.toString())
            .into(dataBinding.ivMoviePoster);
  }
}
