## 特性

* 封装数据集合添加删除等操作
* 添加或删除Header和Footer
* 全局的图片加载器
* Item 的click和longClick事件
* 多Item布局支持
* 滑动到底部加载更多

## 使用示例

* 配置默认的加载更多布局和图片加载器

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 配置默认的加载更多布局和默认的图片加载器
        AdapterConfig.getInstance()
                .setLoadingLayoutId(R.layout.custom_loading)
                .setImageLoader(new PicassoImageLoader());

    }
}

public class PicassoImageLoader implements AdapterImageLoader {
    @Override
    public void load(Context context, ImageView imageView, String imageUrl) {
        Picasso.with(context).load(imageUrl).into(imageView);
    }
}
```

* 单Item布局使用示例

```java
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
                mCommonRecyclerAdapter.setIsHasLoadMore(false);
                mCommonRecyclerAdapter.addAll(getDatas());
            }
        }, 5000);
    }
});

// 设置加载跟多的布局
//        mCommonRecyclerAdapter.setLoadMoreLayoutId(R.layout.custom_loading);
// 设置有加载更多
mCommonRecyclerAdapter.setIsHasLoadMore(true);
```

* 多Item布局使用示例

```java
private class NewsAdapter extends CommonRecyclerAdapter<News>{

    public NewsAdapter(@NonNull Context context) {
        super(context, 0);
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
    public void bindData(ViewHolder holder, News data, int position) {
        switch (data.getNewsType()){
            case News.TYPE_NONE_PICTURE:
                holder.viewBinder().setText(R.id.item_none_picture_title, data.getTitle())
                        .setText(R.id.item_none_picture_author,
                                 String.format(Locale.CHINA, Consts.FORMAT_AUTHOR, data.getAuthor()))
                        .setText(R.id.item_none_picture_date,
                                 Consts.DATE_FORMAT.format(new Date(data.getReleaseTime())))
                        .setText(R.id.item_none_picture_intro, data.getIntro());
                break;
            case News.TYPE_SINGLE_PICTURE:
                holder.viewBinder().setText(R.id.item_single_picture_title, data.getTitle())
                        .setText(R.id.item_single_picture_author,
                                 String.format(Locale.CHINA, Consts.FORMAT_AUTHOR, data.getAuthor()))
                        .setText(R.id.item_single_picture_date,
                                 Consts.DATE_FORMAT.format(new Date(data.getReleaseTime())))
                        .setImageUrl(R.id.item_single_picture_cover,data.getCoverUrl());
                break;
            case News.TYPE_MULTIPLE_PICTURE:
                String[] urls = data.getCoverUrl().split(Consts.URL_SEPARATOR);
                holder.viewBinder().setText(R.id.item_multiple_picture_intro, data.getIntro())
                        .setImageUrl(R.id.item_multiple_picture_cover_left,urls[0])
                        .setImageUrl(R.id.item_multiple_picture_cover_right, urls[1]);
                break;
        }
    }
}
```

##感谢

* [base-adapter-helper](https://github.com/JoanZapata/base-adapter-helper)
* [CommonAdapter](https://github.com/qyxxjd/CommonAdapter)
