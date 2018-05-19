package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.popularmovies.model.MovieListResultObject;
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

    Uri uri = TheMovieDatabaseUtils.getMoviePosterUriFromPath(context, movieListResultObjectList.get(position).getPosterPath());

    Picasso.with(this.context)
            .load(uri.toString())
            .placeholder(android.R.drawable.star_big_on) // TODO: Use a real loading image :-)
            .into(imageView);

    return imageView;
  }
}
