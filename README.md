# andbase

* 该项目包含两部分，基础框架（library）和示例程序（app）。 包含安卓常用流行框架，如：Retrofit2(OkHttp, Gson)、RxJava2(RxAndroid)、Glide、ButterKnife...
* 依赖该库即自动导入Retrofit2和RxJava2及常用类库，同时亦可参考示例程序集成ButterKnife和Glide。

# gradle

* 在项目根目录的build.gradle文件中添加:
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
* 在app/build.gradle文件中添加依赖:
```
compile 'com.github.linxcool:andbase:1.0.4'
```

[![](https://jitpack.io/v/linxcool/andbase.svg)](https://jitpack.io/#linxcool/andbase)

# library
* util
  * CacheUtil 
  * FileUtil
  * ...
* db
  * DbHelper
* net
  * DownloadFile
  * DownloadTask
  * ...
* rx
  * RxBus
  * RxCache
  * ...
* ...
