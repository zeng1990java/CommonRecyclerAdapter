package com.github.zeng1990java.commonadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * $desc
 *
 * @author zxb
 * @date 16/8/6 下午5:10
 */
public abstract class ArrayRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> mDatas;

    public ArrayRecyclerAdapter(){
        this(new ArrayList<T>());
    }

    public ArrayRecyclerAdapter(@NonNull List<T> datas){
        mDatas = datas;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public T getItem(int position){
        return mDatas.get(position);
    }

    public List<T> getData(){
        return mDatas;
    }

    public void add(T data){
        add(0, data);
    }

    public void addLast(T data){
        add(mDatas.size(), data);
    }

    public void add(int position, T data){
        mDatas.add(position, data);
        notifyItemInserted(position+getAdapterPosition(0));
    }

    public void remove(int position){
        remove(position, null);
    }

    public void remove(int position, T data){
        mDatas.remove(position);
        notifyItemRemoved(position+getAdapterPosition(0));
    }

    public void addAll(@NonNull List<T> datas){
        int positionStart = mDatas.size();
        mDatas.addAll(datas);
        notifyItemRangeInserted(positionStart + getAdapterPosition(0), datas.size());
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
                notifyItemMoved(indexOld + getAdapterPosition(0), indexNew + getAdapterPosition(0));
            }
        }

    }

    public void set(int postion, T data){
        mDatas.set(postion, data);
        notifyItemChanged(getAdapterPosition(0) + postion);
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
            notifyItemRemoved(getAdapterPosition(0) + index);
        }

    }

    public void clear(){
        if (mDatas.isEmpty()){
            return;
        }
        int size = mDatas.size();
        mDatas.clear();
        notifyItemRangeRemoved(getAdapterPosition(0), size);
    }

    public boolean contains(T data){
        return mDatas.contains(data);
    }

    /**
     * real data position
     * @param dataPosition
     * @return
     */
    protected int getAdapterPosition(int dataPosition){
        return dataPosition;
    }
}
