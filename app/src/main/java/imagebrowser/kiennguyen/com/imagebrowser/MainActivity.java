package imagebrowser.kiennguyen.com.imagebrowser;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kiennguyen.imagebrowser.MatrixUtils;
import com.kiennguyen.imagebrowser.PhotoChange;
import com.kiennguyen.imagebrowser.PhotoLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        ImageAdapter adapter = new ImageAdapter(this);
        recyclerView.setAdapter(adapter);

        setupAdapter(adapter);
    }

    private void setupAdapter(ImageAdapter adapter) {
        List<String> imageList = new ArrayList<>();
        imageList.add("http://www.statesymbolsusa.org/sites/statesymbolsusa.org/files/primary-images/redcarnationOhioflower.jpg");
        imageList.add("http://www.statesymbolsusa.org/sites/statesymbolsusa.org/files/primary-images/peachblossomspeachflowers.jpg");
        imageList.add("http://www.namesofflowers.net/images/lilac-flower-4.jpg");
        imageList.add("http://www.pondokbambu.com/gallery/flower-plant/015-b.jpg");
        imageList.add("http://photostream.iastate.edu/public/000/0506/506-medium.jpg");
        imageList.add("http://maxpixel.freegreatpicture.com/static/photo/640/Macro-Green-Flowering-Flower-350493.jpg");
        imageList.add("https://s-media-cache-ak0.pinimg.com/736x/aa/f0/3c/aaf03cf013cb4cdef03f23ca4f03cf70.jpg");
        imageList.add("http://www.statesymbolsusa.org/sites/statesymbolsusa.org/files/primary-images/redrosebeautystateflowerNY.jpg");
        imageList.add("https://muachung10.vcmedia.vn/thumb_w/640/i:gallery/2014/09/08/hqokh/2-dem-nghi-khach-san-Da-Lat-Flower-Tang-phieu-giam-gia-Spa.jpg");
        imageList.add("https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Flower-center142058.jpg/640px-Flower-center142058.jpg");
        imageList.add("http://www.thegardenhelper.com/psd/schizanthus_wisetonensis.jpg");
        imageList.add("http://absfreepic.com/absolutely_free_photos/small_photos/violet-flower-4724x3543_28030.jpg");
        imageList.add("http://maxpixel.freegreatpicture.com/static/photo/640/Red-Poppy-Poppy-Poppy-Flower-Klatschmohn-Red-1117450.jpg");
        imageList.add("http://www.tireeimages.com/images/tiree-flower-04.jpg");
        imageList.add("http://maxpixel.freegreatpicture.com/static/photo/640/Blossom-Flower-California-Blooms-Poppy-Plant-539555.jpg");
        imageList.add("http://i230.photobucket.com/albums/ee199/JimmyHullo/Uuu/U037.jpg");
        imageList.add("https://lh4.ggpht.com/urk2eqqCX7FtlhTT1U9yGk0jNZrBDyRWV4bWI3VulzhPD4C6piotwBVT-2NnW5KUyM4=h900");
        imageList.add("https://www.learner.org/jnorth/images/graphics/t/tulip_McGehee046.jpg");
        imageList.add("https://ae01.alicdn.com/kf/HTB161jwHVXXXXagXXXXq6xXFXXXE/-font-b-Japanese-b-font-font-b-traditional-b-font-Style-kimono-flower-hairpin-yukata.jpg");
        imageList.add("https://farm8.staticflickr.com/7155/6712210227_144659cbed_o.jpg");
        imageList.add("http://www.math.iupui.edu/~mmisiure/kwiaty/wallflower.jpg");
        imageList.add("http://media2.wcpo.com/photo/2015/04/15/WCPO_Cincinnati_Flower_Show10_1429095012175_16802452_ver1.0_640_480.JPG");
        imageList.add("http://s1.dmcdn.net/AmGwh.jpg");
        imageList.add("https://4.bp.blogspot.com/-j8E8sKX2kH4/V4_QZHkQhYI/AAAAAAAABTU/K2HXjUIOpCEKrQQqC3EdRKPvqQodqeGbgCLcB/s640/-1491205905.jpg");
        adapter.setData(imageList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPhotoChange(final PhotoChange photoChange) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gridLayoutManager.scrollToPosition(photoChange.position);
            }
        }, 250);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = gridLayoutManager.findViewByPosition(photoChange.position);
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                if (imageView == null) {
                    return;
                }
                float[] initMatrixData = new float[9];
                Matrix initMatrixImageView = MatrixUtils.getImageMatrix(imageView);
                initMatrixImageView.getValues(initMatrixData);

                int[] screenLocation = new int[2];
                view.getLocationInWindow(screenLocation);

                PhotoLocation photoLocation = new PhotoLocation();
                photoLocation.position = photoChange.position;
                photoLocation.top = screenLocation[1];
                photoLocation.left = screenLocation[0];
                photoLocation.matrixData = initMatrixData;

                EventBus.getDefault().post(photoLocation);
            }
        }, 500);
    }
}
