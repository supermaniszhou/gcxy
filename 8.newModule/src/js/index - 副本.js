var _path = 'http://zhoumobile.v5.cmp';
var searchCondition = {};
var url = "http://192.168.31.23:80/seeyon";
var page = {};
var listCmpView = null;
var _storge_key = document.location.href;
var pageX = {};
pageX.searchCondition = {};//查询条件
var searchConditionID = {};
//是否从底导航打开
var isFromM3NavBar = window.location.href.match('m3from=navbar');

cmp.ready(function () {
    // initPageBack()
    cmp.i18n.init(_path + "/i18n/", "zhoumobile", function () {
        //注册懒加载
        // _registLazy();
        loadData();
    });


    // 点击取消按钮
    $cmp(".cancelBtn").on("tap", function () {
        cmp.href.back();
    });
});


function loadData(params) {

    initSearchHTML();

    cmp.listView("#bgdhWrapper", {   //容器标识（当前是用id的方式来查询的）
        imgCache: true,
        config: {
            isClear: true,//是否重新创建新的listview；false：不重新创建（默认：false）true:重新创建
            imgCache: true,//是否进行列表中图片的缓存true：进行缓存；false：不缓存；默认：未定义
            crumbsID: searchConditionID.searchCrumbsId ? searchCondition.searchCrumbsId : "#bgdhWrapper",//面包屑ID，使用场景，在创建一个listview的时候，进行面包屑切换后，可对应到相应的面包屑数据，当再切换回原来的面包屑后，
            // 不需要重新向后台请求数据，可直接进行渲染，并将滚动位置滚动到，开始的那个面包屑最后一次操作，滚动到的位置,（默认：未定义；如果是没有面包屑/页签的场景，可以不用定义）
            captionType: 0,  //不显示条数，只显示文字
            pageSize: 20,    //一次请求数据显示20条数据
            height: 60,      //手势下拉到60px时进行刷新操作
            params: ['', {input: page.input}],//上拉下拉刷新请求函数需要的关键参数
            onePageMaxNum: 80,//一屏最大数据条数，用于上一页下一页的切换显示,默认：80条
            dataFunc: function (fn, params, options) {  //请求数据的函数
                $s.ExAddressbookResource.bgdhListDate({}, params, {
                    success: function (result) {
                        console.log(options, 'zhou-options');
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

}

function renderData(result, isRefresh) {

    //启动懒加载
    // LazyUtil.startLazy();

    var pendingTPL = $("#list_bgdh_tpl").html();
    var html = cmp.tpl(pendingTPL, result);
    if (isRefresh || isSearch) {//是否刷新操作，刷新操作 直接覆盖数据
        $("#bgdhList").html(html);
        isSearch = false;
    } else {
        var table = $("#bgdhList").html();
        $("#bgdhList").html(table + html);
    }
    cmp.IMG.detect();
}

function initSearchHTML() {

    if (pageX.searchCondition.condition != undefined) {
        _$("#searchHeader").style.display = "none";
        _$("#reSearch").style.display = "block";
        _$("#dataCommonDiv").style.top = "44px";

        _$('#search').removeEventListener("tap", searchFn);

        if (pageX.searchCondition.condition != "createDate") {
            _$("#searchText").style.display = "block";
            _$("#searchDate").style.display = "none";
            _$("#cmp_search_title").innerHTML = pageX.searchCondition.text;
            _$("#searchTextValue").value = pageX.searchCondition.value;
        } else {
            _$("#searchText").style.display = "none";
            _$("#searchDate").style.display = "block";
            _$("#cmp_search_title").innerHTML = pageX.searchCondition.text;
            _$("#searchDateBeg").value = pageX.searchCondition.dateBegin;
            _$("#searchDateEnd").value = pageX.searchCondition.dateEnd;
        }
    } else {
        _$('#search').removeEventListener("tap", searchFn);
        _$('#search').addEventListener("tap", searchFn);
        _$("#searchHeader").style.display = "block";
        _$("#reSearch").style.display = "none";
        _$("#dataCommonDiv").style.top = "0";
    }
}

function searchFn(searchParams) {
    if (typeof (searchParams.condition) == "undefined") {
        searchParams = null;
    }
    //查询关键字
    var serarchObj = [{type: "text", condition: "keyWord", text: cmp.i18n("zhoumobile.h5.searchkey")}];
    cmp.search.init({
        id: "#search",
        model: {
            name: "bgdh",
            id: "3002"
        },
        items: serarchObj,
        parameter: searchParams,
        callback: function (result) { //回调函数：会将输入的搜索条件和结果返回给开发者
            page = {};
            page.input = result.searchKey[0];
            var dataSource = result.searchKey[0];
            var params = [{}, page];
            //查询时listview要加上crumbsId参数
            searchConditionID.searchCrumbsId = "search" + cmp.buildUUID();
            loadData(params);
        }
    });
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



