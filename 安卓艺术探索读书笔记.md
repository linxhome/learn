##Activity生命周期

- Activity周期的几种情况
- 第一次启动的回调情况:onCreate,onStart,onResume
- 打开新的Activity或者切回桌面的回调:onPause,onStop
- 再次回到原Activity回调:onRestart,onStart,onResume
- 按back键回退时的回调:onPause,onStop,onDestroy

onStart和onStop，onResume和onPause是配对出现的，StartStop表示是否可见，onResume和onPause是表示是否在前台


>Activity启动由Instrumentation来处理，通过binder向AMS发请求，AMS内部维护一个ActivityStack，AMS通过ActivityThread去同步Activity的状态从而完成生命周期的调用

###异常情况下的生命周期分析

当Acitivity被系统销毁后再次生成会经历如下阶段:
> onSaveInstanceState->onDestory->onCreate->onStart->onRestoreInstanceState

某些特定的View自己也拥有saveInstanceState和RestoreInstance方法，系统会通过委托模式逐级调用

Activity按优化级从高到低:

- 前台Activity
- 可见但非前台Activity
- 后台Activity

>activity设置为响应屏幕旋转事件时，会依次触发onSaveInstanceState和onRestoreInstanceState方法，如果设置 android:configChanges="orientation|screenSize"则不会触发上述两个方法，只触发onConfigurationChange方法

###Activity的LaunchMode

- standard:标准模式；每次start都新建一个，一个任务栈可以有多个
- singleTask:栈内复用；每个Task栈中只有一个实例，再次调用时，会把该实例调到栈顶，并调用onNewIntent方法
- singleTop:栈顶复用；如果在任务栈顶，则不会重建，同时调onNewIntent，onCreate和onStart不会被调用；如果不在栈顶，仍然会创建
- SingleInstance:加强的singleTask，这种模式启动的activity，只能单独的位于一个任务栈中

###activity的任务栈

- 任务栈可以通过lauchModel和taskAffinity两个参数配合使用，生成前台任务栈和后台任务栈
- adb shell dumpsys activity可以看到任务栈和activity的关系

###intentFilter
- 显式调用:明确的指定类名调用
- 隐式调用:只指定filter相关的信息即可调用

**intent匹配规则** 

1. 一个intent必须同时匹配action,catetory,data才算完全匹配
1. 可以有多个intent-filter，只要成功匹配一个即可成功启动activity
2. 一个filter中可以有多个action,只要匹配其中一个即算成功,intent中如果没有指定action,则算匹配失败
3. catetory要求所有的都要匹配成功，但intent中如果没有catetory也算成功；android.intent.category.DEFAULT必须设置
4. data可以匹配一个URI的链接，url的schema和type类型要对应起来



##IPC机制
进程间通信的场景：

- 一个应用因为某种原因需要采用多进程模式来实现
- 当前应用向其它应用获取数据

###Android中的多进程模式

- 通过process指定新的进程名称

###Serializable接口
- serialVersionUID不影响序列化过程，但是会影响反序列化过程
- 反序列化时，如果versionUID不一致的，无法进行反序列化，相当于version的版本号

###Parcelable接口
- 主要是用来通过intent和binder传递
- Parcelabel使用起来更麻烦，但是在传递中效率更高

###Binder
- Binder是Android中的一个类，实现了IBinder接口
- IPC角度来说，Binder是一种跨进程通信方式
- 还可以理解为一种虚拟的物理设备
- FrameWork层来看，连接各种Manager的桥梁

###Messenger
- 服务端建立Service，将Messenger与Service的binder绑定
- 客户端在Service启动后，将服务端对外提供的IBinder与Messenger绑定
简化版的AIDL调用，只是Messenger对其进行了封装

###AIDL
- 创建AIDL接口文件,java会自动生成代码，包含服务端和客户端的
- 不同应用调用的时候，需要复制完整的aidl文件，包结构需要保证完全一致
- 服务端需要自行实现AIDL接口，通常是通过Service来进行binder绑定
- 客户端从Service处拿到binder，然后用aidl的接口与binder进行绑定
- 客户端通过接口向服务端调用接口，服务端通过listener调用客户端，可以是双向的
- aidl定义的接口实现都运行在binder线程池中

###Content Provider
- 继承ContentProvider
- xml中声名provider
- 调用时直接使用getContentResolver
- SQLiteDatabase内部有线程同步

###Binder连接池
将所有的aidl放到一个Service中进行维护

##View的事件体系

###View位置:
- top,left,right,bottom:相对父容器的左上角和右下角坐标(最原始的值，不会变化)
- x,y:左上角的坐标，会改变
- translationX,translationY:左上角平移的坐标(x = left + translationX)

###MotionEvent和TouchSlop
TouchSlop指的是所能识别的最小滑动距离，可以在ViewConfiguration中配置和获取

###VelocityTracker和GestureDetector和Scroller
VelocityTracker:用来在onTouchEvent中追踪滑动的速度
GestureDetector：用来检测单击，滑动，长按，双击等行为




##View的工作原理

###ViewRoot相关

 - ViewRoot对应的是ViewRootImpl类，连接WindowManager和DecorView
 - 在ViewRoot中,经过了 performTraversals->mesure->layout->draw
 - 在ViewGroup中,经过了 performMeasure -> measure -> onMesuare(子元素的measure) 过程，View中只有measure
 - DecorView作为顶级view，一般包含一个LinearLayout两个部分，一个是title，一个是content
 - content是一个Framelayout
 

###MesureSpec

- MesaureSpec代表一个32位值，高2位代表SpecMode,低30位代表SpecSize
- SpecMode指的是测试模式，SpecSize指的是在指定测试模式下的规格大小
 
#####SpecMode的值 

 1. unspecified:父容器不限制View大小
 1. exactly:父容器已确定View的大小，对应的是match_parent和固定值这两种
 1. at_most:父容器指定了一个可用大小，不能大于这个值，对应的是wrap_content


> 对通常的View来讲，LayoutParams和父容器的约束共同决定了MeasureSpec的值; 对DecorView来讲，由窗口尺寸和自身的LayoutPram


子View通过getChildMeasureSpec获得MeasureSpec，它 = 父容器的MeasureSpec + 自己的LayoutParams + margin + padding


###自定义View

自定义View分四类

 - 继承View:适合于一些不规则的形状，需要自己重写wrap_content和padding布局
 - 继承Layout:自定义一些ViewGroup，需要自己重写layout和measure，也要合理的安排子元素的layout和measure
 - 继承特定的View：通常不需要处理wrap_content和padding
 - 继承特定的Layout:通常是各种Layout的组合使用

自定义View的注意事项

 - 处理好wrap_content和padding的问题
 - 尽量不要使用Handler
 - 如果有动画，及时停止。可参考onDetachedFromWindows和onTatchedToWindow
 - 如果有嵌套滑动，需要处理滑动冲突



##RemoteView 的应用

remoteView主要应用于通知栏和桌面小部件


##Android的Drawble

Drawable是一种可以在Canvas上进行绘制的抽象概念，通常指的是图片和颜色

部分drawable有宽高的概念，getIntrinsicWidth和getIntrinsicHeight，颜色的drawable无宽高

####Drawable的具体类型主要有

 1. BitmapDrawable
 2. ShapeDrawable
 3. LayerDrawable-叠加
 4. StateListDrawable --选择
 5. LevelListDrawable --权级
 6. TransitionDrawable
 7. InsetDrawable
 8. ScaleDrawable
 9. ClipDrawable




####ShapeDrawable详解
- shape属性有rectangle oval line ring四种类型
- cornor属性，只适用于rectangle
- gradient属性，与solid互相排斥，表示的是渐变效果
- solid属性:填充颜色
- stroke属性:描边
- padding属性：指的是drawable所在view的空白
- size属性:作为背景时无宽高


##Android的动画

动画效果：平移，缩放，旋转，透明度(Scale,Rotate,Translate,Alpha)

LayoutAnimation:可以作用于ViewGroup

Activity切换效果：overridePendingTrnsition方法

##Window与WindowManager

WindowManager是外界访问Window的窗口，Window的具体实现在WindowManagerService中，通过IPC交互
Window是view的直接管理者

Window有三种类型：应用Window，子Window和系统Window

应用Window对应着一个Activity，子Window不能独立存在，比如dialog就是一个子Window，系统Window是Toast和系统状态栏

Window层级(z-order)

- 应用Window:1-99
- 子Window:1000-1999
- 系统window:2000-2999


##Android的消息机制

- 消息队列的内部存储结构是使用的是单链表
- MessageQueue只是一个消息的存储单元
- Looper来处理消息，运行在handler创建的线程中
- 消息机制是由Looper，handler，和messageQueue一起协同工作
- ViewRootImpl对UI操作进行验证，checkThread（）方法

###ThreadLocal的工作原理

- ThreadLocal是一个线程内部的数据存储类，针对每一个线程存储本线程可见的数据
- 通过Looper.prepare()开启消息循环
- Looper可以退出，looper退出后通过handler send消息会失败
- 如果手动创建了looper，在退出后，应该调用quit进行终止消息循环，否则会一直等待
- Android的主线程就是ActivityThread



##Android的线程与线程池

Android中线程池来源来Java，主要是通过Executor来派生特定类型的线程池


###Androidk中的线程形态

- AsyncTask封装了thread和Handler，可以更方便的执行后台任务，但并不适合特别耗时的后台任务
- AsyncTask是串行执行任务，内部有两个线程池，一个用来排队，一个用来执行
- IntentService是一种特殊的Service，可用于执行后台耗时的任务，优先级又比单纯的线程高很多，不容易被系统杀死

###Android中的线程池

Android中线程池的概念来源于Java 中的executor，Executor是一个接口，真正的线程池实现为threadPoolExecutor

####ThreadPoolExecutor

- cachePoolSize:核心线程的数量
- maximumPoolSize：最大线程数
- keepAliveTime：非核心线程的最大闲置时间，超时的线程会被终止
- unit：超时时间单位
- workQueue：任务队列
- threadFactory：线程工厂

####线程池的种类

- FixedThreadPool:固定线程数
- CachedThreadPool：无固定线程数，只来有任务且没有闲置线程就会新建线程
- ScheduledThreadPool：定时重复线程
- SingleThreadExecutor：单个线程，顺序执行

##Bitmap的加载和Cache

listview错位问题，在加载bitmap前检查当前imageview使用的url与bitmap对应的url是否一致


##综合技术












###QA
>问题：activity的配置里加上configChages="orientation"则屏幕旋转的时候就不重建activity了？

答：需要配置screenSize才可以重现

>问题：如果子View设置的宽高大于父容器，会怎样？

答：

>问题：measure与OnMeasure内容的主要区别是什么?

答 measure处理自身的measure过程，OnMeasure处理子元素的measure过程

>ThreadLocal的应用场景有啥？除了Looper和Handler?




