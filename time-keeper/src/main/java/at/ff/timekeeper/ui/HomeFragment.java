package at.ff.timekeeper.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import run.male.music.R;


public class HomeFragment extends ViewModelFragment {

    private FragmentActivity activity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        getViewModel().getLocation().observe(activity, location -> {
            unbind();
            bind();
        });

        return rootView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbind();
    }

    @Override
    public void bind() {

    }

    @Override
    public void unbind() {
    }

}
