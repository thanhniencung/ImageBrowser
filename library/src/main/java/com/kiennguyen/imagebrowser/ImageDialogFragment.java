package com.kiennguyen.imagebrowser;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class ImageDialogFragment extends DialogFragment {
    public static final String PACKAGE = "com.kiennguyen.imagebrowser";
    private ViewPager mViewpager;
    private List<String> imageList;
    private PhotoAdapter photoAdapter;
    private FrameLayout frameLayout;

    private List<ImageInfo> imageInfoList;
    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private int currentImagePos;
    private float[] currentMatrix = new float[9];
    private ImageView.ScaleType scaleType;
    private ColorDrawable background;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            imageList =bundle.getStringArrayList(PACKAGE + ".images_url");
            currentMatrix = bundle.getFloatArray(PACKAGE + ".current_matrix");
            thumbnailTop = bundle.getInt(PACKAGE + ".top");
            thumbnailLeft = bundle.getInt(PACKAGE + ".left");
            thumbnailWidth = bundle.getInt(PACKAGE + ".width");
            thumbnailHeight = bundle.getInt(PACKAGE + ".height");
            scaleType = (ImageView.ScaleType) bundle.getSerializable(PACKAGE + ".scale_type");
            currentImagePos = bundle.getInt(PACKAGE + ".current");
            imageInfoList = bundle.getParcelableArrayList(PACKAGE + ".arr_location");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_image_browser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        frameLayout = (FrameLayout) view.findViewById(R.id.dialog_image_browser_container);
        background = new ColorDrawable(Color.BLACK);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            frameLayout.setBackgroundDrawable(background);
        } else {
            frameLayout.setBackground(background);
        }

        mViewpager = (ViewPager) view.findViewById(R.id.dialog_image_browser_viewpager);
        mViewpager.addOnPageChangeListener(new MyOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                PhotoChange photoChange = new PhotoChange();
                photoChange.position = position;
                EventBus.getDefault().post(photoChange);
            }
        });

        photoAdapter = new PhotoAdapter(getChildFragmentManager());

        mViewpager.setPageMargin(10);
        mViewpager.setAdapter(photoAdapter);
        mViewpager.setCurrentItem(currentImagePos);
    }

    public void enterBlackBg() {
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(background, "alpha", 25, 255);
        bgAnim.setDuration(AnimationConfig.DURATION);
        bgAnim.start();
    }

    public void exitBlackBg() {
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(background, "alpha", 0);
        bgAnim.setDuration(AnimationConfig.DURATION);
        bgAnim.start();
    }

    class PhotoAdapter extends FragmentPagerAdapter {

        public PhotoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString(PhotoFragment.PHOTO_URL, imageList.get(position));
            bundle.putInt(PhotoFragment.POSITION, position);
            bundle.putFloatArray(PhotoFragment.INIT_MATRIX_THUMBNAIL, currentMatrix);
            bundle.putBoolean(PhotoFragment.ENABLE_ANIMATION, position == currentImagePos);
            bundle.putInt(PhotoFragment.THUMBNAIL_WIDTH, thumbnailWidth);
            bundle.putInt(PhotoFragment.THUMBNAIL_HEIGHT, thumbnailHeight);
            bundle.putInt(PhotoFragment.THUMBNAIL_TOP, thumbnailTop);
            bundle.putInt(PhotoFragment.THUMBNAIL_LEFT, thumbnailLeft);
            bundle.putParcelableArrayList(PhotoFragment.ARRAY_THUMBNAIL, (ArrayList<? extends Parcelable>) imageInfoList);
            bundle.putSerializable(PhotoFragment.THUMBNAIL_SCALE_TYPE, scaleType);
            bundle.putString(PhotoFragment.PHOTO_URL,imageList.get(position));

            return PhotoFragment.getInstance(bundle);
        }

        @Override
        public int getCount() {
            return imageList.size();
        }
    }

    public static void showImages(FragmentManager fm, int currentPosition, ImageView view, List<String> newsFeedImageList) {
        if (view.getDrawable() == null) {
            return;
        }
        int[] screenLocation = new int[2];
        view.getLocationInWindow(screenLocation);

        float[] initMatrixData = new float[9];
        Matrix initMatrixImageView = MatrixUtils.getImageMatrix(view);
        initMatrixImageView.getValues(initMatrixData);

        Bundle bundle = new Bundle();
        bundle.putFloatArray(PACKAGE + ".current_matrix", initMatrixData);
        bundle.putInt(PACKAGE + ".left", screenLocation[0]);
        bundle.putInt(PACKAGE + ".top", screenLocation[1]);
        bundle.putInt(PACKAGE + ".width", view.getWidth());
        bundle.putInt(PACKAGE + ".height", view.getHeight());
        bundle.putSerializable(PACKAGE + ".scale_type", view.getScaleType());
        bundle.putInt(PACKAGE + ".current", currentPosition);
        bundle.putStringArrayList(PACKAGE + ".images_url", (ArrayList<String>) newsFeedImageList);

        ImageDialogFragment dialogFragment = new ImageDialogFragment ();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, ImageDialogFragment.PACKAGE);
    }
}
