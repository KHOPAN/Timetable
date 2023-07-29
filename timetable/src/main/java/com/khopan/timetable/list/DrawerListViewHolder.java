package com.khopan.timetable.list;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.khopan.timetable.R;

public class DrawerListViewHolder extends RecyclerView.ViewHolder {
    private final boolean separator;

    private Typeface normalTypeface;
    private Typeface selectedTypeface;
    private AppCompatImageView iconView;
    private TextView titleView;

    public DrawerListViewHolder(@NonNull View itemView, boolean separator) {
        super(itemView);
        this.separator = separator;

        if(!this.separator) {
            this.iconView = itemView.findViewById(R.id.drawer_item_icon);
            this.titleView = itemView.findViewById(R.id.drawer_item_title);
            this.normalTypeface = Typeface.create("sec-roboto-light", Typeface.NORMAL);
            this.selectedTypeface = Typeface.create("sec-roboto-light", Typeface.BOLD);
        }
    }

    public boolean isSeparator() {
        return this.separator;
    }

    public void setIcon(@DrawableRes int resId) {
        if(!this.separator) {
            this.iconView.setImageResource(resId);
        }
    }

    public void setTitle(@Nullable CharSequence title) {
        if(!this.separator) {
            this.titleView.setText(title);
        }
    }

    public void setSelected(boolean selected) {
        if(!this.separator) {
            this.itemView.setSelected(selected);
            this.titleView.setTypeface(selected ? this.selectedTypeface : this.normalTypeface);
            this.titleView.setEllipsize(selected ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        }
    }
}
