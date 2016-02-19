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
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JaneChung on 2/15/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;

    public TweetsAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ivUserThumbnail)
        ImageView image;
        @Bind(R.id.tvUserName)
        TextView name;
        @Bind(R.id.tvTweet)
        TextView body;
        @Bind(R.id.tvTimeStamp)
        TextView timeStamp;
        @Bind(R.id.tvScreenName)
        TextView screenName;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Intent i = new Intent(itemView.getContext(), ArticleActivity.class);
//
//            Article article = mArticles.get(position);
//            i.putExtra("article", Parcels.wrap(article));
//
//            itemView.getContext().startActivity(i);
        }

    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        String thumbnail = tweet.getUser().getProfileImageUrl();
        String name = tweet.getUser().getName();
        String screenName = "@" + tweet.getUser().getScreenName();
        String body = tweet.getBody();
        String timeStamp = Utils.getTimeStamp(tweet.getCreatedAt());

        ImageView iv = holder.image;

        iv.setImageResource(0);
        Glide.with(context).load(thumbnail).fitCenter().into(iv);

        holder.name.setText(name);
        holder.screenName.setText(screenName);
        holder.body.setText(body);
        holder.timeStamp.setText(timeStamp);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
