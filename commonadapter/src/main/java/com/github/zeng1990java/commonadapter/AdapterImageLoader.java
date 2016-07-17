package com.github.zeng1990java.commonadapter;

import android.content.Context;
import android.widget.ImageView;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:21
 */
public interface AdapterImageLoader {
    void load(Context context, ImageView imageView, String imageUrl);
}
