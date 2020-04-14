package com.example.themoviemb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.themoviemb.R;
import com.example.themoviemb.data.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private static final float POSTER_ASPECT_RATIO = 1.5f;

    private final List<Movie> movies;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void send_details(Movie movie, int position);
    }

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        View view = inflater.inflate(R.layout.cell_layout, parent, false);
        final Context context = view.getContext();





        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        final Context context = holder.view.getContext();

        holder.movie = movie;
        Glide   .with(context)
                .load(movie.getPosterPath())
                .into(holder.imageView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.send_details(movie,holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp();
    }

    //Inner Class
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        public Movie movie;

        @BindView(R.id.ivCell)
        ImageView imageView;
        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }
        //Other methods
        public void cleanUp() {
            final Context context = view.getContext();
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.INVISIBLE);
        }

    }
    public void add(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

}