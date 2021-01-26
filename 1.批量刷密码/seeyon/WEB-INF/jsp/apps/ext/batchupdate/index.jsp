<%@ page contentType="text/html; charset=utf-8" isELIgnored="false" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<html class="h100b overflow_login" style="background-color: #F6F5F5;overflow:hidden;">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <style type="text/css">
        .margin_top {
            margin-top: 3%;
        }
    </style>
    <script type="text/javascript"
            src="${path}/apps_res/collaboration/js/collaborationSet.js${ctp:resSuffix()}"></script>
    <script type="text/javascript">
        var curlocationPath = '${path}';
    </script>
</head>
<body class="h100b">
<form id="submitform" class="h100b" name="submitform" method="post">
    <TABLE width="100%" height="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="">
        <tr>
            <td align="center" valign="top" height="95%" style="overflow:auto">
                <div class="scrollList" id="scrollList"
                     style="height: 100%;overflow:auto;position: relative;top: 20px;padding-bottom:40px;">
                    <table border="0" width="40%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td>
                                <fieldset width="30%">
                                    <legend>批量刷新密码</legend> <!-- 震荡回复  -->
                                    <table border="0" width="100%" cellpadding="4" cellspacing="6">
                                        <tr>
                                            <td width="60%">
                                                <div>是否执行:</div>
                                            </td>
                                            <td width="40%">
                                                <div>
                                                    <a href="javascript:void(0)" class="common_button common_button_emphasize">蓝色按钮</a>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </fieldset>

                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>

    </table>
</form>
<script type="text/javascript">
    $("#scrollList").css("height", $(document.body).height() - 120);
    $(window).resize(function () {
        $("#scrollList").css("height", $(document.body).height() - 120);
    });
</script>

</body>
</html>