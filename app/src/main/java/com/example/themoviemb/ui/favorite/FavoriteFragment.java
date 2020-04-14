package com.example.themoviemb.ui.favorite;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themoviemb.R;
import com.example.themoviemb.adapters.SpacesItemDecoration;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;

public class FavoriteFragment extends Fragment {

    private FavoriteViewModel dashboardViewModel;
    private RecyclerView rvFavorite;
    private RecyclerView.Adapter adapterFavorite;
    private RecyclerView.LayoutManager layoutManagerFavorite;
    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            Log.d("TAGGGGG", result.getResult().get(1).getTitle());
            layoutManagerFavorite=new GridLayoutManager(getContext(),2);
            adapterFavorite=new MoviesAdapter(result.getResult());
            //to get width and height of device
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

            rvFavorite.addItemDecoration(new SpacesItemDecoration(displayMetrics.widthPixels/20));
            rvFavorite.setLayoutManager(layoutManagerFavorite);
            rvFavorite.setAdapter(adapterFavorite);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(FavoriteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        webService = WebService.getInstance();
        rvFavorite=root.findViewById(R.id.rvMovies);

        loadMovies();
        return root;
    }
    private void loadMovies() {
        webService.getMovies(webServerListener);
    }
}
