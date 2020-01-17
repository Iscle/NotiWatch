package me.iscle.notiwatch.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.lang.reflect.Field;

import me.iscle.notiwatch.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
            adjustInset(recyclerView);
            return recyclerView;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            findPreference("about").setOnPreferenceClickListener(this);
            findPreference("licenses").setOnPreferenceClickListener(this);
        }


        private void adjustInset(View view) {
            Resources res = getResources();
            //if (res.getConfiguration().isScreenRound()) {
            DisplayMetrics dm = res.getDisplayMetrics();
            int inset = (int) (0.146467f * (float) dm.widthPixels);
            view.setPadding(0, inset, 0, inset);
            //}
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("about")) {
                Intent i = new Intent(getContext(), AboutActivity.class);
                startActivity(i);
                return true;
            } else if (preference.getKey().equals("licenses")) {
                // TODO: Fix padding and ActionBar
                new LibsBuilder()
                        .withActivityTheme(getContext().getApplicationInfo().theme)
                        .start(getContext());
                return true;
            }

            return false;
        }
    }
}