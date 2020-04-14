package com.example.themoviemb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.themoviemb.R;
import com.example.themoviemb.data.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    public static final float POSTER_ASPECT_RATIO = 1.5f;

    private final List<Movie> mMovies;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void send_details(Movie movie, int position);
    }


    public MoviesAdapter(List<Movie> movies, OnItemClickListener mItemClickListener) {
        mMovies = movies;
        this.mOnItemClickListener = mItemClickListener;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        int layoutForMovieItem = R.layout.cell_layout;
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.cell_layout, parent, shouldAttachToParentImmediately);
        final Context context = view.getContext();
        int gridColsNumber = context.getResources()
                .getInteger(new Integer(2));
        view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                POSTER_ASPECT_RATIO);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();
        holder.mMovie = movie;
        String posterUrl = movie.getPosterPath();
        // Warning: onError() will not be called, if url is null.
        // Empty url leads to app crash.
        Glide.with(context)
                .load(movie.getPosterPath())
                .into(holder.mMovieThumbnail);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.send_details(movie,holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        super.onViewRecycled(holder);
    }

    //Inner Class
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public Movie mMovie;

        @BindView(R.id.ivCell)
        ImageView mMovieThumbnail;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;

        }

    }
    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

/*  public void add(Cursor cursor) {

        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(MovieTableHelper.TITLE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                Movie movie = new Movie(id,v_average,title,backdropPath,overview,releaseDate,posterPath);
                mMovies.add(movie);
            } while (cursor.moveToNext());

        }
        notifyDataSetChanged();
    }*/

    public List<Movie> getMovies() {
        return mMovies;
    }


}