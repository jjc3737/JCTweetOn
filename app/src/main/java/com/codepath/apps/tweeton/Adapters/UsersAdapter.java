package com.codepath.apps.tweeton.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.models.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JaneChung on 2/24/16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context context;

    public UsersAdapter(List<User> users) {
        mUsers = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivUserPicture)
        ImageView thumbnail;
        @Bind(R.id.tvUserProfileName)
        TextView name;
        @Bind(R.id.tvUserTweetHandle)
        TextView handle;
        @Bind(R.id.tvUserTagLine)
        TextView tagline;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.item_user, parent, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUsers.get(position);

        String thumbnail = user.getProfileImageUrl();
        String name = user.getName();
        String handle = "@" + user.getScreenName();
        String tagline = user.getTagline();

        Glide.with(context).load(thumbnail)
                .placeholder(R.drawable.profile_place_holder)
                .fitCenter().into(holder.thumbnail);

        holder.name.setText(name);
        holder.handle.setText(handle);
        if (!tagline.isEmpty() || tagline != null) {
            holder.tagline.setText(tagline);
        } else {
            holder.tagline.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }
}
