package com.out.source.router.transform.extension;

import com.out.source.router.plugin.kernel.extension.BasePluginExtension;

/**
 * Created by yangjian on 2018/12/13.
 */

public class RouterExtension extends BasePluginExtension {


    private String mInjectClass = "com/source/sdk/prouter/PRouter.class";


    public String getInjectClass(){

        return mInjectClass;
    }
}
