package com.seeyon.apps.ext.batchupdate.controller;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.ctp.common.controller.BaseController;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class batchupdateController extends BaseController {

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*JSP file path:ApacheJetspeed\webapps\seeyon\WEB-INF\jsp\apps\ext\batchupdate\batchupdate.jsp*/
        return new ModelAndView("apps/ext/batchupdate/index");
    }
}