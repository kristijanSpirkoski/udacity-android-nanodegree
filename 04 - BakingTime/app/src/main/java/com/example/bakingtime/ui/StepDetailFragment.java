package com.example.bakingtime.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;
import com.example.bakingtime.utils.AppExecutors;
import com.google.android.exoplayer2.DefaultLoadControl;
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

public class StepDetailFragment extends Fragment {

    private int recipeId;
    private int stepId;
    private Step mStep;

    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoPlayerView;
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

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            exoPlayerView.setPlayer(exoPlayer);
//            exoPlayerView.setBackgroundDrawable(R.drawable.ic_launcher_background); //TODO


            String userAgent = Util.getUserAgent(getActivity(), "BakingTime");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getActivity(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
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
}
