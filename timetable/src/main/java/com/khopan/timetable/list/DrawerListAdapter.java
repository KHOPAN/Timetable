package com.khopan.timetable.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.khopan.timetable.FragmentInfo;
import com.sec.sesl.khopan.timetable.R;

import java.util.List;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListViewHolder> {
	private final Context context;
	private final List<Fragment> fragments;
	private final DrawerListener listener;
	private int selectedPosition;

	public DrawerListAdapter(@NonNull Context context, List<Fragment> fragments, @Nullable DrawerListener listener) {
		this.context = context;
		this.fragments = fragments;
		this.listener = listener;
	}

	@NonNull
	@Override
	public DrawerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(this.context);
		boolean separator = viewType == 0;
		return new DrawerListViewHolder(inflater.inflate(separator ? R.layout.drawer_list_separator : R.layout.drawer_list_item, parent, false), separator);
	}

	@Override
	public void onBindViewHolder(@NonNull DrawerListViewHolder holder, int position) {
		if(!holder.isSeparator()) {
			Fragment fragment = this.fragments.get(position);

			if(fragment instanceof FragmentInfo) {
				holder.setIcon(((FragmentInfo) fragment).getIconResourceIdentifier());
				holder.setTitle(((FragmentInfo) fragment).getTitle());
			}

			holder.setSelected(position == this.selectedPosition);
			holder.itemView.setOnClickListener(view -> {
				int itemPosition = holder.getBindingAdapterPosition();

				if(this.listener != null && this.listener.onDrawerItemSelected(itemPosition)) {
					this.setSelectedItem(itemPosition);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return this.fragments.size();
	}

	@Override
	public int getItemViewType(int position) {
		return this.fragments.get(position) == null ? 0 : 1;
	}

	public void setSelectedItem(int position) {
		this.selectedPosition = position;
		this.notifyItemRangeChanged(0, this.getItemCount());
	}

	public interface DrawerListener {
		boolean onDrawerItemSelected(int position);
	}
}
