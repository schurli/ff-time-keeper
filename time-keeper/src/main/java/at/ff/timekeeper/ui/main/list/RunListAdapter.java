package at.ff.timekeeper.ui.main.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.entity.RunEntity;
import at.ff.timekeeper.ui.EqualsItemCallback;

public class RunListAdapter extends ListAdapter<RunEntity, RunViewHolder> {

    private final Context context;
    private final RunListAdapter.OnClickListener onClickListener;
    private final RunViewHolder.Prefix prefix;

    public RunListAdapter(Context context, RunListAdapter.OnClickListener onClickListener, RunViewHolder.Prefix prefix) {
        super(new EqualsItemCallback<>());
        this.context = context;
        this.onClickListener = onClickListener;
        this.prefix = prefix;
        setHasStableIds(true);
    }

    @Override
    @NonNull
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_run, parent, false);
        return new RunViewHolder(context, view, prefix);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        holder.bindView(getItem(position), position, onClickListener);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public interface OnClickListener {
        void onClick(@NonNull View view, @NonNull RunEntity result);
    }

}
