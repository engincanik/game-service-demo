package com.engin.testgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.engin.testgame.event.EventListAdapter;
import com.huawei.hms.jos.games.event.Event;
import com.huawei.hms.jos.games.ranking.RankingScore;

import java.text.SimpleDateFormat;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder> {
    Context context;
    List<RankingScore> scoresBuffer;


    public LeaderboardAdapter(Context ct, List<RankingScore> scoreList) {
        context = ct;
        scoresBuffer = scoreList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userRank.setText("Rank: " + scoresBuffer.get(position).getDisplayRank());
        if (scoresBuffer.get(position).getScoreOwnerIconUri() != null) {
            Glide.with(context).load(scoresBuffer.get(position).getScoreOwnerIconUri()).into(holder.userImg);
        } else {
            holder.userImg.setImageResource(R.drawable.coronavirus);
        }

        StringBuffer buffer = new StringBuffer();
        RankingScore s = scoresBuffer.get(position);
        if (s == null) {
            buffer.append("rankingScore is null\n");
            return;
        }
        String displayScore = s.getRankingDisplayScore();
        buffer.append("Score: " + displayScore).append("\n");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formatedDate = newFormat.format(s.getScoreTimestamp());
        buffer.append("Time: ").append(formatedDate).append("\n");
        String playerDisplayName = s.getScoreOwnerDisplayName();
        buffer.append("Username: " + playerDisplayName).append("\n");
        holder.userDetail.setText(buffer);
    }

    @Override
    public int getItemCount() {
        int length = scoresBuffer.size();
        if (length < 1) {
            return 0;
        } else {
            return length;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userRank, userDetail;
        ImageView userImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userRank = itemView.findViewById(R.id.titleTv);
            userDetail = itemView.findViewById(R.id.pointTv);
            userImg = itemView.findViewById(R.id.imageIv);
        }
    }
}
