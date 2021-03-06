package com.example.themoviemb.ui.favorite;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import com.airbnb.lottie.LottieAnimationView;
import com.example.themoviemb.R;
import com.example.themoviemb.activities.DescriptionActivity;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.FavoriteTableHelper;
import com.example.themoviemb.data.MovieProvider;
import com.example.themoviemb.data.MovieTableHelper;
import com.example.themoviemb.data.models.Movie;
import com.example.themoviemb.interface_movie.DialogFavorite;
import com.example.themoviemb.interface_movie.ErrorZeroItem;
import com.example.themoviemb.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.OnItemClickListener, ErrorZeroItem , DialogFavorite.IFavoritDialog{

    private static final int LOADER_ID = 1;
    private static final String FILM_PER_ROW = "FILM_PER_ROW";
    private static final String SEARCHTEXT = "SEARCTEXT" ;
    private RemoveBadgeInterface listener;
    private static int filmPerRow=0;
    private FavoriteViewModel favoriteViewModel;
    private RecyclerView rvFavorite;
    private MoviesAdapter adapterFavorite;
    private RecyclerView.LayoutManager layoutManagerFavorite;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private TextView error;
    private LottieAnimationView lottieAnimationView;
    ProgressBar pbFavorite2;
    private String searchText;
    private ImageView imageView;
    private boolean isDarkMode = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            filmPerRow=4;
        }else{
            filmPerRow=2;
        }
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCHTEXT);
        }
        favoriteViewModel =
                ViewModelProviders.of(this).get(FavoriteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        pbFavorite2 = root.findViewById(R.id.pbFavorite2);
        rvFavorite = root.findViewById(R.id.rvMovies);
        error = root.findViewById(R.id.errorTextView);
        imageView=root.findViewById(R.id.imageView2);
        setNightMode();
        if(isDarkMode){
            imageView.setImageResource(R.drawable.technology);
        }

        lottieAnimationView=root.findViewById(R.id.deleteAppear);
        layoutManagerFavorite = new GridLayoutManager(getContext(), filmPerRow);

        rvFavorite.setLayoutManager(layoutManagerFavorite);
        adapterFavorite = new MoviesAdapter(null, this,filmPerRow ,getContext());
        rvFavorite.setAdapter(adapterFavorite);

        favoriteViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        Toolbar toolbar = root.findViewById(R.id.toolbarHome);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_favorite));
        rvFavorite = root.findViewById(R.id.rvMovies);
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("Animation:","start");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Animation:","end");
                lottieAnimationView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("Animation:","cancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("Animation:","repeat");
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
                  searchText = newText;
                    Cursor data = (getActivity()).getContentResolver().query(MovieProvider.JOIN_URI, null, MovieTableHelper.TITLE + " LIKE '%" + newText + "%' and " + FavoriteTableHelper.IS_FAVORITE + " = 1", null, null);
                    List<Movie> mArrayList = new ArrayList<>();
                    for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                        // The Cursor is now set to the right position
                        mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)),data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper._ID))));
                    }
                    adapterFavorite.changeCursor(mArrayList);
                    if(mArrayList.size() ==0){
                        setVisibleText(getString(R.string.error_zero_film_cerca));
                    }
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
        return new CursorLoader(getContext(), MovieProvider.JOIN_URI, null, FavoriteTableHelper.IS_FAVORITE + " = 1", null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        List<Movie> mArrayList = new ArrayList<>();
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)),data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper._ID))));
        }
        adapterFavorite.changeCursor(mArrayList);
        pbFavorite2.setVisibility(View.INVISIBLE);
        if (data.getCount() == 0) {
            setVisibleText(getString(R.string.no_film_favorite));

        }
        listener.deleteBadge();
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapterFavorite.changeCursor(null);
    }

    @Override
    public void sendDetails(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class );
        intent.putExtra("ID_MOVIE", id);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    public void longClick(int id, MoviesAdapter.OnItemClickListener onItemClickListener) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(MovieProvider.MOVIES_URI, null, MovieTableHelper._ID + " = " + id, null, null);
            if (cursor.moveToNext()) {
                DialogFavorite vDialog = new DialogFavorite(getString(R.string.dialogtitleiremove), getString(R.string.dilagotextremove) + " \"" + cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)) + "\" " + getString(R.string.dilagotextcomumfasle), id, true);
                vDialog.setmListener(FavoriteFragment.this);
                vDialog.show(getChildFragmentManager(), null);
            }
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }
    }

    @Override
    public void setVisibleText(String message) {
        error.setText(message);
        error.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (searchText != null)
            outState.putString(SEARCHTEXT, searchText);
        super.onSaveInstanceState(outState);
    }
    public void aggiornaLista(){
        Cursor data = (getActivity()).getContentResolver().query(MovieProvider.JOIN_URI, null, FavoriteTableHelper.IS_FAVORITE + " = 1", null, null);
        List<Movie> mArrayList = new ArrayList<>();
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(new Movie(data.getString(data.getColumnIndex(MovieTableHelper.TITLE)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper.DESCRIPTION)),data.getString(data.getColumnIndex(MovieTableHelper.COVER_PHOTO)),data.getString(data.getColumnIndex(MovieTableHelper._ID))));
        }
        adapterFavorite.changeCursor(mArrayList);
        adapterFavorite.notifyDataSetChanged();
        if(mArrayList.size() == 0){
            setVisibleText(getString(R.string.no_film_favorite));

        }
    }
    @Override
    public void onResponse(boolean aResponse, long aId, Boolean isRemoved) {
        if (aResponse) {
            if (!isRemoved) {
                ContentValues cv = new ContentValues();
                cv.put(FavoriteTableHelper.IS_FAVORITE, 1);
                getActivity().getContentResolver().update(MovieProvider.FAVORITE_URI, cv, FavoriteTableHelper.ID_MOVIE + " = " + aId, null);
            } else {
                ContentValues cv = new ContentValues();
                cv.put(FavoriteTableHelper.IS_FAVORITE, 0);
                getActivity().getContentResolver().update(MovieProvider.FAVORITE_URI, cv, FavoriteTableHelper.ID_MOVIE + " = " + aId, null);
                aggiornaLista();
                lottieAnimationView.setAnimation(R.raw.deleted);
                lottieAnimationView.setVisibility(View.VISIBLE);
                startAnimation();
            }
        }
    }

    private void startAnimation() {
        lottieAnimationView.playAnimation();
        lottieAnimationView.setSpeed(1.0F); // How fast does the animation play
        lottieAnimationView.setProgress(0F); // Starts the animation from 50% of the beginning
    }

    public  interface  RemoveBadgeInterface{
        void deleteBadge();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof RemoveBadgeInterface){
            listener= (RemoveBadgeInterface) context;
        }
        else
            listener=null;
    }
    private void setNightMode(){
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = false;
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = true;
                break;
        }
    }
}



