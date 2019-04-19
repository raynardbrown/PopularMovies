package com.example.android.popularmovies;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;

import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.db.MovieFavoriteContract;
import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.model.PopularMoviesSettings;
import com.example.android.popularmovies.receiver.INetworkListener;
import com.example.android.popularmovies.receiver.NetworkBroadcastReceiver;
import com.example.android.popularmovies.tasks.IAsyncTaskCompleteListener;
import com.example.android.popularmovies.tasks.MovieFavoriteDbQueryAsyncTask;
import com.example.android.popularmovies.tasks.MovieListResultAsyncTask;

import java.util.ArrayList;
import java.util.List;


/**
 * MainActivity is the main Activity within the Popular Movies application. This Activity presents
 * the user with a grid arrangement of movie posters.
 *
 * The user can tap on posters within this Activity, which launches a detailed break down on the
 * movie such as:
 *
 * <ul>
 *   <li>original title</li>
 *   <li>movie poster image thumbnail</li>
 *   <li>plot synopsis</li>
 *   <li>user rating</li>
 *   <li>release date</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,
                                                               GridView.OnItemClickListener,
                                                               INetworkListener
{
  /**
   * A collection of movie results from a query of the TMDb.
   */
  private List<MovieListResultObject> movieListResultObjectList;

  private MoviePosterImageAdapter adapter;

  private PopularMoviesSettings popularMovieSettings;

  private ActivityMainBinding dataBinding;

  private NetworkBroadcastReceiver networkBroadcastReceiver;

  /**
   * The last index of the last page that we have successfully retrieved.
   *
   * This field is initially 0 when no pages have yet to be retrieved.
   */
  private int currentRemotePage;

  /**
   * The total number of pages remote pages that available for retrieval.
   *
   * This field is initially 0 when no pages have yet to be retrieved.
   */
  private int totalRemotePages;

  private SharedPreferences sharedPreferences;

  /**
   * Maintains the state of the grid on a configuration change.
   */
  private Parcelable gridViewParcelable;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    // Make the toolbar function as an action bar.
    setSupportActionBar(dataBinding.tbPopularMovies);

    dataBinding.gvMoviePosters.setOnItemClickListener(this);

    initializePageState();

    initializeAdapter();

    if(savedInstanceState != null)
    {
      popularMovieSettings = new PopularMoviesSettings(savedInstanceState.getInt(getString(R.string.movie_sort_by_key)));
    }
    else
    {
      // Default sort value
      popularMovieSettings = new PopularMoviesSettings(PopularMoviesSettings.MOST_POPULAR);
    }

    // We are using cached data in the favorites state, no need to start up a network broadcast
    // receiver.
    if(popularMovieSettings.getSortSetting() != PopularMoviesSettings.FAVORITES)
    {
      launchBroadCastReceiverIfNotRegistered();
    }

    dispatchMovieListResultRequest();
  }

  private void launchBroadCastReceiverIfNotRegistered()
  {
    if(networkBroadcastReceiver == null)
    {
      IntentFilter networkIntentFilter = new IntentFilter();
      networkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

      networkBroadcastReceiver = new NetworkBroadcastReceiver(this);

      registerReceiver(networkBroadcastReceiver, networkIntentFilter);
    }
  }

  private void initializePageState()
  {
    currentRemotePage = 0;
    totalRemotePages = 0;
  }

  private void dispatchMovieListResultRequest()
  {
    if(!(popularMovieSettings.getSortSetting() == PopularMoviesSettings.FAVORITES) &&
            (currentRemotePage == 0 || currentRemotePage < totalRemotePages))
    {
      new MovieListResultAsyncTask(this, new MovieListResultAsyncTaskCompleteListener()).execute(popularMovieSettings);
    }
    else
    {
      // We are in the favorites state, dispatch the appropriate database requests
      MovieFavoriteDbQueryAsyncTask.Param param = new MovieFavoriteDbQueryAsyncTask.Param(null, null);
      new MovieFavoriteDbQueryAsyncTask(this, new MovieFavoriteDbQueryAsyncTaskCompleteListener()).execute(param);
    }
  }

  private void initializeAdapter()
  {
    movieListResultObjectList = new ArrayList<>();

    adapter = new MoviePosterImageAdapter(this, movieListResultObjectList);

    dataBinding.gvMoviePosters.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();

    switch(id)
    {
      case R.id.action_sort_by_menuitem:
      {
        // Data binding is not available for menus, so use good old findViewById
        // This shows the popup menu under the sort button on the action bar.
        View view = findViewById(R.id.action_sort_by_menuitem);
        showPopupMenu(view);
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private void showPopupMenu(View v)
  {
    PopupMenu popup = new PopupMenu(this, v);

    popup.setOnMenuItemClickListener(this);

    popup.inflate(R.menu.sort_by_popup_menu);
    popup.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    int id = item.getItemId();

    // If the sort changed, you must get new json data and refresh the grid
    switch(id)
    {
      case R.id.action_sort_by_most_popular:
      {
        onMenuItemClickHelper(PopularMoviesSettings.MOST_POPULAR);

        return true;
      }

      case R.id.action_sort_by_top_rated:
      {
        onMenuItemClickHelper(PopularMoviesSettings.TOP_RATED);
        return true;
      }

      case R.id.action_sort_by_favorites:
      {
        onMenuItemClickHelper(PopularMoviesSettings.FAVORITES);
        return true;
      }

      default:
      {
        return false;
      }
    }
  }

  private void onMenuItemClickHelper(int sortSetting)
  {
    // Make sure the setting is not the same
    if(popularMovieSettings.getSortSetting() != sortSetting)
    {
      popularMovieSettings.setSortSetting(sortSetting);

      initializePageState();

      initializeAdapter();

      restoreDefaultViewState();

      // reset the grid parcelable
      gridViewParcelable = null;

      if(popularMovieSettings.getSortSetting() != PopularMoviesSettings.FAVORITES)
      {
        launchBroadCastReceiverIfNotRegistered();
      }

      // fire off a new async task
      dispatchMovieListResultRequest();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    Intent intent = new Intent(this, MoviePosterDetailActivity.class);

    // Pass the movie data for the detail view
    intent.putExtra(getString(R.string.movie_detail_poster_path_key), this.movieListResultObjectList.get(position).getPosterPath());
    intent.putExtra(getString(R.string.movie_detail_plot_synopsis_key), this.movieListResultObjectList.get(position).getPlotSynopsis());
    intent.putExtra(getString(R.string.movie_detail_release_date_key), this.movieListResultObjectList.get(position).getReleaseDate());
    intent.putExtra(getString(R.string.movie_detail_original_title_key), this.movieListResultObjectList.get(position).getOriginalTitle());
    intent.putExtra(getString(R.string.movie_detail_user_rating_key), this.movieListResultObjectList.get(position).getUserRating());
    intent.putExtra(getString(R.string.movie_detail_id_key), this.movieListResultObjectList.get(position).getId());

    startActivity(intent);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    // save the sort setting
    outState.putInt(getString(R.string.movie_sort_by_key), popularMovieSettings.getSortSetting());

    // save the grid position
    gridViewParcelable = dataBinding.gvMoviePosters.onSaveInstanceState();
    outState.putParcelable(getString(R.string.grid_position_state), gridViewParcelable);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    popularMovieSettings = new PopularMoviesSettings(savedInstanceState.getInt(getString(R.string.movie_sort_by_key)));

    gridViewParcelable = savedInstanceState.getParcelable(getString(R.string.grid_position_state));
  }

  /**
   * Handle main activity activation. Normally we would handle this via onRestoreInstanceState,
   * however we are using singleTop so onRestoreInstanceState will not be called. We only need to
   * handle movies being removed from the favorites database here.
   */
  @Override
  protected void onResume()
  {
    super.onResume();

    if(popularMovieSettings.getSortSetting() == PopularMoviesSettings.FAVORITES)
    {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

      boolean favoritesChanged = sharedPreferences.getBoolean(getString(R.string.shared_preferences_favorites_changed_key), false);

      if(favoritesChanged)
      {
        // Kick off a query since we are in the favorites state
        initializeAdapter();

        dispatchMovieListResultRequest();
      }
    }
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();

    if(networkBroadcastReceiver != null)
    {
      unregisterReceiver(networkBroadcastReceiver);
    }
  }

  @Override
  public void onNetworkConnected()
  {
    showGridViewAndHideNetworkErrorMessage();

    // We also need to launch the async task if the movieListResultObjectList is empty
    // since we do not have any results.
    if(this.movieListResultObjectList.isEmpty())
    {
      dispatchMovieListResultRequest();
    }
  }

  @Override
  public void onNetworkDisconnected()
  {
    if(this.movieListResultObjectList.isEmpty())
    {
      hideGridViewAndShowNetworkErrorMessage();
    }
  }

  private void hideGridViewAndShowNetworkErrorMessage()
  {
    if(popularMovieSettings.getSortSetting() != PopularMoviesSettings.FAVORITES)
    {
      dataBinding.gvMoviePosters.setVisibility(View.INVISIBLE);
      dataBinding.tvNoNetworkConnection.setVisibility(View.VISIBLE);
    }
  }

  private void showGridViewAndHideNetworkErrorMessage()
  {
    if(popularMovieSettings.getSortSetting() != PopularMoviesSettings.FAVORITES)
    {
      dataBinding.tvNoNetworkConnection.setVisibility(View.INVISIBLE);
      dataBinding.gvMoviePosters.setVisibility(View.VISIBLE);
    }
  }

  private void hideGridViewAndShowFavoriteDatabaseErrorMessage()
  {
    dataBinding.gvMoviePosters.setVisibility(View.INVISIBLE);
    dataBinding.tvNoFavoritesInDatabase.setVisibility(View.VISIBLE);
  }

  private void restoreDefaultViewState()
  {
    dataBinding.gvMoviePosters.setVisibility(View.VISIBLE);
    dataBinding.tvNoNetworkConnection.setVisibility(View.INVISIBLE);
    dataBinding.tvNoFavoritesInDatabase.setVisibility(View.INVISIBLE);
  }

  public class MovieListResultAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieListResultAsyncTask.Results>
  {
    @Override
    public void onTaskComplete(MovieListResultAsyncTask.Results result)
    {
      if(result != null)
      {
        if(result.movieListResultObjectList != null)
        {
          // We have valid results

          // Make sure we are not adding an existing page (for example two async tasks fired)
          // If we are throw the results away since we already have the page
          if(MainActivity.this.currentRemotePage != result.page)
          {
            MainActivity.this.movieListResultObjectList.addAll(result.movieListResultObjectList);

            MainActivity.this.adapter.notifyDataSetChanged();

            // update the current page
            MainActivity.this.currentRemotePage++;

            /*
             * Scroll to the position in the grid before a configuration change occurred.
             *
             * Note: For some strange reason calling onRestoreInstanceState(gridViewParcelable),
             * without at least a 275ms delay (from my testing) doesn't seem to work. I don't
             * understand why since at this point we are on the main/UI thread. The delay seems
             * arbitrary.
             *
             * I've bumped the delay to 500ms. I hope that doesn't cause problems.
             */
            if(gridViewParcelable != null)
            {
              dataBinding.gvMoviePosters.postDelayed(new Runnable()
              {
                @Override
                public void run()
                {
                    dataBinding.gvMoviePosters.onRestoreInstanceState(gridViewParcelable);
                }
              }, 500);
            }
          }
        }
        else if(MainActivity.this.movieListResultObjectList.isEmpty())
        {
          // We didn't get anything from the remote server and we currently have no posters displayed
          MainActivity.this.hideGridViewAndShowNetworkErrorMessage();
        }
      }
    }
  }

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
          for(; movieFavoriteCursor.moveToNext(); )
          {
            int movieId = movieFavoriteCursor.getInt(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites._ID));
            String movieTitle = movieFavoriteCursor.getString(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_TITLE));
            String moviePosterUrl = movieFavoriteCursor.getString(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_URL));
            String moviePlot = movieFavoriteCursor.getString(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_PLOT_SYNOPSIS));
            String movieReleaseDate = movieFavoriteCursor.getString(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_RELEASE_DATE));
            String movieUserRating = movieFavoriteCursor.getString(movieFavoriteCursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_USER_RATING));

            MainActivity.this.movieListResultObjectList.add(new MovieListResultObject(moviePosterUrl, moviePlot, movieReleaseDate, movieTitle, movieUserRating, movieId));
          }

          if(MainActivity.this.movieListResultObjectList.size() > 0)
          {
            MainActivity.this.adapter.notifyDataSetChanged();

            /*
             * Scroll to the position in the grid before a configuration change occurred.
             *
             * Note: For some strange reason calling onRestoreInstanceState(gridViewParcelable),
             * without at least a 275ms delay (from my testing) doesn't seem to work. I don't
             * understand why since at this point we are on the main/UI thread. The delay seems
             * arbitrary.
             *
             * I've bumped the delay to 500ms. I hope that doesn't cause problems.
             */
            if(gridViewParcelable != null)
            {
              dataBinding.gvMoviePosters.postDelayed(new Runnable()
              {
                @Override
                public void run()
                {
                  dataBinding.gvMoviePosters.onRestoreInstanceState(gridViewParcelable);
                }
              }, 500);
            }
          }
          else
          {
            // The cursor is empty, display a message to the user
            hideGridViewAndShowFavoriteDatabaseErrorMessage();
          }

          // finished with the cursor so close it.
          movieFavoriteCursor.close();

          // Also reset the shared preferences

          sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putBoolean(getString(R.string.shared_preferences_favorites_changed_key), false);
          editor.apply();
        }
      }
    }
  }
}
