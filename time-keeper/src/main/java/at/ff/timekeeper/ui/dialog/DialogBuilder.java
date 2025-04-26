package at.ff.timekeeper.ui.dialog;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.model.BleButton;
import timber.log.Timber;

public class DialogBuilder {

    public static LiveData<BleButton> bleButtonDialog(FragmentActivity activity, LiveData<List<BleButton>> bleButtonLiveData) {

        final MutableLiveData<BleButton> liveData = new MutableLiveData<>();

        Dialog dialog = new Dialog(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_ble_button, activity.findViewById(R.id.element_dialog));
        dialog.setContentView(layout);

        View elementReset = layout.findViewById(R.id.element_reset);
        elementReset.setOnClickListener(v -> {
            liveData.postValue(new BleButton());
            dialog.dismiss();
        });

        // list logic
        View emptyView = layout.findViewById(R.id.element_empty);
        RecyclerView recyclerView = layout.findViewById(R.id.element_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(null);
        BleButtonListAdapter listAdapter = new BleButtonListAdapter(activity, (v, item) -> {
            liveData.postValue(item);
            dialog.dismiss();
        });
        recyclerView.setAdapter(listAdapter);
        bleButtonLiveData.observe(activity, all -> {
            List<BleButton> list = all.stream()
                    .peek(ble -> Timber.d("device %s", ble.label))
                    .filter(bleButton -> bleButton.label != null && bleButton.label.startsWith("FF"))
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                listAdapter.submitList(list);
                listAdapter.notifyDataSetChanged();
            }
        });

        dialog.show();

        dialog.setOnCancelListener(v -> {
            Timber.d("Dialog.onCancel");
            bleButtonLiveData.removeObservers(activity);
            liveData.postValue(null);
        });
        dialog.setOnDismissListener(v -> {
            Timber.d("Dialog.onDismiss");
            bleButtonLiveData.removeObservers(activity);
            liveData.postValue(null);
        });

        return liveData;

    }

}
