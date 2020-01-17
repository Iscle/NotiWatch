package me.iscle.notiwatch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import java.util.Arrays;

import me.iscle.notiwatch.activity.NotificationsActivity;
import me.iscle.notiwatch.activity.SettingsActivity;
import me.iscle.notiwatch.activity.TestActivity;
import me.iscle.notiwatch.model.Functionality;
import me.iscle.notiwatch.R;

public class FunctionalityAdapter extends WearableRecyclerView.Adapter {

    private Functionality[] functionalities;
    private Context context;

    public FunctionalityAdapter(Context context) {
        this.context = context;

        this.functionalities = new Functionality[] {
                new Functionality(R.drawable.ic_wrench_color, "TestActivity", TestActivity.class),
                new Functionality(R.drawable.ic_gear_color, "Settings", SettingsActivity.class),
                new Functionality(R.drawable.ic_bell_color, "Notifications", NotificationsActivity.class)
        };

        Arrays.sort(this.functionalities, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.launcher_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.bindFunctionality(functionalities[position]);
    }

    @Override
    public int getItemCount() {
        return functionalities.length;
    }


    public class ViewHolder extends WearableRecyclerView.ViewHolder {
        private View view;
        private ImageView icon;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.title);
        }

        public void bindFunctionality(Functionality functionality) {
            icon.setImageResource(functionality.getIcon());
            title.setText(functionality.getName());
            view.setOnClickListener(v ->
                    context.startActivity(new Intent(context, functionality.getActivity())));
        }
    }
}
