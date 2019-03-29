package com.out.source.router.plugin.kernel.transfrom;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.Sets;
import com.out.source.router.plugin.kernel.extension.BasePluginExtension;
import com.out.source.router.plugin.kernel.log.MLogger;
import com.out.source.router.plugin.kernel.transfrom.utils.TransformUtils;

import org.gradle.api.Project;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Created by yangjian on 2018/6/25.
 */

public abstract class BaseTransfrom<T extends BasePluginExtension> extends Transform {


    private Project mProject;

    private T mExt;

    public MLogger mLogger = new MLogger(MLogger.Level.INFO);

    private boolean isAppPlugin = true;

    public BaseTransfrom(Project project, T ext, boolean isAppPlugin){

        this.isAppPlugin = isAppPlugin;
        this.mProject = project;
        this.mExt = ext;
        this.mLogger.setLogPrint(mExt.isDebug());
        this.mExt.setWorkString(this.getProject().getRootDir().getAbsolutePath());
        this.mExt.setAppString(this.getProject().getName());
        onLog("workspace is : " + this.mExt.getWorkString());
        onLog("app or lib is : " + this.mExt.getAppString());
    }

    @Override
    public String getName() {
        return getClass().getCanonicalName();
    }


    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        Set<QualifiedContent.Scope> SCOPE_FULL_LIBRARY = Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT);

        if(isAppPlugin){
            return TransformManager.SCOPE_FULL_PROJECT;
        }else{
            return SCOPE_FULL_LIBRARY;
        }
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        if(this.mExt.getProjectLibs() == null || this.mExt.getProjectLibs().size() <= 0){
            for(Project projectlib : getProject().getRootProject().getAllprojects()){
                if(!getExt().isEmpty(projectlib.getName()) &&
                        !projectlib.getName().equals(this.mExt.getAppString()) &&
                        !projectlib.getName().equals(getProject().getRootProject().getName())){
                    onLog("workspace other project is : "  + projectlib.getName());
                    this.mExt.projectLibs(projectlib.getName());
                }
            }
        }else{
            onLog("developer setting the libs is : " + this.mExt.getProjectLibs());
        }
        TransformUtils.transform(this,transformInvocation);
    }

    /**
     *
     * @return
     */
    public boolean isEnablePrepar(){

        return false;
    }

    public abstract byte[] onModifyClass(InputStream inputStream,String enterName) throws IOException;

    /**
     * @param jarFile
     * @return
     */
    public void onPrepareJar(JarFile jarFile){

    }

    /**
     *
     * @param classPath
     */
    public void onPrepareClass(String classPath){

    }

    public void onEndClass(){

    }

    public void onEndAllJar(){

    }

    public void onPrepareDirAndClass(List<DirectoryInput> dirPathList, List<JarInput> jarInputs) throws IOException {

        for(DirectoryInput directoryInput : dirPathList){
            File file = directoryInput.getFile();
            List<String> dirList = new ArrayList<>();
            TransformUtils.injectDir(file.getAbsolutePath(),mExt.getPackgets(),dirList,this,true);
        }
        onEndClass();
        for(JarInput jarInput : jarInputs){
            String jarPath = jarInput.getFile().getAbsolutePath();
            JarFile jarFile = new JarFile(jarPath);
            onPrepareJar(jarFile);
        }

        onEndAllJar();
    }
    /**
     *
     * @param pathFile
     * @return
     */
    public abstract boolean isInjectFile(String pathFile);

    public void onLog(String log){
        mLogger.info("=====class : " +
                getClass().getCanonicalName() +
                " ; log is :" + log + "=====");
    }
    public Project getProject(){

        return mProject;
    }
    public T getExt(){
        return mExt;
    }
}
