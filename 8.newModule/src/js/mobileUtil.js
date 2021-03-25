/**
 * 缓加载工具类
 */
var LazyUtil = (function(){

    //缓加载机制
    var lazyStack = {};
    function LazyTool(){}
    LazyTool.prototype.addLazyStack = function(item){

        /**
         * item.code 字符串 | 堆栈标识
         * item.depend 字符串 | 依赖的js
         * item.dependModel strong/强关联，必须等父任务执行完成后在进行加载  weak/若关联，加载顺序没关系（默认值）
         * item.css 数组 | css数组
         * item.js  数组 | js数组
         *
         */
        if(item.code){
            var i = lazyStack[item.code];
            if(!i){
                item.loaded = false;
                item.isLoading = false;
                lazyStack[item.code] = item;
            }else{
                console.warn("重复设置懒加载, code=" + item.code);
            }
        }else{
            alert(cmp.i18n("Edoc.exception.setException"));
        }
    };

    //启动懒加载
    LazyTool.prototype.startLazy = function(groups){

        for(var k in lazyStack){

            var thisI = lazyStack[k], _this = this;
            if(thisI.loaded || thisI.isLoading){
                continue;
            }
            //按组加载
            if(groups && thisI.groups != groups){
                continue;
            }
            thisI.isLoading = true;

            function loadThis(i){
                if(i.css && i.css.length > 0){
                    cmp.asyncLoad.css(i.css);
                }
                if(i.js && i.js.length > 0){
                    //console.log("开始加载:" + i.js);
                    cmp.asyncLoad.js(i.js, function(){
                        //console.log("完成加载:" + i.js);
                        _this._onJSLoad(i);
                    });
                }else{
                    _this._onJSLoad(i);
                }
            }

            if(thisI.depend && thisI.dependModel === "strong"){
                (function(child){
                    _this.addLoadedFn(child.depend, function(){
                        loadThis(child);
                    });
                })(thisI);
            }else{
                loadThis(thisI);
            }
        }
    };

    /**
     * js加载完成后执行脚本
     */
    LazyTool.prototype._onJSLoad = function(i){
        var _this = this;
        i.loaded = true;
        if(i.functions && i.functions.length > 0){
            var checkRet = _this.isLoadChain(i.code);
            if(checkRet.finish){
                for(var j = 0; j < i.functions.length; j++){
                    i.functions[j]();
                }
                i.functions = [];
            }else{
                //事件转移
                for(var j = 0; j < i.functions.length; j++){
                    //console.log("事件转移:" + i.code + " to " + checkRet.code);
                    _this.addLoadedFn(checkRet.code, i.functions[j]);
                }
                i.functions = [];
            }
        }
    }

    //加载脚本加载完成后
    LazyTool.prototype.addLoadedFn = function(code, fn){
        //console.log("接收转移事件:code=" + code + " fn=" + fn);
        var i = lazyStack[code], _this = this;
        if(i){
            var checkRet = _this.isLoadChain(i.code);
            if(checkRet.finish){
                fn();
            }else{
                if(checkRet.code == i.code){
                    i.functions = i.functions || [];
                    i.functions.push(fn);
                }else{
                    //转移
                    _this.addLoadedFn(checkRet.code, fn);
                }
            }
        }else{
            fn();
        }
    };

    //校验依赖路径是否加载完成
    LazyTool.prototype.isLoadChain = function(code){

        if(!code){
            return {
                "finish" : true
            };
        }else{
            var i = lazyStack[code];
            if(!i.loaded){
                return {
                    "finish" : false,
                    "code" : code
                };
            }else{
                return this.isLoadChain(i.depend);
            }
        }
    };

    return new LazyTool();
})();

var _cmpPath="http://cmp/v";
var $verstion="";
/**
 * 注册缓加载
 */
function _registLazy() {
    //注册缓加载
    LazyUtil.addLazyStack({
        "code": "lazy_load",
        "css": [
            _cmpPath + "/css/cmp-picker.css" + $verstion,
            _cmpPath + "/css/cmp-search.css" + $verstion
        ],
        "js": [
            _cmpPath + "/js/cmp-picker.js" + $verstion,
            _cmpPath + "/js/cmp-dtPicker.js" + $verstion,
            _cmpPath + "/js/cmp-search.js" + $verstion
        ]
    });
}

