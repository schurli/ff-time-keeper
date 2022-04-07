package at.ff.timekeeper.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.BleButton;
import at.ff.timekeeper.ui.EqualsItemCallback;


public class BleButtonListAdapter extends ListAdapter<BleButton, BleButtonViewHolder> {

    private final Context context;
    private final OnClickListener onClickListener;

    public BleButtonListAdapter(Context context, OnClickListener onClickListener) {
        super(new EqualsItemCallback<>());
        this.context = context;
        this.onClickListener = onClickListener;
        setHasStableIds(true);
    }

    @Override
    @NonNull
    public BleButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_ble_button, parent, false);
        return new BleButtonViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull BleButtonViewHolder holder, int position) {
        holder.bindView(getItem(position), onClickListener);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public interface OnClickListener {
        void onClick(@NonNull View view, @NonNull BleButton result);
    }

}
