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

import me.iscle.notiwatch.MainActivity;
import me.iscle.notiwatch.model.Functionality;
import me.iscle.notiwatch.R;

public class FunctionalityAdapter extends WearableRecyclerView.Adapter {

    private Functionality[] functionalities;
    private Context context;

    public FunctionalityAdapter(Context context) {
        this.context = context;
        functionalities = new Functionality[] {
                new Functionality(null, "Test", new Intent(context, MainActivity.class))
        };
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
        private View itemView;
        private ImageView icon;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.title);
        }

        public void bindFunctionality(Functionality functionality) {
            if (functionality.getIcon() != null) {
                icon.setImageDrawable(functionality.getIcon());
            }

            title.setText(functionality.getName());
            itemView.setOnClickListener(v -> context.startActivity(functionality.getAction()));
        }
    }
}
