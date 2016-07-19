package com.github.zeng1990java.commonadapter_sample;

import android.app.Application;

import com.github.zeng1990java.commonadapter.AdapterConfig;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/17 上午10:14
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 配置默认的加载更多布局和默认的图片加载器
        AdapterConfig.getInstance()
                .setLoadingLayoutId(R.layout.custom_loading)
                .setImageLoader(new PicassoImageLoader());

    }
}
