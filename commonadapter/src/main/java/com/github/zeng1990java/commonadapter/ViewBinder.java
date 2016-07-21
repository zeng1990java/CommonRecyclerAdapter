package com.github.zeng1990java.commonadapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * $desc
 *
 * @author zxb
 * @date 16/7/16 下午11:18
 */
public class ViewBinder {
    /** Views indexed with their IDs */
    private final SparseArray<View> mViews = new SparseArray<>();
    private Context mContext;
    private View mItemView;
    private AdapterImageLoader mImageLoader;
    private int mPosition;

    private ViewBinder(Context context, View itemView){
        this(context, itemView, AdapterConfig.getInstance().getImageLoader());
    }

    private ViewBinder(Context context, View itemView, AdapterImageLoader imageLoader){
        mContext = context;
        mItemView = itemView;
        mImageLoader = imageLoader;;
    }

    public static ViewBinder create(View itemView){
        return new ViewBinder(itemView.getContext(), itemView);
    }

    public static ViewBinder create(View itemView, AdapterImageLoader imageLoader){
        return new ViewBinder(itemView.getContext(), itemView, imageLoader);
    }

    public static ViewBinder create(Context context, View itemView){
        return new ViewBinder(context, itemView);
    }

    public static ViewBinder create(Context context, View itemView, AdapterImageLoader imageLoader){
        return new ViewBinder(context, itemView, imageLoader);
    }

    public int getPosition() {
        return mPosition;
    }

    void setPosition(int position) {
        this.mPosition = position;
    }

    /**
     * This method allows you to retrieve a view and perform custom
     * operations on it, not covered by the BaseAdapterHelper.
     * If you think it's a common use case, please consider creating
     * a new issue at https://github.com/JoanZapata/base-adapter-helper/issues.
     * @param viewId The id of the view you want to retrieve.
     */
    public <T extends View> T getView(int viewId) {
        return retrieveView(viewId);
    }


    /**
     * Will set the text of a TextView.
     * @param viewId The view id.
     * @param value  The text to put in the text view.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setText(int viewId, String value) {
        TextView view = retrieveView(viewId);
        view.setText(replaceEmpty(value));
        return this;
    }

    /**
     * Will set the image of an ImageView from a resource id.
     * @param viewId     The view id.
     * @param imageResId The image resource id.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setImageResource(int viewId, int imageResId) {
        ImageView view = retrieveView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    /**
     * Will set background color of a view.
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setBackgroundColor(int viewId, int color) {
        View view = retrieveView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * Will set background of a view.
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setBackgroundRes(int viewId, int backgroundRes) {
        View view = retrieveView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * Will set text color of a TextView.
     * @param viewId    The view id.
     * @param textColor The text color (not a resource id).
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setTextColor(int viewId, int textColor) {
        TextView view = retrieveView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    /**
     * Will set text color of a TextView.
     * @param viewId       The view id.
     * @param textColorRes The text color resource id.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setTextColorRes(int viewId, int textColorRes) {
        TextView view = retrieveView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorRes));
        return this;
    }

    /**
     * Will set text color of a TextView.
     * @param viewId       The view id.
     * @param textColorRes The text color resource id.
     * @param theme theme The theme used to style the color attributes, may be
     *              {@code null}.
     * @return The BaseAdapterHelper for chaining.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public ViewBinder setTextColorRes(int viewId, int textColorRes, @Nullable Resources.Theme theme) {
        TextView view = retrieveView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorRes, theme));
        return this;
    }

    /**
     * Will set the image of an ImageView from a drawable.
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setImageDrawable(int viewId, Drawable drawable) {
        if(null != drawable){
            ImageView view = retrieveView(viewId);
            view.setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * Will download an image from a URL and put it in an ImageView.<br/>
     * @param viewId   The view id.
     * @param imageUrl The image URL.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setImageUrl(int viewId, String imageUrl) {
        ImageView view = retrieveView(viewId);
        if(null != this.mImageLoader){
            this.mImageLoader.load(mContext, view, imageUrl);
        }
        return this;
    }

    /**
     * Will download an image from a URL and put it in an ImageView.<br/>
     * @param context  The Context.
     * @param viewId   The view id.
     * @param imageUrl The image URL.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setImageUrl(Context context, int viewId, String imageUrl){
        ImageView view = retrieveView(viewId);
        if(null != this.mImageLoader){
            this.mImageLoader.load(context, view, imageUrl);
        }
        return this;
    }

    /** Custom network load images */
    public ViewBinder setImageLoader(AdapterImageLoader imageLoad){
        this.mImageLoader = imageLoad;
        return this;
    }

    /** Add an action to set the image of an image view. Can be called multiple times. */
    public ViewBinder setImageBitmap(int viewId, Bitmap bitmap) {
        if(null != bitmap){
            ImageView view = retrieveView(viewId);
            view.setImageBitmap(bitmap);
        }
        return this;
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    public ViewBinder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            retrieveView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            retrieveView(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setVisible(int viewId, boolean visible) {
        View view = retrieveView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Add links into a TextView.
     * @param viewId The id of the TextView to linkify.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder linkify(int viewId) {
        TextView view = retrieveView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    /**
     * Add links into a TextView.
     * @param viewId The id of the TextView to linkify.
     * @param mask
     * @see android.text.util.Linkify#addLinks(TextView text, int mask)
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder addLinks(int viewId, int mask) {
        TextView view = retrieveView(viewId);
        Linkify.addLinks(view, mask);
        return this;
    }

    /** Apply the typeface to the given viewId, and enable subpixel rendering. */
    public ViewBinder setTypeface(int viewId, Typeface typeface) {
        TextView view = retrieveView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /** Apply the typeface to all the given viewIds, and enable subpixel rendering. */
    public ViewBinder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = retrieveView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * Sets the progress of a ProgressBar.
     * @param viewId   The view id.
     * @param progress The progress.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setProgress(int viewId, int progress) {
        ProgressBar view = retrieveView(viewId);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the progress and max of a ProgressBar.
     * @param viewId   The view id.
     * @param progress The progress.
     * @param max      The max value of a ProgressBar.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setProgress(int viewId, int progress, int max) {
        ProgressBar view = retrieveView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     * @param viewId The view id.
     * @param max    The max value of a ProgressBar.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setMax(int viewId, int max) {
        ProgressBar view = retrieveView(viewId);
        view.setMax(max);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     * @param viewId The view id.
     * @param rating The rating.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setRating(int viewId, float rating) {
        RatingBar view = retrieveView(viewId);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     * @param viewId The view id.
     * @param rating The rating.
     * @param max    The range of the RatingBar to 0...max.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setRating(int viewId, float rating, int max) {
        RatingBar view = retrieveView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the on click listener of the view.
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = retrieveView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * Sets the on touch listener of the view.
     * @param viewId   The view id.
     * @param listener The on touch listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = retrieveView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    /**
     * Sets the on long click listener of the view.
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = retrieveView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item click listener of the view
     * @param viewId  The view id.
     * @param listener The item on click listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
        AdapterView view = retrieveView(viewId);
        view.setOnItemClickListener(listener);
        return this;
    }
    /**
     * Sets the listview or gridview's item long click listener of the view
     * @param viewId The view id.
     * @param listener   The item long click listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener) {
        AdapterView view = retrieveView(viewId);
        view.setOnItemLongClickListener(listener);
        return this;
    }
    /**
     * Sets the listview or gridview's item selected click listener of the view
     * @param viewId The view id.
     * @param listener The item selected click listener;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setOnItemSelectedClickListener(int viewId, AdapterView.OnItemSelectedListener listener) {
        AdapterView view = retrieveView(viewId);
        view.setOnItemSelectedListener(listener);
        return this;
    }
    /**
     * Sets the tag of the view.
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setTag(int viewId, Object tag) {
        if(null != tag){
            View view = retrieveView(viewId);
            view.setTag(tag);
        }
        return this;
    }

    /**
     * Sets the tag of the view.
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setTag(int viewId, int key, Object tag) {
        if(null != tag){
            View view = retrieveView(viewId);
            view.setTag(key, tag);
        }
        return this;
    }

    /**
     * Sets the checked status of a checkable.
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setChecked(int viewId, boolean checked) {
        View view = retrieveView(viewId);
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setChecked(checked);
        } else if (view instanceof CheckedTextView) {
            ((CheckedTextView) view).setChecked(checked);
        }

        return this;
    }

    /**
     * Sets the adapter of a adapter view.
     * @param viewId  The view id.
     * @param adapter The adapter;
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewBinder setAdapter(int viewId, Adapter adapter) {
        if(null != adapter){
            AdapterView view = retrieveView(viewId);
            view.setAdapter(adapter);
        }
        return this;
    }

    /** Retrieve the mItemView */
    public View getView() {
        return mItemView;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T retrieveView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }



    private String replaceEmpty(String params){
        return TextUtils.isEmpty(params) ? "" : params;
    }

}
