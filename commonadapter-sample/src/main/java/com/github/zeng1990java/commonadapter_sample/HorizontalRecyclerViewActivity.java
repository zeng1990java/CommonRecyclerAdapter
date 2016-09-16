package com.github.zeng1990java.commonadapter_sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.github.zeng1990java.commonadapter.CommonRecyclerAdapter;
import com.github.zeng1990java.commonadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * $desc
 *
 * @author zxb
 * @date 16/9/16 下午8:43
 */
public class HorizontalRecyclerViewActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, HorizontalRecyclerViewActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        final HorizontalAdapter adapter = new HorizontalAdapter(this);
        recyclerView.setAdapter(adapter);
        // 设置方向为水平
        adapter.setOrientation(CommonRecyclerAdapter.HORIZONTAL);

        adapter.addHeaderView(R.layout.item_hotizontail).setText(R.id.text, "Header");

        adapter.addFooterView(R.layout.item_hotizontail).setText(R.id.text, "Footer");

        adapter.addAll(getHorizontalListData());


        adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder holder, int position) {
                Toast.makeText(HorizontalRecyclerViewActivity.this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class HorizontalAdapter extends CommonRecyclerAdapter<String>{

        public HorizontalAdapter(@NonNull Context context) {
            super(context, R.layout.item_hotizontail);
        }

        @Override
        public void bindData(ViewHolder holder, String data, int position) {
            holder.viewBinder().setText(R.id.text, data);
        }
    }


    List<String> getHorizontalListData(){
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("Item "+i);
        }
        return datas;
    }
}
