package at.ff.timekeeper.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.stream.Collectors;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.entity.RunEntity;
import at.ff.timekeeper.data.model.TimerState;
import at.ff.timekeeper.di.DaggerFragment;
import at.ff.timekeeper.service.BluetoothService;
import at.ff.timekeeper.ui.HasOnBackPressed;
import at.ff.timekeeper.ui.dialog.DialogBuilder;
import at.ff.timekeeper.ui.main.BleActivity;
import at.ff.timekeeper.ui.main.list.RunListAdapter;
import at.ff.timekeeper.ui.main.list.RunViewHolder;
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

    private Button elementStart;
    private Button elementStop;
    private Button elementAttack;

    private TextView elementTime;
    private View elementModeSetting;
    private TextView elementMode;
    private RecyclerView topRunRecyclerView;
    private RecyclerView latestRunRecyclerView;

    private RunListAdapter topRunListAdapter;
    private RunListAdapter latestRunListAdapter;

    private RunEntity.Mode mode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);

        elementPairStartButton = rootView.findViewById(R.id.element_pair_start_button);
        elementPairStopButton = rootView.findViewById(R.id.element_pair_stop_button);

        elementStartButtonName = rootView.findViewById(R.id.element_start_button_name);
        elementStopButtonName = rootView.findViewById(R.id.element_stop_button_name);

        elementStart = rootView.findViewById(R.id.element_start);
        elementStop = rootView.findViewById(R.id.element_stop);
        elementAttack = rootView.findViewById(R.id.element_attack);

        elementTime = rootView.findViewById(R.id.element_time);
        elementModeSetting = rootView.findViewById(R.id.element_mode_setting);
        elementMode = rootView.findViewById(R.id.element_mode);

        topRunRecyclerView = rootView.findViewById(R.id.element_top_run_list);
        latestRunRecyclerView = rootView.findViewById(R.id.element_latest_run_list);

        topRunRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        topRunRecyclerView.setItemAnimator(null);
        topRunListAdapter = new RunListAdapter(activity, (v, item) -> { }, RunViewHolder.Prefix.NUMBER);
        topRunRecyclerView.setAdapter(topRunListAdapter);

        latestRunRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        latestRunRecyclerView.setItemAnimator(null);
        latestRunListAdapter = new RunListAdapter(activity, (v, item) -> { }, RunViewHolder.Prefix.PLUS);
        latestRunRecyclerView.setAdapter(latestRunListAdapter);

        elementPairStartButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_START_AVAILABLE);
        });
        elementPairStopButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, BleActivity.class), REQUEST_BLE_STOP_AVAILABLE);
        });
        elementStart.setOnClickListener(v -> viewModel.executeStart());
        elementStop.setOnClickListener(v -> viewModel.executeStop());
        elementAttack.setOnClickListener(v -> viewModel.executeAttack());
        elementModeSetting.setOnClickListener(v -> {
            RunEntity.Mode[] modes = RunEntity.Mode.values();
            viewModel.setMode(modes[(mode.ordinal() + 1) % modes.length]);
        });

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

        viewModel.timerState().observe(getViewLifecycleOwner(), timerState -> {
            timerState = timerState == null ? TimerState.ATTACK : timerState;
            switch (timerState) {
                case ATTACK:
                    elementAttack.setVisibility(View.VISIBLE);
                    elementStart.setVisibility(View.GONE);
                    elementStop.setVisibility(View.GONE);
                    break;
                case START:
                    elementTime.setText("00.00");
                    elementAttack.setVisibility(View.GONE);
                    elementStart.setVisibility(View.VISIBLE);
                    elementStop.setVisibility(View.GONE);
                    break;
                case STOP:
                    elementAttack.setVisibility(View.GONE);
                    elementStart.setVisibility(View.GONE);
                    elementStop.setVisibility(View.VISIBLE);
                    break;
            }
        });
        viewModel.timeKeeper().observe(getViewLifecycleOwner(), time -> {
            time = time == null ? 0L : time;
            elementTime.setText(String.format(Locale.getDefault(), "%02d.%02d", time / 1000, (time % 1000) / 10));
        });
        viewModel.mode().observe(getViewLifecycleOwner(), mode -> {
            if (this.mode != null) {
                viewModel.topRuns(this.mode).removeObservers(getViewLifecycleOwner());
                viewModel.latestRuns(this.mode).removeObservers(getViewLifecycleOwner());
            }
            this.mode = mode == null ? RunEntity.Mode.BRONZE : mode;
            switch (this.mode) {
                case BRONZE: elementMode.setText(R.string.mode_bronze); break;
                case SILVER: elementMode.setText(R.string.mode_silver); break;
                case BRONZE_SUCTION: elementMode.setText(R.string.mode_bronze_suction); break;
                case SILVER_SUCTION: elementMode.setText(R.string.mode_silver_suction); break;
            }
            viewModel.topRuns(this.mode).observe(getViewLifecycleOwner(), runs -> {
                if (runs != null) {
                    topRunListAdapter.submitList(runs.stream().limit(5).collect(Collectors.toList()));
                    topRunListAdapter.notifyDataSetChanged();
                }
            });
            viewModel.latestRuns(this.mode).observe(getViewLifecycleOwner(), runs -> {
                if (runs != null) {
                    latestRunListAdapter.submitList(runs.stream().limit(5).collect(Collectors.toList()));
                    latestRunListAdapter.notifyDataSetChanged();
                }
            });
        });
        viewModel.getBleStartButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_active);
                elementStartButtonName.setText(bleButton.mac);
            } else {
                // todo get online status
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_inactive);
                elementStartButtonName.setText(R.string.buzzer_start_name_na);
            }
        });
        viewModel.getBleStopButton().observe(getViewLifecycleOwner(), bleButton -> {
            if (bleButton != null) {
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_active);
                elementStopButtonName.setText(bleButton.mac);
            } else {
                // todo get online status
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_inactive);
                elementStopButtonName.setText(R.string.buzzer_stop_name_na);
            }
        });

    }

    public void unbind() {
        // only remove layout change listener
    }

    @Override
    public void onBackPressed() {
        // exit app. service may run in foreground mode.
        viewModel.executeStop();
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
                        viewModel.setBleStartButton(bleButton.mac == null ? null : bleButton);
                    }
                    if (requestCode == REQUEST_BLE_STOP_AVAILABLE) {
                        viewModel.setBleStopButton(bleButton.mac == null ? null : bleButton);
                    }
                }
            });
        }
    }

}
