<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%@ include file="/WEB-INF/jsp/common/common.jsp" %>
    <title></title>
    <script type="text/javascript" src="${path}/ajax.do?managerName=logRecordManager"></script>
    <script type="text/javascript" language="javascript">
        //请勿轻易修改这个变量不仅批量关闭窗口用，角色回填也需要回传值
        $().ready(function () {
            var lrManager = new logRecordManager();

            //加载表格
            var o = {};
            var searchobj = $.searchCondition({
                top: 10,
                right: 10,
                //点搜索按钮取值
                searchHandler: function () {
                    var params = searchobj.g.getReturnValue();
                    if (params != null) {
                        o = {};
                        if (params.condition == 'opContent') {
                            o.opContent = params.value;
                        }

                        // if (params.condition == 'starttime') {
                        //     if (params.value[0] != "") {
                        //         o.beginTime = params.value[0];
                        //     }
                        //     if (params.value[1] != "") {
                        //         o.endTime = params.value[1];
                        //     }
                        // }
                    }
                    $('#logRecordTable').ajaxgridLoad(o);
                },
                conditions: [{
                    id: 'opContent',
                    name: 'opContent',
                    type: 'input',
                    text: "内容",//标题
                    value: 'opContent',
                    maxLength: 100
                }]
            });
            //列表
            var grid = $("#logRecordTable").ajaxgrid({
                // gridType: 'autoGrid',
                colModel: [
                    {
                        display: "操作者",
                        name: 'updateUser',
                        width: '12%'
                    }
                    , {
                        display: "操作时间",
                        name: 'updateDate',
                        width: '8%'
                    }
                    , {
                        display: "操作内容",
                        name: 'opContent',
                        width: '46%'
                    }
                    , {
                        display: "操作者的ip",
                        name: 'opUserIp',
                        width: '12%'
                    }
                    , {
                        display: "操作类型",
                        name: 'opType',
                        width: '8%'
                    }
                    , {
                        display: "操作结果",
                        name: 'opResult',
                        width: '8%'
                    }
                ],
                height: 200,
                showTableToggleBtn: true,
                parentId: 'center',
                vChange: true,
                vChangeParam: {
                    overflow: "hidden",
                    autoResize: true
                },
                isHaveIframe: true,
                slideToggleBtn: true,
                managerName: "lrManager",
                managerMethod: "selectAllPage",

            });
            $('#logRecordTable').ajaxgridLoad(o);

        });
    </script>
</head>
<body class="body-pading" leftmargin="0" topmargin="" marginwidth="0" marginheight="0">
<div id='layout' class="comp" comp="type:'layout'">
    <div class="layout_north f0f0f0" id="north">
        <div height="100%" id="webfx-menu-object-1" class="webfx-menu-bar-gray">
            <table border="0" cellspacing="0" cellpadding="0" height="55">
                <tbody>
                <tr height="100%" valign="middle">
                    <td height="55">
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="layout_center over_hidden" id="center">
        <table id="logRecordTable" class="flexme3" style="display: none;"></table>
    </div>
</div>
</body>
</html>
