package com.example.themoviemb.ui.home;

import android.os.Bundle;
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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private GridView gridViewHome;
    private RecyclerView rvHome;
    private RecyclerView.Adapter adapterHome;
    private RecyclerView.LayoutManager layoutManagerHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridViewHome=root.findViewById(R.id.gvHome);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        rvHome=root.findViewById(R.id.rvMovies);
        layoutManagerHome=new GridLayoutManager(getContext(),2);
        adapterHome=new MoviesAdapter(this, fetchedData);
        rvHome.setAdapter(adapterHome);
        return root;
    }
}
