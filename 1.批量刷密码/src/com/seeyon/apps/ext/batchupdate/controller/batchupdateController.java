package com.seeyon.apps.ext.batchupdate.controller;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.apps.ext.batchupdate.manager.batchupdateManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.controller.BaseController;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class batchupdateController extends BaseController {

    private batchupdateManager manager = (batchupdateManager) AppContext.getBean("batchupdateManager");

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        manager.batchUpdate();
        return new ModelAndView("apps/ext/batchupdate/index");
    }
}