package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.popularmovies.db.MovieFavoriteContract;
import com.example.android.popularmovies.model.MovieListResultObject;
import com.example.android.popularmovies.tasks.IAsyncTaskCompleteListener;
import com.example.android.popularmovies.tasks.MovieFavoriteDbQueryAsyncTask;
import com.example.android.popularmovies.utils.TheMovieDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviePosterImageAdapter extends BaseAdapter
{
  private Context context;
  private List<MovieListResultObject> movieListResultObjectList;

  public MoviePosterImageAdapter(Context context,
                                 List<MovieListResultObject> movieListResultObjectList)
  {
    this.context = context;
    this.movieListResultObjectList = movieListResultObjectList;
  }

  @Override
  public int getCount()
  {
    return this.movieListResultObjectList.size();
  }

  @Override
  public Object getItem(int position)
  {
    return null;
  }

  @Override
  public long getItemId(int position)
  {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ImageView imageView;
    if(convertView == null)
    {
      // Initialize a new view
      imageView = new ImageView(this.context);
      imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setPadding(0, 0, 0, 0);
    }
    else
    {
      // Reuse/Recycle an existing view
      imageView = (ImageView) convertView;
    }

    DbQueryPojo dbQueryPojo = new DbQueryPojo(imageView, movieListResultObjectList.get(position).getPosterPath());
    MovieFavoriteDbQueryAsyncTask.Param param = new MovieFavoriteDbQueryAsyncTask.Param(movieListResultObjectList.get(position).getId(), dbQueryPojo);
    new MovieFavoriteDbQueryAsyncTask(this.context, new MovieFavoriteDbQueryAsyncTaskCompleteListener()).execute(param);

    return imageView;
  }

  private class DbQueryPojo
  {
    ImageView imageView;
    String moviePosterPath;

    DbQueryPojo(ImageView imageView, String moviePosterPath)
    {
      this.imageView = imageView;
      this.moviePosterPath = moviePosterPath;
    }
  }

  class MovieFavoriteDbQueryAsyncTaskCompleteListener implements IAsyncTaskCompleteListener<MovieFavoriteDbQueryAsyncTask.Result>
  {
    @Override
    public void onTaskComplete(MovieFavoriteDbQueryAsyncTask.Result result)
    {
      if(result != null)
      {
        Cursor cursor = result.cursor;

        if(cursor != null)
        {
          if(cursor.moveToFirst())
          {
            // use the image data from the favorites database
            byte[] moviePosterImageData = cursor.getBlob(cursor.getColumnIndex(MovieFavoriteContract.MovieFavorites.MOVIE_POSTER_IMAGE_DATA));

            ImageView imageView = ((DbQueryPojo)result.extraData).imageView;
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(moviePosterImageData, 0, moviePosterImageData.length));
          }
          else
          {
            // cursor is empty, so fetch the image remotely

            ImageView imageView = ((DbQueryPojo)result.extraData).imageView;
            String moviePosterPath = ((DbQueryPojo)result.extraData).moviePosterPath;

            Uri uri = TheMovieDatabaseUtils.getMoviePosterUriFromPath(MoviePosterImageAdapter.this.context, moviePosterPath);

            Picasso.with(MoviePosterImageAdapter.this.context)
                    .load(uri.toString())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageView);
          }

          // Done with the cursor so close it
          cursor.close();
        }
      }
    }
  }
}
