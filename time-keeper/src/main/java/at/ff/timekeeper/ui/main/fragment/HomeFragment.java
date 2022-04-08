package at.ff.timekeeper.ui.main.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.entity.RunEntity;
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

    private View elementPairStartButton;
    private View elementPairStopButton;

    private TextView elementStartButtonName;
    private TextView elementStopButtonName;

    private Button elementStartButton;
    private Button elementStopButton;

    private TextView elementTime;
    private View elementModeSetting;
    private TextView elementMode;

    private RunEntity.Mode mode;

    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);

        elementPairStartButton = rootView.findViewById(R.id.element_pair_start_button);
        elementPairStopButton = rootView.findViewById(R.id.element_pair_stop_button);

        elementStartButtonName = rootView.findViewById(R.id.element_start_button_name);
        elementStopButtonName = rootView.findViewById(R.id.element_stop_button_name);

        elementStartButton = rootView.findViewById(R.id.element_start_button);
        elementStopButton = rootView.findViewById(R.id.element_stop_button);

        elementTime = rootView.findViewById(R.id.element_time);
        elementModeSetting = rootView.findViewById(R.id.element_mode_setting);
        elementMode = rootView.findViewById(R.id.element_mode);

        elementPairStartButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_START_AVAILABLE);
        });
        elementPairStopButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_STOP_AVAILABLE);
        });
        elementStartButton.setOnClickListener(v -> viewModel.executeStart());
        elementStopButton.setOnClickListener(v -> viewModel.executeStop());
        elementModeSetting.setOnClickListener(v -> {
            RunEntity.Mode[] modes = RunEntity.Mode.values();
            viewModel.setMode(modes[(mode.ordinal() + 1) % modes.length]);
        });

        mediaPlayer = MediaPlayer.create(activity, R.raw.angriffsbefehl);

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
        viewModel.active().observe(getViewLifecycleOwner(), active -> {
            active = active != null && active;
            if (!active) {
                elementTime.setText("00.00");
            }
        });
        viewModel.timeKeeper().observe(getViewLifecycleOwner(), time -> {
            time = time == null ? 0L : time;
            elementTime.setText(String.format(Locale.getDefault(), "%02d.%02d", time / 1000, (time % 1000) / 10));
        });
        viewModel.mode().observe(getViewLifecycleOwner(), mode -> {
            this.mode = mode == null ? RunEntity.Mode.BRONZE : mode;
            switch (this.mode) {
                case BRONZE: elementMode.setText("Bronze"); break;
                case SILVER: elementMode.setText("Silber"); break;
                case GOLD: elementMode.setText("Gold"); break;
            }
        });
        viewModel.getBleStartButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_active);
                elementStartButtonName.setText(bleButton.mac);
            } else {
                // todo get online status
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_inactive);
                elementStartButtonName.setText("verbinden");
            }
        });
        viewModel.getBleStopButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_active);
                elementStopButtonName.setText(bleButton.mac);
            } else {
                // todo get online status
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_inactive);
                elementStopButtonName.setText("verbinden");
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
