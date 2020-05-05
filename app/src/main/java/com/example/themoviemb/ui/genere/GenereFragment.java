package com.example.themoviemb.ui.genere;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;
import com.example.themoviemb.activities.DescriptionActivity;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.GenreMovieTableHelper;
import com.example.themoviemb.data.GenreTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.ErrorZeroItem;

import java.util.ArrayList;
import java.util.List;

public class GenereFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnItemClickListener, DialogFavorite.IFavoritDialog, ErrorZeroItem {


    private static final int LOADER_ID = 1;
    private int filmPerRow;
    private RecyclerView rvGenre;
    private MoviesAdapter adapterGenre;
    private RecyclerView.LayoutManager layoutManagerGenre;
    private SearchView.OnQueryTextListener queryTextListener;
    private boolean search = false;
    private LottieAnimationView lottieAnimationView;
    private AddOrCreateBadge listener;
    ProgressBar pbHome;
    TextView tvHome;
    private Toolbar toolbar;

    /**
     * isPortrait()
     * true->telefono in portrait
     * false->telefono in landscape
     * setViews()
     * findviewbyid degli elementi
     * setRecyclerView()
     * configurazione della recyclerview e settaggio dell'adapter
     * WebService.getInstance()
     * prendo l'istanza del webservice (singleton)
     * addOnScrollListener
     * scarico i film divisi in page, quando arrivo alla fine della page, inizia a caricarmi la page seguente
     */
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        filmPerRow = (isPortrait()) ? setPortrait() : setLandscape();
        GenereViewModel genereViewModel = ViewModelProviders.of(this).get(GenereViewModel.class);
        View root = inflater.inflate(R.layout.genere_fragment, container, false);
        setViews(root);
        setRecyclerView(root);
        Toolbar toolbar = root.findViewById(R.id.toolbarHome);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_genere));
        LinearLayout linearLayout = root.findViewById(R.id.linear);
        Cursor c = new CursorLoader(getContext(), MovieProvider.GENRE_URI, null, null, null, null).loadInBackground();
        Button[] chip = new Button[c.getCount()];
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            chip[i] = new Button(getContext(), null, R.attr.materialButtonStyle);
            chip[i].setText(c.getString(c.getColumnIndex(GenreTableHelper.TEXT_GENRE)));
            chip[i].setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            chip[i].setId(c.getInt(c.getColumnIndex(GenreTableHelper.ID_GENRE)));
            chip[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            chip[i].setBackground(ContextCompat.getDrawable(chip[i].getContext(), R.drawable.ripple_effect_button));
            linearLayout.addView(chip[i]);
            chip[i].setOnClickListener(this::onClick);
        }
        return root;
    }

    private boolean isPortrait() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? false : true;
    }

    private int setPortrait() {
        return 2;
    }

    private int setLandscape() {
        return 4;
    }

    private void setViews(View root) {
        rvGenre = root.findViewById(R.id.rvMovies);
        tvHome = root.findViewById(R.id.errorTextView);
        toolbar = root.findViewById(R.id.toolbarHome);
        lottieAnimationView = root.findViewById(R.id.heartAppear);
    }

    private void setRecyclerView(View root) {
        layoutManagerGenre = new GridLayoutManager(getActivity().getApplicationContext(), filmPerRow);
        pbHome = root.findViewById(R.id.pbFavorite2);
        rvGenre.setLayoutManager(layoutManagerGenre);
        adapterGenre = new MoviesAdapter(null, this, filmPerRow, getContext());
        rvGenre.setAdapter(adapterGenre);
    }


    /**
     * setHasOptionsMenu(true)
     * In modo da avere la searchview
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    /**
     * Inizializzazione del loader
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    /**
     * Creo il Loader
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), MovieProvider.MOVIES_URI, null, null, null, null);
    }

    /**
     * Una volta finito il caricamento...
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        List<Movie> mArrayList = new ArrayList<>();
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)),
                    data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)),
                    data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)),
                    data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)),
                    data.getString(data.getColumnIndex(MovieTableHelper._ID))));
        }
        adapterGenre.changeCursor(mArrayList);

        pbHome.setVisibility(View.GONE);
        if (data.getCount() == 0) {
            setVisibleText(getString(R.string.no_film_favorite));
        } else
            tvHome.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapterGenre.changeCursor(null);
    }

    /**
     * Al click di un film, vengo reindirizzato alla descrizione
     */
    @Override
    public void sendDetails(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
        intent.putExtra("ID_MOVIE", id);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    /**
     * Al long click mi si apre un dialog se voglio aggiungere ai preferiti o rimuovere in caso sia stato già aggiunto
     */
    @Override
    public void longClick(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(MovieProvider.JOIN_URI, null, MovieTableHelper.TABLE_NAME + "." + MovieTableHelper._ID + " = " + id, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(FavoriteTableHelper.IS_FAVORITE)) == 0) {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleinsert), getString(R.string.dilagotextinsert) + " \"" + cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) + " \"" + getString(R.string.dilagotextcomum), id, false);
                    vDialog.setmListener(GenereFragment.this);
                    vDialog.show(getChildFragmentManager(), null);
                } else {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleiremove), getString(R.string.dilagotextremove) + " \"" + cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) + "\" " + getString(R.string.dilagotextcomumfasle), id, true);
                    vDialog.setmListener(GenereFragment.this);
                    vDialog.show(getChildFragmentManager(), null);
                }
            }
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }


    }

    /**
     * Gestione della risposta dell'utente al dialog che compare
     * Quando aggiungo un film ai preferiti,
     */
    @Override
    public void onResponse(boolean aResponse, long aId, Boolean isRemoved) {
        if (aResponse) {
            if (!isRemoved) {
                ContentValues cv = new ContentValues();
                cv.put(FavoriteTableHelper.IS_FAVORITE, 1);
                getActivity().getContentResolver().update(MovieProvider.FAVORITE_URI, cv, FavoriteTableHelper.ID_MOVIE + " = " + aId, null);
                startAnimation();
                listener.createOrAddBadge();
            } else {
                ContentValues cv = new ContentValues();
                cv.put(FavoriteTableHelper.IS_FAVORITE, 0);
                getActivity().getContentResolver().update(MovieProvider.FAVORITE_URI, cv, FavoriteTableHelper.ID_MOVIE + " = " + aId, null);
            }
        }
    }

    private void startAnimation() {
        lottieAnimationView.playAnimation();
        lottieAnimationView.setSpeed(1.0F); // How fast does the animation play
        lottieAnimationView.setProgress(0F); // Starts the animation from 50% of the beginning
    }

    private void stopAnimation() {
        lottieAnimationView.cancelAnimation(); // Cancels the animation
    }

    @Override
    public void setVisibleText(String message) {
        tvHome.setVisibility(View.VISIBLE);
        tvHome.setText(getString(R.string.error_zero_film_home));
    }

    public void setText() {
        tvHome.setVisibility(View.VISIBLE);
        tvHome.setText(getString(R.string.nofilm));
    }

    /**
     * Quando aggiungo un film ai preferiti, si può notare che compare il badge*/
    public interface AddOrCreateBadge {
        void createOrAddBadge();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddOrCreateBadge) {
            listener = (AddOrCreateBadge) context;
        } else
            listener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view) {
        Cursor dataGenre = (getActivity()).getContentResolver().query(MovieProvider.ALL_GENRE_JOIN_URI, null, GenreTableHelper.TABLE_NAME + "." + GenreTableHelper.ID_GENRE + " = " + view.getId(), null, null);
        String genre = "";
        Button b = (Button)view;
        toolbar.setTitle(getString(R.string.title_genere)+":"+b.getText().toString());
        if (dataGenre.getCount() >= 1) {
            tvHome.setVisibility(View.INVISIBLE);
            dataGenre.moveToFirst();
            genre = MovieTableHelper._ID + " = " + dataGenre.getString(dataGenre.getColumnIndex(GenreMovieTableHelper.ID_MOVIE));
            if (dataGenre.getCount() > 1) {
                dataGenre.moveToNext();
                for (; !dataGenre.isAfterLast(); dataGenre.moveToNext()) {
                    genre = genre + " or " + MovieTableHelper._ID + " = " + dataGenre.getString(dataGenre.getColumnIndex(GenreMovieTableHelper.ID_MOVIE));
                }
            }
            Cursor data = (getActivity()).getContentResolver().query(MovieProvider.MOVIES_URI, null, genre, null, null);
            List<Movie> mArrayList = new ArrayList<>();
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                // The Cursor is now set to the right position
                mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)), data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)), data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)), data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)), data.getString(data.getColumnIndex(MovieTableHelper._ID))));
            }
            adapterGenre.changeCursor(mArrayList);
            adapterGenre.notifyDataSetChanged();
        } else {
            List<Movie> mArrayList = new ArrayList<>();
            adapterGenre.changeCursor(mArrayList);
            adapterGenre.notifyDataSetChanged();
            setText();
        }
    }
}