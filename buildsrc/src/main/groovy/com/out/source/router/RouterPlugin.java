package com.out.source.router;

import com.out.source.router.plugin.BasePlugin;
import com.out.source.router.transform.RouterTransform;
import com.out.source.router.transform.extension.RouterExtension;

/**
 * Created by yangjian on 2018/12/13.
 */

public class RouterPlugin extends BasePlugin<RouterExtension,RouterTransform> {

    @Override
    public String onCreateExtensionName() {
        return "routerExt";
    }

}
