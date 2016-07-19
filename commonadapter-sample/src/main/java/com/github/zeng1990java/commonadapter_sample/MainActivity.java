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

        mRecyclerView.setAdapter(mCommonRecyclerAdapter = new CommonRecyclerAdapter<String>(this, R.layout.item_view, getDatas()) {
            @Override
            public void bindData(ViewHolder holder, String data, int position) {
                holder.viewBinder().setText(R.id.text, data + " position: "+position);
            }
        });

        View header1 = mCommonRecyclerAdapter.addHeaderView(R.layout.header_view);
        header1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("header click");
            }
        });

        ViewBinder.create(header1).setText(R.id.header_text, "Header 1");

        View header2 = mCommonRecyclerAdapter.addHeaderView(R.layout.header_view);
        header2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonRecyclerAdapter.removeHeaderView(v);
            }
        });


        View footer = mCommonRecyclerAdapter.addFooterView(R.layout.footer_view);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonRecyclerAdapter.removeFooterView(v);
            }
        });

        mCommonRecyclerAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder holder, int position) {
//                toast("Item click "+position);
                MultiTypeActivity.start(MainActivity.this);
            }
        });

        mCommonRecyclerAdapter.setOnItemLongClickListener(new CommonRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(ViewHolder holder, int position) {
                toast("Item long click "+position);
                return true;
            }
        });

        mCommonRecyclerAdapter.setOnLoadMoreListener(new CommonRecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCommonRecyclerAdapter.setIsHasLoadMore(false);
                        mCommonRecyclerAdapter.addAll(getDatas());
                    }
                }, 5000);
            }
        });

//        mCommonRecyclerAdapter.setLoadMoreLayoutId(R.layout.custom_loading);
        mCommonRecyclerAdapter.setIsHasLoadMore(true);
    }

    private List<String> getDatas(){
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            datas.add("Item "+i);
        }

        return datas;
    }

    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
