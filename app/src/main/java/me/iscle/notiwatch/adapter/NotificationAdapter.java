package me.iscle.notiwatch.adapter;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.iscle.notiwatch.NotiDrawer;
import me.iscle.notiwatch.model.PhoneNotification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<PhoneNotification> phoneNotifications;
    private final View placeholder;

    public NotificationAdapter(List<PhoneNotification> phoneNotifications, View placeholder) {
        this.phoneNotifications = phoneNotifications;
        this.placeholder = placeholder;

        checkItemCount();
    }

    private void checkItemCount() {
        if (phoneNotifications == null || phoneNotifications.size() == 0)
            placeholder.setVisibility(View.VISIBLE);
        else
            placeholder.setVisibility(View.GONE);
    }

    public void setPhoneNotifications(List<PhoneNotification> phoneNotifications) {
        this.phoneNotifications = phoneNotifications;
        checkItemCount();
        notifyDataSetChanged();
    }

    public void updateNotifications() {
        checkItemCount();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ScrollView scrollView = new ScrollView(parent.getContext());
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        return new ViewHolder(scrollView);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return phoneNotifications == null ? 0 : phoneNotifications.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPhoneNotification(phoneNotifications.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ScrollView scrollView;
        private View notificationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.scrollView = (ScrollView) itemView;
        }

        public void setPhoneNotification(PhoneNotification pn) {
            scrollView.removeAllViews();
            notificationView = new NotiDrawer(itemView.getContext(), pn).createContentView();
            adjustInset(notificationView);
            scrollView.addView(notificationView);
        }
    }

    private void adjustInset(View v) {
        Resources res = v.getContext().getResources();
        //if (res.getConfiguration().isScreenRound()) {
        DisplayMetrics dm = res.getDisplayMetrics();
        int p1 = (int) (0.146467f * (float) dm.widthPixels);
        int p2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, res.getDisplayMetrics());
        v.setPadding(p1, p2, p1, p1);
        //}
    }
}
