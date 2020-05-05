package com.example.themoviemb.ui.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.EndlessRecyclerViewScrollListener;
import com.example.themoviemb.R;
import com.example.themoviemb.VerificaInternet;
import com.example.themoviemb.activities.DescriptionActivity;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.GenreMovieTableHelper;
import com.example.themoviemb.data.GenreTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Genres;
import com.example.themoviemb.data.models.GenresList;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.ErrorZeroItem;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnItemClickListener, DialogFavorite.IFavoritDialog, ErrorZeroItem {

    private static final int LOADER_ID = 1;
    private static final String SEARCHTEXT = "SEARCHTEXT";
    private int filmPerRow;
    private WebService webService;
    private RecyclerView rvHome;
    private MoviesAdapter adapterHome;
    private RecyclerView.LayoutManager layoutManagerHome;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private boolean search = false;
    private LottieAnimationView lottieAnimationView;
    private AddOrCreateBadge listener;
    ProgressBar pbHome, getPbHomeStart;
    TextView tvHome;
    private String searchText;
    private Toolbar toolbar;
    // codice scrool
    private IWebServer webServerListener =new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            if (result != null) {
                for (Movie movie : result.getResult()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieTableHelper.TITLE, movie.getTitle());
                    contentValues.put(MovieTableHelper.COVER_PHOTO, movie.getPosterPath());
                    contentValues.put(MovieTableHelper.DESCRIPTION, movie.getOverview());
                    contentValues.put(MovieTableHelper.DESCRIPTION_PHOTO, movie.getBackdropPath());
                    contentValues.put(MovieTableHelper.ID_FILM, movie.getIdFilm());
                    Uri r = getActivity().getContentResolver().insert(MovieProvider.MOVIES_URI, contentValues);
                    long id = Long.parseLong(r.getLastPathSegment());
                    ContentValues contentValuesFavorite = new ContentValues();
                    contentValuesFavorite.put(FavoriteTableHelper.ID_MOVIE, id);
                    contentValuesFavorite.put(FavoriteTableHelper.IS_FAVORITE, 0);
                    getActivity().getContentResolver().insert(MovieProvider.FAVORITE_URI, contentValuesFavorite);
                    for (int i = 0 ; i<movie.getGenres().length ; i++){
                        ContentValues contentValuesGenre = new ContentValues();
                        contentValuesGenre.put(GenreMovieTableHelper.ID_MOVIE, id);
                        contentValuesGenre.put(GenreMovieTableHelper.ID_GENRE, movie.getGenres()[i]);
                        getActivity().getContentResolver().insert(MovieProvider.GENREMOVIE_URI, contentValuesGenre);
                    }
                }
            }
        }

        @Override
        public void onGeneresFetched(boolean success, GenresList result, int errorCode, String errorMessage) {
            return;
        }
    };

    /**
     * isPortrait()
     *              true->telefono in portrait
     *              false->telefono in landscape
     * setViews()
     *              findviewbyid degli elementi
     * setRecyclerView()
     *              configurazione della recyclerview e settaggio dell'adapter
     * WebService.getInstance()
     *              prendo l'istanza del webservice (singleton)
     * addOnScrollListener
     *              scarico i film divisi in page, quando arrivo alla fine della page, inizia a caricarmi la page seguente
     * */
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        filmPerRow=(isPortrait())?setPortrait():setLandscape();

        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setViews(root);
        setRecyclerView(root);


        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCHTEXT);
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        webService = WebService.getInstance();

        // codice scrool
        rvHome.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManagerHome) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!search) {
                    if (!VerificaInternet.getConnectivityStatusString(getContext())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialog);
                        builder.setMessage(R.string.dialog_message_homeInternet)
                                .setTitle(R.string.dialog_title)
                                .setNeutralButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        pbHome.setVisibility(View.VISIBLE);
                        insertScroll(page + 1);
                        Cursor data = (getActivity()).getContentResolver().query(MovieProvider.MOVIES_URI, null, null, null, null);
                        List<Movie> mArrayList = new ArrayList<>();
                        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                            // The Cursor is now set to the right position
                            mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)),
                                    data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)),
                                    data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)),
                                    data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)),
                                    data.getString(data.getColumnIndex(MovieTableHelper._ID))));
                        }
                        adapterHome.changeCursor(mArrayList);
                    }
                }
            }
        });
        return root;
    }

    private void insertScroll(int page) {
        webService.getMoviesPage(webServerListener, page, getString(R.string.lingua));
    }

    private boolean isPortrait(){
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)?  false : true;
    }
    private int setPortrait(){
        return 2;
    }
    private int setLandscape(){
        return 4;
    }

    private void setViews(View root) {
        rvHome = root.findViewById(R.id.rvMovies);
        tvHome = root.findViewById(R.id.errorTextView);
        getPbHomeStart = root.findViewById(R.id.pbHomeStart);
        toolbar = root.findViewById(R.id.toolbarHome);
        lottieAnimationView = root.findViewById(R.id.heartAppear);
    }

    private void setRecyclerView(View root) {
        layoutManagerHome = new GridLayoutManager(getActivity().getApplicationContext(), filmPerRow);
        pbHome = root.findViewById(R.id.pbHome);
        rvHome.setLayoutManager(layoutManagerHome);
        adapterHome = new MoviesAdapter(null, this, filmPerRow, getContext());
        rvHome.setAdapter(adapterHome);
    }


    /**
     * setHasOptionsMenu(true)
     *          In modo da avere la searchview*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    /**
     * Qui creo il filtro ricerca, che andrà a eseguire una query sul db locale e imposterà il nuovo cursor sulla lista*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    Cursor data = (getActivity()).getContentResolver().query(MovieProvider.JOIN_URI, null, MovieTableHelper.TITLE + " LIKE '%" + newText + "%'", null, null);
                    List<Movie> mArrayList = new ArrayList<>();
                    for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                        // The Cursor is now set to the right position
                        mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)), data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)), data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)), data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)), data.getString(data.getColumnIndex(MovieTableHelper._ID))));
                    }
                    adapterHome.changeCursor(mArrayList);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        if (searchText != null && !searchText.equals("")) {
            searchItem.expandActionView();
            searchView.setQuery(searchText, false);
            searchView.clearFocus();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * Quale elemento della toolbar vado a cliccare (in questo caso ce n'è solo uno)*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inizializzazione del loader*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    /**
     * Creo il Loader*/
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), MovieProvider.MOVIES_URI, null, null, null, null);
    }

    /**
     * Una volta finito il caricamento...*/
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
        adapterHome.changeCursor(mArrayList);

        pbHome.setVisibility(View.GONE);
        getPbHomeStart.setVisibility(View.GONE);
        if (data.getCount() == 0) {
            setVisibleText(getString(R.string.no_film_favorite));
        } else
            tvHome.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapterHome.changeCursor(null);
    }

    /**
     * Al click di un film, vengo reindirizzato alla descrizione*/
    @Override
    public void sendDetails(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
        intent.putExtra("ID_MOVIE", id);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    /**
     * Al long click mi si apre un dialog se voglio aggiungere ai preferiti o rimuovere in caso sia stato già aggiunto
     * */
    @Override
    public void longClick(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(MovieProvider.JOIN_URI, null, MovieTableHelper.TABLE_NAME + "." + MovieTableHelper._ID + " = " + id, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(FavoriteTableHelper.IS_FAVORITE)) == 0) {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleinsert), getString(R.string.dilagotextinsert) + " \"" + cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) + " \"" + getString(R.string.dilagotextcomum), id, false);
                    vDialog.setmListener(HomeFragment.this);
                    vDialog.show(getChildFragmentManager(), null);
                } else {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleiremove), getString(R.string.dilagotextremove) + " \"" + cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) + "\" " + getString(R.string.dilagotextcomumfasle), id, true);
                    vDialog.setmListener(HomeFragment.this);
                    vDialog.show(getChildFragmentManager(), null);
                }
            }
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }


    }

    /**
     * Gestione della risposta dell'utente al dialog che compare
     * Quando aggiungo un film ai preferiti, */
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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (searchText != null && searchText != "")
            outState.putString(SEARCHTEXT, searchText);
        super.onSaveInstanceState(outState);
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
}
