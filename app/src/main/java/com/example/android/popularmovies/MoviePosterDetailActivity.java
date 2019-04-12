package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.databinding.ActivityMoviePosterDetailBinding;
import com.example.android.popularmovies.db.MovieFavoriteContract;
import com.example.android.popularmovies.model.MovieFavoriteObject;
import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.model.MovieReviewResultObject;
import com.example.android.popularmovies.model.MovieVideoResultObject;
import com.example.android.popularmovies.tasks.IAsyncTaskCompleteListener;
import com.example.android.popularmovies.tasks.MovieFavoriteDbDeleteAsyncTask;
import com.example.android.popularmovies.tasks.MovieFavoriteDbInsertAsyncTask;
import com.example.android.popularmovies.tasks.MovieFavoriteDbQueryAsyncTask;
import com.example.android.popularmovies.tasks.MovieReviewResultAsyncTask;
import com.example.android.popularmovies.tasks.MovieTrailerResultAsyncTask;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MoviePosterDetailActivity is an Activity that show detailed information about a movie that a
 * user selected from the main activity in this application.
 *
 * This activity shall provide the following additional functionality:
 *
 * <ul>
 *   <li>Allow users to view and play trailers.</li>
 *   <li>Allow users to read reviews of a selected movie.</li>
 *   <li>Allow users to favorite a movie by tapping a button.</li>
 * </ul>
 */
public class MoviePosterDetailActivity extends AppCompatActivity implements IMovieTrailerListItemClickListener
{
  private ActivityMoviePosterDetailBinding dataBinding;

  private MovieTrailerAdapter movieTrailerAdapter;
  private MovieReviewAdapter movieReviewAdapter;

  private List<MovieVideoResultObject> movieVideoResultObjectList;
  private List<MovieReviewResultObject> movieReviewResultObjectList;

  private MovieListResultObject movieListResultObject;

  private static final int FAVORITE_STATE = 1;
  private static final int UNDO_FAVORITE_STATE = 2;

  private int favoriteState;
  private boolean trailerTaskComplete;
  private boolean reviewTaskComplete;
  private boolean queryTaskComplete;
  private boolean moviePosterLoadingComplete;

  /**
   * Should the share button be enabled.
   */
  private boolean enableShareButton;

  private byte[] moviePosterImageData;

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

    favoriteState = -1;
    trailerTaskComplete = false;
    reviewTaskComplete = false;
    queryTaskComplete = false;
    moviePosterLoadingComplete = false;
    enableShareButton = false;

    Intent intent = getIntent();

    if(intent != null)
    {
      movieListResultObject = new MovieListResultObject(intent.getStringExtra(getString(R.string.movie_detail_poster_path_key)),
              intent.getStringExtra(getString(R.string.movie_detail_plot_synopsis_key)),
              intent.getStringExtra(getString(R.string.movie_detail_release_date_key)),
              intent.getStringExtra(getString(R.string.movie_detail_original_title_key)),
              intent.getStringExtra(getString(R.string.movie_detail_user_rating_key)),
              intent.getIntExtra(getString(R.string.movie_detail_id_key), 0));

      // Set up the recycler view (trailers)
      LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
      dataBinding.rvTrailers.setLayoutManager(trailerLayoutManager);
      movieVideoResultObjectList = new ArrayList<>();
      movieTrailerAdapter = new MovieTrailerAdapter(movieVideoResultObjectList, this);
      dataBinding.rvTrailers.setAdapter(movieTrailerAdapter);

      // Set up the recycler view (reviews)
      LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
      dataBinding.rvReviews.setLayoutManager(reviewLayoutManager);
      movieReviewResultObjectList = new ArrayList<>();
      movieReviewAdapter = new MovieReviewAdapter(movieReviewResultObjectList);
      dataBinding.rvReviews.setAdapter(movieReviewAdapter);

      // The favorite button should be initially invisible until we can determine its state
      dataBinding.buttonFavorite.setVisibility(View.INVISIBLE);
      updateViews();

      // Fire off the trailer async task
      new MovieTrailerResultAsyncTask(this, new MovieTrailerResultAsyncTaskCompleteListener()).execute(movieListResultObject.getId());

      // Fire off the review async task
      new MovieReviewResultAsyncTask(this, new MovieReviewResultAsyncTaskCompleteListener()).execute(movieListResultObject.getId());

      MovieFavoriteDbQueryAsyncTask.Param param = new MovieFavoriteDbQueryAsyncTask.Param(movieListResultObject.getId(), null);
      new MovieFavoriteDbQueryAsyncTask(this, new MovieFavoriteDbQueryAsyncTaskCompleteListener()).execute(param);
    }
  }

  private void updateViews()
  {
    dataBinding.tvMovieSynopsis.setText(movieListResultObject.getPlotSynopsis());
    dataBinding.tvReleaseDate.setText(movieListResultObject.getReleaseDate());
    dataBinding.tvOriginalTitle.setText(movieListResultObject.getOriginalTitle());
    dataBinding.tvUserRating.setText(movieListResultObject.getUserRating());

    dataBinding.buttonFavorite.setOnClickListener(new FavoriteButtonClick());
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    outState.putIntArray(getString(R.string.scroll_position_state), new int[]
            {
                    dataBinding.svMoviePosterDetail.getScrollX(),
                    dataBinding.svMoviePosterDetail.getScrollY()
            });
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    final int[] position = savedInstanceState.getIntArray(getString(R.string.scroll_position_state));

    if(position != null)
    {
      dataBinding.svMoviePosterDetail.postDelayed(new Runnable()
      {
        @Override
        public void run()
        {
          dataBinding.svMoviePosterDetail.scrollTo(position[0], position[1]);
        }
      }, 500);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.detail_menu, menu);

    if(enableShareButton)
    {
      menu.findItem(R.id.action_share_menuitem).setVisible(true);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();

    switch(id)
    {
      case R.id.action_share_menuitem:
      {
        onShareButtonClick();

        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private void onShareButtonClick()
  {
    String mimeType = "text/plain";
    String title = this.movieVideoResultObjectList.get(0).getVideoClipName();

    Uri uri = Uri.parse(getString(R.string.youtube_website_base_url_string)).buildUpon()
            .appendQueryParameter("v", this.movieVideoResultObjectList.get(0).getKey())
            .build();

    String shareText = uri.toString();

    Intent shareIntent = ShareCompat.IntentBuilder.from(this)
            .setChooserTitle(title)
            .setType(mimeType)
            .setText(shareText)
            .createChooserIntent();

    if(shareIntent.resolveActivity(getPackageManager()) != null)
    {
      startActivity(shareIntent);
    }
  }

  @Override
  public void onMovieTrailerListItemClick(int clickedItemIndex)
  {
    String youTubeKey = movieVideoResultObjectList.get(clickedItemIndex).getKey();

    if(!openVideoOnYouTubeAppIfInstalled(youTubeKey))
    {
      // couldn't use the app, try the browser
      openVideoOnYouTubePageIfBrowserInstalled(youTubeKey);
    }
  }

  private boolean openVideoOnYouTubeAppIfInstalled(String videoKey)
  {
    Uri uri = Uri.parse(getString(R.string.youtube_app_base_url_string) + videoKey);

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

    if (intent.resolveActivity(getPackageManager()) != null)
    {
      startActivity(intent);
      return true;
    }
    else
    {
      return false;
    }
  }

  private boolean openVideoOnYouTubePageIfBrowserInstalled(String videoKey)
  {
    Uri uri = Uri.parse(getString(R.string.youtube_website_base_url_string)).buildUpon()
            .appendQueryParameter("v", videoKey)
            .build();

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

    if (intent.resolveActivity(getPackageManager()) != null)
    {
      startActivity(intent);
      return true;
    }
    else
    {
      return false;
    }
  }

  private void dispatchMoviePosterImageDownload()
  {
    if(moviePosterImageData != null && moviePosterImageData.length > 0)
    {
      dataBinding.ivMoviePoster.setImageBitmap(BitmapFactory.decodeByteArray(moviePosterImageData, 0, moviePosterImageData.length));
      moviePosterLoadingComplete = true;
      notifyTaskComplete();
    }
    else
    {
      // grab the image from the remote site.
      Uri uri = TheMovieDatabaseUtils.getMoviePosterUriFromPath(getApplicationContext(),
              movieListResultObject.getPosterPath());

      Picasso.with(getApplicationContext())
              .load(uri.toString())
              .placeholder(R.drawable.image_placeholder)
              .into(dataBinding.ivMoviePoster, new MoviePosterCompleteHandler());
    }
  }

  private byte[] getMoviePosterImageData()
  {
    Bitmap bitmap = ((BitmapDrawable)dataBinding.ivMoviePoster.getDrawable()).getBitmap();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // use png so we don't lose quality
    return byteArrayOutputStream.toByteArray();
  }

  class FavoriteButtonClick implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      if(favoriteState == MoviePosterDetailActivity.FAVORITE_STATE)
      {
        // Button clicked while in the favorite state, add this movie to the favorite database

        MovieFavoriteObject movieFavoriteObject = new MovieFavoriteObject(MoviePosterDetailActivity.this.movieListResultObject,
                MoviePosterDetailActivity.this.movieVideoResultObjectList,
                MoviePosterDetailActivity.this.movieReviewResultObjectList,
                getMoviePosterImageData());

        new MovieFavoriteDbInsertAsyncTask(MoviePosterDetailActivity.this, new MovieFavoriteDbInsertAsyncTaskCompleteListener()).execute(movieFavoriteObject);
      }
      else
      {
        // Button clicked while in the undo favorite state, remove this movie from the favorite database
        MovieFavoriteDbDeleteAsyncTask.Param param = new MovieFavoriteDbDeleteAsyncTask.Param(MoviePosterDetailActivity.this.movieListResultObject.getId());
        new MovieFavoriteDbDeleteAsyncTask(MoviePosterDetailActivity.this, new MovieFavoriteDbDeleteAsyncTaskCompleteListener()).execute(param);
      }
    } // end onClick
  } // end FavoriteButtonClick

  /**
   * Called when all the initial async tasks have completed execution.
   */
  private void onAllTasksComplete()
  {
    // Set the text for the favorite button based on the "favorite state".
    if(favoriteState == FAVORITE_STATE)
    {
      dataBinding.buttonFavorite.setText(R.string.favorite_button_favorite_label);
    }
    else if(favoriteState == MoviePosterDetailActivity.UNDO_FAVORITE_STATE)
    {
      dataBinding.buttonFavorite.setText(R.string.favorite_button_undo_favorite_label);
    }

    // Set the favorite button visible
    dataBinding.buttonFavorite.setVisibility(View.VISIBLE);
  }

  private void notifyTaskComplete()
  {
    if(queryTaskComplete && reviewTaskComplete && trailerTaskComplete && moviePosterLoadingComplete)
    {
      onAllTasksComplete();
    }

    if(enableShareButton)
    {
      // Enable the share button
      // Data binding is not available for menus, so use good old findViewById
      invalidateOptionsMenu();
    }
  }

  class MovieTrailerResultAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieTrailerResultAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieTrailerResultAsyncTask.Result result)
    {
      if(result != null)
      {
        if(result.movieVideoResultObjectList != null && result.movieVideoResultObjectList.size() > 0)
        {
          // We have valid results
          MoviePosterDetailActivity.this.movieVideoResultObjectList.addAll(result.movieVideoResultObjectList);

          // Enable the share button flag
          enableShareButton = true;

          MoviePosterDetailActivity.this.movieTrailerAdapter.notifyDataSetChanged();
        }
        else
        {
          // no trailers available
          MoviePosterDetailActivity.this.dataBinding.tvMovieTrailersNotAvailable.setVisibility(View.VISIBLE);
        }
      }
      MoviePosterDetailActivity.this.trailerTaskComplete = true;
      MoviePosterDetailActivity.this.notifyTaskComplete();
    } // end onTaskComplete
  } // end MovieTrailerResultAsyncTaskCompleteListener

  class MovieReviewResultAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieReviewResultAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieReviewResultAsyncTask.Result result)
    {
      if(result != null)
      {
        if(result.movieReviewResultObjectList != null && result.movieReviewResultObjectList.size() > 0)
        {
          // We have valid results
          MoviePosterDetailActivity.this.movieReviewResultObjectList.addAll(result.movieReviewResultObjectList);

          MoviePosterDetailActivity.this.movieReviewAdapter.notifyDataSetChanged();
        }
        else
        {
          // no reviews available
          MoviePosterDetailActivity.this.dataBinding.tvMovieReviewsNotAvailable.setVisibility(View.VISIBLE);
        }
      }
      MoviePosterDetailActivity.this.reviewTaskComplete = true;
      MoviePosterDetailActivity.this.notifyTaskComplete();
    } // end onTaskComplete
  } // end MovieReviewResultAsyncTaskCompleteListener

  class MovieFavoriteDbQueryAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieFavoriteDbQueryAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieFavoriteDbQueryAsyncTask.Result result)
    {
      if(result != null)
      {
        Cursor movieFavoriteCursor = result.movieFavoriteCursor;

        if(movieFavoriteCursor != null)
        {
          if(movieFavoriteCursor.moveToFirst())
          {
            int id = movieFavoriteCursor.getInt(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites._ID));

            if (id == MoviePosterDetailActivity.this.movieListResultObject.getId())
            {
              MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.UNDO_FAVORITE_STATE;

              moviePosterImageData = movieFavoriteCursor.getBlob(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_IMAGE_DATA));
            }
            else
            {
              // Got an id that wasn't this detail activity's movie id. (Should never happen)
              MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.FAVORITE_STATE;
            }
          }
          else
          {
            // cursor is empty
            MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.FAVORITE_STATE;
          }

          // We are finished with the cursor so close it
          movieFavoriteCursor.close();
        }
        else
        {
          MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.FAVORITE_STATE;
        }
      }
      MoviePosterDetailActivity.this.queryTaskComplete = true;
      MoviePosterDetailActivity.this.notifyTaskComplete();

      // Fetch the movie poster image
      MoviePosterDetailActivity.this.dispatchMoviePosterImageDownload();
    } // end onTaskComplete
  } // end MovieFavoriteDbQueryAsyncTaskCompleteListener

  class MovieFavoriteDbInsertAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieFavoriteDbInsertAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieFavoriteDbInsertAsyncTask.Result result)
    {
      if(result != null)
      {
        // This movie was added to the database
        MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.UNDO_FAVORITE_STATE;

        // Make sure we update the shared preferences so that the main activity will know about
        // the change
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MoviePosterDetailActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.shared_preferences_favorites_changed_key), true);
        editor.apply();

        MoviePosterDetailActivity.this.notifyTaskComplete();
      }
    }
  }

  class MovieFavoriteDbDeleteAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieFavoriteDbDeleteAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieFavoriteDbDeleteAsyncTask.Result result)
    {
      if(result != null)
      {
        if(result.numberRowsDeleted > 0)
        {
          // This movie was removed from the database.
          MoviePosterDetailActivity.this.favoriteState = MoviePosterDetailActivity.FAVORITE_STATE;

          // Make sure we update the shared preferences so that the main activity will know about
          // the change
          SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MoviePosterDetailActivity.this);
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putBoolean(getString(R.string.shared_preferences_favorites_changed_key), true);
          editor.apply();

          MoviePosterDetailActivity.this.notifyTaskComplete();
        }
      }
    }
  }

  /**
   * Handler that is triggered when the poster poster is downloaded and loaded into the image view.
   */
  class MoviePosterCompleteHandler implements Callback
  {
    @Override
    public void onSuccess()
    {
      MoviePosterDetailActivity.this.moviePosterLoadingComplete = true;

      MoviePosterDetailActivity.this.notifyTaskComplete();
    }

    @Override
    public void onError()
    {

    }
  }
}
