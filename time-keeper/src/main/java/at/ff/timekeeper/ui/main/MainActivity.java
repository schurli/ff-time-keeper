package at.ff.timekeeper.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.model.Navigation;
import at.ff.timekeeper.ui.HasOnBackPressed;
import at.ff.timekeeper.ui.main.fragment.HomeFragment;
import at.ff.timekeeper.vm.MainViewModel;
import at.ff.timekeeper.vm.ViewModelFactory;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity {

    private MainActivity activity;
    private FragmentManager fragmentManager;
    private HasOnBackPressed contentFragment;

    private ViewModelFactory viewModelFactory;

    private MainViewModel viewModel;
    private Navigation navigation;

    private View elementAppBar;
    private ImageButton elementSettingButton;
    private ImageButton elementBackButton;

    @Inject
    public void setViewModelFactory(ViewModelFactory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        activity = this;
        fragmentManager = getSupportFragmentManager();
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(MainViewModel.class);

        setContentView(R.layout.activity_main);

        elementAppBar = findViewById(R.id.element_app_bar);
        elementSettingButton = findViewById(R.id.element_app_bar_setting);
        elementBackButton = findViewById(R.id.element_app_bar_back);

        elementSettingButton.setOnClickListener(v -> viewModel.setNavigation(Navigation.SYSTEM_SETTING));
        elementBackButton.setOnClickListener(v -> viewModel.setNavigation(Navigation.HOME));

        bind();

    }

    private void bind() {

        viewModel.getNavigation().observe(activity, navigation -> {
            Timber.d("getNavigation(%s)", navigation);
            if (navigation == null) {
                navigation = Navigation.HOME;
            }
            this.navigation = navigation;
            fragment(this.navigation);
        });

    }

    private void fragment(Navigation navigation) {

        Timber.d("fragment(%s)", navigation.name());

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (navigation) {
            default:
                hideAppBar();
                hideSetting();
                break;
        }

        switch (navigation) {

            case HOME:
                showSetting();
                contentFragment = new HomeFragment();
                break;
            case SYSTEM_SETTING:
                hideSetting();
                // todo contentFragment = new SystemSettingFragment();
                break;
            default:
                Timber.e("unknown Navigation %s", navigation.toString());
                fragment(Navigation.HOME);
                return;

        }

        transaction
                .replace(R.id.content, (Fragment) contentFragment)
                .commit();

    }

    private void showSetting() {
        elementSettingButton.setVisibility(View.VISIBLE);
        elementBackButton.setVisibility(View.GONE);
    }

    private void hideSetting() {
        elementSettingButton.setVisibility(View.GONE);
        elementBackButton.setVisibility(View.VISIBLE);
    }

    private void showAppBar() {
        elementAppBar.setVisibility(View.VISIBLE);
    }

    private void hideAppBar() {
        elementAppBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        contentFragment.onBackPressed();
    }


}
