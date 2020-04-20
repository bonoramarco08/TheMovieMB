package com.example.themoviemb.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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


import com.example.themoviemb.R;
import com.example.themoviemb.activities.DescriptionActivity;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.interface_movie.DialogFavorite;


public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnItemClickListener {

    private static final int LOADER_ID = 1;
    private HomeViewModel homeViewModel;
    private RecyclerView rvHome;
    private MoviesAdapter adapterHome;
    private RecyclerView.LayoutManager layoutManagerHome;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rvHome = root.findViewById(R.id.rvMovies);
        layoutManagerHome = new GridLayoutManager(getContext(), 2);

        rvHome.setLayoutManager(layoutManagerHome);
        adapterHome = new MoviesAdapter(null, this);
        rvHome.setAdapter(adapterHome);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        Toolbar toolbar = root.findViewById(R.id.toolbarHome);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        rvHome = root.findViewById(R.id.rvMovies);
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
                    Cursor cursor = (getActivity()).getContentResolver().query(MovieProvider.MOVIES_URI, null, MovieTableHelper.TITLE + " LIKE '%" + newText+"%'", null, null);
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
                // Not implemented here
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
    public void longClick(int id, String titolo, MoviesAdapter.OnItemClickListener onItemClickListener) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(MovieProvider.MOVIES_URI, null, MovieTableHelper._ID + " = " + id, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.IS_FAVORITE)) == 0) {
                    DialogFavorite vDialog = new DialogFavorite("Aggiunta", "Vuoi aggiungere " + titolo + " dai tuoi film preferiti?", id, false);
                    vDialog.show(getChildFragmentManager(), null);
                } else {
                    DialogFavorite vDialog = new DialogFavorite("Rimozione", "Vuoi rimuovere " + titolo + " dai tuoi film preferiti?", id, true);
                    vDialog.show(getChildFragmentManager(), null);
                }
            }
        }catch (NullPointerException e){
            Log.d("Error", e.getMessage());
        }


    }



}
