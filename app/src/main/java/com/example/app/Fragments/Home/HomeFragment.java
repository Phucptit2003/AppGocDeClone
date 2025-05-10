package com.example.app.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.app.Activities.Home.FindActivity;
import com.example.app.Adapters.Home.TechBaloAdapter;
import com.example.app.databinding.FragmentHomeBinding;

import com.google.android.material.tabs.TabLayoutMediator;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private String userId;

    public HomeFragment(String id) {
        userId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        initUI();

        return binding.getRoot();
    }

    private void initUI() {
        TechBaloAdapter adapter1=new TechBaloAdapter(HomeFragment.this, userId);
        binding.viewpaperHome.setAdapter(adapter1);
        binding.viewpaperHome.setUserInputEnabled(false);
        binding.layoutSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), FindActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        new TabLayoutMediator(binding.tabHome,binding.viewpaperHome, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Technology Accessories");
                    break;
                case 1:
                    tab.setText("Balo");
                    break;
            }
        })).attach();
    }
}