package com.github.zeng1990java.commonadapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * $desc
 *
 * @author zxb
 * @date 16/8/6 下午9:16
 */
public class DefaultLoadMoreView extends FrameLayout implements LoadMoreView{

    private LoadState mLoadState = LoadState.IDLE;

    private TextView mLoadText;
    private ProgressBar mProgressBar;

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LoadState getLoadState() {
        return mLoadState;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLoadText = (TextView) findViewById(R.id.loading_text);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
    }

    @Override
    public void setLoadState(LoadState loadState) {
        mLoadState = loadState;
        if (mLoadState == LoadState.ERROR){
            mLoadText.setText(R.string.load_more_error);
            mProgressBar.setVisibility(GONE);
        }else {
            mLoadText.setText(R.string.load_more_loading);
            mProgressBar.setVisibility(VISIBLE);
        }
    }
}
