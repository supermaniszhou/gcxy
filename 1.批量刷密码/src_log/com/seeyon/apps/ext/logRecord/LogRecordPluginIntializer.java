package com.seeyon.apps.ext.logRecord;

import com.seeyon.ctp.common.AbstractSystemInitializer;

public class LogRecordPluginIntializer extends AbstractSystemInitializer {
    @Override
    public void initialize() {
        System.out.println("初始化");
    }

    @Override
    public void destroy() {
        System.out.println("销毁");
    }
}
