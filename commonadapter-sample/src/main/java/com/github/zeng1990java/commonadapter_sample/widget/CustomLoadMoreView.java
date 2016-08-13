package com.github.zeng1990java.commonadapter_sample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.zeng1990java.commonadapter.LoadMoreView;

/**
 * $desc
 *
 * @author zxb
 * @date 16/8/6 下午9:41
 */
public class CustomLoadMoreView extends FrameLayout implements LoadMoreView{

    LoadState mLoadState = LoadState.IDLE;

    private TextView mLoadText;
    private ProgressBar mProgressBar;

    public CustomLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLoadText = (TextView) findViewById(com.github.zeng1990java.commonadapter.R.id.loading_text);
        mProgressBar = (ProgressBar) findViewById(com.github.zeng1990java.commonadapter.R.id.loading_progress);
    }

    @Override
    public LoadState getLoadState() {
        return mLoadState;
    }

    @Override
    public void setLoadState(LoadState loadState) {
        mLoadState = loadState;
        if (mLoadState == LoadState.ERROR){
            mLoadText.setText("自定义加载失败");
            mProgressBar.setVisibility(GONE);
        }else {
            mLoadText.setText("自定义加载更多...");
            mProgressBar.setVisibility(VISIBLE);
        }
    }
}
