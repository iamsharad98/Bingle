package in.komu.komu.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

//import com.google.android.exoplayer2.DefaultLoadControl;
//import com.google.android.exoplayer2.DefaultRenderersFactory;
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.Format;
//import com.google.android.exoplayer2.PlaybackParameters;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.audio.AudioRendererEventListener;
//import com.google.android.exoplayer2.decoder.DecoderCounters;
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.source.dash.DashChunkSource;
//import com.google.android.exoplayer2.source.dash.DashMediaSource;
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelection;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
//import com.google.android.exoplayer2.util.Util;
//import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.share.NextActivity;

public class MediaActivity extends AppCompatActivity{

    private static final String TAG = "MediaActivity";


//    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private Context mContext;
//    private SimpleExoPlayer player;
//    private SimpleExoPlayerView playerView;
//    private ComponentListener componentListener;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private VideoView videoView;
    private String videoUrl;
    private Intent intent;
    private boolean isplaying;
    private ImageView playVideo, pauseVideo;
    private FrameLayout videoSection;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_media);
        mContext = MediaActivity.this;


        videoView = findViewById(R.id.videoView);
        playVideo = (ImageView) findViewById(R.id.playVideo);
        pauseVideo = (ImageView) findViewById(R.id.pauseVideo);
        videoSection = findViewById(R.id.videoSection);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        String videoPath = getVideoUrl();
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.pause();
        super.onCreate(savedInstanceState);

//        videoView.start();
        playVideo.setVisibility(View.VISIBLE);
        pauseVideo.setVisibility(View.GONE);
        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
                hideSystemUi();
                progressBar.setVisibility(View.GONE);
                playVideo.setVisibility(View.GONE);
                pauseVideo.setVisibility(View.GONE);
                isplaying = true;


            }
        });

        videoSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplaying) {
                    playVideo.setVisibility(View.GONE);
                    pauseVideo.setVisibility(View.VISIBLE);
                    pauseVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoView.pause();
                            isplaying = false;
                            playVideo.setVisibility(View.VISIBLE);
                            pauseVideo.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });


        if (!isplaying){
            playVideo.setVisibility(View.VISIBLE);
            pauseVideo.setVisibility(View.GONE);
            playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();
                    hideSystemUi();
                    progressBar.setVisibility(View.GONE);
                    isplaying = true;
                    playVideo.setVisibility(View.GONE);
                    pauseVideo.setVisibility(View.GONE);
                }
            });
        }
    }

        @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private String getVideoUrl() {
        intent = getIntent();

        if (intent.hasExtra(mContext.getString(R.string.field_video_url))) {
            videoUrl = intent.getStringExtra(mContext.getString(R.string.field_video_url));

        }
        return videoUrl;
    }

//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23){
//            initializeExoplayer();
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        hideSystemUi();
//        if (Util.SDK_INT <= 23 || player == null){
//            initializeExoplayer();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23){
//            releasePlayer();
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23){
//            releasePlayer();
//        }
//    }
//
//    private void initializeExoplayer(){
//        String path = getVideoUrl();
//        Toast.makeText(mContext, "Path "+ path, Toast.LENGTH_SHORT).show();
//        if (player == null){
//            TrackSelection.Factory adaptiveTrackSelectionFactory =
//                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
//            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
//                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
//                    new DefaultLoadControl());
//            player.addListener(componentListener);
//            player.setVideoDebugListener(componentListener);
//            player.setAudioDebugListener(componentListener);
//            playerView.setPlayer(player);
//            player.setPlayWhenReady(playWhenReady);
//            player.seekTo(currentWindow, playbackPosition);
//        }
//
//        MediaSource mediaSource = buildMediaSource(Uri.parse(path));
//
//        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
////
////MediaSource mediaSource = new ExtractorMediaSource(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"),
////        mediaDataSourceFactory, extractorsFactory, null, null);
//
////player.prepare(mediaSource);
////
////player.setPlayWhenReady(shouldAutoPlay);
//
//        player.prepare(mediaSource, true, false);
//    }
//
//
//
//    private void releasePlayer(){
//        if (player != null){
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            playWhenReady = player.getPlayWhenReady();
//            player.removeListener(componentListener);
//            player.setVideoListener(null);
//            player.setVideoDebugListener(null);
//            player.setAudioDebugListener(null);
//            player.release();
//            player = null;
//
//        }
//    }
//
//    private MediaSource buildMediaSource(Uri uri){
//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER );
//        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
//                dataSourceFactory
//        );
//        return new DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory,
//                null, null);
////        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER))
////        val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
////        return DashMediaSource.Factory(
////                dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
//
//    }
//
//
//
//    @SuppressLint("InlinedApi")
//    private void hideSystemUi() {
//        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }
//
//
//    private class ComponentListener implements ExoPlayer.EventListener, VideoRendererEventListener, AudioRendererEventListener {
//
//        @Override
//        public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//        }
//
//        @Override
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//        }
//
//        @Override
//        public void onLoadingChanged(boolean isLoading) {
//
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            String stateString;
//            switch (playbackState){
//                case ExoPlayer.STATE_IDLE:
//                    stateString = "ExoPlayer.STATE_IDLE     -";
//                    break;
//                case ExoPlayer.STATE_BUFFERING:
//                    stateString = "ExoPlayer.STATE_BUFFERING -";
//                    break;
//                case ExoPlayer.STATE_READY:
//                    stateString = "ExoPlayer.STATE_READY   -";
//                    break;
//                case ExoPlayer.STATE_ENDED:
//                    stateString = "ExoPlayer.STATE_ENDED  -";
//                    break;
//                default:
//                    stateString = "UNKNOWN_STATE   -";
//                    break;
//            }
//            Log.d(TAG, "onPlayerStateChanged: " + stateString);
//        }
//
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {
//
//        }
//
//        @Override
//        public void onPositionDiscontinuity() {
//
//        }
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//        }
//
//        @Override
//        public void onAudioEnabled(DecoderCounters counters) {
//
//        }
//
//        @Override
//        public void onAudioSessionId(int audioSessionId) {
//
//        }
//
//        @Override
//        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
//
//        }
//
//        @Override
//        public void onAudioInputFormatChanged(Format format) {
//
//        }
//
//        @Override
//        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
//
//        }
//
//        @Override
//        public void onAudioDisabled(DecoderCounters counters) {
//
//        }
//
//        @Override
//        public void onVideoEnabled(DecoderCounters counters) {
//
//        }
//
//        @Override
//        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
//
//        }
//
//        @Override
//        public void onVideoInputFormatChanged(Format format) {
//
//        }
//
//        @Override
//        public void onDroppedFrames(int count, long elapsedMs) {
//
//        }
//
//        @Override
//        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//
//        }
//
//        @Override
//        public void onRenderedFirstFrame(Surface surface) {
//
//        }
//
//        @Override
//        public void onVideoDisabled(DecoderCounters counters) {
//
//        }
//    }

//    private void initializePlayer() {
//        player = ExoPlayerFactory.newSimpleInstance(
//                new DefaultRenderersFactory(this),
//                new DefaultTrackSelector(), new DefaultLoadControl());
//
//        exoPlayer.setPlayer(player);
//
//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);
//    }



}




