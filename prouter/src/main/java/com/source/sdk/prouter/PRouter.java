package com.source.sdk.prouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.source.sdk.annotaion.IActivity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by yangjian on 2018/5/17.
 */

public class PRouter {

    private static final String TAG = PRouter.class.getName();
    private static final PRouter router = new PRouter();
    private static HashMap<String, Class> mClassMap = new HashMap();
    private static HashMap<String, Class> mServiceClassMap = new HashMap();
    private static HashMap<String, Class> mFragmentClassMap = new HashMap();
    private static HashMap<String, Class> mActivityClassMap = new HashMap();

    private PRouter() {
    }

    public static synchronized PRouter getInstance() {
        return router;
    }

    public void init() {
    }

    private static void register(Class className) {
        if(!registerIActivity(className)) {
        }
    }


    private static boolean registerIActivity(Class className) {
        Annotation anno = className.getAnnotation(IActivity.class);
        if(checkAnnotationNotNull(anno)) {
            IActivity activity = (IActivity)anno;
            String innerKey = activity.value();
            String htmlKey = activity.html();
            if(innerKey != null && mActivityClassMap.containsKey(innerKey)) {
                return true;
            } else {
                mActivityClassMap.put(innerKey, className);
                if(mActivityClassMap.containsKey(htmlKey)) {
                    return true;
                } else {
                    if(htmlKey != null && htmlKey.length() > 0) {
                        mActivityClassMap.put(htmlKey, className);
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private static boolean checkAnnotationNotNull(Annotation anno) {
        return anno != null;
    }

    public Class getClass(String path) {
        Class targetClass = (Class)mClassMap.get(path);
        return targetClass;
    }

    private Class getServiceClass(String path) {
        Class targetClass = (Class)mServiceClassMap.get(path);
        return targetClass;
    }

    private Class getFragmentClass(String path) {
        Class targetClass = (Class)mFragmentClassMap.get(path);
        return targetClass;
    }

    private Class getActivityClass(String path) {
        Class targetClass = (Class)mActivityClassMap.get(path);
        return targetClass;
    }




    void navigation(Activity activity, Postcard postcard, int requestCode) {


        System.out.println("===========mActivityClassMap : ====" + (mActivityClassMap == null ? null : mActivityClassMap.size()));
        if(activity == null) {
            throw new RuntimeException("Context is null");
        } else if(null == postcard) {
            throw new RuntimeException(TAG + " :: No postcard!");
        } else {
            Class targetClass = this.getActivityClass(postcard.getPath());
            NavigationCallback callback = postcard.getCallback();
            if(targetClass == null) {
                if(callback != null) {
                    callback.onLost(postcard);
                }
                System.out.println("===========target Activity : ====" + postcard.getPath()+ "  not found");
                Log.e(TAG, "target Activity: " + postcard.getPath() + "  not found");
            } else {
                if(callback != null) {
                    callback.onFound(postcard);
                }

                Intent intent = new Intent(activity, targetClass);
                Bundle bundle = postcard.getBundle();
                if(bundle != null) {
                    intent.putExtras(postcard.getBundle());
                }

                System.out.println("===========start Activity : ====" + postcard.getPath());
                activity.startActivityForResult(intent, requestCode);
                if(callback != null) {
                    callback.onArrival(postcard);
                }

            }
        }
    }


    void navigation(Context context, Postcard postcard) {
        if(context == null) {
            throw new RuntimeException("context is null");
        } else if(null == postcard) {
            throw new RuntimeException(TAG + " :: No postcard!");
        } else {
            Class targetClass = this.getActivityClass(postcard.getPath());
            NavigationCallback callback = postcard.getCallback();
            if(targetClass == null) {
                if(callback != null) {
                    callback.onLost(postcard);
                }

                Log.e(TAG, "target Activity: " + postcard.getPath() + "  not found");
            } else {
                if(callback != null) {
                    callback.onFound(postcard);
                }

                Intent intent = new Intent(context, targetClass);
                Bundle bundle = postcard.getBundle();
                if(bundle != null) {
                    intent.putExtras(postcard.getBundle());
                }

                context.startActivity(intent);
                if(callback != null) {
                    callback.onArrival(postcard);
                }

            }
        }
    }

    public Postcard build(Uri uri) {
        if(uri != null && uri instanceof Uri && !TextUtils.isEmpty(uri.toString())) {
            Set<String> argsName = uri.getQueryParameterNames();
            Bundle bundle = null;
            if(!TextUtils.isEmpty(uri.getQuery())) {
                bundle = new Bundle();
                Iterator var4 = argsName.iterator();

                while(var4.hasNext()) {
                    String key = (String)var4.next();
                    String value = uri.getQueryParameter(key);
                    bundle.putString(key, value);
                }
            }

            Postcard postcard = new Postcard(uri.getPath());
            if(bundle != null) {
                postcard.with(bundle);
            }

            return postcard;
        } else {
            throw new IllegalArgumentException("Parameter is invalid! uri = " + uri);
        }
    }

    public Postcard build(String path) {
        if(TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Parameter is invalid! path empty");
        } else {
            return new Postcard(path);
        }
    }

    public void navigation(Activity activity, String path, int requestCode) {
        this.navigation(activity, new Postcard(path), requestCode);
    }

    public void navigation(Activity activity, String path) {
        this.navigation((Activity)activity, (Postcard)(new Postcard(path)), -1);
    }

    public void navigation(Application application, String path) {
        this.navigation((Context)application, (Postcard)(new Postcard(path)));
    }

    public void navigation(View view, String path) {
        this.navigation(view.getContext(), new Postcard(path));
    }
}
