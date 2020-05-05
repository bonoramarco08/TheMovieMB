package com.example.themoviemb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.themoviemb.R;
import com.example.themoviemb.activities.HomeActivity;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter per la recycleview
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private static final float POSTER_ASPECT_RATIO = 1.5f;
    private List<Movie> movies;
    private int filmPerRow;
    private CardView cvMovie;
    private Context context;

    private OnItemClickListener onItemClickListener;

    /**
     * interfaccia per il click e il longclick sulla cella
     */
    public interface OnItemClickListener {
        void sendDetails(int id, OnItemClickListener onItemClickListener);

        void longClick(int id, OnItemClickListener onItemClickListener);
    }

    /**
     * Costruttore con tutti i campi
     * @param movies lista dei film da inserire nella recycleview
     * @param onItemClickListener istanza dell' interfaccia per il click e il longclick
     * @param filmPerRow numero di film per riga (2 in portrait e 4 in landscape)
     * @param context il context della recycleview
     */
    public MoviesAdapter(List<Movie> movies, OnItemClickListener onItemClickListener, int filmPerRow ,Context context) {
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
        this.filmPerRow=filmPerRow;
        this.context = context;
    }

    /**
     * imposta il numero di film per riga e imposta il layout della cella
     * @param parent
     * @param viewType
     * @return il viewHolder con la cella personalizzata
     */
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

    /**
     * medoto che cabia la lista dei film , controlla che il parametro passsato non sia null o che non sia uguale a quello già impostato
     * @param moviesChenge list di movie da inserire
     * @return restituisce la lista di movie ( o quella attuale o quella "nuova" dopo aver effettuato i dovuti controlli)
     */
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

    /**
     *  Inserisco nella cella i vaalori del film , attraverso glide imposto un immagine di placeholder e faccio il load per l'immagine del film
     *  e richiamo l'interfaccia del long e del click sulla card view dellla cella
     * @param holder
     * @param position la positione della cella nella recycleview
     */
    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Context context = holder.view.getContext();
        if (movies.size() > position) {
            Movie movie = movies.get(position);
            holder.textViewId.setText(movie.get_id());
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(10);
            circularProgressDrawable.setCenterRadius(40);
            circularProgressDrawable.setColorSchemeColors(R.color.colorPrimary , R.color.colorPrimary);
            circularProgressDrawable.start();

            Glide.with(context)
                    .load(movie.getPosterPath())
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.error_image)
                    .into(holder.imageView);
            holder.cvMovie.setOnClickListener(view -> {
                try {
                    onItemClickListener.sendDetails(Integer.parseInt(holder.textViewId.getText().toString()), onItemClickListener);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                }
            });
            holder.cvMovie.setOnLongClickListener(view->{
                onItemClickListener.longClick(Integer.parseInt(holder.textViewId.getText().toString()), onItemClickListener);
                return true;
            });
        }
    }

    /**
     *
     * @return restituisce il numero di movie
     */
    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }


    @Override
    public void onViewRecycled(@NonNull MovieViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /**
     * crea il viewHolde e faccio il bind con la cella scelta prima sui vari elementi
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        @BindView(R.id.ivCell)
        ImageView imageView;
        @BindView(R.id.textViewId)
        TextView textViewId;
        @BindView(R.id.cdMovie)
        CardView cvMovie;

        /**
         * costruttore del viewHolder
         * @param view
         */
        private MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

        }

    }

}