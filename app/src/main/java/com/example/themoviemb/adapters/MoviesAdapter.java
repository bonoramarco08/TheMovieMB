package com.example.themoviemb.adapters;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private static final float POSTER_ASPECT_RATIO = 1.5f;
    private Cursor cursor;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void sendDetails(int position, OnItemClickListener onItemClickListener);

        void longClick(int position, String titolo, OnItemClickListener onItemClickListener);
    }

    public MoviesAdapter(Cursor cursor, OnItemClickListener onItemClickListener) {
        this.cursor = cursor;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        View view = inflater.inflate(R.layout.cell_layout, parent, false);
        view.getLayoutParams().height = (int) (parent.getWidth() / 2 *
                POSTER_ASPECT_RATIO);
        return new MovieViewHolder(view);
    }

    public Cursor changeCursor(Cursor dataCursor) {
        if (cursor == dataCursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        cursor = dataCursor;
        if (dataCursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        final Context context = holder.view.getContext();

        if (cursor.moveToPosition(position)) {
            holder.textViewId.setText(cursor.getString(cursor.getColumnIndex(MovieTableHelper._ID)));
            Glide.with(context)
                    .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.COVER_PHOTO)))
                    .into(holder.imageView);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onItemClickListener.sendDetails(Integer.parseInt(holder.textViewId.getText().toString()), onItemClickListener);
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                }
            });
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.longClick(Integer.parseInt(holder.textViewId.getText().toString()), "Film", onItemClickListener);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
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