package com.kiennguyen.imagebrowser;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class PhotoFragment extends Fragment implements View.OnTouchListener {
    public static final String ENABLE_ANIMATION = "enable_animation";
    public static final String POSITION = "postion";
    public static final String PHOTO_URL = "photo_url";

    public static final String THUMBNAIL_WIDTH = "thumbnail_width";
    public static final String THUMBNAIL_HEIGHT = "thumbnail_height";
    public static final String THUMBNAIL_TOP = "thumbnail_top";
    public static final String THUMBNAIL_LEFT = "thumbnail_left";
    public static final String THUMBNAIL_SCALE_TYPE = "thumbnail_scale_type";
    public static final String ARRAY_THUMBNAIL= "array_thumbnail";
    public static final String INIT_MATRIX_THUMBNAIL= "init_matrix_thumbnail";

    private ImageView imageView;
    private ImageView transitionImage;
    private FrameLayout frameLayout;
    private String photoUrl;
    private boolean enableAnimation;

    private ImageView.ScaleType scaleType;
    private List<ImageInfo> imageInfoList;

    private int thumbnailWidth;
    private int thumbnailHeight;
    private int thumbnailTop;
    private int thumbnailLeft;
    private int position;

    private float[] initMatrixData = new float[9];

    private int toLeft;
    private int toTop;
    private int toWidth;
    private int toHeight;



    public static Fragment getInstance(Bundle bundle) {
        PhotoFragment f = new PhotoFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMatrixData = getArguments().getFloatArray(INIT_MATRIX_THUMBNAIL);
        enableAnimation = getArguments().getBoolean(ENABLE_ANIMATION);
        photoUrl = getArguments().getString(PHOTO_URL);
        thumbnailWidth = getArguments().getInt(THUMBNAIL_WIDTH);
        thumbnailHeight = getArguments().getInt(THUMBNAIL_HEIGHT);
        thumbnailTop = getArguments().getInt(THUMBNAIL_TOP);
        thumbnailLeft = getArguments().getInt(THUMBNAIL_LEFT);
        photoUrl = getArguments().getString(PHOTO_URL);
        position = getArguments().getInt(POSITION);
        imageInfoList = getArguments().getParcelableArrayList(ARRAY_THUMBNAIL);
        scaleType = (ImageView.ScaleType) getArguments().getSerializable(THUMBNAIL_SCALE_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        imageView = (ImageView) view.findViewById(R.id.fragment_photo_image);
        imageView.setOnTouchListener(this);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_photo_container);

        if (!enableAnimation) {
            imageView.setVisibility(View.VISIBLE);
        }

        Picasso.with(getActivity())
                .load(photoUrl)
                .into(imageView);

        initTempImageView();

        if (savedInstanceState == null) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    imageView.getLocationOnScreen(screenLocation);

                    toLeft = screenLocation[0];
                    toTop = screenLocation[1];

                    toWidth = imageView.getWidth();
                    toHeight = imageView.getHeight();

                    if (enableAnimation) {
                        runEnterAnimation();
                    }
                    return false;
                }
            });
        }
    }

    private void runEnterAnimation() {
        getParent().enterBlackBg();

        AnimatorSet imageAnimatorSet = createEnteringImageAnimation();
        AnimatorSet mEnteringAnimation = new AnimatorSet();

        mEnteringAnimation.setDuration(AnimationConfig.DURATION);
        mEnteringAnimation.setInterpolator(new DecelerateInterpolator());
        mEnteringAnimation.addListener(new PhotoAnimationListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setVisibility(View.VISIBLE);
                transitionImage.setVisibility(View.INVISIBLE);
            }
        });

        mEnteringAnimation.playTogether(imageAnimatorSet);
        mEnteringAnimation.start();
    }

    private void runExitAnimation() {
        try {
            imageView.setVisibility(View.VISIBLE);

            AnimatorSet imageAnimatorSet = createExitingImageAnimation();

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(AnimationConfig.DURATION);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new PhotoAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                }
            });

            animatorSet.playTogether(imageAnimatorSet);
            animatorSet.start();

            getParent().exitBlackBg();
        } catch (Exception exp) {
            finish();
        }
    }

    private void finish() {
        getParent().dismiss();
    }

    private ImageDialogFragment getParent() {
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(ImageDialogFragment.PACKAGE);
        if (prev != null) {
            return (ImageDialogFragment) prev;
        }
        return null;
    }

    private void initTempImageView() {
        if (!enableAnimation) {
            return;
        }
        transitionImage = new ImageView(getActivity());
        frameLayout.addView(transitionImage);

        transitionImage.setScaleType(scaleType);

        Picasso.with(getActivity())
                .load(photoUrl)
                .into(transitionImage);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) transitionImage.getLayoutParams();
        layoutParams.height = thumbnailHeight;
        layoutParams.width = thumbnailWidth;

        int thumbnailTopTemp = thumbnailTop - getStatusBarHeight(getActivity());
        layoutParams.setMargins(thumbnailLeft, thumbnailTopTemp, 0, 0);
        transitionImage.requestLayout();
    }

    @NonNull
    private ObjectAnimator createExitingImagePositionAnimator() {
        try {
            int[] locationOnScreen = new int[2];
            imageView.getLocationOnScreen(locationOnScreen);

            PropertyValuesHolder propertyLeft = PropertyValuesHolder.ofInt("left",
                    locationOnScreen[0],
                    thumbnailLeft);

            PropertyValuesHolder propertyTop = PropertyValuesHolder.ofInt("top",
                    locationOnScreen[1] - getStatusBarHeight(getActivity()),
                    thumbnailTop - getStatusBarHeight(getActivity()));

            PropertyValuesHolder propertyRight = PropertyValuesHolder.ofInt("right",
                    locationOnScreen[0] + imageView.getWidth(),
                    thumbnailLeft + thumbnailWidth);

            PropertyValuesHolder propertyBottom = PropertyValuesHolder.ofInt("bottom",
                    imageView.getBottom(),
                    thumbnailTop + thumbnailHeight - getStatusBarHeight(getActivity()));

            return ObjectAnimator.ofPropertyValuesHolder(imageView, propertyLeft, propertyTop, propertyRight, propertyBottom);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return null;
    }

    private ObjectAnimator createExitingImageMatrixAnimator() {
        Matrix initialMatrix = MatrixUtils.getImageMatrix(imageView);

        Matrix endMatrix = new Matrix();
        endMatrix.setValues(initMatrixData);

        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        return ObjectAnimator.ofObject(imageView, MatrixEvaluator.ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), initialMatrix, endMatrix);
    }

    private AnimatorSet createExitingImageAnimation() {
        ObjectAnimator positionAnimator = createExitingImagePositionAnimator();
        ObjectAnimator matrixAnimator = createExitingImageMatrixAnimator();

        if (positionAnimator == null || matrixAnimator == null) {
            return null;
        }
        AnimatorSet exitingImageAnimation = new AnimatorSet();
        exitingImageAnimation.playTogether(positionAnimator, matrixAnimator);

        return exitingImageAnimation;
    }

    @NonNull
    private AnimatorSet createEnteringImageAnimation() {
        ObjectAnimator positionAnimator = createEnteringImagePositionAnimator();
        ObjectAnimator matrixAnimator = createEnteringImageMatrixAnimator();

        AnimatorSet enteringImageAnimation = new AnimatorSet();
        enteringImageAnimation.playTogether(positionAnimator, matrixAnimator);

        return enteringImageAnimation;
    }

    @NonNull
    private ObjectAnimator createEnteringImagePositionAnimator() {
        PropertyValuesHolder propertyLeft = PropertyValuesHolder.ofInt("left",
                transitionImage.getLeft(), toLeft);

        PropertyValuesHolder propertyTop = PropertyValuesHolder.ofInt("top",
                transitionImage.getTop(),
                toTop - getStatusBarHeight(getActivity()));

        PropertyValuesHolder propertyRight = PropertyValuesHolder.ofInt("right",
                transitionImage.getRight(), toLeft + toWidth);

        PropertyValuesHolder propertyBottom = PropertyValuesHolder.ofInt("bottom",
                transitionImage.getBottom(), toTop + toHeight -
                        getStatusBarHeight(getActivity()));

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(transitionImage, propertyLeft,
                propertyTop, propertyRight, propertyBottom);
        animator.addListener(new PhotoAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) transitionImage.getLayoutParams();
                layoutParams.height = imageView.getHeight();
                layoutParams.width = imageView.getWidth();
                layoutParams.setMargins(toLeft, toTop - getStatusBarHeight(getActivity()), 0, 0);
            }
        });
        return animator;
    }

    private ObjectAnimator createEnteringImageMatrixAnimator() {
        Matrix initMatrix = MatrixUtils.getImageMatrix(transitionImage);
        initMatrix.getValues(initMatrixData);

        Matrix endMatrix = MatrixUtils.getImageMatrix(imageView);

        transitionImage.setScaleType(ImageView.ScaleType.MATRIX);

        return ObjectAnimator.ofObject(transitionImage, MatrixEvaluator.ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), initMatrix, endMatrix);
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onPhotoChangeLocation(PhotoLocation photoLocation) {
        Log.d("onPhotoChangeLocation", String.valueOf(photoLocation.position));
        Log.d("onPhotoChangeLocation", String.valueOf(position));
        if (photoLocation.position == position) {
            thumbnailTop = photoLocation.top;
            thumbnailLeft = photoLocation.left;
            initMatrixData = photoLocation.matrixData;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    runExitAnimation();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}