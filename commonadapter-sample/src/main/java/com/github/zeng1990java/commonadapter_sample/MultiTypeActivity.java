package com.github.zeng1990java.commonadapter_sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.github.zeng1990java.commonadapter.CommonRecyclerAdapter;
import com.github.zeng1990java.commonadapter.ViewBinder;
import com.github.zeng1990java.commonadapter_sample.bean.News;
import com.github.zeng1990java.commonadapter_sample.consts.Consts;
import com.github.zeng1990java.commonadapter_sample.data.NewDataSource;

import java.util.Date;
import java.util.Locale;

public class MultiTypeActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, MultiTypeActivity.class);
        context.startActivity(starter);
    }

    RecyclerView mRecyclerView;
    NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_type);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mNewsAdapter = new NewsAdapter(this));

        mNewsAdapter.addAll(NewDataSource.getNewsList());

        mNewsAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewBinder binder) {
                News news = mNewsAdapter.getItem(binder.getPosition());
                Toast.makeText(MultiTypeActivity.this, news.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        mNewsAdapter.addHeaderView(R.layout.header_view);
    }

    private class NewsAdapter extends CommonRecyclerAdapter<News>{

        public NewsAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutResId(News data, int position) {
            int layoutResId = -1;
            switch (data.getNewsType()){
                case News.TYPE_NONE_PICTURE:
                    layoutResId = R.layout.item_none_picture;
                    break;
                case News.TYPE_SINGLE_PICTURE:
                    layoutResId = R.layout.item_single_picture;
                    break;
                case News.TYPE_MULTIPLE_PICTURE:
                    layoutResId = R.layout.item_multiple_picture;
                    break;
            }
            return layoutResId;
        }

        @Override
        public void bindData(ViewBinder binder, News data) {
            switch (data.getNewsType()){
                case News.TYPE_NONE_PICTURE:
                    binder.setText(R.id.item_none_picture_title, data.getTitle())
                            .setText(R.id.item_none_picture_author,
                                     String.format(Locale.CHINA, Consts.FORMAT_AUTHOR, data.getAuthor()))
                            .setText(R.id.item_none_picture_date,
                                     Consts.DATE_FORMAT.format(new Date(data.getReleaseTime())))
                            .setText(R.id.item_none_picture_intro, data.getIntro());
                    break;
                case News.TYPE_SINGLE_PICTURE:
                    binder.setText(R.id.item_single_picture_title, data.getTitle())
                            .setText(R.id.item_single_picture_author,
                                     String.format(Locale.CHINA, Consts.FORMAT_AUTHOR, data.getAuthor()))
                            .setText(R.id.item_single_picture_date,
                                     Consts.DATE_FORMAT.format(new Date(data.getReleaseTime())))
                            .setImageUrl(R.id.item_single_picture_cover,data.getCoverUrl());
                    break;
                case News.TYPE_MULTIPLE_PICTURE:
                    String[] urls = data.getCoverUrl().split(Consts.URL_SEPARATOR);
                    binder.setText(R.id.item_multiple_picture_intro, data.getIntro())
                            .setImageUrl(R.id.item_multiple_picture_cover_left,urls[0])
                            .setImageUrl(R.id.item_multiple_picture_cover_right, urls[1]);
                    break;
            }
        }
    }
}
