package com.seeyon.apps.ext.batchupdate;

import com.seeyon.apps.ext.batchupdate.quartz.SyncDataRunnable;
import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.timer.TimerHolder;

public class batchupdatePluginInitializer extends AbstractSystemInitializer {

    @Override
    public void initialize() {
        //一小時執行一次
        final long Mills = 1 * 60 * 60 * 1000;
        SyncDataRunnable dataRunnable = new SyncDataRunnable();
        TimerHolder.newTimer(dataRunnable, Mills);

        System.out.println("初始化batchupdate");
    }


    @Override
    public void destroy() {
        System.out.println("销毁batchupdate");
    }
}