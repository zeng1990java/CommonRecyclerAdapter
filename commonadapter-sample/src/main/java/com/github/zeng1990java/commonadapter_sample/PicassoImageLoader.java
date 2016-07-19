package com.github.zeng1990java.commonadapter_sample;

import android.content.Context;
import android.widget.ImageView;

import com.github.zeng1990java.commonadapter.AdapterImageLoader;
import com.squareup.picasso.Picasso;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/17 下午10:53
 */
public class PicassoImageLoader implements AdapterImageLoader {
    @Override
    public void load(Context context, ImageView imageView, String imageUrl) {
        Picasso.with(context).load(imageUrl).into(imageView);
    }
}
