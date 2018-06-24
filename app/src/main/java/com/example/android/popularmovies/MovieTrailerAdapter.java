package com.example.android.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.databinding.MovieTrailerListItemBinding;
import com.example.android.popularmovies.model.MovieVideoResultObject;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder>
{
  private List<MovieVideoResultObject> movieVideoResultObjectList;
  private IMovieTrailerListItemClickListener movieTrailerListItemClickListener;

  MovieTrailerAdapter(List<MovieVideoResultObject> movieVideoResultObjectList,
                      IMovieTrailerListItemClickListener movieTrailerListItemClickListener)
  {
    this.movieVideoResultObjectList = movieVideoResultObjectList;
    this.movieTrailerListItemClickListener = movieTrailerListItemClickListener;
  }

  @Override
  public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.movie_trailer_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    MovieTrailerListItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new MovieTrailerViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(MovieTrailerViewHolder holder, int position)
  {
    holder.itemBinding.tvMovieTrailerName.setText(movieVideoResultObjectList.get(position).getVideoClipName());
  }

  @Override
  public int getItemCount()
  {
    return movieVideoResultObjectList.size();
  }

  class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
  {
    private MovieTrailerListItemBinding itemBinding;

    MovieTrailerViewHolder(MovieTrailerListItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;

      this.itemBinding.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
      int clickedIndex = getAdapterPosition();
      MovieTrailerAdapter.this.movieTrailerListItemClickListener.onMovieTrailerListItemClick(clickedIndex);
    }
  }
}
