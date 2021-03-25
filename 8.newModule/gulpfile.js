var gulp = require('gulp'),
    fs = require('fs'),
    wrench = require('wrench');
var less=require('gulp-less'),
    concat = require('gulp-concat'),
    depend = require("gulp-load-depence-source"),
    jsmin = require('gulp-uglify'),
    cssmin = require('gulp-minify-css'),
    replacePath = require("gulp-just-replace"),
    zip = require('gulp-zip'),
    buildversion = require("gulp-asset-rev"),
    rename = require('gulp-rename'),
    clean = require('gulp-clean'),
    cmpPathMap = require("gulp-cmp-path"),
    del = require('del'),
    analysisS3 = require('gulp-analysis-s3'),
    analysisS3Mergelist = require('gulp-analysis-s3-mergelist'),
    vinylPaths = require('vinyl-paths'),
    gulpSequence = require('gulp-sequence');

var tasks= wrench.readdirSyncRecursive('./tasks');
var taskList = [];
tasks.forEach(function(item){
    var taskConfig = require("./tasks/" + item);
    taskList.push(taskConfig);
});


//================================================发布任务==========================================//

var cordovaRegex = /<s3[\s|\S]*(cordova)['|"](\s)*(\/>){1}/;
var cordovaRegex2 = /\$\{data:cordova\}/;
var buildversionRegex = /\?*\$\{data:buildversion\}/g;

gulp.task("default",function(){
    if(taskList.length == 0) return;
    new Promise(function(resolve,reject){

    });
    taskList.forEach(function(oneTaskConfig){
        var sources = oneTaskConfig.sources,
            commons = oneTaskConfig.commons,
            pack = oneTaskConfig.pack,
            projectName = oneTaskConfig.projectName;
        var lessConfig = commons.less,
            concatConfig = commons.concat,
            jsminConfig = commons.jsmin,
            cssminConfig = commons.cssmin;
        var templatePath =  "./"+projectName+"_template";
        var s3mergePath = "./"+projectName + "_mergeTemp";


        var promise = new Promise(function(resolve){
            gulp.src(sources+"/**/*")   //copy到temp
                .pipe(gulp.dest(templatePath))
                .on("end",resolve);
        }).then(function(){
            return new Promise(function(resolve){//less任务
                if(lessConfig){
                    var files = _transeFilePath(lessConfig.files,templatePath);
                        dest = _transeFilePath(lessConfig.dest,templatePath);
                    if(files){
                        gulp.src(files)
                            .pipe(less())
                            .pipe(gulp.dest(dest))
                            .on("end",function(){
                                resolve(files);
                            });
                    }
                }else {
                    resolve();
                }
            })
        }).then(function(files){
            return new Promise(function(resolve){//删除less源文件
                if(files){
                    del(files);
                }
                resolve();
            });
        }).then(function(){

            return new Promise(function(resolve){//合并任务
                if(concatConfig && concatConfig.length >0){
                    var concatTaskNum = concatConfig.length;
                    var taskEndCallback = function(){
                        concatTaskNum --;
                        if(!concatTaskNum){
                            resolve();
                        }
                    };
                    concatConfig.forEach(function (item) {
                        var src = _transeFilePath(item.files,templatePath);
                        var target = item.target;
                        var dest = _transeFilePath(item.dest,templatePath);
                        gulp.src(src)
                            .pipe(concat(target))
                            .pipe(gulp.dest(dest))
                            .on("end",function(){//删除合并后的子文件
                                del(src);
                                taskEndCallback();
                            });
                    })
                }else {
                    resolve();
                }
            });
        }).then(function(){
            return new Promise(function(resolve){//压缩js
                if(jsminConfig){
                    var files = _transeFilePath(jsminConfig.files,templatePath);
                    var jsminTemp = templatePath + "/jsmintemp";//压缩到临时目录
                    var suffix = jsminConfig.suffix || "";
                    var dest = _transeFilePath(jsminConfig.dest,templatePath);
                    var exclude = _transeFilePath(jsminConfig.exclude,templatePath);
                    var src = _contactIncludeAndExclude(files,exclude);
                    gulp.src(src)
                        .pipe(jsmin())
                        .pipe(rename({suffix:suffix}))
                        .pipe(gulp.dest(jsminTemp))
                        .on("end",function(){
                            del(files);
                            resolve({tempPath:jsminTemp,dest:dest});
                        });

                }else {
                    resolve();
                }
            });
        }).then(function(jsminTempConfig){
            return new Promise(function(resolve){
                if(jsminTempConfig){
                    var tempPath = jsminTempConfig.tempPath,
                        dest = jsminTempConfig.dest;
                    gulp.src(tempPath+"/*")   //copy到temp
                        .pipe(gulp.dest(dest))
                        .on("end",function(){//删除jsmin产生的临时目录
                            del(tempPath);
                            resolve();
                        });
                }else {
                    resolve();
                }
            });

        }).then(function(){
            return new Promise(function(resolve){
                if(cssminConfig){
                    var files = _transeFilePath(cssminConfig.files,templatePath);
                    var cssminTemp = templatePath + "/cssmintemp";
                    var suffix = cssminConfig.suffix || "";
                    var dest = _transeFilePath(cssminConfig.dest,templatePath);
                    var exclude = _transeFilePath(cssminConfig.exclude,templatePath);
                    var src = _contactIncludeAndExclude(files,exclude);
                    return gulp.src(src)
                        .pipe(cssmin())
                        .pipe(rename({suffix:suffix}))
                        .pipe(gulp.dest(cssminTemp))
                        .on("end",function(){
                            del(files);
                            resolve({tempPath:cssminTemp,dest:dest});
                        });
                }else {
                    resolve();
                }
            })
        }).then(function(cssminTempConfig){
            return new Promise(function(resolve){
                if(cssminTempConfig){
                    var tempPath = cssminTempConfig.tempPath,
                        dest = cssminTempConfig.dest;
                    gulp.src(tempPath+"/*")   //copy到temp
                        .pipe(gulp.dest(dest))
                        .on("end",function(){
                            del(tempPath);
                            resolve();
                        });
                }else {
                    resolve();
                }
            });

        }).then(function(){
            return new Promise(function(resolve){
                var packTaskNum = Object.keys(pack).length;
                var taskEndCallback = function(){
                    packTaskNum --;
                    if(!packTaskNum){
                        resolve();
                    }
                };
                for(var key in pack){
                    var envItem = pack[key];
                    var s3scriptjspdata = envItem.s3scriptjspdata;
                    if(s3scriptjspdata){
                        s3scriptjspdata = _transeFilePath(s3scriptjspdata,templatePath);
                        gulp.src(s3scriptjspdata)
                            .pipe(analysisS3Mergelist(templatePath,s3mergePath))
                            .pipe(gulp.dest(templatePath))
                            .on("end",taskEndCallback)
                    }else {
                        taskEndCallback();
                    }
                }
            })
        }).then(function(){
            return new Promise(function(resolve){
                var packTaskNum = Object.keys(pack).length;
                var taskEndCallback = function(){
                    packTaskNum --;
                    if(!packTaskNum){
                        resolve();
                    }
                };
                for (var key in pack) {
                    var replacements = [];
                    var envItem = pack[key];
                    var files = _getEditFiles(templatePath);
                    var cantEditFiles = _getCantEditFiles(templatePath);
                    files = _contactIncludeAndExclude(files,cantEditFiles);
                    var dest = envItem.type == "zip"?templatePath + "/ziptemp" :envItem.dest;//zip打包的先产生临时文件目录
                    var cordovaReplacement = "";
                    var v = envItem.buildversion?"?buildversion="+new Date().getTime():"";
                    if (envItem.cordova) {
                        cordovaReplacement = "<script src=\"http://cmp/v/js/cordova/__CMPSHELL_PLATFORM__/cordova.js";
                        cordovaReplacement += "\"></script>\r\n<script src=\"http://cmp/v/js/cordova/cordova-plugins.js";
                        cordovaReplacement += "\"></script>";
                    }

                    replacements.push({
                        search: cordovaRegex,
                        replacement: cordovaReplacement
                    });
                    replacements.push({
                        search: cordovaRegex2,
                        replacement: cordovaReplacement
                    });
                    replacements.push({
                        search: buildversionRegex,
                        replacement: v
                    });
                    var modules = envItem.modules;
                    var s3scriptjspdata = envItem.s3scriptjspdata;//使用S3编辑文件来编辑
                    if(s3scriptjspdata){
                        s3scriptjspdata = _transeFilePath(s3scriptjspdata,templatePath);
                    }

                    modules.forEach(function (item) {
                        replacements.push({
                            search: _transeReplaseRegex(item.match),
                            replacement: item.replacement
                        })
                    });
                    if(envItem.buildversion){
                        gulp.src(files)
                            .pipe(analysisS3(s3scriptjspdata,templatePath))
                            .pipe(buildversion())
                            .pipe(replacePath(replacements))
                            .pipe(gulp.dest(dest))
                            .on("end",function(){
                                gulp.src(cantEditFiles)
                                    .pipe(gulp.dest(dest))
                                    .on("end",taskEndCallback);
                            });
                    }else {
                        gulp.src(files)
                            .pipe(analysisS3(s3scriptjspdata,templatePath))
                            .pipe(replacePath(replacements))
                            .pipe(gulp.dest(dest))
                            .on("end",function(){
                                gulp.src(cantEditFiles)
                                    .pipe(gulp.dest(dest))
                                    .on("end",taskEndCallback);
                            });
                    }

                }
            });
            
        }).then(function(){
            return new Promise(function(resolve){
                var packTaskNum = Object.keys(pack).length;
                var taskEndCallback = function(){
                    packTaskNum --;
                    if(!packTaskNum){
                        resolve();
                    }
                };
                for(var key in pack){
                    var envItem = pack[key];
                    var type = envItem.type;
                    var dest =envItem.dest;
                    var tp = type == "zip"?templatePath + "/ziptemp":templatePath;
                    var include = _transeFilePath(envItem.files,tp);
                    var exclude = _transeFilePath(envItem.exclude,tp);
                    var src = _contactIncludeAndExclude(include,exclude);
                    if(type == "url"){
                        var cantEditFiles = _getCantEditFiles(templatePath);//url方式的将图片，字体文件复制过去
                        gulp.src(cantEditFiles)
                            .pipe(gulp.dest(dest))
                            .on("end",taskEndCallback);
                    }else if(type == "zip"){
                        var name = envItem.name || key + ".zip";
                        gulp.src(src)
                            .pipe(zip(name))
                            .pipe(gulp.dest(dest))
                            .on("end",taskEndCallback);
                    }
                }
            });
        }).then(function(){
           del(templatePath);
        })


    });    
});



function _transeReplaseRegex (param){
    param = param.replace("$","\\$").replace("{","\\{").replace("}","\\}").replace("?","\\?");
    var regex = new RegExp(param,"g");
    return regex;
}
 function _contactIncludeAndExclude(include,exclude){
     var src = [];
     if(exclude){
         if(include instanceof Array){
             src = src.concat(include);
         }else {
             src.push(include);
         }

         if(exclude instanceof Array){
             exclude.forEach(function(item){
                 item = item.indexOf("!") == -1?"!"+item : item;
                 src.push(item);
             });
         }else if(typeof exclude == "string"){
             exclude = exclude.indexOf("!") == -1 ? "!" + exclude : exclude;
             src.push(exclude);
         }
     }else {
         src = include;
     }
     return src
 }
function _transeFilePath(paths,source){
    var filePath = "";
    if(paths instanceof Array){
        filePath  = [];
        for(var i = 0;i<paths.length;i++){
            filePath.push(source + paths[i]);
        }
    }else {
        filePath = source + paths;
    }
    return filePath;
}
function _getEditFiles(source){
    var editSuffix = ["html","s3js","css","js","json"];
    var editFiles = [];
    for(var i = 0;i<editSuffix.length;i++){
        var fileSuffix = editSuffix[i];
        editFiles.push(source + "/**/*." + fileSuffix);
        editFiles.push(source + "/**/*." + fileSuffix.toUpperCase());
    }
    return editFiles;
}
function _getCantEditFiles(source){
    var fontFile = ["svg","ttf"],imgFile = ["jpg","jpeg","gif","png","bmp","img","image","tif","tiff"];
    var concatTemp = fontFile.concat(imgFile);
    var cantEditFiles = [];
    for(var i = 0;i<concatTemp.length;i++){
        var fileSuffix = concatTemp[i];
        cantEditFiles.push(source + "/**/*." + fileSuffix);
        cantEditFiles.push(source + "/**/*." + fileSuffix.toUpperCase());
    }
    cantEditFiles.push(source + "/jsmintemp/**/*");//压缩js产生的临时文件不进入
    cantEditFiles.push(source + "/cssmintemp/**/*");//压缩css产生的临时文件不进入
    return cantEditFiles;
}




// gulp.task("release_depend",function(){
//     if(taskList.length == 0) return;
//     taskList.forEach(function(oneTask){
//         if(!oneTask.releaseTask) return;
//         var releaseDepend = oneTask.releaseTask.depend;
//         if(!releaseDepend) return;
//         var files = releaseDepend.files;
//         if(!files) return; 
//         var dest = releaseDepend.dest || "./" + oneTask.projectName+"_" + oneTask.version + "/release/depend";
//         var modules = releaseDepend.modules;
//         for(var key in modules){
//             var moduleItem = modules[key];
//             if("cmp" == key && !moduleItem.mapping){
//                 moduleItem.mapping = cmpPathMap;
//                 moduleItem.name = key;
//             }
//             gulp.src(files)
//                 .pipe(depend(moduleItem))
//                 .pipe(gulp.dest(dest));
//         }
//     });    
// });


