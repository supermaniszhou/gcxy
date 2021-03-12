package com.seeyon.apps.ext.gcxym3;

import com.seeyon.ctp.common.AbstractSystemInitializer;

public class Gcxym3PluginInitializer extends AbstractSystemInitializer {

    @Override
    public void initialize() {
        System.out.println("正在启动工程学院M3插件");
    }

    @Override
    public void destroy() {
        System.out.println("销毁工程学院M3插件");
    }
}
