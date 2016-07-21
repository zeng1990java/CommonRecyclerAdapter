package com.github.zeng1990java.commonadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:31
 */
class ViewHolder extends RecyclerView.ViewHolder {

    private ViewBinder mViewBinder;

    public ViewHolder(View itemView) {
        super(itemView);
        mViewBinder = ViewBinder.create(itemView);
    }

    public ViewHolder(ViewBinder viewBinder){
        super(viewBinder.getView());
        mViewBinder = viewBinder;
    }

    public ViewHolder imageLoader(AdapterImageLoader imageLoader){
        mViewBinder.setImageLoader(imageLoader);
        return this;
    }

    public ViewBinder viewBinder(){
        return mViewBinder;
    }
}
