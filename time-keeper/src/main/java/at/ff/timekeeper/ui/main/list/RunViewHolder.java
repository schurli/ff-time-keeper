package at.ff.timekeeper.ui.main.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.entity.RunEntity;

public class RunViewHolder extends RecyclerView.ViewHolder {

    public enum Prefix {
        NUMBER, PLUS
    }

    private final Context context;
    private final TextView elementLabel;
    private final Prefix prefix;

    public RunViewHolder(Context context, @NonNull View itemView, Prefix prefix) {
        super(itemView);
        this.context = context;
        elementLabel = itemView.findViewById(R.id.element_label);
        this.prefix = prefix;
    }

    public void bindView(RunEntity item, int position, RunListAdapter.OnClickListener onClickListenerActive) {

        String format = "%02d.%02d";
        if (Prefix.NUMBER.equals(prefix)) {
            format = (position+1) + ". " + format;
        }
        if (Prefix.PLUS.equals(prefix)) {
            format = "+ " + format;
        }

        elementLabel.setText(String.format(Locale.getDefault(), format, item.duration / 1000, (item.duration % 1000) / 10));
        elementLabel.setBackgroundColor(context.getColor(position % 2 == 0 ? R.color.colorRowB : R.color.colorRowA));
        elementLabel.setOnClickListener(v -> onClickListenerActive.onClick(v, item));
        elementLabel.setTextColor(context.getColor(R.color.colorOnPrimary));

    }

}
