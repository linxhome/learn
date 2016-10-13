###So文件无法加载的问题小解

自从另入了一个自定义的So文件后，后台Crash日志就常报UnsatisfiedException，简单说来就是通过System.load方法无法找到So文件。

关键是有限的测试机很少遇到这样的错误，所以本地基本无法复现。

第一步肯定是上StackOverflow上搜索，貌似遇到这个问题的人不多，看来这坑多半是国产厂商造成的。

简单的try Catch处理肯定不行，毕竟So文件没有加载的话，相关逻辑肯定是运行不了。

先看看用户上报的详细日志,先摘两条出来看看

第一条
> Couldn't load ac_jni: findLibrary returned null

第二条
> Couldn't load ac_jni from loader dalvik.system.PathClassLoader[DexPathList[[zip file "/mnt/asec/appPackName-1/pkg.apk", zip file "/data/data/appPackName/code_cache/secondary-dexes/pkg.apk.classes2.zip"],nativeLibraryDirectories=[/vendor/lib, /system/lib]]]: findLibrary returned null

第一条只是提示findLibrary return null,没有什么价值，第二条则把搜索So的路径都给打出来了，找到一部手机，去这几个路径下看了看，apk里面都有包含so文件，难道是跟CPU指令集有关？因为最开始图方便，只放了一个armeabi的目录，手工生成了其它几种指令集的目录，把so复制进去，问题仍然存在。

只能再换个思路，找了几部手机，发现so文件不仅在apk里存在，在"/data/data/packageName/lib/lib/abc.so这个路径下也存在，看起来似乎可以通过手工指定加载路径。

 	try {
            String soFilePath = "/data/data/" + getPackageName() + "/lib/lib" + soFileName + ".so";
            File soFile = new File(soFilePath);
            if (soFile.exists()) {
                System.load(soFile.getAbsolutePath());
               
            } else {
                System.loadLibrary(soFileName);
            }
        } catch (Throwable t) {
            throw t;
        }



首先通过指定的路径去加载so文件，如果不存在，再通过系统的路径来进行自动加载。通过统计，
1. 通过"/data/data/" + getPackageName() + "/lib/lib"成功加载为64万人
2. 通过System.load成功加载的有91人
3. 加载失败的11人

至此，这种方式成功加载的为99.998%，可以说是比较好的解决了这个问题。

不过还是有一个遗留问题，就是为什么系统默认的加载方式有这么多的失败的情况？到底跟哪些配置相关，这就可能需要研究到Rom层了。

参考资料：

[简书：android loadlibrary 更改libPath 路径，指定路径加载.so](http://www.jianshu.com/p/f751be55d1fb)
[Java中System.loadLibrary() 的执行过程](https://my.oschina.net/wolfcs/blog/129696?fromerr=feHAVGhI)
[部分手机不能加载so文件，couldn't find *.so](http://blog.csdn.net/it_talk/article/details/50606222)
