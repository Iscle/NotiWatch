package me.iscle.notiwatch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.os.Bundle;

import me.iscle.notiwatch.adapter.FunctionalityAdapter;
import me.iscle.notiwatch.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private WearableRecyclerView functionalityRecyclerView;
    private FunctionalityAdapter functionalityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        functionalityRecyclerView = findViewById(R.id.wearable_recycler_view);
        functionalityRecyclerView.setEdgeItemsCenteringEnabled(true);
        functionalityRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        functionalityAdapter = new FunctionalityAdapter(this);
        functionalityRecyclerView.setAdapter(functionalityAdapter);
        new LinearSnapHelper().attachToRecyclerView(functionalityRecyclerView);
    }
}
