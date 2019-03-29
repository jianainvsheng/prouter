package com.out.source.router.plugin;

import com.android.build.api.transform.Transform;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.TestedExtension;
import com.out.source.router.plugin.kernel.extension.BasePluginExtension;
import com.out.source.router.plugin.kernel.log.MLogger;
import com.out.source.router.plugin.kernel.transfrom.BaseTransfrom;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangjian on 2018/6/25.
 */

public abstract class BasePlugin<T extends BasePluginExtension,B extends BaseTransfrom<T>> implements Plugin<Project> {

    private String mExtensionName = getClass().getCanonicalName();

    private T mExt;

    private MLogger mLogger = new MLogger(MLogger.Level.INFO);
    @Override
    public void apply(Project project) {

        boolean isApp = project.getPlugins().hasPlugin(AppPlugin.class);
        if(isApp){
            AppExtension android = project.getExtensions().getByType(AppExtension.class);
            init(project,android,true);
        }else{
            boolean isLib = project.getPlugins().hasPlugin(LibraryPlugin.class);
            if(isLib){
                onLog("this model is lib");
                LibraryExtension lib = project.getExtensions().getByType(LibraryExtension.class);
                init(project,lib,false);
            }
        }
    }

    private void init(Project project, TestedExtension extension, boolean isApp){
        String extensionName = onCreateExtensionName();
        mExtensionName = (extensionName == null || "".equals(extensionName)) ? mExtensionName : extensionName;
        Class<T> tClass = getExtensionClass();
        if(tClass == null){
            onLog("Extension is null");
            throw new NullPointerException("=====onCreateExtension is null");
        }
        project.getExtensions().create(mExtensionName, tClass);
        mExt = (T) project.getExtensions().findByName(getExtensionName());

        mLogger.setLogPrint(mExt.isDebug());
        onLog("extensionName is : " + mExtensionName);
        onLog("extensionName class is : " + tClass.getCanonicalName());
        onLog("create Extension success");

        Class<B> classTransform = getTransformClass();
        if(classTransform == null){
            onLog("onCreateTransform is null");
            throw new NullPointerException("onCreateTransform is null");
        }
        Class[] param = {Project.class,tClass,boolean.class};
        try {
            Constructor constructor = classTransform.getConstructor(param);
            Object[] parameters = {project,mExt,isApp};
            Transform transform = (Transform) constructor.newInstance(parameters);
            extension.registerTransform(transform);
            onLog("onCreateTransform is success");
        } catch (Exception e) {
            onLog("onCreateTransform is fail");
        }
    }
    public abstract String onCreateExtensionName();


    public String getExtensionName(){

        return mExtensionName;
    }

    private Class<T> getExtensionClass() {

        Type type = getClass().getGenericSuperclass();
        if (type == null) {
            return null;
        }
        if (type.toString().contains("<")
                && type.toString().contains(">")) {
            Type[] tClass = ((ParameterizedType) getClass().
                    getGenericSuperclass()).getActualTypeArguments();
            if (tClass != null
                    && tClass.length >= 1
                    && tClass[0].toString().contains("class")) {
                Class<T> cls = (Class<T>) tClass[0];
                return cls;
            }
        }
        return null;
    }
    private Class<B> getTransformClass() {

        Type type = getClass().getGenericSuperclass();
        if (type == null) {
            return null;
        }
        if (type.toString().contains("<")
                && type.toString().contains(">")) {
            Type[] tClass = ((ParameterizedType) getClass().
                    getGenericSuperclass()).getActualTypeArguments();

            if (tClass != null
                    && tClass.length >= 1
                    && tClass[0].toString().contains("class")) {
                Class<B> cls = (Class<B>) tClass[1];
                return cls;
            }
        }
        return null;
    }

    public void onLog(String log){

        mLogger.info("=====class : " +
                getClass().getCanonicalName() +
                " ; log is :" + log + "=====");
    }
}
