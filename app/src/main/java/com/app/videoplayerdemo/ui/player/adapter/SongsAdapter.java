package com.app.videoplayerdemo.ui.player.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.videoplayerdemo.R;
import com.app.videoplayerdemo.databinding.LayoutSongsBinding;
import com.app.videoplayerdemo.model.SongData;
import com.app.videoplayerdemo.util.handler.ItemClickHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MySongsViewHolder> {
    private List<SongData> songDataList;
    private ItemClickHandler<View, Integer, List<SongData>> clickHandler;
    private Context context;

    public SongsAdapter(ItemClickHandler<View, Integer, List<SongData>> clickHandler, Context context) {
        this.clickHandler = clickHandler;
        this.context = context;
    }

    @NonNull
    @Override
    public MySongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSongsBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_songs, parent, false);
        return new MySongsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MySongsViewHolder holder, int position) {
        SongData songData = songDataList.get(position);
        holder.binding.tvSongName.setText(songData.getSongName());

        String video_image = "https://img.youtube.com/vi/" + trimUrl(songDataList.get(position).getSongLink()) + "/hqdefault.jpg";

        Glide.with(context).load(video_image).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).into(holder.binding.ivSongImage);

    }

    public void setList(List<SongData> songDataList) {
        this.songDataList = songDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (songDataList != null) {
            return Math.max(songDataList.size(), 0);
        }
        return 0;
    }

    class MySongsViewHolder extends RecyclerView.ViewHolder {

        LayoutSongsBinding binding;

        MySongsViewHolder(@NonNull LayoutSongsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.llSongs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickHandler.onItemClick(v, getAdapterPosition(), songDataList);
                }
            });
        }
    }

    private static String trimUrl(String text) {
        if (text.contains("feature=youtu.be")) {
            Log.e("&feature=youtu.be", "true");
            return text.split("//")[1].split("=")[1].split("&")[0];
        } else if (text.contains("youtube.com")) {
            Log.e("&feature=youtu.be", "false");
            return text.split("//")[1].split("=")[1];
        } else {
            return text.split("//")[1].split("/")[1];
        }
    }
}
