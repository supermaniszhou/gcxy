package com.seeyon.apps.ext.batchupdate;

import com.seeyon.ctp.common.AbstractSystemInitializer;

public class batchupdatePluginInitializer extends AbstractSystemInitializer {

    @Override
    public void initialize() {
        System.out.println("初始化batchupdate");
    }


    @Override
    public void destroy() {
        System.out.println("销毁batchupdate");
    }
}