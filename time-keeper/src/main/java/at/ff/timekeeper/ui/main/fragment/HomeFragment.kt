package at.ff.timekeeper.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.ff.timekeeper.R
import at.ff.timekeeper.data.entity.RunEntity
import at.ff.timekeeper.data.model.BleButton
import at.ff.timekeeper.data.model.TimerState
import at.ff.timekeeper.databinding.FragmentHomeBinding
import at.ff.timekeeper.di.DaggerFragment
import at.ff.timekeeper.service.BluetoothService
import at.ff.timekeeper.ui.HasOnBackPressed
import at.ff.timekeeper.ui.dialog.DialogBuilder
import at.ff.timekeeper.ui.main.BleActivity
import at.ff.timekeeper.ui.main.list.RunListAdapter
import at.ff.timekeeper.ui.main.list.RunViewHolder
import at.ff.timekeeper.util.hasBluetooth
import at.ff.timekeeper.util.hasCoarseLocation
import at.ff.timekeeper.util.hasFineLocation
import at.ff.timekeeper.util.hasForegroundServiceLocation
import at.ff.timekeeper.util.hasNotification
import at.ff.timekeeper.vm.HomeViewModel
import timber.log.Timber
import java.util.Locale
import java.util.stream.Collectors

class HomeFragment : DaggerFragment(), HasOnBackPressed {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var topRunListAdapter: RunListAdapter
    private lateinit var latestRunListAdapter: RunListAdapter

    private var mode: RunEntity.Mode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(
            HomeViewModel::class.java
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            elementTopRunList.setLayoutManager(LinearLayoutManager(activity))
            elementTopRunList.setItemAnimator(null)
            topRunListAdapter = RunListAdapter(
                activity,
                { _: View?, item: RunEntity? -> viewModel.removeRun(item) },
                RunViewHolder.Prefix.NUMBER
            )
            elementTopRunList.setAdapter(topRunListAdapter)

            elementLatestRunList.setLayoutManager(LinearLayoutManager(activity))
            elementLatestRunList.setItemAnimator(null)
            latestRunListAdapter = RunListAdapter(
                activity,
                { _: View?, item: RunEntity? -> viewModel.removeRun(item) },
                RunViewHolder.Prefix.PLUS
            )
            elementLatestRunList.setAdapter(latestRunListAdapter)

            elementPairStartButton.setOnClickListener { v: View? ->
                startActivityForResult(
                    Intent(
                        activity,
                        BleActivity::class.java
                    ), REQUEST_BLE_START_AVAILABLE
                )
            }
            elementPairStopButton.setOnClickListener { v: View? ->
                startActivityForResult(
                    Intent(
                        activity,
                        BleActivity::class.java
                    ), REQUEST_BLE_STOP_AVAILABLE
                )
            }
            elementStart.setOnClickListener {
                viewModel.executeStart()
            }
            elementStop.setOnClickListener {
                viewModel.executeStop()
            }
            elementAttack.setOnClickListener {
                viewModel.executeAttack()
            }
            elementModeSetting.setOnClickListener {
                val modes = RunEntity.Mode.values()
                viewModel.setMode(modes[(mode!!.ordinal + 1) % modes.size])
            }
        }

        bind()

    }

    override fun onResume() {
        super.onResume()

        context?.apply {
            if (
                hasForegroundServiceLocation()
                && (hasFineLocation() || hasCoarseLocation())
                && hasNotification()
            ) {
                BluetoothService.start(activity)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.POST_NOTIFICATIONS,
                    ),
                    REQUEST_LOCATION,
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbind()
    }

    private fun bind() = with(binding) {
        viewModel.timerState().observe(
            viewLifecycleOwner
        ) {
            var timerState = it
            timerState = timerState ?: TimerState.ATTACK
            when (timerState) {
                TimerState.ATTACK -> {
                    elementAttack.visibility = View.VISIBLE
                    elementStart.visibility = View.GONE
                    elementStop.visibility = View.GONE
                }

                TimerState.START -> {
                    elementTime.text = "00.00"
                    elementAttack.visibility = View.GONE
                    elementStart.visibility = View.VISIBLE
                    elementStop.visibility = View.GONE
                }

                TimerState.STOP -> {
                    elementAttack.visibility = View.GONE
                    elementStart.visibility = View.GONE
                    elementStop.visibility = View.VISIBLE
                }
            }
        }
        viewModel.timeKeeper().observe(viewLifecycleOwner) {
            val time = it ?: 0L
            elementTime.text =
                String.format(
                    Locale.getDefault(),
                    "%02d.%02d",
                    time / 1000,
                    (time % 1000) / 10
                )
        }
        viewModel.mode().observe(viewLifecycleOwner) {
            if (mode != null) {
                viewModel.topRuns(mode).removeObservers(viewLifecycleOwner)
                viewModel.latestRuns(mode).removeObservers(viewLifecycleOwner)
            }
            mode = it ?: RunEntity.Mode.BRONZE
            when (mode) {
                RunEntity.Mode.SILVER -> elementMode.setText(R.string.mode_silver)
                RunEntity.Mode.BRONZE_SUCTION -> elementMode.setText(R.string.mode_bronze_suction)
                RunEntity.Mode.SILVER_SUCTION -> elementMode.setText(R.string.mode_silver_suction)
                else -> elementMode.setText(R.string.mode_bronze)
            }
            viewModel.topRuns(mode).observe(
                viewLifecycleOwner
            ) { runs: List<RunEntity?>? ->
                if (runs != null) {
                    topRunListAdapter.submitList(
                        runs.stream().limit(5)
                            .collect(Collectors.toList())
                    )
                    topRunListAdapter.notifyDataSetChanged()
                }
            }
            viewModel.latestRuns(mode).observe(
                viewLifecycleOwner
            ) { runs: List<RunEntity?>? ->
                if (runs != null) {
                    latestRunListAdapter.submitList(
                        runs.stream().limit(5)
                            .collect(Collectors.toList())
                    )
                    latestRunListAdapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.bleStartButton.observe(
            viewLifecycleOwner
        ) { bleButton: BleButton? ->
            if (bleButton != null) {
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_active)
                elementStartButtonName.text = bleButton.mac
            } else {
                // todo get online status
                elementPairStartButton.setBackgroundResource(R.drawable.secondary_button_inactive)
                elementStartButtonName.setText(R.string.buzzer_start_name_na)
            }
        }
        viewModel.bleStopButton.observe(
            viewLifecycleOwner
        ) { bleButton: BleButton? ->
            if (bleButton != null) {
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_active)
                elementStopButtonName.text = bleButton.mac
            } else {
                // todo get online status
                elementPairStopButton.setBackgroundResource(R.drawable.secondary_button_inactive)
                elementStopButtonName.setText(R.string.buzzer_stop_name_na)
            }
        }
    }

    private fun unbind() {
        // only remove layout change listener
    }

    override fun onBackPressed() {
        // exit app. service may run in foreground mode.
        viewModel.executeStop()
        activity.finish()
        BluetoothService.stop(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == REQUEST_BLE_START_AVAILABLE || requestCode == REQUEST_BLE_STOP_AVAILABLE) && resultCode == BleActivity.BLE_ACCESS_AVAILABLE) {
            DialogBuilder.bleButtonDialog(activity, viewModel.bleButtons).observe(
                activity
            ) { bleButton: BleButton? ->
                if (bleButton != null) {
                    Timber.i("bleButton %d %s", requestCode, bleButton.mac)
                    if (requestCode == REQUEST_BLE_START_AVAILABLE) {
                        viewModel.setBleStartButton(if (bleButton.mac == null) null else bleButton)
                    }
                    if (requestCode == REQUEST_BLE_STOP_AVAILABLE) {
                        viewModel.setBleStopButton(if (bleButton.mac == null) null else bleButton)
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_BLE_START_AVAILABLE: Int = 200
        const val REQUEST_BLE_STOP_AVAILABLE: Int = 201
        const val REQUEST_LOCATION: Int = 202
    }
}
