package com.example.themoviemb.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.themoviemb.R;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private static final float POSTER_ASPECT_RATIO = 1.5f;
    private List<Movie> movies;
    private int filmPerRow;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void sendDetails(int id, OnItemClickListener onItemClickListener);

        void longClick(int id, OnItemClickListener onItemClickListener);
    }

    public MoviesAdapter(List<Movie> movies, OnItemClickListener onItemClickListener, int filmPerRow) {
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
        this.filmPerRow=filmPerRow;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        View view = inflater.inflate(R.layout.cell_layout, parent, false);
        view.getLayoutParams().height = (int) (parent.getWidth() / filmPerRow *
                POSTER_ASPECT_RATIO);
        return new MovieViewHolder(view);
    }

    public List<Movie> changeCursor(List<Movie> moviesChenge) {
        if (movies == moviesChenge) {
            return null;
        }
        List<Movie> oldCursor = movies;
        movies = moviesChenge;
        if (moviesChenge != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Context context = holder.view.getContext();

        if (movies.size() > position) {
            Movie movie = movies.get(position);
            holder.textViewId.setText(movie.get_id());
            Glide.with(context)
                    .load(movie.getPosterPath())
                    .into(holder.imageView);
            holder.view.setOnClickListener(v -> {
                try {
                    onItemClickListener.sendDetails(Integer.parseInt(holder.textViewId.getText().toString()), onItemClickListener);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
            });
            holder.view.setOnLongClickListener(view -> {
                onItemClickListener.longClick(Integer.parseInt(holder.textViewId.getText().toString()), onItemClickListener);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }


    @Override
    public void onViewRecycled(@NonNull MovieViewHolder holder) {
        super.onViewRecycled(holder);
    }





    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        @BindView(R.id.ivCell)
        ImageView imageView;
        @BindView(R.id.textViewId)
        TextView textViewId;

        private MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }

    }

}