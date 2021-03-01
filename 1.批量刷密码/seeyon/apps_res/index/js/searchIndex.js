"use strict";

/**
 * 全文检索-首页
 * @author huy
 * 总思路：
 * jsp中会生成的allModules数据，通过ajax的searchModule方法请求回“筛选条件”和右侧的“关联模块”
 * 拼装出所有模块的ajax方法名
 * 渲染“筛选条件”
 * 第一次进来，当前处于“全部”页签，通过对应的ajax方法名，请求左侧的N个模块
 * 根据relatedModules数据，渲染右侧的模块
 * 渲染模块的逻辑：从缓存中取tpl模板和js，如果没有就通过ajax请求对应模块的html和js文件，然后渲染，最后执行afterInit（如果有的话）
 */
    //zhou:不需要的tab页签，暂使用数组存储
var noNeedTo=['application','collaboration','schedule','ThirdPartyIntegration','vreport'];

$(function() {
    // 页签
    // 拼装页签数据
    var tabsData = {
        size: "S",
        styleType: 1,
        items: []
    };

    for (var i = 0; i < allModules.length; i++) {
        var item = {};
        item.id = "searchTab_" + allModules[i].id;
        item.label = $.i18n(allModules[i].name);
        if (currentModule === "all") {
            item.current = i === 0 ? true : false;
        } else {
            item.current = allModules[i].id === currentModule ? true : false;
        }
        var index = i;
        item.clickFun = function(index) {
            changeTabs(allModules[index].id);
        };
        //zhou
        var arrStr=noNeedTo.join(",");
        if(arrStr.indexOf(allModules[i].id)==-1){
            tabsData.items.push(item);
        }
    }

    // 渲染：页签
    var renderTabsObj = new CtpUiTabs(document.getElementById("headerTabs"), tabsData);

    // 渲染：搜索结果
    var searchResultObj = searchAction("init");

    // 给自动完成的div绑定一下事件
    $("body").on("click", function(e) {
        if (e.target.id !== "searchWord" && $("#autoCompletion:visible")) {
            $("#autoCompletion").hide();
        }
    });

    // 输入框监听回车键
    var searchWordIpt = document.getElementById("searchWord");
    searchWordIpt.onkeyup = function(_event) {
        var _event = _event ? _event : window.event;
        autoCompletionFun.action(this);
    }

});

// 点击了搜索按钮后的动作
var searchAction = function(type) {
    searchKeyword = document.getElementById("searchWord").value.trim();
    // if (searchKeyword === "") {
    //     searchResultCache.alert.searchWord && searchResultCache.alert.searchWord.close();
    //     delete searchResultCache.alert.searchWord;
    //     searchResultCache.alert.searchWord = $.alert($.i18n('index.input.error'));
    //     return;
    // }
    if (document.getElementById("searchApp") && searchResult.prototype.getInputsVal("searchApp") === "") {
        searchResultCache.alert.searchApp && searchResultCache.alert.searchApp.close();
        searchResultCache.alert.searchApp;
        searchResultCache.alert.searchApp = $.alert($.i18n('index.tips.searchApp.label'));
        return;
    }
    var customDateDom = $("#leftModule-filterList").find("#customDate");
    if ($("#searchTime").find("input:checked").val() === "custom" && (customDateDom.val() === "" || customDateDom.siblings(".end_date_ctl").val() === "")) {
        searchResultCache.alert.customDate && searchResultCache.alert.customDate.close();
        searchResultCache.alert.customDate;
        searchResultCache.alert.customDate = $.alert($.i18n('common.login.log.check.7') + "," + $.i18n('common.login.log.check.8'));
        return;
    }
    if (searchResultCache.finishedModuleNum < searchResultCache.moduleTotalNum || searchResultCache.loading && searchResultCache.loading.status === "showed") {
        return;
    }
    searchResultCache.alert = {};
    var searchResultObj4SearchAction = new searchResult(type);
}

// 切换页签
var changeTabs = function(id) {
    $("#headerTabs").find("li.current").removeClass("current");
    $("#searchTab_" + id).addClass("current");
    if (searchResultCache.currentTab !== id) {
        searchResultCache.currentTab = id;
        var searchResultObj4ChangeTabs = searchAction("changeTabs");
    }
}

// 显示/隐藏搜索条件区域
var toggleFilterContent = function() {
    SeeUtils.toggleClass(document.querySelector(".topContent"), "showedFilterList");
}

// 定义模块事件
var searchModuleHandler = {};
// 缓存，用于存放一些杂七杂八的内容
var searchResultCache = {
    moduleTplCache: {}, //按模块名存放tpl模块
    searchModuleHandlerCache: {}, //按模块名存放flag
    currentTab: currentModule,
    currentPage: "1",
    finishedModuleNum: 0,
    moduleTotalNum: 0,
    from: "init",
    nInOne4All: ["application,collaboration,form,edoc,doc,video"],
    alert: {}
};

// 渲染：搜索结果
// from：init（页面初始化），changeTabs（切换页签），searchButton（搜索按钮），filterButton（筛选条件下的确定按钮），pager（翻页）
var searchResult = function(from) {
    searchResultCache.from = from;
    if (from !== "pager") {
        searchResultCache.currentPage = "1";
    }
    searchResultCache.loading = getCtpTop().$.progressBar({
        styleType: "3",
        target: "html",
        fixed: true
    });
    searchResultCache.finishedModuleNum = 0;
    searchResultCache.moduleTotalNum = 0;
    autoCompletionFun.currentIndex = -1;
    // 清空之前的旧数据
    var leftModuleSectionDom = document.querySelectorAll(".leftModuleSection");
    for (var i = 0; i < leftModuleSectionDom.length; i++) {
        leftModuleSectionDom[i].innerHTML = "";
    }
    var rightModuleSectionDom = document.querySelectorAll(".rightModuleSection");
    for (var i = 0; i < rightModuleSectionDom.length; i++) {
        rightModuleSectionDom[i].innerHTML = "";
    }
    if (from === "filterButton") {
        SeeUtils.removeClass(document.getElementById("topContent"), "showedFilterList");
    }
    document.getElementById("pagerArea").innerHTML = "";
    // 获取当前页签下的“筛选条件”和右侧的“关联模块”
    this.getFilterAndrelatedModules(searchResultCache.currentTab, from);
}
// 通过ajax接口获取当前页签下的“筛选条件”和右侧的“关联模块”
searchResult.prototype.getFilterAndrelatedModules = function(moduleName, from) {
    var _this = this;
    var parameter = {
        searchModule: moduleName
    };
    callBackendMethod("indexAjaxManager", "searchModule", parameter, {
        success: function(result) {
            if (result == '__LOGOUT') {
                offlineFun();
                return;
            }
            // console.log(result);
            // 获取各模块的ajax方法名
            _this.relatedModules = result.relatedModules;
            _this.getAjaxMethodName4AllModules(result.allApp);
            if (result.showCondition) { // 有筛选条件：渲染筛选条件，渲染完成后会在afterInit4FilterData中调用渲染左右两侧模块的方法
                _this.initFilterData(result, from, moduleName);
                document.getElementById("toggleFilter").style.visibility = "visible";
                SeeUtils.removeClass(document.getElementById("topContent"), "showedFilterList");
            } else { // 无筛选条件：清空筛选条件区域，然后渲染左右两侧模块
                document.getElementById("leftModule-filterList").innerHTML = "";
                document.getElementById("toggleFilter").style.visibility = "hidden";
                _this.renderLeftAndRightModule(result, from, moduleName);
            }
        }
    });
}
// 渲染左右两侧的模块，在筛选条件渲染完成后执行
searchResult.prototype.renderLeftAndRightModule = function(result, from, moduleName) {
    var parameter = {};
    this.renderLeftModules(searchResultCache.currentTab, "leftModule", from, parameter);
    // 右侧模块有两种渲染机制，一种使用searchModule返回的数据，另一种使用对应模块返回的数据，此处为第一种
    this.renderRightModules4SearchModuleData(result);
}
// 梳理各模块的ajax方法名和tpl文件名
searchResult.prototype.getAjaxMethodName4AllModules = function(allApp) {
    var allAppList = [];
    for (var i = 0; i < allApp.length; i++) {
        if (allApp[i] && allApp[i].id) {
            allAppList.push(allApp[i].id);
        }
    }
    for (var i = 0; i < allModules.length; i++) {
        if (allModules[i] && allModules[i].id) {
            allAppList.push(allModules[i].id);
        }
    }
    for (var i = 0; i < allAppList.length; i++) {
        for (var j = i + 1; j < allAppList.length; j++) {
            if (allAppList[i] == allAppList[j]) { //第一个等同于第二个，splice方法删除第二个
                allAppList.splice(j, 1);
                j--;
            }
        }
    }
    this.ajaxMethod = {};
    for (var i = 0; i < allAppList.length; i++) {
        var app = allAppList[i];
        this.ajaxMethod[app] = {
            methodName: ""
        }
        // ajax方法名
        if (app !== "all") {
            var methodName;
            // 通讯录、问答比较特殊，其它的统一使用searchApp接口
            if (app === "addressbook") {
                methodName = "searchAddressbook";
            } else if (app === "xiaoz") {
                methodName = "searchXiaoz";
            } else {
                methodName = "searchApp";
            }
            this.ajaxMethod[app].methodName = methodName;
        }
        // tpl文件名
        // 处理多个模块对一个tpl的情况，如：“新闻”“公告”“讨论”“调查”共用“文化建设”，“领导行程”“会议任务”“事件“计划”共用“时间安排”
        if (app === "news" || app === "bulletin" || app === "bbs" || app === "inquiry") {
            this.ajaxMethod[app].tplName = "culture";
        } else if (app === "leaderagenda" || app === "meeting" || app === "taskManage" || app === "calendar" || app === "plan") {
            this.ajaxMethod[app].tplName = "schedule";
        } else {
            this.ajaxMethod[app].tplName = app;
        }
    }
    for (var i = 0; i < this.relatedModules.length; i++) {
        var app = this.relatedModules[i].id;
        this.ajaxMethod[app] = {
            methodName: "",
            tplName: app
        }
    }
    // “全部”页签下，“应用，协同，表单，公文，文档，视频”合并成一个请求
    this.ajaxMethod['searchMultiApp'] = {
        methodName: "searchMultiApp",
        tplName: "searchMultiApp"
    }
    // “应用”模块统一使用searchMultiApp，因为后台数据不满足原本的searchApp
    this.ajaxMethod['application'] = {
        methodName: "searchMultiApp",
        tplName: "application"
    }
    // N合一：“除了xiaoz，cap4business，portal，template，vreport，video”都属于左侧的“其它”,展现于“XX_相关”中。
    this.ajaxMethod['related'] = {
        methodName: "searchApp",
        tplName: "related"
    }
}
// 拼装筛选条件的数据，并渲染
searchResult.prototype.initFilterData = function(result, from, moduleName) {
    var range = result.allApp;
    // 内容范围是动态获取的，开始拼装
    var filterRange = [];
    var searchAppData = {
        name: $.i18n('index.search.searchRange'),
        id: "searchApp",
        items: []
    };
    console.log(range);
    //zhou
    //zhou:不需要的tab页签，暂使用数组存储
    var noNeedToStr=noNeedTo.join(",");
    for (var i = 0; i < range.length; i++) {
        var item = JSON.parse(JSON.stringify(range[i]));
        item.type = "checkbox";
        item.checked = true;
        if(noNeedToStr.indexOf(range[i].id)==-1){
            searchAppData.items.push(item);
        }
    }
    if (range.length > 0) {
        filterRange.push(searchAppData);
    }
    // “搜索域、搜索时间、发起人”是相对固定的，开始拼装
    // 搜索域
    var searchAreaData = {
        name: $.i18n('index.search.searchField'),
        id: "searchArea",
        items: [{
            name: $.i18n('common.pending.all'),
            id: "all",
            type: "radio",
            checked: true
        }, {
            name: $.i18n('common.subject.label'),
            id: "title",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('common.attachment.label'),
            id: "accessory",
            type: "radio",
            checked: false
        }]
    };
    // 文档、第三方待办、文化建设、时间安排不显示搜索域
    if (moduleName !== "doc" && moduleName !== "ThirdPartyIntegration" && moduleName !== "schedule" && moduleName !== "culture") {
        filterRange.push(searchAreaData);
    }
    // 搜索时间
    var searchTimeData = {
        name: $.i18n('index.search.timesearch'),
        id: "searchTime",
        items: [{
            name: $.i18n('common.pending.all'),
            id: "all",
            type: "radio",
            checked: true
        }, {
            name: $.i18n('index.search.time.oneweek'),
            id: "oneweek",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('index.search.time.onemonth'),
            id: "onemonth",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('index.search.time.threemonth'),
            id: "threemonth",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('index.search.time.halfyear'),
            id: "halfyear",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('index.search.time.oneyear'),
            id: "oneyear",
            type: "radio",
            checked: false
        }, {
            name: $.i18n('common.custom.label'),
            id: "custom",
            type: "dateRange",
            checked: false,
            disabled: true
        }]
    };
    filterRange.push(searchTimeData);
    // 发起人
    var authorData = {
        name: $.i18n('common.sender.label'),
        id: "author",
        items: [{
            name: $.i18n('common.sender.label'),
            id: "author",
            type: "input"
        }]
    };
    filterRange.push(authorData);
    this.filterRange = filterRange;
    if (from === "filterButton" || from === "pager") { // 点击搜索按钮后，不需要再渲染筛选条件，直接渲染左右侧的即可
        this.renderLeftAndRightModule(result, from, moduleName);
    } else { //init、changeTabs、searchButton时，需要渲染筛选条件
        this.renderFilterData(result, from, moduleName);
    }
}
// 渲染筛选条件
searchResult.prototype.renderFilterData = function(result, from, moduleName) {
    var _this = this;
    var filterListTpl = document.getElementById("tpl-filterList").innerHTML;
    laytpl(filterListTpl).render(this.filterRange, function(_htmlTpl) {
        document.getElementById("leftModule-filterList").innerHTML = _htmlTpl;
        filterListTpl = null;
        _htmlTpl = null;
        _this.afterInit4FilterData(result, from, moduleName);
    });
}
// 筛选条件的afterInit方法
searchResult.prototype.afterInit4FilterData = function(result, from, moduleName) {
    $("#customDate").compThis();
    //处理“全部”按钮联动效果
    $("#leftModule-filterList").find(".row").each(function(index) {
        $(this).find(".all").on("change", function() {
            var isChecked = $(this).is(':checked');
            $(this).parent(".ctpUiCheckbox").siblings(".ctpUiCheckbox").find("input").each(function() {
                $(this).prop("checked", isChecked);
            })
        });
    });
    $("#leftModule-filterList").find(".row").each(function(index) {
        var rowDom = $(this);
        var singleChebkox = $(this).find(".single");
        var allCheckboxNum = singleChebkox.length;
        $(this).find(".single").each(function() {
            $(this).on("change", function() {
                var checkedNum = rowDom.find(".single:checked").length;
                if (checkedNum === allCheckboxNum) {
                    rowDom.find(".all").prop("checked", true);
                } else {
                    rowDom.find(".all").prop("checked", false);
                }
            });
        });
    });
    // 筛选条件渲染完后，时机已成熟，可以渲染左右模块的数据，因为请求模块数据时需要从筛选条件中取值
    this.renderLeftAndRightModule(result, from, moduleName);
}
// 渲染左侧区域的模块：“我关注的报表”“最新新闻”“最新发帖”“大家都在搜”，跟searchModule走
searchResult.prototype.renderRightModules4SearchModuleData = function(result) {
    var moduleList = ["hotWord", "latestNews", "latestBbs", "attentionReport"];
    for (var i = 0; i < moduleList.length; i++) {
        if (result[moduleList[i]] && result[moduleList[i]].length > 0) {
            this.getTplAndRenderTpl(moduleList[i], result[moduleList[i]], "rightModule");
        }
    }
}
// 渲染右侧区域的模块：“同部门的人”跟左侧的走，有结果才渲染
searchResult.prototype.renderRightModules4LeftModulesData = function(result) {
    if (result.deptMembers && result.deptMembers.length > 0) {
        // searchModule中未返回deptMembers，单独处理
        this.ajaxMethod['deptMembers'] = {
            methodName: "",
            tplName: "deptMembers"
        }
        this.getTplAndRenderTpl("deptMembers", result.deptMembers, "rightModule");
    }
}
// 渲染左侧区域的模块
searchResult.prototype.renderLeftModules = function(moduleName, position, from, parameter) {
    var modulesList;
    modulesList = this.getInputsVal("searchApp");
    modulesList = modulesList === "" ? [moduleName] : modulesList.split(","); //某些页签下没有搜索域
    this.renderLeftModuleList(modulesList, position, from, parameter);
}
// 根据modulesList的数据，特殊处理一下，然后进行渲染
searchResult.prototype.renderLeftModuleList = function(modulesList, position, from, parameter) {
    // 处理多个模块对一个tpl的情况，如：“新闻”“公告”“讨论”“调查”共用“文化建设”，“领导行程”“会议任务”“事件“计划”共用“时间安排”
    var searchAppData = this.getSearchAppValue(modulesList, from);
    if (searchAppData.related && searchAppData.related !== "all" && searchAppData.related.indexOf(",") > 0) {
        searchAppData.related = searchAppData.related.replace("video,","");  //后端"视频"模块有报错，暂时没人改，前端处理一下。xiaoz，cap4business，portal，template，vreport，video
    }
    // 删除空数据，因为getModule4NInOne取交集时有时会返回多余的""
    for (var item in searchAppData) {
        if (searchAppData[item][0] === "") {
            delete searchAppData[item];
        }
    }
    if (searchResultCache.currentTab === "all" && from === "pager" && searchResultCache.currentPage !== "1") {
        var relatedSearchAppData = this.getInputsVal("searchApp");
        relatedSearchAppData = relatedSearchAppData.replace("video,","");  //后端"视频"模块有报错，暂时没人改，前端处理一下。
        if (relatedSearchAppData.indexOf("all,") > -1) { // 为all时就不需要传后面那一大串了
            searchAppData = {
                related: "all"
            }
        } else {
            searchAppData = {
                related: [relatedSearchAppData]
            };
        }
    }
    for (var module in searchAppData) {
        searchResultCache.moduleTotalNum++; //用最原始的方法计数（为了ie8）
    }
    for (var module in searchAppData) {
        var parameter = this.getFilterCondition4Dom(module, from);
        parameter.searchApp = module === "application" ? "portal,template,vreport" : searchAppData[module].toString(); //"应用"模块的searchApp为portal,template,vreport
        parameter.fromModule = module; //加点料，方便排查问题
        this.getModulesData(module, position, from, parameter);
    }
}
// 获取searchApp的数据
searchResult.prototype.getSearchAppValue = function(modulesList, from) {
    var modulesObj = {};
    // 取筛选条件区域选中的值，并组合一波，以应用多个模块共一个tpl的情况，此种情况下它们共用一个请求即可
    for (var i = 0; i < modulesList.length; i++) {
        var module = modulesList[i];
        var tplName = this.ajaxMethod[module].tplName;
        if (modulesObj[tplName] === undefined) {
            modulesObj[tplName] = [];
        }
        modulesObj[tplName].push(module);
    }
    if (modulesObj.all) {
        delete modulesObj.all;
    }
    // 对“全部”页签做一些特殊处理
    if (searchResultCache.currentTab === "all") {
        if (from === "init" || from === "changeTabs" || from === "searchButton") { // from为init、changeTabs、searchButton时，“相关”模块的searchApp为all
            modulesObj.related = "all";
        } else { // from为filterButton、pager时，“相关”模块的searchApp为筛选条件区域中选中的值
            var searchAppVal = this.getInputsVal("searchApp");
            var trashList = ["culture", "schedule"]; // “相关”模块中不搜“文化建设”“时间安排”
            for (var i = 0; i < trashList.length; i++) {
                searchAppVal = this.delOne(trashList[i], searchAppVal);
            }
            if (searchAppVal.indexOf("all,") > -1) { // 为all时就不需要传后面那一大串了
                modulesObj.related = "all";
            } else { // 获取选中的模块
                searchAppVal = searchAppVal.split(",");
                var trashList = ["xiaoz", "addressbook", "application"]; // “相关”模块中不搜“问答”“通讯录”“应用”
                for (var i = 0; i < trashList.length; i++) {
                    searchAppVal = this.delOne(trashList[i], searchAppVal);
                }
                searchAppVal = searchAppVal.toString();
                if (searchAppVal !== "") {
                    modulesObj.related = searchAppVal;
                }
            }
        }
        // “全部”页签下不渲染“文化建设”“时间安排”“第三方待办”
        if (modulesObj.culture) {
            delete modulesObj.culture;
        }
        if (modulesObj.schedule) {
            delete modulesObj.schedule;
        }
        if (modulesObj.ThirdPartyIntegration) {
            delete modulesObj.ThirdPartyIntegration;
        }
        // “应用，协同，表单，公文，文档，视频”合并成一条请求，不分页。
        // if (from === "init" || from === "changeTabs" || from === "searchButton" || from === "pager" && searchResultCache.currentPage === "1") {
        delete modulesObj.application;
        delete modulesObj.collaboration;
        delete modulesObj.form;
        delete modulesObj.edoc;
        delete modulesObj.doc;
        delete modulesObj.video;
        modulesObj.searchMultiApp = this.getModule4NInOne();
        // }
    }
    return modulesObj;
}
// 将N合1请求中的模块列表和筛选条件中选中的模块进行对比，取交集并返回
searchResult.prototype.getModule4NInOne = function() {
    // portal,template,vreport
    var selectModuleList = this.getInputsVal("searchApp").split(",");
    var nInOne4AllList = searchResultCache.nInOne4All[0].split(",");
    var newList = "";
    for (var i = 0; i < selectModuleList.length; i++) {
        for (var j = 0; j < nInOne4AllList.length; j++) {
            if (selectModuleList[i] === nInOne4AllList[j]) {
                if (nInOne4AllList[j] === "application") {
                    newList += "portal,template,vreport,";
                } else {
                    newList += nInOne4AllList[j].toString() + ",";
                }
            } else {
                continue;
            }
        }
    }
    return [newList.substring(0,newList.length-1)];
}
// delOne：从数组中删除某个值
searchResult.prototype.delOne = function(str, arr) {
    var index = arr.indexOf(str);
    if (index > -1) {
        arr.splice(index, 1);
        return arr;
    } else {
        return arr;
    }
}
// 从dom中获取除searchApp之外的筛选条件
searchResult.prototype.getFilterCondition4Dom = function(moduleName, from) {
    var conditionObj = {};
    // 处理“搜索时间”的值
    var getSearchTimeVal = function(value) {
        var searchTime, startTime, endTime;
        if (v === "custom") {
            searchTime = "custom";
            var customDateDom = $("#leftModule-filterList").find("#customDate");
            startTime = customDateDom.val();
            endTime = customDateDom.siblings(".end_date_ctl").val();
        } else {
            searchTime = v;
            startTime = "";
            endTime = "";
        }
        return {
            "searchTime": searchTime,
            "startTime": startTime,
            "endTime": endTime
        }
    }
    var rowDoms = $("#leftModule-filterList").find(".row");
    for (var i = 0; i < rowDoms.length; i++) {
        var id = rowDoms[i].id;
        if (id === "searchApp") {
            continue;
        }
        var checkbox4All = rowDoms[i].querySelector(".all");
        if (checkbox4All && checkbox4All.checked) {
            if (searchResultCache.currentTab === "all") {
                conditionObj[id] = "all";
            } else {
                conditionObj[id] = this.getInputsVal(id).replace("all,", "");
            }
        } else if (id === "author") {
            conditionObj[id] = rowDoms[i].querySelector("input").value;
        } else {
            var v = this.getInputsVal(id);
            if (id === "searchTime") {
                // 搜索时间需要特殊处理一下
                var searchTimeVal = getSearchTimeVal(v);
                for (var item in searchTimeVal) {
                    conditionObj[item] = searchTimeVal[item];
                }
            } else {
                conditionObj[id] = v;
            }
        }
    }
    return conditionObj;
}
// 获取checkbox选中的值
searchResult.prototype.getInputsVal = function(name) {
    var chk_value = "";
    var chk = 'input[name="' + name + '"]:checked';
    $(chk).each(function(index) {
        if (index < $(chk).length - 1) {
            chk_value = chk_value + ($(this).val()) + ",";
        } else {
            chk_value = chk_value + ($(this).val());
        }
    });
    return chk_value;
}

// 通过ajax接口获取单一模块的数据
searchResult.prototype.getModulesData = function(moduleName, position, from, parameter) {
    var _this = this;
    parameter.searchKeyword = searchKeyword;
    parameter.currentTab = searchResultCache.currentTab;
    parameter.page = searchResultCache.currentPage;
    callBackendMethod("indexAjaxManager", _this.ajaxMethod[moduleName].methodName, parameter, {
        success: function(result) {
            var originalResult = result;
            var resultStringify = JSON.stringify(originalResult);
            if (result == '__LOGOUT') {
                offlineFun();
                return;
            }
            // console.log(result);
            if (!result) {
                return;
            }
            searchResultCache.finishedModuleNum++;
            if (searchResultCache.finishedModuleNum >= searchResultCache.moduleTotalNum) {
                searchResultCache.loading.close();
            }
            if ((!result || JSON.stringify(result) == "{}") && searchResultCache.currentTab !== "all") {
                var drawArea = document.getElementById(position + "-" + moduleName);
                drawArea.innerHTML = "<div class='noData'><p><img src='/seeyon/skin/dist/images/noData.png'></p><p>" + $.i18n("common.nodata.label") + "</p></div>";
                return;
            }
            // N合一请求（searchMultiApp）需要单独调用各自的模板进行渲染
            if (moduleName === "searchMultiApp") {
                // "应用"模块后台需要的参数是"portal,template,vreport"，但tpl模板和模块有是用的"application"，还原成"application"再继续走渲染逻辑
                var moduleListArray = _this.getModule4NInOne()[0].replace("portal,template,vreport","application").split(",");
                for (var i = 0; i < moduleListArray.length; i++) {
                    var thisModule = moduleListArray[i];
                    // "应用"模块返回的数据不满足UE需求，重新组装一下
                    if (thisModule === "application" && resultStringify !== "{}") {
                        result = _this.result2GroupData(originalResult);
                        _this.getTplAndRenderTpl("application", result, position);
                    } else if (resultStringify !== "{}") {
                        _this.getTplAndRenderTpl(thisModule, originalResult[thisModule], position);
                    }
                }
            } else if (moduleName === "application") {  // 当前页签为“应用”时，"应用"模块返回的数据不满足UE需求，重新组装一下
                result = _this.result2GroupData(originalResult);
                _this.getTplAndRenderTpl("application", result, position);
            } else {
                _this.getTplAndRenderTpl(moduleName, result, position);
            }
            // “同部门的人”有数据时，才渲染右侧对应的模块
            if (moduleName === "addressbook" && result.deptMembers && result.deptMembers.length > 0) {
                _this.renderRightModules4LeftModulesData(result);
            }
            // 分页
            if (searchResultCache.currentTab === "all") {
                // “全部”页签下有多个ajax请求，只渲染“相关”的分页功能
                if (parameter.fromModule === "related") {
                    searchResultPager.init(originalResult.currentPage, originalResult.totalPage, originalResult.totalCount, originalResult.pageSize);
                }
            } else {
                if (searchResultCache.currentTab !== "application" && originalResult && JSON.stringify(originalResult) !== "{}" && typeof(originalResult.currentPage) !== "undefined" && typeof(originalResult.totalPage) !== "undefined" && typeof(originalResult.totalCount) !== "undefined" && typeof(originalResult.pageSize) !== "undefined") {
                    searchResultPager.init(originalResult.currentPage, originalResult.totalPage, originalResult.totalCount, originalResult.pageSize);
                } else {
                    searchResultPager.init(1, 0, 0);
                }
            }
        }
    });
}
// 获取模板的tpl
searchResult.prototype.getTplAndRenderTpl = function(moduleName, data, position) {
    if (!this.ajaxMethod[moduleName] || this.ajaxMethod[moduleName].tplName === "") {
        return;
    }
    var _this = this;
    //缓存中是否在本tpl模板，如果有，直接调用，否则就通过ajax请求tpl模板
    if (searchResultCache.moduleTplCache[moduleName] !== undefined) {
        var currentModuleTplCache = searchResultCache.moduleTplCache[this.ajaxMethod[moduleName].tplName];
        this.renderTpl(currentModuleTplCache, data, moduleName, position);
    } else {
        var url = _ctxPath + "/apps_res/index/tpl/" + position + "-" + this.ajaxMethod[moduleName].tplName + ".html" + "?a=1" + CsrfGuard.getUrlSurffix() + resSuffix;
        $.ajax({
            url: url,
            type: 'get',
            dataType: 'html',
            cache: true,
            beforeSend: CsrfGuard.beforeAjaxSend,
            success: function(result) {
                searchResultCache.moduleTplCache[_this.ajaxMethod[moduleName].tplName] = result;
                _this.renderTpl(result, data, moduleName, position);
            },
            error: function() {
                console.error("no tpl：" + moduleName, position);
            }
        });
    }
}
// 渲染某1个模块的数据
searchResult.prototype.renderTpl = function(tpl, data, moduleName, position) {
    var _this = this;
    var drawArea = document.getElementById(position + "-" + this.ajaxMethod[moduleName].tplName);
    if (moduleName === "xiaoz") {
        data = this.packageData2Xiaoz(data);
    };
    if (moduleName === "addressbook") {
        data.result = this.redKeyWord2Addressbook(data);
    }
    laytpl(tpl).render(data, function(html) {
        drawArea.innerHTML = html;
        html = null;
        tpl = null;
        _this.afterInit(data, _this.ajaxMethod[moduleName].tplName, position);
    });
}
// 执行afterInit
searchResult.prototype.afterInit = function(data, moduleName, position) {
    var _this = this;
    //缓存中是否在本tpl模板，如果有，直接调用，否则就通过ajax请求tpl模板
    if (searchResultCache.searchModuleHandlerCache[moduleName] !== undefined) {
        //从缓存中取flag，如为true，表明已经请求过对应的JS且有after，可直接执行对应的after
        if (searchResultCache.searchModuleHandlerCache[moduleName] === true) {
            searchModuleHandler[moduleName].afterInit(data, moduleName, position);
        }

    } else {
        var url = _ctxPath + "/apps_res/index/tpl/" + position + "-" + moduleName + ".js" + "?a=1" + CsrfGuard.getUrlSurffix() + resSuffix;
        $.ajax({
            url: url,
            type: 'get',
            dataType: 'script',
            cache: true,
            beforeSend: CsrfGuard.beforeAjaxSend,
            success: function(result) {
                eval(result);
                if (typeof(searchModuleHandler[moduleName].afterInit) === "function") {
                    searchResultCache.searchModuleHandlerCache[moduleName] = true;
                    searchModuleHandler[moduleName].afterInit(data, moduleName, position);
                } else {
                    searchResultCache.searchModuleHandlerCache[moduleName] = false;
                }
            },
            error: function(error) {
                searchResultCache.searchModuleHandlerCache[moduleName] = undefined;
            }
        });
    }
}
// "应用"模块返回的数据不满足产品需求，重新组装一下
searchResult.prototype.result2GroupData = function(data) {
    var newData = {
        portal: [],
        template: [],
        vreport: []
    };
    for (var item in newData) {
        newData[item] = data[item]
    }
    return newData;
}
// “小智”模块后端未套红，附件和关联文档未分类，前端单独处理
searchResult.prototype.packageData2Xiaoz = function(data) {
    var newData = JSON.parse(JSON.stringify(data));
    if (newData.qa && newData.qa.length > 0) {
        for (var i = 0; i < newData.qa.length; i++) {
            var answer = newData.qa[i].answer ? newData.qa[i].answer.escapeHTML().replace(searchKeyword, "<span class='red'>" + searchKeyword + "</span>") : "";
            var question = newData.qa[i].question ? newData.qa[i].question.escapeHTML().replace(searchKeyword, "<span class='red'>" + searchKeyword + "</span>") : "";
            newData.qa[i].answer = answer;
            newData.qa[i].question = question;
            newData.qa[i].attsData = [];
            newData.qa[i].assData = [];
            for(var j = 0; j < newData.qa[i].attachments.length; j++) {
                if(newData.qa[i].attachments[j].type === 0) {
                    newData.qa[i].attsData.push(newData.qa[i].attachments[j]);
                } else if(newData.qa[i].attachments[j].type === 2) {
                    newData.qa[i].assData.push(newData.qa[i].attachments[j]);
                }
            }
        }
    }
    return newData;
}
// “通讯录”模块后端未套红，前端单独处理
searchResult.prototype.redKeyWord2Addressbook = function(data) {
    var newData = JSON.parse(JSON.stringify(data));
    if (newData.memberList && newData.memberList.length > 0) {
        for (var i = 0; i < newData.memberList.length; i++) {
            var N = newData.memberList[i].N ? newData.memberList[i].N.replace(searchKeyword, "<span class='red'>" + searchKeyword + "</span>") : "";
            newData.memberList[i].N = N;
        }
    }
    return newData;
}

// 自动补全
var autoCompletionFun = {
    action: function(obj) {
        var _event = _event ? _event : window.event;
        if (currentModule !== "all") {
            return;
        }
        if (_event.keyCode === 13) {
            document.getElementById("autoCompletion").style.display = "none";
            searchAction('searchButton');
        } else if (_event.keyCode === 38 || _event.keyCode === 40) {
            this.key4Arrow(_event.keyCode);
        } else if (obj.value === "") {
            autoCompletionFun.currentIndex = -1;
            this.getData4HistoryRecord(obj);
        } else {
            this.getData4AutoCompletion(obj);
        }
    },
    getData4HistoryRecord: function(obj) {
        var _this = this;
        callBackendMethod("indexAjaxManager", "searchHis", {
            success: function(result) {
                if (result == '__LOGOUT') {
                    offlineFun();
                    return;
                }
                if (result && result instanceof Array && result.length > 0) {
                    _this.render(result, "history");
                } else {
                    document.getElementById("autoCompletion").innerHTML = "";
                    document.getElementById("autoCompletion").style.display = "none";
                }
            }
        });
    },
    getData4AutoCompletion: function(obj) {
        var _this = this;
        callBackendMethod("indexAjaxManager", "autoCompletion", obj.value, {
            success: function(result) {
                if (result == '__LOGOUT') {
                    offlineFun();
                    return;
                }
                if (result && result instanceof Array && result.length > 0) {
                    _this.render(result, "complete");
                } else {
                    document.getElementById("autoCompletion").innerHTML = "";
                    document.getElementById("autoCompletion").style.display = "none";
                }
            }
        });
    },
    render: function(result, type) {
        if (result.length === 0) {
            return;
        }
        document.getElementById("autoCompletion").style.display = "block";
        var tempDom = "<ul>";
        for (var i = 0; i < result.length; i++) {
            tempDom += "<li onclick='javascript:autoCompletionFun.doSearch(this)'>" + result[i].toString().escapeHTML() + "</li>";
        }
        if (result.length > 0 && type === "history") {
            tempDom += "<div class='clearSearchHis' onclick='javascript:autoCompletionFun.clearSearchHis()'>" + $.i18n('index.search.clearHistoryRecord') + "</div>";
        }
        tempDom += "</ul>";
        document.getElementById("autoCompletion").innerHTML = tempDom;
    },
    doSearch: function(obj) {
        document.getElementById("searchWord").value = obj.innerText;
        var searchKeyword = obj.innerText;
        document.getElementById("autoCompletion").style.display = "none";
        searchAction('searchButton');
    },
    clearSearchHis: function() {
        var _this = this;
        callBackendMethod("indexAjaxManager", "clearSearchHis", {
            success: function(result) {
                if (result == '__LOGOUT') {
                    offlineFun();
                    return;
                }
                document.getElementById("autoCompletion").innerHTML = "";
            },
            error: function(result) {
                $.alert(result);
            }
        });
    },
    key4Arrow: function(keyCode) {
        var autoCompletionLi = $("#autoCompletion").find("li");
        if (autoCompletionLi.length === 0 || !$("#autoCompletion").is(":visible")) {
            return;
        }
        if (keyCode === 38) { //上箭头
            this.currentIndex--;
            if (this.currentIndex < 0) {
                this.currentIndex = autoCompletionLi.length - 1;
            }
            var currentLi = autoCompletionLi.eq(this.currentIndex);
            currentLi.addClass("current").siblings("li").removeClass("current");
            document.getElementById("searchWord").value = currentLi.text();
        } else if (keyCode === 40) { //下箭头
            this.currentIndex++;
            if (this.currentIndex >= autoCompletionLi.length) {
                this.currentIndex = 0;
            }
            var currentLi = autoCompletionLi.eq(this.currentIndex);
            currentLi.addClass("current").siblings("li").removeClass("current");
            document.getElementById("searchWord").value = currentLi.text();
        }
    },
    currentIndex: -1
}

// 大家都在搜
var hotWordEvent = function(value) {
    document.getElementById("searchWord").value = value;
    searchKeyword = value;
    searchAction('searchButton');
}

// 分页组件
var searchResultPager = {
    init: function(currentPage, totalPage, totalCount, pageSize) {
        if (!currentPage && !totalPage && !totalCount && !pageSize || totalPage === 0) {
            document.getElementById("pagerArea").innerHTML = "";
            return;
        }
        var pagerStr = "",
            goPage;
        // 摘要信息
        pagerStr += "<i>" + $.i18n('common.display.per.page.label') + "<span>" + pageSize + "</span>" + $.i18n('validate.grid.over_page2.js') + totalCount + $.i18n('validate.grid.over_page3.js') + " " + $.i18n('common.common.label') + totalPage + $.i18n('common.page.label') + "</i>";
        // // 第一页
        pagerStr += "<a href='javascript:searchResultPager.go(1)'><em class='pageFirst'></em></a>";
        // 上一页
        goPage = currentPage > 1 ? (currentPage - 1) : 1;
        pagerStr += "<a href='javascript:searchResultPager.go(" + goPage + ")'><em class='pagePrev'></em></a>";
        // 前三页?
        var startPage = currentPage - 3 > 0 ? currentPage - 3 : 1;
        for (var i = startPage; i < currentPage; i++) {
            pagerStr += "<a href='javascript:searchResultPager.go(" + i + ")'>" + i + "</a>";
        }
        // 当前页
        pagerStr += "<label>" + currentPage + "</label>";
        // 后三页
        var endPage = currentPage + 3 > totalPage ? totalPage : currentPage + 3;
        for (var i = currentPage + 1; i <= endPage; i++) {
            pagerStr += "<a href='javascript:searchResultPager.go(" + i + ")'>" + i + "</a>";
        }
        // 下一页
        goPage = currentPage < totalPage ? (currentPage + 1) : totalPage;
        pagerStr += "<a href='javascript:searchResultPager.go(" + goPage + ")'><em class='pageNext'></em></a>";
        // 性能问题，先屏蔽最后一页
        // 最后一页
        // pagerStr += "<a href='javascript:searchResultPager.go(" + (totalPage) + ")'><em class='pageLast'></em></a>";
        document.getElementById("pagerArea").innerHTML = pagerStr;
    },
    go: function(index) {
        if (searchResultCache.currentPage === String(index)) {
            return;
        }
        searchResultCache.currentPage = String(index);
        searchAction("pager");
    }
}

// 重置搜索条件
var resetFilterCondition = function() {
    var filterListDom = document.getElementById("leftModule-filterList");
    var rowDoms = filterListDom.querySelectorAll(".row");
    for (var i = 0; i < rowDoms.length; i++) {
        var checkbox4All = rowDoms[i].querySelector(".all");
        if (checkbox4All) {
            if (checkbox4All.checked) {
                continue;
            } else {
                var ipts = rowDoms[i].querySelectorAll("input");
                for (var j = 0; j < ipts.length; j++) {
                    if (ipts[j].type === "checkbox") {
                        ipts[j].checked = true;
                    } else if (ipts[j].type === "radio") {
                        ipts[j].checked = false;
                    } else {
                        ipts[j].value = "";
                    }
                }
                checkbox4All.checked = true;
            }
        } else {
            var ipts = rowDoms[i].querySelectorAll("input");
            for (var j = 0; j < ipts.length; j++) {
                if (ipts[j].type === "text") {
                    ipts[j].value = "";
                }
            }
        }
    }
}

/**
 * 来源穿透
 */
function openIndexSource(appType, sourceUrl) {
    if (appType == "3") {
        getCtpTop().openDocument(sourceUrl, 1);
    } else if (appType == '94' || appType == '6' || appType == '30' || appType == '11' || appType == '5') {
        getCtpTop().showMenu(getCtpTop()._ctxPath + sourceUrl, '', 'mainFrame', '', this);
    } else {
        openCtpWindow({
            'url': getCtpTop()._ctxPath + sourceUrl
        });
    }
}

/**
 * 详情穿透
 */
function openIndexResult(appType, linkId, clientType) {
    var _appType = appType;
    var _linkId = linkId;
    var _title = $("#" + linkId + "_title").text();
    var _portalType = "";
    var _openType = ""; // 打开方式
    var _openUrl = ""; // 打开url
    var _isMobile = "";
    if (appType == '99' || appType == '65' || appType == '101' || appType == '1' || appType == '2' || appType == '4' || appType == '39') {
        var requestCaller = new XMLHttpRequestCaller(this, "ajaxIndexController", "getIndexParams", false);
        requestCaller.addParameter(1, "String", appType);
        requestCaller.addParameter(2, "String", linkId);
        var ds = requestCaller.serviceRequest();
        if (ds) {
            _appType = ds.get("appType");
            _linkId = ds.get("linkId");
            if (appType == "99" || appType == "101" || _appType == '39') {
                _openType = ds.get("openType");
                _openUrl = ds.get("openUrl");
                _isMobile = ds.get("isMobile");
            } else if (appType == "65") {
                _portalType = ds.get("type");
                var realSpaceId = ds.get("realSpaceId");
                if (realSpaceId) {
                    _linkId = realSpaceId;
                }
            }
        }
    }

    if (_appType == '99') { // 应用菜单
        openIndex4Menu(_openType, _openUrl);
    } else if (_appType == '83') { // cap4业务空间
        openIndex4Menu("workspace", "/cap4/bizportal.do?method=viewBizPortalSpace&platform=1&spaceId=" + _linkId);
    } else if (_appType == '65') { // 门户空间
        if (clientType == "2") {
            getCtpTop().$.alert(getCtpTop().$.i18n("index.application.portal.msg1", _title));
        } else {
            openIndex4Portal(_portalType, _linkId);
        }
    } else if (_appType == '101') { // 流程模板
        openCtpWindow({
            'url': getCtpTop()._ctxPath + _openUrl
        });
    } else if (_appType == '1') { // 协同
        getCtpTop().openDocument('message.link.col.pending|' + _linkId, 0);
    } else if (_appType == '2') { // cap3无流程表单
        getCtpTop().openDocument('message.link.form.unFlow.view|' + _linkId, 0);
    } else if (_appType == '66') { // cap4无流程表单
        getCtpTop().openDocument('message.link.formtrigger.cap4.msg.unflow|' + _linkId, 0);
    } else if (_appType == '4') { // 公文
        getCtpTop().openDocument('message.link.edoc.done|' + _linkId, 0);
    } else if (_appType == '70') { // 报表
        if (clientType == "2") {
            getCtpTop().$.alert(getCtpTop().$.i18n("index.application.vreport.msg1", _title));
        } else {
            openCtpWindow({
                'url': getCtpTop()._ctxPath + "/vreport/vReport.do?method=showReport&id=" + _linkId
            });
        }
    } else if (_appType == '3') { // 文档
        getCtpTop().openDocument('message.link.doc.open.index|' + _linkId, 0);
    } else if (_appType == '8') { // 新闻
        getCtpTop().openDocument('message.link.news.open|' + _linkId, 0);
    } else if (_appType == '7') { // 公告
        getCtpTop().openDocument('message.link.bulletin.open|' + _linkId, 0);
    } else if (_appType == '9') { // 讨论
        getCtpTop().openDocument('message.link.bbs.open|' + _linkId, 0);
    } else if (_appType == '10') { // 调查
        getCtpTop().openDocument('message.link.inquiry.send|' + _linkId, 0);
    } else if (_appType == '94') { // 领导行程
        var ajaxManager = new leaderAgendaManager();
        var data = ajaxManager.getAgendaDetailsById({
            agendaId: _linkId
        });
        agendaAPI.showAgendaWindows(data, true);
    } else if (_appType == '6') { // 会议
        getCtpTop().openDocument('message.link.mt.send|' + _linkId, 0);
    } else if (_appType == '30') { // 任务
        getCtpTop().openDocument('message.link.taskmanage.view|' + _linkId, 0);
    } else if (_appType == '11') { // 事件
        getCtpTop().openDocument('message.link.cal.view|' + _linkId, 0);
    } else if (_appType == '5') { // 计划
        getCtpTop().openDocument('message.link.plan.summary|' + _linkId, 0);
    } else if (_appType == '39') { // 第三方待办
        if (_isMobile == "true") {
            $.alert($.i18n('cip.index.search.tip'));
            return;
        }
        openCtpWindow({
            'url': _openUrl
        });
    }
}

function openIndex4Menu(_openType, _menuUrl) {
    switch (_openType) {
        case 'workspace':
            // 系统工作区地址
            var _openUrl = _menuUrl.indexOf("http") == 0 || _menuUrl.indexOf("ftp") == 0 ? _menuUrl : getCtpTop()._ctxPath + _menuUrl;
            getCtpTop().showMenu(_openUrl, '', 'mainFrame', '', this);
            break;
        case 'open':
            // 系统新窗口地址
            var _openUrl = _menuUrl.indexOf("http") == 0 || _menuUrl.indexOf("ftp") == 0 ? _menuUrl : getCtpTop()._ctxPath + _menuUrl;
            openCtpWindow({
                'url': _openUrl
            });
            break;
    }
}

function openIndex4Portal(_portalType, linkId) {
    if (_portalType == "portal") { // 门户
        if (linkId == parent.parent.vPortal.portalId) {
            getCtpTop().$.alert(getCtpTop().$.i18n('portal.currentportalIsOpened'));
        } else {
            // 门户地址打开
            var _openUrl = _ctxPath + "/main.do?method=main&portalId=" + linkId + "&subPortal=true" + CsrfGuard.getUrlSurffix();
            openCtpWindow({
                'url': _openUrl,
                'id': linkId
            });
        }
    } else if (_portalType == "space") { // 空间
        var _url = linkId;
        var spaceIndex = getCtpTop().getSpaceIndex(_url);
        if (spaceIndex !== -1) {
            // 当前门户空间切换
            if (spaceIndex instanceof Array) {
                // 空间在vPortal.space下的某个list中
                var _currentSpaceObject = getCtpTop().vPortal.space[spaceIndex[0]].list[spaceIndex[1]];
            } else {
                // 空间直接位于vPortal.space下
                var _currentSpaceObject = getCtpTop().vPortal.space[spaceIndex];
            }
            getCtpTop().refreshSpaceMenuNavForPortal(_url, "true")
            getCtpTop().initV5Space(_currentSpaceObject);
        } else {
            // 打开新门户
            var requestCaller = new XMLHttpRequestCaller(this, "portalManager", "getSpaceBelongedPortalId", false);
            requestCaller.addParameter(1, "Long", _url);
            var subPortalId = requestCaller.serviceRequest();
            if (subPortalId) {
                if (parent.parent.vPortal.subPortal == "false") {
                    if (subPortalId == parent.parent.vPortal.portalId || subPortalId == 1) {
                        // 导航缓存中没有当前要打开的空间的时候
                        getCtpTop().showMenu(_ctxPath + "/portal/spaceController.do?method=showThemSpace&spaceId=" + _url);
                    } else {
                        // 门户地址打开
                        var _openUrl = _ctxPath + "/main.do?method=main&portalId=" + subPortalId + "&subPortal=true&spaceId=" + _url + CsrfGuard.getUrlSurffix();
                        openCtpWindow({
                            'url': _openUrl,
                            'id': subPortalId
                        });
                    }
                } else {
                    if (subPortalId == parent.parent.vPortal.portalId) {
                        // 导航缓存中没有当前要打开的空间的时候
                        getCtpTop().showMenu(_ctxPath + "/portal/spaceController.do?method=showThemSpace&spaceId=" + _url);
                    } else {
                        // 门户地址打开
                        var _openUrl = _ctxPath + "/main.do?method=main&portalId=" + subPortalId + "&subPortal=true&spaceId=" + _url + CsrfGuard.getUrlSurffix();
                        openCtpWindow({
                            'url': _openUrl,
                            'id': subPortalId
                        });
                    }
                }
            }
        }
    }
}

// 某人的时间视图
function openIndexSource4MembersSchedule(memberName, memberId) {
    var dialogAgenda = $.dialog({
        id: "dialogTimeViewDetail",
        title: memberName + $.i18n('leaderagenda.agenda.span.part'),
        url: _ctxPath + "/timeView.do?method=myTimeView&sourceType=6&memberId=" + memberId + CsrfGuard.getUrlSurffix(),
        width: 1000,
        height: 600,
        buttons: [{
            id: 'btn2',
            text: $.i18n("common.button.close.label"),
            handler: function() {
                dialogAgenda.close();
            }
        }]
    });
}

// 后端返回的问答数据结构不满足附件组件，需要前端拼装一下
var stringifyAttachment = function(atts) {
    var newStr = "";
    for (var i = 0; i < atts.length; i++) {
        newStr += JSON.stringify(atts[i]);
        newStr = i < atts.length - 1 ? newStr + "," : newStr;
    }
    newStr.substring(newStr.length - 1, newStr.length);
    return newStr;
}