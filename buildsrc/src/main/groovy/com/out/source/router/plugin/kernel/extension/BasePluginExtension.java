package com.out.source.router.plugin.kernel.extension;
import com.out.source.router.plugin.kernel.log.MLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjian on 2018/6/25.
 */

public class BasePluginExtension {

    private List<String> mPackgets = new ArrayList<>();

    private boolean debug = true;

    private String mWorkString = "";

    private String mAppString = "";

    private MLogger mLogger = new MLogger(MLogger.Level.INFO);

    private List<String> mProjectLibs = new ArrayList<>();

    public List<String> getPackgets() {
        return mPackgets;
    }

    public void packgets(String... strs) {
        if(strs == null){
            return;
        }
        for(String s : strs){
            if(!isEmpty(s)){
                changeSeparator(s);
                this.mPackgets.add(s);
            }
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void debug(boolean debug) {
        this.debug = debug;
        mLogger.setLogPrint(debug);
    }

    public String getWorkString() {
        return mWorkString;
    }

    public void setWorkString(String mWorkString) {
        this.mWorkString = mWorkString;
    }

    public String getAppString() {
        return mAppString;
    }

    public void setAppString(String mAppString) {
        this.mAppString = mAppString;
    }

    public List<String> getProjectLibs() {
        return mProjectLibs;
    }

    public void projectLibs(String... libs) {

        if(libs == null){
            return;
        }
        for(String lib : libs){

            if(!isEmpty(lib)){

                this.mProjectLibs.add(lib);
            }
        }
    }

    public void onLog(String log){
        mLogger.info("=====class : " +
                getClass().getCanonicalName() +
                " ; log is :" + log + "=====");
    }

    private void changeSeparator(String path){

        if(path != null && !"".equals(path)){

            path.replace("/", File.separator);
        }
    }

    public boolean isEmpty(String s){

        return (s == null || "".equals(s));
    }
}
