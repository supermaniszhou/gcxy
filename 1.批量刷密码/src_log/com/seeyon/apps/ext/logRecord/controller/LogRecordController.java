package com.seeyon.apps.ext.logRecord.controller;

import com.seeyon.ctp.common.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogRecordController extends BaseController {

    public ModelAndView toLogRecordPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("apps/ext/logRecord/index");
        return mav;
    }

}
