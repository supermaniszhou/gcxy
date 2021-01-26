package com.seeyon.apps.ext.batchupdate.controller;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.apps.ext.batchupdate.manager.batchupdateManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.controller.BaseController;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class batchupdateController extends BaseController {

    private batchupdateManager manager = (batchupdateManager) AppContext.getBean("batchupdateManager");

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("apps/ext/batchupdate/index");
    }

    public ModelAndView doBatchUpdatePwd(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        try {
            manager.batchUpdate();
            map.put("code", 0);
            map.put("message", "success");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            map.put("code", -1);
            map.put("message", "error");
        }
        JSONObject json = new JSONObject(map);
        String js = json.toString();
        render(response, json.toString());
        return null;
    }

    public void render(HttpServletResponse response, String map) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(map.getBytes("UTF-8").length);
            response.getWriter().write(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}