package com.example.themoviemb.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themoviemb.R;
import com.example.themoviemb.adapters.MoviesAdapter;
import com.example.themoviemb.data.models.Result;
import com.example.themoviemb.interface_movie.IWebServer;
import com.example.themoviemb.networks.WebService;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView rvHome;
    private RecyclerView.Adapter adapterHome;
    private RecyclerView.LayoutManager layoutManagerHome;
    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage) {
            Log.d("TAGGGGG", result.getResult().get(1).getTitle());
            layoutManagerHome=new GridLayoutManager(getContext(),2);
            adapterHome=new MoviesAdapter(result.getResult());
            rvHome.setLayoutManager(layoutManagerHome);
            rvHome.setAdapter(adapterHome);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        rvHome=root.findViewById(R.id.rvMovies);

        //loadMovies();
        return root;
    }

    /*
    private void loadMovies() {
        webService.getMovies(webServerListener);
    }

     */
}
