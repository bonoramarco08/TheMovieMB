package com.example.themoviemb.ui.home;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.themoviemb.EndlessRecyclerViewScrollListener;
import com.example.themoviemb.R;
import com.example.themoviemb.activities.DescriptionActivity;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;


public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnItemClickListener {

    private static final int LOADER_ID = 1;
    private int insert;
    int i = 1;
    private WebService webService;
    private HomeViewModel homeViewModel;
    private RecyclerView rvHome;
    private MoviesAdapter adapterHome;
    private RecyclerView.LayoutManager layoutManagerHome;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private boolean search = false;
    ProgressBar pbHome;

    // codice scrool
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            insert = 0;
            if (result != null) {
                try {
                    boolean b = true;
                    Cursor cursor = new CursorLoader(getActivity().getApplicationContext(), MovieProvider.MOVIES_URI, null, null, null, null).loadInBackground();
                    for (Movie movie : result.getResult()) {
                        b = true;
                        for (int y = 0; y < cursor.getCount(); y++) {
                            cursor.moveToPosition(y);
                            if (cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)).equals(movie.getTitle())) {
                                {
                                    b = false;
                                    y  = cursor.getCount();
                                }
                            }
                        }
                        if (b){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MovieTableHelper.TITLE, movie.getTitle());
                            contentValues.put(MovieTableHelper.COVER_PHOTO, movie.getPosterPath());
                            contentValues.put(MovieTableHelper.DESCRIPTION, movie.getOverview());
                            contentValues.put(MovieTableHelper.DESCRIPTION_PHOTO, movie.getBackdropPath());
                            Uri r=  getActivity().getContentResolver().insert(MovieProvider.MOVIES_URI, contentValues);
                            long id = Long.parseLong(r.getLastPathSegment());
                            ContentValues contentValuesFavorite = new ContentValues();
                            contentValuesFavorite.put(FavoriteTableHelper.ID_MOVIE,id);
                            contentValuesFavorite.put(FavoriteTableHelper.IS_FAVORITE, 0);
                            getActivity().getContentResolver().insert(MovieProvider.FAVORITE_URI, contentValuesFavorite);
                            insert++;
                        }
                    }
                } catch (Exception e) {
                }
            }

        }
    };


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rvHome = root.findViewById(R.id.rvMovies);
        layoutManagerHome = new GridLayoutManager(getContext(), 2);
        pbHome = root.findViewById(R.id.pbHome);
        rvHome.setLayoutManager(layoutManagerHome);
        adapterHome = new MoviesAdapter(null, this);
        rvHome.setAdapter(adapterHome);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        Toolbar toolbar = root.findViewById(R.id.toolbarHome);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        rvHome = root.findViewById(R.id.rvMovies);
        webService = WebService.getInstance();


        // codice scrool
        rvHome.setOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManagerHome) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(!search)
                new BackgroundTask().execute();
            }
        });

        return root;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
              if(newText.equals("")){
                  search=false;
              }
              else {
                  search = true;}
                  Cursor cursor = (getActivity()).getContentResolver().query(MovieProvider.MOVIES_URI, null, MovieTableHelper.TITLE + " LIKE '%" + newText + "%'", null, null);
                  adapterHome.changeCursor(cursor);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getContext(), MovieProvider.MOVIES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapterHome.changeCursor(data);
        pbHome.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapterHome.changeCursor(null);
    }

    @Override
    public void sendDetails(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class);
        intent.putExtra("ID_MOVIE", id);
        startActivity(intent);
    }

    @Override
    public void longClick(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(MovieProvider.JOIN_URI, null, MovieTableHelper.TABLE_NAME+"."+MovieTableHelper._ID + " = " + id, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(FavoriteTableHelper.IS_FAVORITE)) == 0) {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleinsert), getString(R.string.dilagotextinsert) +" \"" +  cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) +" \"" + getString(R.string.dilagotextcomum), id, false);
                    vDialog.show(getChildFragmentManager(), null);
                } else {
                    DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleiremove), getString(R.string.dilagotextremove) +" \"" +  cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) +"\" " + getString(R.string.dilagotextcomum), id, true);
                    vDialog.show(getChildFragmentManager(), null);
                }
            }
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }


    }


    // codice scrool

    public int insertScroll(int page) {
        webService.getMoviesPage(webServerListener, page);
        return  insert;
    }

    private class BackgroundTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbHome.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try {
                while(insert < 2) {
                    insertScroll(i);
                    Thread.sleep(500);
                    i++;
                }
                insert = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Lavoro Terminato!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            adapterHome.changeCursor(new CursorLoader(getContext(), MovieProvider.MOVIES_URI, null, null, null, null).loadInBackground());
            adapterHome.notifyDataSetChanged();
            super.onPostExecute(result);

        }
    }
}
