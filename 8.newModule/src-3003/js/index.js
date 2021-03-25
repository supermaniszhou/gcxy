var _path = 'http://zhoumobile.v5.cmp';
var searchCondition = {};
var url = "http://192.168.31.23:80/seeyon";
var page = {};
var pageX = {};
pageX.searchCondition = {};//查询条件
var searchConditionID = {};
//是否从底导航打开
var isFromM3NavBar = window.location.href.match('m3from=navbar');
var isSearch = true;
cmp.require(['ui-nav'], function () {

    searchEvent();
    // 点击取消按钮
    $cmp(".cancelBtn").on("tap", function () {
        cmp.href.back();
    });
    loadData(page);
});

/**
 * 搜索绑定事件
 */
function searchEvent() {
    cmp.event.click(_$('#searchHeader'), function () {
        if (_$("#search_container")) {
            return;
        } else {
            pageSearch();
        }
    });
}

/**
 * 页面搜索条件初始化
 */
function pageSearch() {
    var serarchObj = [{type: "text", condition: "关键字", text: cmp.i18n("xntl.h5.keyword")}];
    cmp.groupSearch.init({
        id: "#searchHeader",
        model: {
            name: "xntl",
            id: "3003"
        },
        items: serarchObj,
        H5Header: false,
        openSearchResult: false,//是否打开存放搜索结果的容器
        callback: function (result) {//回调函数：会将输入的搜索条件和结果返回给开发者
            var arr = result.result;
            page = {};
            for (let i = 0; i < arr.length; i++) {
                page['keyWord'] = arr[i].value;
            }
            // page.input = result.searchKey[0];
            // var dataSource = result.searchKey[0];
            // var params = [{}, page];
            // //查询时listview要加上crumbsId参数
            // searchConditionID.searchCrumbsId = "search" + cmp.buildUUID();
            window.setTimeout(function () {
                loadData(page);
            }, 200);
        }
    });
}


function loadData(params) {
    cmp.listView("#xntlWrapper", {   //容器标识（当前是用id的方式来查询的）
        imgCache: true,
        config: {
            isClear: true,//是否重新创建新的listview；false：不重新创建（默认：false）true:重新创建
            imgCache: true,//是否进行列表中图片的缓存true：进行缓存；false：不缓存；默认：未定义
            crumbsID: searchConditionID.searchCrumbsId ? searchCondition.searchCrumbsId : "#xntlWrapper",//面包屑ID，使用场景，在创建一个listview的时候，进行面包屑切换后，可对应到相应的面包屑数据，当再切换回原来的面包屑后，
            // 不需要重新向后台请求数据，可直接进行渲染，并将滚动位置滚动到，开始的那个面包屑最后一次操作，滚动到的位置,（默认：未定义；如果是没有面包屑/页签的场景，可以不用定义）
            captionType: 0,  //不显示条数，只显示文字
            pageSize: 20,    //一次请求数据显示20条数据
            height: 60,      //手势下拉到60px时进行刷新操作
            params: ['', params],//上拉下拉刷新请求函数需要的关键参数
            onePageMaxNum: 80,//一屏最大数据条数，用于上一页下一页的切换显示,默认：80条
            dataFunc: function (fn, params, options) {  //请求数据的函数
                $s.ExAddressbookResource.xntlListDate({}, params, {
                    success: function (result) {
                        if (options.success) {
                            options.success(result);
                        }

                    },
                    error: function (result) {
                        options.error();
                    }
                });
            },
            renderFunc: renderData
        },
        down: {
            contentdown: "下拉可以刷新",
            contentover: "释放立即刷新",
            contentrefresh: "正在刷新...",
            contentprepage: "上一页"
        },
        up: {
            contentdown: "上拉显示更多",
            contentrefresh: "正在加载...",
            contentnomore: "没有更多数据了",
            contentnextpage: "下一页"

        }
    });
    prevPage();

}

function renderData(result, isRefresh) {
    var pendingTPL = $("#list_xntl_tpl").html();
    var html = cmp.tpl(pendingTPL, result);
    if (isRefresh) {//是否刷新操作，刷新操作 直接覆盖数据
        $("#xntlList").html(html);
    } else {
        var table = $("#bgdhList").html();
        $("#xntlList").html(table + html);
    }
    document.getElementById("cmp-control").style.height = window.innerHeight + "px";
    cmp.listView("#xntlWrapper").refresh();
    cmp.IMG.detect();
}

function prevPage() {
    cmp.backbutton();
    cmp.backbutton.push(backFrom);
}

function backFrom() {
    if (_getQueryString("backURL") == "weixin") {
        cmp.href.closePage();
        return;
    }
    if (page.condition != undefined) {
        page = {};
        loadData(page);
    } else {
        if (isFromM3NavBar) {
            cmp.closeM3App();
            return;
        } else {
            cmp.href.back();
        }
    }
}

function _getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return decodeURIComponent(r[2]);
    return null; //返回参数值
}

/**
 * 简化选择器
 * @param selector 选择器
 * @param queryAll 是否选择全部
 * @param 父节点
 * @returns
 */
function _$(selector, queryAll, pEl) {
    var p = pEl ? pEl : document;
    if (queryAll) {
        return p.querySelectorAll(selector);
    } else {
        return p.querySelector(selector);
    }
}



