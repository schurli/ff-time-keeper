package at.ff.timekeeper.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import at.ff.timekeeper.R;
import at.ff.timekeeper.di.DaggerFragment;
import at.ff.timekeeper.service.BluetoothService;
import at.ff.timekeeper.ui.HasOnBackPressed;
import at.ff.timekeeper.ui.dialog.DialogBuilder;
import at.ff.timekeeper.ui.main.BleActivity;
import at.ff.timekeeper.vm.HomeViewModel;
import timber.log.Timber;


public class HomeFragment extends DaggerFragment implements HasOnBackPressed {

    public static final int REQUEST_BLE_START_AVAILABLE = 200;
    public static final int REQUEST_BLE_STOP_AVAILABLE = 201;

    private HomeViewModel viewModel;

    private Button elementPairStartButton;
    private Button elementPairStopButton;
    private Button elementStartButton;
    private Button elementStopButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);

        elementPairStartButton = rootView.findViewById(R.id.element_pair_start_button);
        elementPairStopButton = rootView.findViewById(R.id.element_pair_stop_button);
        elementStartButton = rootView.findViewById(R.id.element_start_button);
        elementStopButton = rootView.findViewById(R.id.element_stop_button);

        elementPairStartButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_START_AVAILABLE);
        });
        elementPairStopButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_STOP_AVAILABLE);
        });
        elementStartButton.setOnClickListener(v -> viewModel.executeStart());
        elementStopButton.setOnClickListener(v -> viewModel.executeStop());

        bind();

        BluetoothService.start(activity);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind();
    }

    public void bind() {

        // todo bind time and ble status
        viewModel.getBleStartButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStartButton.setText(String.format("Pair Start Button (%s)", bleButton.label));
            }
        });
        viewModel.getBleStopButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStopButton.setText(String.format("Pair Stop Button (%s)", bleButton.label));
            }
        });

    }

    public void unbind() {
        // only remove layout change listener
    }

    @Override
    public void onBackPressed() {
        // exit app. service may run in foreground mode.
        activity.finish();
        BluetoothService.stop(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == REQUEST_BLE_START_AVAILABLE || requestCode == REQUEST_BLE_STOP_AVAILABLE) && resultCode == BleActivity.BLE_ACCESS_AVAILABLE) {
            DialogBuilder.bleButtonDialog(activity, viewModel.getBleButtons()).observe(activity, bleButton -> {
                if (bleButton != null) {
                    Timber.i("bleButton %d %s", requestCode, bleButton.mac);
                    if (requestCode == REQUEST_BLE_START_AVAILABLE) {
                        viewModel.setBleStartButton(bleButton);
                    }
                    if (requestCode == REQUEST_BLE_STOP_AVAILABLE) {
                        viewModel.setBleStopButton(bleButton);
                    }
                }
            });
        }
    }

}
