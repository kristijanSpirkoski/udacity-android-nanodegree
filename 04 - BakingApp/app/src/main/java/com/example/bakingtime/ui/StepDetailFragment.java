package com.example.bakingtime.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;
import com.example.bakingtime.utils.AppExecutors;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener{

    private Step mStep;
    private Recipe mRecipe;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoPlayerView;


    private TextView mErrorView;
    private ImageView placeHolder;
    private TextView mDescriptionView;


    public StepDetailFragment() {}

    public StepDetailFragment(Recipe recipe, Step step) {

        this.mRecipe = recipe;
        this.mStep = step;
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View viewRoot = inflater.inflate(R.layout.fragment_step_detail, container);

        exoPlayerView = viewRoot.findViewById(R.id.step_exo_player);
        placeHolder = viewRoot.findViewById(R.id.video_placeholder);
        mDescriptionView = viewRoot.findViewById(R.id.step_description);
        mErrorView = viewRoot.findViewById(R.id.video_unavailable_label);


        String videoUrl = mStep.getVideoURL();
        String thumbnailUrl = mStep.getThumbnailURL();


        mDescriptionView.setText(mStep.getDescription());

        if(isUrlValid(videoUrl)) {
            placeHolder.setVisibility(View.INVISIBLE);
            mErrorView.setVisibility(View.INVISIBLE);

            initializeMediaSession();
            initializePlayer(Uri.parse(videoUrl));
        } else {
            exoPlayerView.setVisibility(View.GONE);
            placeHolder.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.VISIBLE);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if(mMediaSession != null) {
            releasePlayer();
            mMediaSession.setActive(false);
            mMediaSession = null;
        }
        super.onDestroyView();
    }

    private void initializeMediaSession() {

        mMediaSession = new MediaSessionCompat(getActivity(), StepDetailFragment.class.getSimpleName());
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MediaSessionCallback());

        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            exoPlayerView.setPlayer(exoPlayer);




            String userAgent = Util.getUserAgent(getActivity(), "BakingTime");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getActivity(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private boolean isUrlValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){

            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);

        } else if((playbackState == ExoPlayer.STATE_READY)){

            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);

        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    public void updateVideoUrl(Uri mediaUri) {
        exoPlayer.setPlayWhenReady(false);
        if(isUrlValid(mediaUri.toString())) {
            exoPlayerView.setVisibility(View.VISIBLE);
            placeHolder.setVisibility(View.INVISIBLE);
            mErrorView.setVisibility(View.INVISIBLE);


            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getActivity(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        } else {
            exoPlayerView.setVisibility(View.GONE);
            placeHolder.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.VISIBLE);
        }


    }
    public void updateStepDescription(String newDesc) {
        mDescriptionView.setText(newDesc);
    }
    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }

}
