package com.seeyon.apps.ext.batchupdate.controller;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.apps.ext.batchupdate.manager.batchupdateManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.controller.BaseController;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;

public class batchupdateController extends BaseController {

    private batchupdateManager manager = (batchupdateManager) AppContext.getBean("batchupdateManager");

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("apps/ext/batchupdate/index");
    }

    public ModelAndView doBatchUpdatePwd(HttpServletRequest request, HttpServletResponse response) {
        try {
            manager.batchUpdate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}