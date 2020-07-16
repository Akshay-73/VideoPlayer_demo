package com.app.videoplayerdemo.ui.player;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;

import com.app.videoplayerdemo.R;
import com.app.videoplayerdemo.databinding.ActivityPlayerBinding;
import com.app.videoplayerdemo.model.SongData;
import com.app.videoplayerdemo.ui.player.adapter.SongsAdapter;
import com.app.videoplayerdemo.util.handler.ItemClickHandler;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

@SuppressLint("LogNotTimber")
public class PlayerActivity extends AppCompatActivity implements ItemClickHandler<View, Integer, List<SongData>>, View.OnClickListener, Player.EventListener {
    private ActivityPlayerBinding binding;
    private SimpleExoPlayer player;
    PlayerView playerView;

    private ProgressObserver progressLive;
    private List<SongData> songDataList = new ArrayList<>();
    private YouTubePlayer youTubePlayer;
    int position;
    MotionLayout motionLayout;
    ImageButton exoNext;
    ImageButton exoPrevious;
    ViewGroup.LayoutParams layoutParams;
    private boolean isReleased = true;
    private int count = 0;

    private static String URLDemo = "https://www.youtube.com/watch?v=bKDdT_nyP54";

    private SongsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        motionLayout = findViewById(R.id.motionLayout);
        layoutParams = motionLayout.getLayoutParams();
        exoNext = findViewById(R.id.exo_next_button);
        exoPrevious = findViewById(R.id.exo_previous_button);
        playerView = findViewById(R.id.exo_player);

        exoNext.setOnClickListener(this);
        exoPrevious.setOnClickListener(this);

        if (adapter == null) {
            adapter = new SongsAdapter(this, this);
        } else {
            adapter.notifyDataSetChanged();
        }
        binding.rvSongs.setAdapter(adapter);

        setSongData();

        setUp();
        runEverySec();
    }

    private void runEverySec() {
        final Handler handler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (motionLayout.getProgress() != 0.0) {
                    layoutParams.width = MotionLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = MotionLayout.LayoutParams.WRAP_CONTENT;
                    playerView.setUseController(false);
                } else {
                    layoutParams.width = MotionLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = MotionLayout.LayoutParams.MATCH_PARENT;
                    playerView.setUseController(true);
                }
                motionLayout.setLayoutParams(layoutParams);

                handler.postDelayed(this, 500);

            }
        });

    }


    private void setSongData() {
        songDataList.add(new SongData("Song 1", "https://www.youtube.com/watch?v=y1ITlvCq5Ro"));
        songDataList.add(new SongData("Song 2", "https://www.youtube.com/watch?v=y1ITlvCq5Ro"));
        songDataList.add(new SongData("Song 3", "https://www.youtube.com/watch?v=CCF1_jI8Prk"));
        songDataList.add(new SongData("Song 4", "https://www.youtube.com/watch?v=y1ITlvCq5Ro"));
        songDataList.add(new SongData("Song 5", "https://www.youtube.com/watch?v=vZ_NpLWuL00&list=RDKRz6N5E3nHU&index=6"));
        songDataList.add(new SongData("Song 6", "https://www.youtube.com/watch?v=bKDdT_nyP54"));
        songDataList.add(new SongData("Audio 7", "https://hd1.upload.solutions/cc42df1b798ae623373fad333b7e08ce/mrmov/Aj%20Kal%20Ve-(Mr-Jatt.com).mp3"));
        songDataList.add(new SongData("Audio 8", "https://hd1.upload.solutions/40a04ab73173433e0e7ef8ad1782b54e/oizov/Muchh-(Mr-Jatt.com).mp3"));
        songDataList.add(new SongData("Audio 9", "http://s1281.ve.vc/data/128/48366/295803/Bambiha%20Bole%20-%20Sidhu%20Moose%20Wala%20(DjPunjab.Com).mp3"));
       /* songDataList.add(new SongData("Song 7", "https://www.youtube.com/watch?v=bKDdT_nyP54"));
        songDataList.add(new SongData("Song 8", "https://www.youtube.com/watch?v=D--rQj49heE&list=RDD--rQj49heE&index=1"));
        songDataList.add(new SongData("Song 9", "https://www.youtube.com/watch?v=KRz6N5E3nHU&list=RDKRz6N5E3nHU&index=1"));
        songDataList.add(new SongData("Song 10", "https://www.youtube.com/watch?v=iiQmg8Sldu8&list=RDKRz6N5E3nHU&index=3"));
        songDataList.add(new SongData("Song 11", "https://www.youtube.com/watch?v=vZ_NpLWuL00&list=RDKRz6N5E3nHU&index=6"));
        songDataList.add(new SongData("Song 12", "https://www.youtube.com/watch?v=bKDdT_nyP54"));
        songDataList.add(new SongData("Song 13", "https://www.youtube.com/watch?v=bKDdT_nyP54"));
        songDataList.add(new SongData("Song 14", "https://www.youtube.com/watch?v=D--rQj49heE&list=RDD--rQj49heE&index=1"));
        songDataList.add(new SongData("Song 15", "https://www.youtube.com/watch?v=KRz6N5E3nHU&list=RDKRz6N5E3nHU&index=1"));
        songDataList.add(new SongData("Song 16", "https://www.youtube.com/watch?v=iiQmg8Sldu8&list=RDKRz6N5E3nHU&index=3"));
        songDataList.add(new SongData("Song 17", "https://www.youtube.com/watch?v=vZ_NpLWuL00&list=RDKRz6N5E3nHU&index=6"));
        songDataList.add(new SongData("Song 18", "https://www.youtube.com/watch?v=bKDdT_nyP54"));*/

        adapter.setList(songDataList);

        Log.e("SizeOf", "Songs " + songDataList.size());
    }

    private void setUp() {
        //todo
        //binding.youtubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        binding.setObserver(getProgressLive());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // initPlayer();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    private void releasePlayer() {
        isReleased = false;
        if (player != null && player.isPlaying()) {
            player.release();
            player = null;
            isReleased = true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void initPlayer(final String URLDemo) {

        getProgressLive().setProgress(true);

        player = new SimpleExoPlayer.Builder(this).build();
        player.setPlayWhenReady(true);
        player.addListener(this);

        // Produces DataSource instances through which media data is loaded.

        if (URLDemo.contains("://youtu.be/") || URLDemo.contains("youtube.com/watch?v=")) {
            //binding.ivMusic.setVisibility(View.GONE);
            Log.e("YouTube", " Url true");
            new YouTubeExtractor(this) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    getProgressLive().setProgress(false);

                    if (ytFiles != null) {
                        List<YtFile> list = new ArrayList<>();
                        for (int i = 0, itag; i < ytFiles.size(); i++) {
                            itag = ytFiles.keyAt(i);
                            // ytFile represents one file with its url and meta data
                            YtFile ytFile = ytFiles.get(itag);

                            // Just add videos in a decent format => height -1 = audio
                            if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                                list.add(ytFile);
                            }
                        }

                        if (list.isEmpty()) {
                            //youtube
                            checkForYoutube(URLDemo);
                        } else {
                            Log.e("Size", list.size() + "");
                            String downloadUrl = list.get(0).getUrl().replaceAll("\\\\", "");
                            Log.e("Size", downloadUrl);

                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                DataSource.Factory dataSourceFactory;

                                if (downloadUrl.toUpperCase().contains("M3U8")) {
                                    dataSourceFactory = new DefaultHttpDataSourceFactory(
                                            Util.getUserAgent(PlayerActivity.this, getPackageName()));

                                    // Create a HLS media source pointing to a playlist uri.
                                    HlsMediaSource hlsMediaSource =
                                            new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(downloadUrl));

                               /* HlsMediaSource hlsMediaSource1 =
                                        new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(downloadUrl1));

                                ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource(hlsMediaSource, hlsMediaSource1);*/

                                    if (player != null) {

                                        binding.exoPlayer.setVisibility(View.VISIBLE);
                                        binding.exoPlayer.setPlayer(player);

                                        player.prepare(hlsMediaSource);
                                    } else {
                                        Toast.makeText(PlayerActivity.this, "Failed to play!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this,
                                            Util.getUserAgent(PlayerActivity.this, getPackageName()));

                                    MediaSource dashStreamingMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(Uri.parse(downloadUrl));

                                    if (player != null) {
                                        binding.exoPlayer.setVisibility(View.VISIBLE);
                                        binding.exoPlayer.setPlayer(player);

                                        player.prepare(dashStreamingMediaSource);
                                    } else {
                                        Toast.makeText(PlayerActivity.this, "Failed to play!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                //check for youtube url
                                checkForYoutube(URLDemo);
                            }
                        }

                    } else {

                        checkForYoutube(URLDemo);
                    }
                }
            }.extract(URLDemo, true, true);
        } else {
            Log.e("YouTube", " Url false");
            playAudio(URLDemo);
        }
    }

    private void playAudio(String url) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this,
                Util.getUserAgent(PlayerActivity.this, getPackageName()));

        MediaSource dashStreamingMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url));
        //binding.ivMusic.setVisibility(View.VISIBLE);
        if (player != null) {
            binding.exoPlayer.setVisibility(View.VISIBLE);
            binding.exoPlayer.setPlayer(player);

            player.prepare(dashStreamingMediaSource);
            getProgressLive().setProgress(false);
        } else {
            getProgressLive().setProgress(false);
            Toast.makeText(PlayerActivity.this, "Failed to play!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkForYoutube(String downloadUrl) {
        if (downloadUrl.contains("://youtu.be/") || downloadUrl.contains("youtube.com/watch?v=")) {
            play(downloadUrl);
        } else {
            Toast.makeText(PlayerActivity.this, "Failed to play!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("LogNotTimber")
    private void play(String url) {
        if (youTubePlayer != null) {
            binding.youtubeView.setVisibility(View.VISIBLE);
            binding.exoPlayer.setVisibility(View.GONE);
            //youTubePlayer.cueVideo(getYouTubeId(url));
            inittt(getYouTubeId(url));
        }
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }

    private void inittt(final String url) {
        getLifecycle().addObserver(binding.youtubeView);
        binding.youtubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

            @Override
            public void onReady(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                Log.e("HHHH", "hhhhhh");
                Log.e("URRR", url);
                youTubePlayer.cueVideo(url, 0f);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.youtubeView.release();
    }

    public ProgressObserver getProgressLive() {
        if (progressLive == null) {
            progressLive = new ProgressObserver();
        }
        return progressLive;
    }

    @Override
    public void onItemClick(View view, Integer position, List<SongData> songData) {
        this.position = position;
        if (view.getId() == R.id.ll_songs) {
            binding.exoPlayer.setVisibility(View.VISIBLE);
            binding.topImageContainer.setVisibility(View.VISIBLE);
            binding.motionLayout.setVisibility(View.VISIBLE);
            if (count != 0) {
                releasePlayer();
            } else {
                isReleased = true;
                count++;
            }
            if (isReleased) {
                initPlayer(songData.get(position).getSongLink());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exo_next_button:
                nextClick(v);
                break;
            case R.id.exo_previous_button:
                previousClick(v);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            releasePlayer();
            int position = (int) (Math.random() * songDataList.size());
            // position++;
            Log.e("Position2", " " + position);
            Log.e("Position", " " + songDataList.get(position).getSongLink());
            initPlayer(songDataList.get(position).getSongLink());
        }
    }

    public static class ProgressObserver extends BaseObservable {
        private Boolean progress;

        /**
         * 1-exo
         * 2-youtube
         */
        private int player;

        public int getPlayer() {
            return player;
        }

        public void setPlayer(int player) {
            this.player = player;
        }

        public Boolean getProgress() {
            if (progress == null) {
                progress = false;
            }
            return progress;
        }

        void setProgress(Boolean progress) {
            this.progress = progress;
            notifyChange();
        }
    }

    public void nextClick(View view) {
        if (position >= songDataList.size() - 1) {
            Log.e("Position1", " " + position);
            return;
        }
        int position = (int) (Math.random() * songDataList.size());
        //position++;
        releasePlayer();
        Log.e("Position2", " " + position);
        Log.e("Position", " " + songDataList.get(position).getSongLink());
        initPlayer(songDataList.get(position).getSongLink());

    }


    public void previousClick(View view) {
        if (position > 0) {
            position--;
            releasePlayer();
            Log.e("Position", " " + position);
            Log.e("Position", " " + songDataList.get(position).getSongLink());
            initPlayer(songDataList.get(position).getSongLink());
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.exoPlayer.getVisibility() == View.VISIBLE) {
            count = 0;
            releasePlayer();
            isReleased = true;
            binding.exoPlayer.setVisibility(View.GONE);
            binding.topImageContainer.setVisibility(View.GONE);
            binding.motionLayout.setVisibility(View.GONE);
            binding.rvSongs.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }

    public static String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json"
                );

                String url = "http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json";


                return new JSONObject(url).getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // This is the MediaSource representing the media to be played.
//        MediaSource videoSource =
//                new ProgressiveMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(Uri.parse("https://www.youtube.com/watch?v=Uot1AfGn0fE&list=OLAK5uy_msH2p-zIeFPlwAbUkLh90YSzUnfoVdToM&index=2"));
//
//
//        YouTubeVideoInfoRetriever retriever = new YouTubeVideoInfoRetriever();
//
//        try {
//            retriever.retrieve(getYouTubeId("https://www.youtube.com/watch?v=Uot1AfGn0fE&list=OLAK5uy_msH2p-zIeFPlwAbUkLh90YSzUnfoVdToM&index=2"));
//
//            String url=retriever.getInfo(YouTubeVideoInfoRetriever.KEY_DASH_VIDEO);
//
//            MediaSource dashStreamingMediaSource = new DashMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(Uri.parse(url));
//
////            MediaSource dashStreamingMediaSource = new DashMediaSource.Factory(dataSourceFactory)
////                    .createMediaSource(Uri.parse("https://www.youtube.com/watch?v=Uot1AfGn0fE&list=OLAK5uy_msH2p-zIeFPlwAbUkLh90YSzUnfoVdToM&index=2"));
//
//            MediaSource smoothStreamingMediaSource=new SsMediaSource.Factory(smoothStreamingDataSource)
//                    .createMediaSource(Uri.parse("https://www.youtube.com/watch?v=Uot1AfGn0fE&list=OLAK5uy_msH2p-zIeFPlwAbUkLh90YSzUnfoVdToM&index=2"));

    // Prepare the player with the source.
//            if(player!=null){
//
//                binding.exoPlayer.setPlayer(player);
//
//                player.prepare(dashStreamingMediaSource);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}
