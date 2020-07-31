package com.example.bakingtime.ui;

import android.app.NotificationManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener{

    private int recipeId;
    private int stepId;
    private Step mStep;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoPlayerView;
    private NotificationManager mNotificationManager;


    private ImageView placeHolder;



    public StepDetailFragment(int recipeId, int stepId) {
        this.recipeId = recipeId;
        this.stepId = stepId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View viewRoot = inflater.inflate(R.layout.fragment_step_detail, container);

        exoPlayerView = viewRoot.findViewById(R.id.step_exo_player);
        placeHolder = viewRoot.findViewById(R.id.video_placeholder);



        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                Recipe recipe = AppDatabase.getInstance(getActivity()).recipeDao().getRecipeById(recipeId);
                for (Step s : recipe.getSteps()) {
                    if (s.getId() == stepId) {
                        mStep = s;
                        break;
                    }
                }

                String videoUrl = mStep.getVideoURL();
                String thumbnailUrl = mStep.getThumbnailURL();

                if( isUrlValid(videoUrl) ) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            placeHolder.setVisibility(View.INVISIBLE);

                            initializeMediaSession();
                            initializePlayer(Uri.parse(videoUrl));
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            exoPlayerView.setVisibility(View.GONE);
                            placeHolder.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMediaSession.setActive(false);
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

    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

        int icon;
        String play_pause;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity(getActivity(), 0,
                new Intent(getActivity(), StepActivity.class), 0);

        builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(mStep.getShortDescription())
                .setContentIntent(contentPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession.getSessionToken())
                    .setShowActionsInCompactView(0,1));

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());

    }
    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            mNotificationManager.cancelAll();
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
