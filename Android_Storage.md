#Android存储的探究小结

   开发Android原生app的同学或多或少都会遇到存储问题，因为Android本身就是一台小型的计算机，所以它的存储也跟传统的计算机结构一样，具有多层结构：从速度最快，容量最小的寄存器到容量最大，速度最慢的外部存储。 
  因为Android本身安全性的要求，开发原生app的同学大多数的时候谈到的存储都是外部存储 。 在android中，主要的外部存储被分成了三个部分: 
  - 第一个是最简单的**SharePreference**，可以直接存储 boolean , float ,int ,long,String 这些基础变量的封装类,使用起来简便快捷。实际上，SharedPreferences 将数据文件写在了手机内存私有的目录中该app的文件夹下。可以通过DDMS的【File Explorer】找到`data\data\(package_name)\shared_prefs`目录（如果使用真机测试，必须保存已root，否则因为权限问题无法进入data目录），存储格式是一个xml文件，如下图，

``` xml
 <!--   test.xml   -->
  <?xml version='1.0' encoding='utf-8' standalone='yes'>
  <map>
       <string name="name">小明</string>
</map>
```

- 一个是基于开源数据库**SQLite**的关系数据库, 数据文件 的存储位置是`/data/data/(package_name)/databases`
- 最后一个就是文件存储

今天我们要讨论的重点就是这个文件存储， 大家用日常开发中或多或少都会遇到使用文件存储的情况，通常如果只是作一些简单记录的话，可能还比较简单。但对一些重试依赖文件存储的APP来讲，或多或少都会遇到一些这方面的坑，今天我们就来总结一下这个问题，踩一下这些坑到底在哪里。 

####首先我们需要搞清一些存储上的概念:

> **Internal Storage **与 **Extermal Storage ** : 早期的Android系统手机都内置一块较小的存储板 即Internal Storage，  在Internal的基础上，有的时候会上一个外置插拔的存储卡，即External Storage。后来部分手机开始将最初定义的 Internal Storage分为Internal 和Extrenal两部分

**Internal 和External的差别**：Internal永远可用，External在mount之后才可用(这里就有个大坑，后面详说) ；Internal的内容随着App卸载内容会消失，但External仅会删除package_name路径下的文件；External中的存储的内容，可以被其它APP读取

**Internal、External与版本的关系**：4.4以后的手机上，External 区域中`Android/data/packageName`路径下的读写不需要申请任何权限，在非`packagename`路径中读写需要申请WRITE_EXTERNAL_STORAGE 权限；4.4之前的手机，无论哪个路径下，写权限都需要申请WRITE_EXTERNAL_STORAGE 。 所以当你要读写External中的文件时，最好是全部申请权限

> **Primary Storage** 与 **Secondary Storage** : 因前面提到过的，部分手机把内置存储也分成的Internal 和Exernal两部分，那么这时候再算上SD卡的存储区域，就有两个External。 所以通知把从Internal中分出来的那部分External存储称为**Primary Storage**，插入的外置存储卡称之为**Secondary Storage**

从上面可以看出，可用存储最多的情况会出现三个，一个是传统的Internal Storage，一个是从Internal中分而来的Priamry Storage，一个是代表外置存储卡的Secondary区域。 

External区域虽然读写方便，权限要求小，但有时会出现不可用的情况，或者存储空间不被保护，可被其它APP恶意篡改的问题，所以大家在使用过程中要格外小心，一不小心就会出现大问题。

> **Physical Primary **与**Emulated Primary **: 最原始的Android手机只存在机身自带的Internal External，这时候只有一个主存储，并且是Physical的。 当出现Internal中分出一部分给External Storage之后，这个空间就是模拟上去的，所以你看到主存储路径常常有`emulated`字样

在Android6.0之后，又出现了一种新的存储方式

> **Adoptable Storage **:  由于External的固有缺陷，6.0之后，多出一种'Adoptable'的存储方式。 当Android系统`Adopt`了一块External存储区域之后，它会被视为Internal Storage，同时会被格式化和加密。 格式化是使用GPT分区，存储上限为9ZB

总而言之，Android在开发过程中面临着不同版本的Android系统，不同的手机硬件厂商和各种定制ROM，因此对存储的使用方式也可能千差万别。

####各种存储路径的获取
前面提到的几个存储区域，Android系统提供了不同的方法，而且随着Android系统的不断更新，这些方法获取的内容也在不断的变化，下面我们来详细的讨论这个问题。 

首先我们将所有可能需要用到的方法都列举出来

**Internal 存储使用方法**
- ContextWrapper.getFilesDir

**主存储路径获取方法**
- Environment.getExternalStorageDirectory()
-  Context.getExtrenalFilesDir()

**副存储路径获取方法**
- context.getExtrenalFilesDirs (注意多了个s)
- Context.getSystemService(STORAGE_SERVICE)

这些方法在不同的Android系统下有不同的表现，下面我们统计一下从4.0到6.0系统下这些方法对应的各种返回值


手机型号：CoolPad
系统版本：2.3.5
(内)ContextWrapper.getFilesDir : `/data/data/com.application.my/files`
(主)Environment.getExternalStorageDirectory() :`/mnt/sdcard`
(主)Context.getExternalFilesDir() : null
(副)Context.getExtrenalFilesDirs : 不可调用
(副)Context.getSystemService(STORAGE_SERVICE): 


手机型号：MI2
系统版本：4.1.1
(内)ContextWrapper.getFilesDir : `/data/data/com.application.my/files`
(主)Environment.getExternalStorageDirectory() :`/storage/sdcard0`
(主)Context.getExternalFilesDir() : null
(副)Context.getExtrenalFilesDirs : 不可调用
(副)Context.getSystemService(STORAGE_SERVICE): `/storage/sdcard0`


手机型号：MI 4LTE
系统版本：4.4.4
(内)ContextWrapper.getFilesDir : `/data/data/com.application.my/files`
(主)Environment.getExternalStorageDirectory() :`/storage/emulated/0/ `
(主)Context.getExternalFilesDir() : `/storage/emulated/0/Android/data/com.application.my/files`
(副)Context.getExtrenalFilesDirs : `/storage/emulated/0/Android/data/com.application.my/files`
(副)Context.getSystemService(STORAGE_SERVICE): `/storage/emulated/0/`


手机型号：MEIZU PRO 5
系统版本：5.1
(内)ContextWrapper.getFilesDir : `/data/data/com.application.my/files`
(主)Environment.getExternalStorageDirectory() :`/storage/emulated/0/ `
(主)Context.getExternalFilesDir() : `/storage/emulated/0/Android/data/com.application.my/files`
(副)Context.getExtrenalFilesDirs : `/storage/emulated/0/Android/data/com.application.my/files`
(副)Context.getSystemService(STORAGE_SERVICE): `/storage/emulated/0` 和 `/storage/sdcard1`


手机型号：红米
系统版本：6.0.1
(内)ContextWrapper.getFilesDir : `/data/user/0/com.application.my/files`
(主)Environment.getExternalStorageDirectory() :`/storage/emulated/0/`
(主)Context.getExternalFilesDir() : `/storage/emulated/0/Android/data/com.application.my/files`
(副)Context.getExtrenalFilesDirs : `/storage/emulated/0/Android/data/com.application.my/files`  和   `/storage/3433-3862/Android/data/com.application.my/files`
(副)Context.getSystemService(STORAGE_SERVICE): `/storage/emulated/0` 和 `/storage/3433-3862`
