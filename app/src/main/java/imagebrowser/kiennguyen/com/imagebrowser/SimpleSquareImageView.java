package imagebrowser.kiennguyen.com.imagebrowser;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class SimpleSquareImageView extends AppCompatImageView {

    public SimpleSquareImageView(Context context) {
        super(context);
    }

    public SimpleSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleSquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int width = displaymetrics.widthPixels/3;
        setMeasuredDimension(width, width);
    }
}