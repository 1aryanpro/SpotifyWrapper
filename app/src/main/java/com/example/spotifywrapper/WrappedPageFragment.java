package com.example.spotifywrapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class WrappedPageFragment extends Fragment {

    private View view;

    public static WrappedPageFragment newInstance(View view) {
        WrappedPageFragment fragment = new WrappedPageFragment();
        fragment.view = view;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }
}