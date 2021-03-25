package com.seeyon.apps.ext.batchupdate.quartz;

import com.seeyon.apps.ext.batchupdate.manager.batchupdateManager;
import com.seeyon.ctp.common.AppContext;

import java.security.NoSuchAlgorithmException;

public class SyncDataRunnable implements Runnable {
    private batchupdateManager manager = (batchupdateManager) AppContext.getBean("batchupdateManager");


    @Override
    public void run() {

        try {
            System.out.println("start update pwd =================================");
            //先把身份证信息从中间库中抽取到OA的mid_user表中
            manager.extractData();
            //执行刷库操作
            manager.batchUpdate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
