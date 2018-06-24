package com.example.android.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.popularmovies.databinding.MovieReviewListItemBinding;
import com.example.android.popularmovies.model.MovieReviewResultObject;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder>
{
  private List<MovieReviewResultObject> movieReviewResultObjectList;

  MovieReviewAdapter(List<MovieReviewResultObject> movieReviewResultObjectList)
  {
    this.movieReviewResultObjectList = movieReviewResultObjectList;
  }

  @Override
  public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.movie_review_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    MovieReviewListItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new MovieReviewViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(MovieReviewViewHolder holder, int position)
  {
    holder.itemBinding.tvMovieReviewAuthor.setText(movieReviewResultObjectList.get(position).getAuthor());
    holder.itemBinding.tvMovieReviewContent.setText(movieReviewResultObjectList.get(position).getReviewContent());
  }

  @Override
  public int getItemCount()
  {
    return movieReviewResultObjectList.size();
  }

  class MovieReviewViewHolder extends RecyclerView.ViewHolder
  {
    private MovieReviewListItemBinding itemBinding;

    MovieReviewViewHolder(MovieReviewListItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
