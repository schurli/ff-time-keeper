package at.ff.timekeeper.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import at.ff.timekeeper.vm.ViewModelFactory;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import dagger.android.support.AndroidSupportInjection;

public abstract class DaggerFragment extends Fragment implements HasAndroidInjector {

    @Inject
    public ViewModelFactory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    public FragmentActivity activity;
    public FragmentManager fragmentManager;


    @Override
    public void onAttach(@NonNull Context context) {
        if (viewModelFactory == null) {
            AndroidSupportInjection.inject(this);
        }
        super.onAttach(context);
        activity = getActivity();
        fragmentManager = getChildFragmentManager();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

}
