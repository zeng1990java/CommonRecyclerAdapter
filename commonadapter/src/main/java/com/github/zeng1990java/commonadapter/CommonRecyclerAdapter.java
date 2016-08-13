package com.github.zeng1990java.commonadapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:54
 */
public abstract class CommonRecyclerAdapter<T> extends ArrayRecyclerAdapter<T,ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(ViewHolder holder, int position);
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    private Context mContext;
    private int mLayoutId;
    private LayoutInflater mInflater;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;

    private boolean isHasLoadMore = false;
/* private boolean isLoadingMore = false; */

    private LoadMoreView.LoadState mLoadState = LoadMoreView.LoadState.IDLE;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    @LayoutRes
    private int mLoadMoreLayoutId;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setLoadMoreLayoutId(int loadMoreLayoutId) {
        mLoadMoreLayoutId = loadMoreLayoutId;
    }

    public CommonRecyclerAdapter(@NonNull Context context, int layoutId){
        this(context, layoutId, new ArrayList<T>());
    }

    public CommonRecyclerAdapter(@NonNull Context context, int layoutId, @NonNull List<T> datas){
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
        mLoadMoreLayoutId = AdapterConfig.getInstance().getLoadingLayoutId();
    }

    /**
     * 是否有加载更多
     * @param isHasLoadMore
     */
    public void setIsHasLoadMore(boolean isHasLoadMore){
        mLoadState = LoadMoreView.LoadState.IDLE;
        if (this.isHasLoadMore == isHasLoadMore){
            return;
        }
        this.isHasLoadMore = isHasLoadMore;
        notifyDataSetChanged();
    }

    public void onLoadMoreComplete(){
        mLoadState = LoadMoreView.LoadState.IDLE;
        notifyItemChanged(getHeaderViewItemCount() + getDataItemCount()+getFooterViewItemCount());
    }

    public void onLoadMoreError(){
        mLoadState = LoadMoreView.LoadState.ERROR;
        notifyItemChanged(getHeaderViewItemCount() + getDataItemCount()+getFooterViewItemCount());
    }

    public boolean isLoadingMore(){
        return mLoadState == LoadMoreView.LoadState.LOADING;
    }

    public ViewBinder addHeaderView(@LayoutRes int layoutId){
        ensureHeaderLayout();
        View header = mInflater.inflate(layoutId, mHeaderLayout, false);
        return addHeaderView(header);
    }

    public ViewBinder addHeaderView(View header){
        ensureHeaderLayout();
        mHeaderLayout.addView(header);
        notifyDataSetChanged();
        return ViewBinder.create(header);
    }

    public void removeHeaderView(View header){
        ensureHeaderLayout();
        mHeaderLayout.removeView(header);
        notifyDataSetChanged();
    }

    public ViewBinder addFooterView(@LayoutRes int layoutId){
        ensureFooterLayout();
        View footer = mInflater.inflate(layoutId, mFooterLayout, false);
        return addFooterView(footer);
    }

    public ViewBinder addFooterView(View footer){
        ensureFooterLayout();
        mFooterLayout.addView(footer);
        notifyDataSetChanged();
        return ViewBinder.create(footer);
    }

    public void removeFooterView(View footer){
        ensureFooterLayout();
        mFooterLayout.removeView(footer);
        notifyDataSetChanged();
    }

    private void ensureHeaderLayout(){
        if (mHeaderLayout == null){
            mHeaderLayout = createVerticalLayout();
        }
    }

    private void ensureFooterLayout(){
        if (mFooterLayout == null){
            mFooterLayout = createVerticalLayout();
        }
    }

    private LinearLayout createVerticalLayout(){
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);

        return linearLayout;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (isLoadMore(position)){
            if (mLoadState == LoadMoreView.LoadState.ERROR){
                mLoadState = LoadMoreView.LoadState.IDLE;
            }
        }

        if (isHeader(position) || isFooter(position) || isLoadMore(position)){
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams){
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeader(position) || isFooter(position) || isLoadMore(position)){
                        return gridLayoutManager.getSpanCount();
                    }

                    if (spanSizeLookup != null){
                        return spanSizeLookup.getSpanSize(position);
                    }

                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.adapter_view_header_layout){
            return new ViewHolder(mHeaderLayout);
        }

        if (viewType == R.layout.adapter_view_footer_layout){
            return new ViewHolder(mFooterLayout);
        }

        if (viewType == R.layout.adapter_view_load_layout){
            View itemView = inflateItemView(parent, mLoadMoreLayoutId);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isHasLoadMore && !isLoadingMore()){
                        mLoadState = LoadMoreView.LoadState.LOADING;
                        if (mOnLoadMoreListener != null){
                            mOnLoadMoreListener.onLoadMore();
                        }

                        notifyItemChanged(getHeaderViewItemCount() + getDataItemCount() + getFooterViewItemCount());
                    }
                }
            });
            return new ViewHolder(itemView);
        }
        View itemView = inflateItemView(parent, viewType);
        ViewBinder viewBinder = createViewBinder(itemView);
        return new ViewHolder(itemView, viewBinder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (isHeader(position) || isFooter(position)){
            return;
        }

        if (isLoadMore(position)){

            if (holder.itemView instanceof LoadMoreView){
                LoadMoreView loadMoreView = (LoadMoreView) holder.itemView;
                loadMoreView.setLoadState(mLoadState);
            }

            if (isHasLoadMore && !isLoadingMore() && mLoadState == LoadMoreView.LoadState.IDLE){
                mLoadState = LoadMoreView.LoadState.LOADING;
                if (mOnLoadMoreListener != null){
                    mOnLoadMoreListener.onLoadMore();
                }
            }
            return;
        }

        final int dataPosition = position - getHeaderViewItemCount();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(holder, dataPosition);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null){
                    return mOnItemLongClickListener.onItemLongClick(holder, dataPosition);
                }
                return false;
            }
        });

        bindData(holder, getItem(dataPosition), dataPosition);
    }

    public abstract void bindData(ViewHolder holder, T data, int position);

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)){
            return R.layout.adapter_view_header_layout;
        }

        if (isFooter(position)){
            return R.layout.adapter_view_footer_layout;
        }

        if (isLoadMore(position)){
            return R.layout.adapter_view_load_layout;
        }
        int dataPosition = position - getHeaderViewItemCount();
        return getItemLayoutResId(getItem(dataPosition), dataPosition);
    }

    protected ViewBinder createViewBinder(View itemView){
        return ViewBinder.create(mContext, itemView, getImageLoader());
    }

    protected AdapterImageLoader getImageLoader(){
        return AdapterConfig.getInstance().getImageLoader();
    }

    protected View inflateItemView(ViewGroup parent, int layoutId){
        return mInflater.inflate(layoutId, parent, false);
    }

    public int getItemLayoutResId(T data, int position){
        return mLayoutId;
    }

    @Override
    public int getItemCount() {
        return getDataItemCount() + getHeaderViewItemCount() + getFooterViewItemCount() + getLoadMoreViewItemCount();
    }

    public int getDataItemCount(){
        return mDatas.size();
    }

    private boolean isHeader(int position){
        return position == 0 && hasHeaderView();
    }


    private boolean isFooter(int position){
        return (position == getHeaderViewItemCount() + getDataItemCount()) && hasFooterView();
    }

    private boolean isLoadMore(int position){
        return hasLoadMoreView() && (position == getHeaderViewItemCount() + getDataItemCount() + getFooterViewItemCount());
    }

    private int getHeaderViewItemCount(){
        return hasHeaderView() ? 1 : 0;
    }

    private int getFooterViewItemCount(){
        return hasFooterView() ? 1 : 0;
    }

    private int getLoadMoreViewItemCount(){
        return hasLoadMoreView() ? 1 : 0;
    }

    private boolean hasHeaderView(){
        if (mHeaderLayout == null){
            return false;
        }

        return mHeaderLayout.getChildCount() > 0;
    }

    private boolean hasFooterView(){
        if(mFooterLayout == null){
            return false;
        }

        return mFooterLayout.getChildCount() > 0;
    }

    private boolean hasLoadMoreView(){
        return isHasLoadMore;
    }

    @Override
    protected int getAdapterPosition(int dataPosition) {
        return getHeaderViewItemCount() + dataPosition;
    }

    @Override
    public void replaceAll(@NonNull List<T> datas) {
        boolean curHasMore = isHasLoadMore;
        setIsHasLoadMore(false);
        super.replaceAll(datas);
        setIsHasLoadMore(curHasMore);
    }

    @Override
    public void retainAll(@NonNull List<T> datas) {
        boolean curHasMore = isHasLoadMore;
        setIsHasLoadMore(false);
        super.retainAll(datas);
        setIsHasLoadMore(curHasMore);
    }
}
