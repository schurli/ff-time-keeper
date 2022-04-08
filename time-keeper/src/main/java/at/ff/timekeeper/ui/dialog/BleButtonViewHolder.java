package at.ff.timekeeper.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.model.BleButton;

public class BleButtonViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final TextView elementLabel;
    private final TextView elementMac;

    public BleButtonViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        elementLabel = itemView.findViewById(R.id.element_label);
        elementMac = itemView.findViewById(R.id.element_mac);
    }

    public void bindView(BleButton item, BleButtonListAdapter.OnClickListener onClickListenerActive) {

        elementLabel.setText(item.label);
        elementLabel.setOnClickListener(v -> onClickListenerActive.onClick(v, item));
        elementLabel.setTextColor(context.getColor(R.color.colorOnPrimary));

        elementMac.setText(item.mac);
        elementMac.setOnClickListener(v -> onClickListenerActive.onClick(v, item));


    }

}
