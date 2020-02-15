package in.komu.komu.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


public class GridImageViewDiscover extends AppCompatImageView {

    public GridImageViewDiscover(Context context) {
        super(context);
    }

    public GridImageViewDiscover(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridImageViewDiscover(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
