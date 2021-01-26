package com.seeyon.apps.ext.logRecord.controller;

import com.seeyon.ctp.common.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

public class LogRecordController extends BaseController {

    public ModelAndView toLogRecordPage() {
        ModelAndView mav = new ModelAndView("apps/ext/logRecord/index");
        return mav;
    }

}
