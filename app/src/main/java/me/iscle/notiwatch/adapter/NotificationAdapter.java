package me.iscle.notiwatch.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.iscle.notiwatch.NotificationAction;
import me.iscle.notiwatch.PhoneNotification;
import me.iscle.notiwatch.R;
import me.iscle.notiwatch.view.DateTimeView;

import static me.iscle.notiwatch.Utils.base64ToBitmap;
import static me.iscle.notiwatch.Utils.dpToPixels;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<PhoneNotification> phoneNotifications;
    private View placeholder;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_test, parent, false);
        return new ViewHolder(view);
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

        private View view;
        private View mainLayout;
        private View constraintLayout;
        private ImageView appIcon;
        private TextView appName;
        private TextView notificationSummaryText;
        private DateTimeView notificationTime;
        private TextView notificationTitle;
        private TextView notificationText;
        private TextView notificationSubText;
        private LinearLayout actionLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mainLayout = itemView.findViewById(R.id.main_layout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            appIcon = itemView.findViewById(R.id.icon);
            appName = itemView.findViewById(R.id.app_name);
            notificationSummaryText = itemView.findViewById(R.id.notification_summary_text);
            notificationTime = itemView.findViewById(R.id.notification_time);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationText = itemView.findViewById(R.id.notification_text);
            notificationSubText = itemView.findViewById(R.id.notification_sub_text);
            actionLayout = itemView.findViewById(R.id.action_layout);
        }

        public void setPhoneNotification(PhoneNotification pn) {
            Bitmap smallIcon;
            if (pn.getSmallIcon() != null) {
                smallIcon = base64ToBitmap(pn.getSmallIcon());
            } else {
                smallIcon = null;
            }

            Bitmap largeIcon;
            if (pn.getLargeIcon() != null) {
                largeIcon = base64ToBitmap(pn.getLargeIcon());
            } else {
                largeIcon = null;
            }

            if (smallIcon != null)
                appIcon.setImageBitmap(smallIcon);
            if (largeIcon != null) {
                Glide.with(view.getContext())
                        .load(largeIcon)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(15)))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                mainLayout.setBackground(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                mainLayout.setBackground(placeholder);
                            }
                        });
            } else {
                mainLayout.setBackgroundColor(Color.BLACK);
            }
            appIcon.setColorFilter(pn.getColor());
            appName.setText(pn.getAppName());
            appName.setTextColor(pn.getColor());
            if (pn.getSubText() != null) {
                notificationSubText.setVisibility(View.VISIBLE);
                notificationTime.setVisibility(View.GONE);
                notificationSubText.setText(pn.getSubText());
            } else {
                if (pn.getSummaryText() != null && !(pn.getSummaryText().equals("null"))) {
                    notificationSummaryText.setText(pn.getSummaryText());
                    notificationSummaryText.setVisibility(View.VISIBLE);
                } else {
                    notificationSummaryText.setVisibility(View.GONE);
                }
                notificationTime.setVisibility(View.VISIBLE);
                notificationSubText.setVisibility(View.GONE);
                notificationTime.setTime(pn.getWhen());
            }
            notificationTitle.setText(pn.getTitle());
            notificationText.setText(pn.getText());
            actionLayout.removeAllViews();
            if (pn.getActions() != null && pn.getActions().length > 0) {
                if (pn.getCompactActions() != null && pn.getCompactActions().length > 0) {
                    NotificationAction[] actions = pn.getActions();
                    for (int i : pn.getCompactActions()) {
                        NotificationAction na = actions[i];
                        if (na.getIcon() == null) continue;
                        ImageView iv = new ImageView(view.getContext());
                        Bitmap icon = base64ToBitmap(na.getIcon());
                        iv.setImageBitmap(icon);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        params.height = Math.min(icon.getHeight(), dpToPixels(view.getContext(), 72));
                        iv.setLayoutParams(params);
                        iv.setAdjustViewBounds(true);
                        //iv.setOnClickListener(view ->
                                //phoneBluetoothService.sendCommand(Command.NOTIFICATION_ACTION_CALLBACK,
                                //        new NotificationAction.Callback(pn.getId(), na.getHashCode())));
                        actionLayout.addView(iv);
                    }
                } else {
                    for (NotificationAction na : pn.getActions()) {
                        if (na.getIcon() == null) continue;
                        ImageView iv = new ImageView(view.getContext());
                        Bitmap icon = base64ToBitmap(na.getIcon());
                        iv.setImageBitmap(icon);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                        params.height = Math.min(icon.getHeight(), dpToPixels(view.getContext(), 72));
                        iv.setLayoutParams(params);
                        iv.setAdjustViewBounds(true);
                        //iv.setOnClickListener(view ->
                                //phoneBluetoothService.sendCommand(Command.NOTIFICATION_ACTION_CALLBACK,
                                //        new NotificationAction.Callback(pn.getId(), na.getHashCode())));
                        actionLayout.addView(iv);
                    }
                }
            }
        }

        private void adjustInset() {
            Resources res = view.getContext().getResources();
            //if (res.getConfiguration().isScreenRound()) {
            DisplayMetrics dm = res.getDisplayMetrics();
            int inset = (int) (0.146467f * (float) dm.widthPixels);
            constraintLayout.setPadding(inset, 0, inset, inset);
            //}
        }
    }
}
