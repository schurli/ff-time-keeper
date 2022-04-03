package at.ff.timekeeper.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;

import javax.inject.Inject;

import at.ff.timekeeper.data.Navigation;
import at.ff.timekeeper.vm.BasicViewModel;
import at.ff.timekeeper.vm.HomeViewModel;
import at.ff.timekeeper.vm.ViewModelFactory;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import run.male.music.R;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 27;

    private static final int REQUEST_PERMISSIONS = 70;

    private static final String[] PERMISSIONS;

    static {
        // todo add all app permissions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            PERMISSIONS = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        } else {
            PERMISSIONS = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        }
    }

    private MainActivity activity;
    private FragmentManager fragmentManager;
    private IViewModelFragment contentFragment;

    private ViewModelFactory viewModelFactory;
    private BluetoothAdapter bluetoothAdapter;
    private BasicViewModel viewModel;

    private ImageButton elementBackButton;
    private ImageButton elementSettingButton;

    private Navigation.Location location;


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
        viewModel = getViewModel(Navigation.Location.HOME);

        setContentView(R.layout.activity_main);

        elementSettingButton = findViewById(R.id.element_app_bar_setting);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        elementBackButton.setOnClickListener(v -> viewModel.onBack());
        elementSettingButton.setOnClickListener(v -> viewModel.onSetting());

        viewModel.getLocation().observe(activity, location -> {
            if (location == null) {
                location = Navigation.Location.HOME;
            }
            unbind();
            this.location = location;
            viewModel = getViewModel(location);
            fragment(location);
            Timber.d("new location %s", location.name());
            contentFragment.setViewModel(viewModel);
            bind();
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (this.location != null) {
            fragment(this.location);
            contentFragment.setViewModel(viewModel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragmentManager.beginTransaction().remove((Fragment) contentFragment).commit();
    }


    private BasicViewModel getViewModel(Navigation.Location location) {
        switch (location) {
            case HOME:
                return ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
            default:
                Timber.e("unknown location %s", location.name());
                return ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
        }
    }

    private void fragment(Navigation.Location location) {

        switch (location) {

            case HOME:
            default:
                contentFragment = new HomeFragment();
                hideBack();
                showSetting();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.content, (Fragment) contentFragment)
                .commit();

    }

    private void bind() {


    }

    private void unbind() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();

    }

    private void checkPermissions() {
        String[] missingPermissions = Permissions.INSTANCE.missing(activity, PERMISSIONS);

        if (missingPermissions.length > 0) {
            Timber.i("missing permissions, send permission request");
            requestPermissions(missingPermissions, REQUEST_PERMISSIONS);
            return;
        }
    }

    private void checkBluetooth() {

        if (bluetoothAdapter == null) {
            Timber.w("no bluetooth adapter.");
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Timber.i("bluetooth disabled, send enable request");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            }
            return;
        }

    }

    private void showSetting() {
        elementSettingButton.setVisibility(View.VISIBLE);
    }

    private void hideSetting() {
        elementSettingButton.setVisibility(View.INVISIBLE);
    }

    private void showBack() {
        elementBackButton.setVisibility(View.VISIBLE);
    }

    private void hideBack() {
        elementBackButton.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getCurrentLocation().equals(Navigation.Location.HOME)) {
            finish();
        }
        viewModel.onBack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String[] missingPermissions = Permissions.INSTANCE.missing(activity, PERMISSIONS);

        if (missingPermissions.length > 0) {
            Toast.makeText(activity, String.format("required permissions %s was not granted", Arrays.toString(missingPermissions)), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // this method is called by the "bluetooth turn on request"
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity, "bluetooth is required", Toast.LENGTH_LONG).show();
            }
        }

    }

}
