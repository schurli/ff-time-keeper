package at.ff.timekeeper.ui.main;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;

import javax.inject.Inject;

import at.ff.timekeeper.R;
import at.ff.timekeeper.util.Permissions;
import at.ff.timekeeper.vm.BleViewModel;
import at.ff.timekeeper.vm.ViewModelFactory;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

import timber.log.Timber;

/**
 * This activity is launched if ble access is required.
 */
public class BleActivity extends DaggerAppCompatActivity {

    public static final int BLE_ACCESS_AVAILABLE = 100;
    public static final int BLE_ACCESS_UNAVAILABLE = 101;

    private static final int REQUEST_ENABLE_BT = 27;

    private static final int REQUEST_PERMISSIONS = 70;

    private static final String[] PERMISSIONS = Permissions.BLE;

    private BleActivity activity;
    private FragmentManager fragmentManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ViewModelFactory viewModelFactory;
    private BluetoothAdapter bluetoothAdapter;
    private BleViewModel viewModel;

    private TextView elementNoBluetooth;
    private TextView elementEnableBluetooth;
    private TextView elementMissingPermission;
    private TextView elementEnableLocation;
    private Button elementCancel;

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
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(BleViewModel.class);

        setContentView(R.layout.activity_ble);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        elementEnableBluetooth = findViewById(R.id.element_enable_bluetooth);
        elementNoBluetooth = findViewById(R.id.element_no_bluetooth);
        elementMissingPermission = findViewById(R.id.element_missing_permission);
        elementEnableLocation = findViewById(R.id.element_enable_location);
        elementCancel = findViewById(R.id.element_cancel);

        elementCancel.setOnClickListener(v -> finish());

        setResult(BLE_ACCESS_UNAVAILABLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbind();
    }

    public void bind() {
        viewModel.getBleAvailable().observe(activity, bleAvailable -> {
            if (bleAvailable != null) {

                if (!bleAvailable.hasPermission) {
                    checkPermissions();
                    elementMissingPermission.setVisibility(View.VISIBLE);
                } else {
                    elementMissingPermission.setVisibility(View.GONE);
                }
                if (!bleAvailable.hasBluetooth) {
                    elementNoBluetooth.setVisibility(View.VISIBLE);
                } else {
                    elementNoBluetooth.setVisibility(View.GONE);
                }
                if (!bleAvailable.isBluetoothEnabled) {
                    checkBluetooth();
                    elementEnableBluetooth.setVisibility(View.VISIBLE);
                } else {
                    elementEnableBluetooth.setVisibility(View.GONE);
                }
                if (!bleAvailable.isLocationEnabled) {
                    elementEnableLocation.setVisibility(View.VISIBLE);
                } else {
                    elementEnableLocation.setVisibility(View.GONE);
                }

                if (bleAvailable.hasPermission && bleAvailable.hasBluetooth && bleAvailable.isBluetoothEnabled && bleAvailable.isLocationEnabled) {
                    setResult(BLE_ACCESS_AVAILABLE);
                    finish();
                }

            }
        });
    }

    public void unbind() {
        viewModel.getBleAvailable().removeObservers(activity);
    }

    private void checkPermissions() {
        String[] missingPermissions = Permissions.INSTANCE.missing(activity, PERMISSIONS);
        if (missingPermissions.length > 0) {
            Timber.i("missing permissions, send permission request for %s", Arrays.toString(missingPermissions));
            handler.postDelayed(() -> requestPermissions(missingPermissions, REQUEST_PERMISSIONS), 3000);
            return;
        }
    }

    private void checkBluetooth() {

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
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
                return;
            }
            handler.postDelayed(() -> startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT), 3000);
            return;
        }

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
