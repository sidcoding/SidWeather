# SidWeather
ARouter 

  一、配置项目build.gradle文件(可选配置)
  apply plugin: 'com.alibaba.arouter'//可选
  buildscript {
      ext.kotlin_version = '1.3.31'
      ext.arouter_register_version = '1.0.2'
      repositories {
          google()
          jcenter()
      }
      dependencies {
          classpath 'com.android.tools.build:gradle:3.2.0'
          classpath "com.alibaba:arouter-register:$arouter_register_version"//可选
          classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
          // NOTE: Do not place your application dependencies here; they belong
          // in the individual module build.gradle files
      }
  }
  
  二、配置app Module中的build.gradle文件
  注意：
    1、使用kapt 进行依赖
    2、所有使用到ARouter的Module中都要按如下方式添加依赖
    3、app(主入口)Module中一定要添加其他Module的依赖
    
    ...
    apply plugin: 'kotlin-kapt'

    android {
       ...
        kapt {
            arguments {
                arg("AROUTER_MODULE_NAME", project.getName())
            }
        }
    }

    dependencies {
        ...

        //阿里ARouter框架
        api 'com.alibaba:arouter-api:1.4.1'
       //或者 implementation 'com.alibaba:arouter-api:1.4.1'
        kapt 'com.alibaba:arouter-compiler:1.2.2'
        //app Module中在此处添加若干需要使用到ARouter的Module依赖，但这些Module间不用相互依赖，即可通过ARouter相互通信
        implementation project(':baselibrary')
        ...
    }
    
  三、问题汇总
    问题一：弹窗提示：W/ARouter::: ARouter::There is no route match the path [/xxx/xxx], in group [xxx][ ]

    原因：
    1、app(主入口)Module中未添加使用到的Module的依赖
    2、com.alibaba:arouter-compiler未使用kapt 依赖（compiler依赖出错）

    问题二：报错日志
    com.alibaba.android.arouter.exception.HandlerException: ARouter::Extract the default group failed, the path must be start with '/' and contain more than 2 '/'!

    原因：在ARouter的path时格式不对，在path中一定要是两级路径并包含两个“/”，例如：
    正确：
      @Route(path = "/app/MainActivity")//注意path中"/"个数
      class MainActivity : AppCompatActivity() {

          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)
              setContentView(R.layout.activity_main)
              ARouter.getInstance().inject(this)//使用ARouter前一定要在onCreate中调用这行代码
              btn_1.setOnClickListener{v->
                      //注意path中"/"个数
                      ARouter.getInstance().build("/testlibrary/MainActivity").navigation()
              }
          }
      }

      错误：
      @Route(path = "app/MainActivity")///注意path中"/"个数
      class MainActivity : AppCompatActivity() {

          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)
              setContentView(R.layout.activity_main)
              ARouter.getInstance().inject(this)//使用ARouter前一定要在onCreate中调用这行代码
              btn_1.setOnClickListener{v->
                      ///注意path中"/"个数
                      ARouter.getInstance().build("testlibrary/MainActivity").navigation()
              }
          }
      }
    
    问题三：build报错：警告: 来自注释处理程序 'org.jetbrains.kotlin.kapt3.base.ProcessorWrapper' 的受支持 source 版本 'RELEASE_7' 低于 -source '1.8'

原因：source 版本过高引起的，但不会导致程序无法运行。
注：如果程序无法运行可能是其他原因造成，请检查Run build报红部分

问题四：使用@Autowired注解后，build报错：ARouter::Compiler An exception is encountered, [The inject fields CAN NOT BE 'private'!!! please check field [*] in class [*.*.*.MainActivity]]
注意：

使用@Autowired注解时一定要在onCreate方法中使用如下代码ARouter.getInstance().inject(this)
使用@Autowired注解的变量一定要初始化，尽量少用lateinit字段
原因：注解使用错误，1、注解的变量是私有变量，2、未添加@JvmField注解
例如：

错误示范1：
    @Autowired(name = Constants.INTENT_DATA_KEY.KEY1)
    private var msg = -1
    
错误示范2：
    @Autowired(name = Constants.INTENT_DATA_KEY.KEY1)
    var msg = -1

正确示范：
    @JvmField
    @Autowired(name = Constants.INTENT_DATA_KEY.KEY1)
    var msg = -1
