package com.seeyon.ctp.rest.resources;

import com.seeyon.apps.ext.gcxym3.manager.Gcxym3Manager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.dao.paginate.Pagination;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.RestInterfaceAnnotation;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("addbook")
@Produces({MediaType.APPLICATION_JSON})
public class ExAddressbookResource extends BaseResource {

    private Gcxym3Manager gcxym3Manager = (Gcxym3Manager) AppContext.getBean("gcxym3Manager");

    @POST
    @Path("xntlListDate")
    @Produces({"application/json"})
    @RestInterfaceAnnotation
    public Response xntlListDate(Map<String, String> params) {
        //分页
        FlipInfo flipInfo = super.getFlipInfo();
        flipInfo.setPage(Integer.parseInt(params.get("pageNo")));
        flipInfo.setSize(Integer.parseInt(params.get("pageSize")));
        flipInfo.setNeedTotal(true);
        User user = AppContext.getCurrentUser();
        Map<String, String> map = null;
        if (null != params.get("keyWord") && !"".equals(params.get("keyWord"))) {
            map = new HashMap<>();
            map.put("field0001", params.get("keyWord"));
        }
        gcxym3Manager.getXntlList(flipInfo, map);

        return Response.ok(flipInfo).build();
    }

    @POST
    @Path("bgdhListDate")
    @Produces({"application/json"})
    @RestInterfaceAnnotation
    public Response bgdhListDate(Map<String, String> params) {
        //分页
        FlipInfo flipInfo = super.getFlipInfo();
        flipInfo.setPage(Integer.parseInt(params.get("pageNo")));
        flipInfo.setSize(Integer.parseInt(params.get("pageSize")));
        flipInfo.setNeedTotal(true);
        User user = AppContext.getCurrentUser();
        Map<String, String> map = null;
        if (null != params.get("keyWord") && !"".equals(params.get("keyWord"))) {
            map = new HashMap<>();
            map.put("field0001", params.get("keyWord"));
        }
        gcxym3Manager.getBgdhList(flipInfo, map);

        return Response.ok(flipInfo).build();
    }
}
