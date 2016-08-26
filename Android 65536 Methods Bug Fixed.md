###简述过程
 - 遇到NoClassFoundException，追查发现根源是64k Method限制
 - 正确使用MultiDex方案后，但是遇到2.3.7以下的无法安装问题
 - 使用dexknife解决自定义分包问题
 
###正文

事故发生在某一个平常得不能再平常的下午，同事在开发的时候，突然发现App Crash了。查看了下日志，发现是**ClassNotFoundException**的异常，现场如下
>java.lang.ClassNotFoundException: Didn't find class "com.a.b.XListView"

顿感奇怪，这个类是我们自己定义的，并非第三方包，排除第三方SDK加载的问题。试了试其它手机，发现一部分手机可以正常运行，一部分手机会出现这个异常。百思不得其姐的时候，怀疑是不是XListView这个类写得有问题，虽然编译通过了，但是运行的时候无法正常找到并初始化。习惯性的用排除法，改了下名字，把XlistView改成AListView，居然不再报错。

顺着这个思路想下去，改个名字就可以Found到了，是不是分包的问题？把Apk解压一看，果然有两个classes.dex和classes2.dex，一定是把XListView分到了第二个classes2.dex里去了。但是为何分到第二个包里又会抛ClassNotFoundException异常呢？

赶紧 google 了一下MultiDex的相关知识，根据[官方的说法](https://developer.android.com/studio/build/multidex.html)，要实现MultiDex需要以下两个步骤

 - 在gradle配置对multiDex的支持
 - 修改manifest配置文件，指向MultiDexApplication类

Gradle文件的配置

		android {
		    compileSdkVersion 21
		    buildToolsVersion "21.1.0"
		
		    defaultConfig {
		        ...
		        minSdkVersion 14
		        targetSdkVersion 21
		        ...
		
		        // Enabling multidex support.
		        multiDexEnabled true
		    }
		    ...
		}
		
		dependencies {
		  compile 'com.android.support:multidex:1.0.0'
		}


manifest配置文件

	<?xml version="1.0" encoding="utf-8"?>
	<manifest xmlns:android="http://schemas.android.com/apk/res/android"
	    package="com.example.android.multidex.myapplication">
	    <application
	        ...
	        android:name="android.support.multidex.MultiDexApplication">
	        ...
	    </application>
	</manifest>


如果用的是自定义的Application的话,只需要继承**MultiDexApplication**,然后重写下面这个方法就行了

	 @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

既然我们的apk包里已经有了两个classes，说明**multiDexEnabled**已经开启了，然后再去看application方法，果然没有实现attachBaseContext这个方法.
>问题的根源看来找到了，原来是方法数据突破65536之后，gradle自动将dex文件分成了两个，而我们忘了实现MultiDex.install(this)，导致没有加载第二个classes2.dex，最终出现ClassNoFoundException的问题

这里顺便谈一下65536方法数据限制的问题
>Android平台的Java虚拟机Dalvik在执行DEX格式的Java应用程序时，使用原生类型short来索引DEX文件中的方法。这意味着单个DEX文件可被引用的方法总数被限制为65536

上面是网上通用的说法，也有一些[深入的分析](http://jayfeng.com/2016/03/10/%E7%94%B1Android-65K%E6%96%B9%E6%B3%95%E6%95%B0%E9%99%90%E5%88%B6%E5%BC%95%E5%8F%91%E7%9A%84%E6%80%9D%E8%80%83/). 总而言之，一个dex中出现的方法数不能超过65536，所以也称为**64K Methods**问题。

这个问题Google官方给出的建议是减少依赖库，或者通过ProGuard移除一些不使用的方法（这不是明显的胡扯吗），最后又给出Multidex的方案，就是上面列出来的。 

####本以为事情就此结束了

就在新版本开发完，准备要灰度上线的时候，突然发现App在Android 3.0以下的设备无法安装，真是晴天霹雳。

本来2.3.7以下系统已经给crash率贡献不少了，这时候又出现这个问题。报错信息相当简单，只有INSTALL_FAILED_DEXOPT ，Google了一下，反馈的人不少。

大概原因是Android 4.0之前存在两个linearAlloc 相关的bug（见 [Issue 22586](https://code.google.com/p/android/issues/detail?id=22586)和[Issue 78035](https://code.google.com/p/android/issues/detail?id=78035) )，其结果就是安装失败。。。。

还是先看看官方的说法[Limitations of the multidex support library](https://developer.android.com/studio/build/multidex.html#mdex-gradle) ,简单说来，就是multidex方案是有缺陷的，真是一坑不如一坑。网上看看其它资料，大概了解问题的原因.
>因为Dalvik linearAlloc的限制，如果请求大量内存可能导致崩溃。Dalvik linearAlloc是一个固定大小的缓冲区。在应用的安装过程中，系统会运行一个名为dexopt的程序为该应用在当前机型中运行做准备。dexopt使用LinearAlloc来存储应用的方法信息。Android 2.2和2.3的缓冲区只有5MB，Android 4.x提高到了8MB或16MB。当方法数量过多导致超出缓冲区大小时，会造成dexopt崩溃。

也就是说，虽然通过分包的方式解决了64k方法数的问题，但第一个dex如果方法数过多，在4.0之前的设备中仍有可能会超过Dalvik linearAlloc的限制，从而导致dexopt崩溃。

解压包一看，class2.dex果然比classes.dex要小非常多。看来是优先把classes.dex填到一定程度才把其余的类放到了class2.dex中。但类到底是如何分配的呢 ? 只有解压dex文件才能看到，此时，祭出神器**dex-method-counts-master**， 如下图

>dex-method-counts-master 可以用来查看classes的整个包结构


查询结果如下图

	Processing app_2006.apk
	Read in 64825 method IDs.
	Read in 3019 method IDs.
	<root>: 67844
	    <default>: 1
	    MTT: 1
	    android: 14154
	        accessibilityservice: 6
	        accounts: 6
	        animation: 45
	        app: 305
	        bluetooth: 2
	        content: 320
	            pm: 29
	            res: 75
	        database: 64
	            sqlite: 23
	        graphics: 369
	            drawable: 89
	                shapes: 2
	            pdf: 3
	        hardware: 23
	            display: 3
	            fingerprint: 11
	        location: 8
	        media: 277
	            session: 87
	        net: 84
	            http: 4
	            wifi: 16
	        opengl: 44
	        os: 228
	        preference: 1


可以看出，第一个dex包中有64825方法，第二个包中有3019个方法。而且，看样子类的分布是根据类路径名按字母排序的。这也验证了之前的想法，AListView被放在了第一个包中，XListView放到了第二个包中(不过也有一丝巧合的成份在里面)。

要解决前面说的Dalvik linearAlloc缓冲区不足的问题，根源在于第一个classes.dex中的方法数不能太多，方案就是尽量将一些不是那么重要的类放到第二个dex中。心想，Gradle这神器应该有这本事，赶紧Google一下。

功夫不负有心人，dx果然有一个参数设置**additionalParameters:set-max-idx-number=4800**，可以指定第一个dex中的方法数，只需要在gradle尾部添加几行

	afterEvaluate { 
	  tasks.matching { 
	    it.name.startsWith('dex') 
	  }.each { dx -> 
	    if (dx.additionalParameters == null) { 
	      dx.additionalParameters = []
	    }  
	    dx.additionalParameters += '--set-max-idx-number=48000' 
	  } 
	}

一试，不行，再看分包情况，好像没有生效。再查，原来这个参数在gradle 1.5版本已经删除了，看看项目的gradle版本已经到2.1.2了，怎么办怎么办。一查2.1版本的[DLS文档](http://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.DexOptions.html)，果然没这个选项。
再看，说是[2.2版本](http://google.github.io/android-gradle-dsl/2.2/com.android.build.gradle.internal.dsl.DexOptions.html)支持了，然后2.2现在还是Alpha版本，怎么办怎么办。

再Google之，终于发现原来已有大神写有分包工具，[DexKnifePlugin](https://github.com/ceabie/DexKnifePlugin)，可以手动指定main dex中要保留的类。使用方法很简单

先在gradle中添加插件

	apply plugin: 'com.ceabie.dexnkife'

然后配置上依赖

	 dependencies {
	        ....
	        classpath 'com.android.tools.build:gradle:2.1.0'  // or other
	        classpath 'com.ceabie.dextools:gradle-dexknife-plugin:1.5.2' // Experimental
	    }

在项目根目录下新建一个叫**dexknife.txt**的文件，配置内容如下:

	# 如果你想要某个包路径在maindex中，则使用 -keep 选项，即使他已经在分包的路径中.
	-keep com.a.b.android.**
	
	# 这条配置可以指定这个包下类在第二dex中，把独立的第三方包放到这里
	com.tencent.qqlive.**
	com.xiaomi.**
	
	
	# 使用.class后缀，代表单个类.
	#-keep android.support.v7.app.AppCompatDialogFragment.class
	
	# 不包含Android gradle 插件自动生成的maindex列表.
	-donot-use-suggest
	
	# 不进行dex分包， 直到 dex 的id数量超过 65536.
	# -auto-maindex
	
	# 显示miandex的日志.
	-log-mainlist


或者通过前面的**dex-method-counts-master**工具查看下哪个第三方库的方法数过多，如果依赖性不强的，都可以放到第二个dex文件里。

写好配置文件，编译安装，一次性通过。解压看看dex文件的大小，果然比之前要分布均匀了些，只要保证2.3.x手机可以正常安装即可，第二个dex文件也不能过大，否则可能出现ANR问题。

>至此为止，问题圆满解决，不过要注意的是两个dex文件中是否有类的依赖关系，不小心的话，可能会出现**classDefNotFound**的错误。

这里有两点要注意:

1. Gradle中的preDexLibraries = false这一项要设置为false
2. 如果开启了instant run功能记得关闭，否则会出现empty dex的错误。在Setting中搜索instant run，然后把勾都去掉即可

###总结一下
 - 问题源自64k 方法数，一旦项目到达一定程度是必定会遇到这个问题的
 - MultiDex只是一个简陋的方案，会带来其它的问题，Google也说了，他有很多[使用上的限制](https://developer.android.com/studio/build/multidex.html#mdex-gradle)
 - Dalvik LinearAlloc的Bug也是个大坑，只希望2.3.X系列的手机尽快完全淘汰掉
 - 这个方案也不是完美的，如果项目继续膨胀，将来可能出来第二个包过大而导致的ANR问题，插件化或者自制加载分包都是可实现的方案，但前期不必过度设计


###参考文献
1. [Configure Apps with Over 64K Methods](https://developer.android.com/studio/build/multidex.html#about)
1. [由Android 65K方法数限制引发的思考](http://jayfeng.com/2016/03/10/%E7%94%B1Android-65K%E6%96%B9%E6%B3%95%E6%95%B0%E9%99%90%E5%88%B6%E5%BC%95%E5%8F%91%E7%9A%84%E6%80%9D%E8%80%83/)
1. [android天花板问题以及解决方案分析](http://www.jianshu.com/p/d2d6e87e5a42)
1. [dex分包变形记](http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=193)
1. [Android傻瓜式分包插件及一些坑](http://www.ioclub.info/topic/579af782744bdeb962290211)
1. [Android Dex分包之旅](http://yydcdut.com/2016/03/20/split-dex/index.html#Build-Tool-是如何分包的)



