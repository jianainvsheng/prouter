package com.source.sdk.prouter;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.source.sdk.annotaion.IActivity;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by yangjian on 2018/5/17.
 */
public class Postcard {
    private String mPath;
    private Bundle mBundle;
    private Bundle optionsCompat;
    private int enterAnim = -1;
    private int exitAnim = -1;
    private NavigationCallback mCallback;

    public Postcard(String path) {
        this.mPath = path;
        this.mBundle = new Bundle();
    }

    public String getPath() {
        return this.mPath;
    }

    public Bundle getBundle() {
        return this.mBundle;
    }

    public NavigationCallback getCallback() {
        return this.mCallback;
    }

    public Postcard withCallback(NavigationCallback callback) {
        this.mCallback = callback;
        return this;
    }

    public void navigation(Activity activity) {
        this.navigation((Activity)activity, -1);
    }

    public void navigation(Activity activity, int requestCode) {
        PRouter.getInstance().navigation(activity, this, requestCode);
    }


    public void navigation(Application application) {
        PRouter.getInstance().navigation(application, this);
    }

    public void navigation(View view) {
        if(view != null) {
            PRouter.getInstance().navigation(view.getContext(), this);
        }
    }

    public Postcard with(Bundle bundle) {
        this.mBundle = bundle;
        return this;
    }

    public Postcard withParameterPraser(ParametersPraserAdapter adapter) {
        if(TextUtils.isEmpty(this.mPath)) {
            throw new IllegalStateException("you should call withParameterPraser after had a mPath");
        } else {
            if(adapter != null) {
                Bundle bundle = adapter.praser(this.mPath);
                this.mPath = adapter.getUrl(this.mPath);
                this.with(bundle);
            }

            return this;
        }
    }

    public Postcard withObject(@Nullable String key, @Nullable Object value) {
        this.mBundle.putString(key, JSONUtil.object2Json(value));
        return this;
    }

    public Postcard withString(@Nullable String key, @Nullable String value) {
        this.mBundle.putString(key, value);
        return this;
    }

    public Postcard withBoolean(@Nullable String key, boolean value) {
        this.mBundle.putBoolean(key, value);
        return this;
    }

    public Postcard withShort(@Nullable String key, short value) {
        this.mBundle.putShort(key, value);
        return this;
    }

    public Postcard withInt(@Nullable String key, int value) {
        this.mBundle.putInt(key, value);
        return this;
    }

    public Postcard withLong(@Nullable String key, long value) {
        this.mBundle.putLong(key, value);
        return this;
    }

    public Postcard withDouble(@Nullable String key, double value) {
        this.mBundle.putDouble(key, value);
        return this;
    }

    public Postcard withByte(@Nullable String key, byte value) {
        this.mBundle.putByte(key, value);
        return this;
    }

    public Postcard withChar(@Nullable String key, char value) {
        this.mBundle.putChar(key, value);
        return this;
    }

    public Postcard withFloat(@Nullable String key, float value) {
        this.mBundle.putFloat(key, value);
        return this;
    }

    public Postcard withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        this.mBundle.putCharSequence(key, value);
        return this;
    }

    public Postcard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        this.mBundle.putParcelable(key, value);
        return this;
    }

    public Postcard withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        this.mBundle.putParcelableArray(key, value);
        return this;
    }

    public Postcard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        this.mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public Postcard withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        this.mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    public Postcard withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        this.mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public Postcard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        this.mBundle.putStringArrayList(key, value);
        return this;
    }

    public Postcard withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        this.mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public Postcard withSerializable(@Nullable String key, @Nullable Serializable value) {
        this.mBundle.putSerializable(key, value);
        return this;
    }

    public Postcard withByteArray(@Nullable String key, @Nullable byte[] value) {
        this.mBundle.putByteArray(key, value);
        return this;
    }

    public Postcard withShortArray(@Nullable String key, @Nullable short[] value) {
        this.mBundle.putShortArray(key, value);
        return this;
    }

    public Postcard withCharArray(@Nullable String key, @Nullable char[] value) {
        this.mBundle.putCharArray(key, value);
        return this;
    }

    public Postcard withFloatArray(@Nullable String key, @Nullable float[] value) {
        this.mBundle.putFloatArray(key, value);
        return this;
    }

    public Postcard withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        this.mBundle.putCharSequenceArray(key, value);
        return this;
    }

    public Postcard withBundle(@Nullable String key, @Nullable Bundle value) {
        this.mBundle.putBundle(key, value);
        return this;
    }

    public Postcard withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    @RequiresApi(16)
    public Postcard withOptionsCompat(ActivityOptionsCompat compat) {
        if(null != compat) {
            this.optionsCompat = compat.toBundle();
        }

        return this;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FlagInt {
    }
}
