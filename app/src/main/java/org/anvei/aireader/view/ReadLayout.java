package org.anvei.aireader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public abstract class ReadLayout extends ViewGroup implements IReadLayout {

    public ReadLayout(Context context) {
        super(context);
    }

    public ReadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
