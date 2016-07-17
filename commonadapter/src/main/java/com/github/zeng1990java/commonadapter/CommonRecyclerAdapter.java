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
import java.util.Iterator;
import java.util.List;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:54
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

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
    private List<T> mDatas;
    private LayoutInflater mInflater;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;

    private boolean isHasLoadMore = false;
    private boolean isLoadingMore = false;

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
        isLoadingMore = false;
        if (this.isHasLoadMore == isHasLoadMore){
            return;
        }
        this.isHasLoadMore = isHasLoadMore;
        notifyDataSetChanged();
    }

    public void onLoadMoreComplete(){
        isLoadingMore = false;
    }

    public boolean isLoadingMore(){
        return isLoadingMore;
    }

    public View addHeaderView(@LayoutRes int layoutId){
        ensureHeaderLayout();
        View header = mInflater.inflate(layoutId, mHeaderLayout, false);
        return addHeaderView(header);
    }

    public View addHeaderView(View header){
        ensureHeaderLayout();
        mHeaderLayout.addView(header);
        notifyDataSetChanged();
        return header;
    }

    public void removeHeaderView(View header){
        ensureHeaderLayout();
        mHeaderLayout.removeView(header);
        notifyDataSetChanged();
    }

    public View addFooterView(@LayoutRes int layoutId){
        ensureFooterLayout();
        View footer = mInflater.inflate(layoutId, mFooterLayout, false);
        return addFooterView(footer);
    }

    public View addFooterView(View footer){
        ensureFooterLayout();
        mFooterLayout.addView(footer);
        notifyDataSetChanged();
        return footer;
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
            if (isHasLoadMore && !isLoadingMore){
                isLoadingMore = true;
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

    public T getItem(int position){
        return mDatas.get(position);
    }

    public void add(T data){
        add(0, data);
    }

    public void add(int position, T data){
        mDatas.add(position, data);
        notifyItemInserted(position+getHeaderViewItemCount());
    }

    public void remove(int position, T data){
        mDatas.remove(position);
        notifyItemRemoved(position+getHeaderViewItemCount());
    }

    public void addAll(@NonNull List<T> datas){
        int positionStart = mDatas.size();
        mDatas.addAll(datas);
        notifyItemRangeInserted(positionStart + getHeaderViewItemCount(), datas.size());
    }

    public void replaceAll(@NonNull List<T> datas){
        if (mDatas.equals(datas)){
            return;
        }

        if (mDatas.isEmpty() && datas.isEmpty()) {
            return;
        }

        if (mDatas.isEmpty()){
            addAll(datas);
            return;
        }

        if (datas.isEmpty()){
            clear();
            return;
        }

        // 首先将就列表有，新列表没有的从旧列表中移除
        retainAll(datas);

        // 如果列表空了，直接插入全部就好了
        if (mDatas.isEmpty()) {
            addAll(datas);
            return;
        }

        // 遍历新列表，对旧列表数据进行更新，增加，删除
        for (int indexNew = 0; indexNew < datas.size(); indexNew++) {

            T item = datas.get(indexNew);
            int indexOld = mDatas.indexOf(item);
            if (indexOld == -1){
                add(indexNew, item);
            }else if (indexNew == indexOld){
                set(indexNew, item);
            }else {
                mDatas.remove(indexOld);
                mDatas.add(indexNew, item);
                notifyItemMoved(indexOld + getHeaderViewItemCount(), indexNew + getHeaderViewItemCount());
            }
        }

    }

    public void set(int postion, T data){
        mDatas.set(postion, data);
        notifyItemChanged(getHeaderViewItemCount() + postion);
    }

    public void set(T oldData, T newData){
        if (contains(oldData)) {
            set(mDatas.indexOf(oldData), newData);
        }
    }

    public void retainAll(@NonNull List<T> datas){
        if (datas.isEmpty()){
            clear();
            return;
        }
        Iterator<T> iterator = mDatas.iterator();
        while (iterator.hasNext()){
            T next = iterator.next();
            if (datas.contains(next)){
                continue;
            }
            int index = mDatas.indexOf(next);
            iterator.remove();
            notifyItemRemoved(getHeaderViewItemCount() + index);
        }

    }

    public void clear(){
        if (mDatas.isEmpty()){
            return;
        }
        int size = mDatas.size();
        mDatas.clear();
        notifyItemRangeRemoved(getHeaderViewItemCount(), size);
    }

    public boolean contains(T data){
        return mDatas.contains(data);
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
}
