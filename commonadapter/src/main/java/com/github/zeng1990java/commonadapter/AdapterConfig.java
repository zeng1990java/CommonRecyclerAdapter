package com.github.zeng1990java.commonadapter;

import android.support.annotation.LayoutRes;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:40
 */
public class AdapterConfig {

    private static AdapterConfig sInstance = new AdapterConfig();

    private AdapterImageLoader mImageLoader;
    private int mLoadingLayoutId = R.layout.loading_layout;

    private AdapterConfig(){

    }

    public static AdapterConfig getInstance(){
        return sInstance;
    }

    public AdapterConfig setImageLoader(AdapterImageLoader imageLoader){
        mImageLoader = imageLoader;
        return this;
    }

    public AdapterImageLoader getImageLoader(){
        return mImageLoader;
    }

    public AdapterConfig setLoadingLayoutId(@LayoutRes int layoutId){
        mLoadingLayoutId = layoutId;
        return this;
    }

    public int getLoadingLayoutId(){
        return mLoadingLayoutId;
    }
}
