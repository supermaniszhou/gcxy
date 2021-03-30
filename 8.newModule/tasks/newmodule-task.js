/**
 * 一、此构建任务使用nodejs的gulp，开发者需要准备nodejs的环境
 * 二、此文件为构建任务的例子，开发者可根据下面的注释，带注释的都是可以修改的，修改成自己项目的构建工程，即可进行项目的构建。
 * 三、此任务完全兼容S3工具的编译模式，开发者只需设置s3编辑文件的路径即可按照s3的方式编辑。
 * 四、此文件设计的构建任务是按照前端常用构建模式进行设计的，能满足90%以上的前端构建任务
 * 五、构建任务分为1、通用任务；2、打包任务。
 *    其中：1、通用任务是无论打成什么包都会用到的任务：如：1.编译less---->2.合并文件---->3.压缩文件
 *          2、打包任务就是分类型打包，打成zip包或者url的模式，打包任务兼容s3的编辑模式
 * 六、通用任务包含：
 *     1、less任务：将less语法的文件转换成css文件
 *     2、concat任务：合并文件任务，包括可以合并css文件，js文件等 可多个不同的类型的合并同时进行
 *     3、jsmin任务：压缩js任务
 *     4、cssmin任务：压缩css任务
 *     5、pack任务：打包任务，将项目打包成zip包或者url访问的资源路径
 * 七、路径中  ./  代表的是当前目录，一般将构建任务和工程项目放在同一目录下，：如
 *     ---src   //项目文件夹
 *     ---node_modules        //构建依赖库
 *     ---tasks               //构建任务文件夹，可同时放置多个任务文件进行一起编译
 *     ---gulpfile.js         //gulp插件
 *     ---package-lock.json   //gulp插件配置
 * 八、构建任务中的dest输出目录，都是相对于sources文件目录结构的相对路径
 */


var task = {
    projectName:"newmodule",//-------------------------------------------------------------------------------------项目名称
    // sources:"./src",//------------------------------------------------------------------------------------------------工程源码文件夹，即需要被构建的代码
    sources:"./src-3003",//------------------------------------------------------------------------------------------------工程源码文件夹，即需要被构建的代码
    commons:{//--------------------------------------------------------------------------------------------------------编辑less、合并代码、压缩js文件、压缩css文件都属于是通用任务
        less:{//--------------------------------------------------------------------------------------------------------less任务
            files:"/less/*.less",//----------------------------------------------------------------------------------被编辑的less文件
            dest:"/css"//---------------------------------------------------------------------------------------------编辑后的css文件输出路径
        },
        concat:[{//----------------------------------------------------------------------------------------------------合并文件任务
            files:["/js/one_debuge.js","/js/two_debug.js"],//----------------------------------------------------需要合并的文件
            target:"oneTwo.js",//------------------------------------------------------------------------------------合并成一个文件的文件名
            dest:"/js"//-----------------------------------------------------------------------------------------------输出路径
        },{
            files:["/css/css_one.css","/css/css_two.css"],
            target:"oneTwo.css",
            dest:"/css"
        }],
        jsmin:{//------------------------------------------------------------------------------------------------------js压缩任务
            files:"/js/amapComponent.js",//------------------------------------------------------------------------需要被压缩的文件
            exclude:"",//---------------------------------------------------------------------------------------------排除的文件
            suffix:"",//-----------------------------------------------------------------------------------------------如果设置了后缀名，如：.min 则压缩输出的文件名为xxx.min.js
            dest:"/js"//-----------------------------------------------------------------------------------------------输出路径
        },
        cssmin:{//-----------------------------------------------------------------------------------------------------css压缩任务
            files:"/css/*.css",//------------------------------------------------------------------------------------需要被压缩的文件
            exclude:"",
            suffix:"",
            dest:"/css"
        }
    },
    pack:{//-----------------------------------------------------------------------------------------------------------打包任务，包括打成zip包和url路径资源
        wechat:{//-----------------------------------------------------------------------------------------------------打包任务名，随便命名
            type:"url",//----------------------------------------------------------------------------------------------打包类型，url打成url地址的资源路径
            // dest:"F:\\Seeyon\\v8sp1-qiye\\ApacheJetspeed\\webapps\\seeyon\\m3\\apps\\v5\\zhoumobile",//----------------打包输出路径
            dest:"F:\\Seeyon\\v8sp1-qiye\\ApacheJetspeed\\webapps\\seeyon\\m3\\apps\\v5\\xntl",//----------------打包输出路径
            // dest:"F:/wkHome",//----------------打包输出路径
            files:"/**/*",//------------------------------------------------------------------------------------------需要被打包的文件
            exclude:"",//----------------------------------------------------------------------------------------------被排除的文件
            buildversion:true,//--------------------------------------------------------------------------------------是否对路径中${data:buildversion}进行编译，微协同要写成true
            modules:[{  //---------------------------------------------------------------------------------------------本应用所需要依赖的模块的路径编辑
                match:"${data:dependencies.cmp}",//----------------------------------------------------------------被编译的路径
                replacement:"/seeyon/m3/cmp"//----------------------------------------------------------------------编译成的真实路径，微协同就是其A8服务器资源路径
            },{
                match:"${data:dependencies.newmodule}",  //------------------------------------------------------本应用依赖自身模块，即mobilework
                replacement:"/seeyon/m3/apps/v5/newmodule"  //--------------------------------------------------将本模块资源路径编辑成真实路径，微协同就是其A8服务器资源路径
            }],
            s3scriptjspdata:"/s3scriptjspdata/wechat_commondata.data"//----------------------------------------如果使用S3的编辑文件来编辑的话，定义s3编辑文件路径
        },
        m3:{
            type:"zip",//----------------------------------------------------------------------------------------------打包类型zip  打成能运行在M3 app上的zip包
            name:"3003.zip",//----------------------------------------------------------------------------------------打包名称
            // dest:"F:\\Seeyon\\v8sp1-qiye\\ApacheJetspeed\\webapps\\seeyon\\m3files\\v5",//---------------------------------zip包输出路径
            dest:"F:\\",//---------------------------------zip包输出路径
            files:"/**/*",
            exclude:"",
            nativeheader:true,//-------------------------------------------------------------------------------------是否将html写的header标签编辑成原生的头部
            buildversion:false,  //----------------------------------------------------------------------------------是否对路径中${data:buildversion}进行编译，写成false
            cordova:true,  //-----------------------------------------------------------------------------------------是否编辑<s3:data name='cordova' />或者${data:cordova}标签，打zip包，此项必须写成true
            modules:[{  //---------------------------------------------------------------------------------------------本应用所需要依赖的模块的路径编辑
                match:"${data:dependencies.cmp}",  //--------------------------------------------------------------本应用依赖cmp模块
                replacement:"http://cmp/v"  //----------------------------------------------------------------------将cmp模块编译成的真实的资源导入路径，依赖模块的真实路径，可查看每个模块的manifest.json文件中的urlSchemes这项配置,其中必须加上/v
            },{
                match:"${data:dependencies.newmodule}",  //------------------------------------------------------本应用依赖自身模块，即mobilework
                replacement:"http://newmodule.v5.cmp/v"  //------------------------------------------------------将本模块资源路径编辑成真实路径
            }],
            s3scriptjspdata:"/s3scriptjspdata/cmp_commondata.data"//--------------------------------------------如果使用S3的编辑文件来编辑的话，定义s3编辑文件路径
        }
    },

};

module.exports = task;