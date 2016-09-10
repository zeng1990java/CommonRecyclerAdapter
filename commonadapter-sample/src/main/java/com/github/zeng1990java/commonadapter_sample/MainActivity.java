package com.github.zeng1990java.commonadapter_sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.zeng1990java.commonadapter.CommonRecyclerAdapter;
import com.github.zeng1990java.commonadapter.ViewBinder;
import com.github.zeng1990java.commonadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    CommonRecyclerAdapter<String> mCommonRecyclerAdapter;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // 单布局使用
        mRecyclerView.setAdapter(mCommonRecyclerAdapter = new CommonRecyclerAdapter<String>(this, R.layout.item_view, getDatas()) {
            @Override
            public void bindData(ViewHolder holder, String data, int position) {
                holder.viewBinder().setText(R.id.text, data + " position: "+position);
            }
        });

        // 添加Header
        ViewBinder header1Binder = mCommonRecyclerAdapter.addHeaderView(R.layout.header_view);
        header1Binder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("header click");
            }
        });
        // header 数据绑定
        header1Binder.setText(R.id.header_text, "Header 1");

        //
        ViewBinder header2Binder = mCommonRecyclerAdapter.addHeaderView(R.layout.header_view);
        header2Binder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移除header
                mCommonRecyclerAdapter.removeHeaderView(v);
            }
        });

        // 添加footer
        ViewBinder footerBinder = mCommonRecyclerAdapter.addFooterView(R.layout.footer_view);
        footerBinder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移除footer
                mCommonRecyclerAdapter.removeFooterView(v);
            }
        });

        // 设置item点击事件
        mCommonRecyclerAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder holder, int position) {
                MultiTypeActivity.start(MainActivity.this);
            }
        });

        // 设置item长按事件
        mCommonRecyclerAdapter.setOnItemLongClickListener(new CommonRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(ViewHolder holder, int position) {
                mCommonRecyclerAdapter.set(position, "Item Long Click "+position);
                return true;
            }
        });

        // 设置加载更多监听器
        mCommonRecyclerAdapter.setOnLoadMoreListener(new CommonRecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int random = new Random().nextInt(20);
                        Toast.makeText(getApplicationContext(), "random: "+random, Toast.LENGTH_SHORT).show();
                        if (random < 10){
                            mCommonRecyclerAdapter.onLoadMoreError();
                        }else {
                            mCommonRecyclerAdapter.setIsHasLoadMore(true);
                            mCommonRecyclerAdapter.addAll(getDatas());
                        }

                    }
                }, 2000);
            }
        });

        // 设置加载跟多的布局
//        mCommonRecyclerAdapter.setLoadMoreLayoutId(R.layout.custom_loading);
        // 设置有加载更多
        mCommonRecyclerAdapter.setIsHasLoadMore(true);
    }

    private List<String> getDatas(){
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            datas.add("Item "+i);
        }

        return datas;
    }

    public void add(View view){
        mCommonRecyclerAdapter.add("new add data "+new Random().nextInt(20));
    }

    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
