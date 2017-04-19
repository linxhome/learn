## Android 进程等级

>Android官方文档对于进程等级有比较详细的[说明](https://developer.android.com/guide/topics/processes/process-lifecycle.html)了。

这里把官方文档的主要内容提炼出来，简单介绍一下Android的进程等级的作用。首先，需要把进程等级与进程优先级(Process Priority)区分开来。在Android底层的Linux中，进程优先级通常由一个整形数字来表示，代表的意义通常与进程切换和CPU时间片的分配有关，而进程等级通常与系统内存回收相关。

首先提出一个问题
>App什么时候会用到进程等级 ？或者开发者什么时候需要关心进程的等级？

当我们提到进程的等级的时候，通常会与进程的生命周期联系起来。

根据官方说法，Android系统中App的进程生命周期长短并不直接由App自身来控制，相反，它由下面三个因素来决定

 - App中运行的能被系统感知到的组件
 - 这些组件对用户的重要性
 - 系统中总共可用的内存大小

对于开发者来讲需要了解Android中不同组件(Activity,Service,BroadCastReceiver)对App进程生命周期的影响，如果使用不当的话，则可能会导致不该被杀掉的进程被杀掉了。

>举一个反例，BroadcastReceiver中，启动一个线程去处理一条消息，然后立即return 。这样做的结果是，系统会认为消息处理已经结束，那么就有可能会杀掉BR所在的进程来回收内存，一旦进程被kill掉了，那么处理消息的线程也被中止。解决的方法是使用JobService来完成一项任务，让系统知道这个进程中仍然有重要的任务需要完成。

在内存不够时，系统会主动的Kill掉一些进程，而决定哪些进程应该被Kill的关键值就是进程的等级。
因此，回到第一个问题中，当开发者需要关心自己的App组件会不会随时被系统Kill掉的时候，就应该关注到App进程的等级问题。

###Android的进程等级是如何划分的
官方文档中有两个版本，英文文档和中文文档稍有不同。
>在中文文档中，分为五个层次

1. 前台进程 
2. 可见进程
3. 服务进程
4. 后台进程
5. 空进程

>在英文文档中，分为四个层次

 1. foregournd process(前台进程)
 2. visible process(可见进程)
 3. service process (服务进程)
 4. cached process

 可以看到两个版本前三个等级是完全一样的，我们先来看这三个进程等级的具体描述
 
#### 1.  前台进程 

 符合下面三个条件中的任一条件都会被系统认定为前台进程
 
 - 进程中运行着一个正在与用户进行交互的Activity(简单说就是走完了onResume方法) 
 - 进程中有一个BroardReceiver的onReceive方法正在运行
 - 进程中有一个正在运行的Service,并且它正在运行它生命周期相关的方法(onCreate,onStart,onDestroy）
 
 上面三条是官方的说法，当然，其它的资料也给了补充如下：
 
 - 如果一个service被bind到前台activity中，那么它也被赋予了同样的优先级，对contentProvider也适用
  
  通常来讲，处于前台的进程通常非常的少，当系统需要回收内存时，这些进程一般是最后才回收的。当然，制定这样的规则也非常容易理解，与用户交互的内容最为重要
 
#### 2.  可见进程

符合下面三个条件中的任一条件都会被系统认定为可见进程

 - 对用户可见，但又不在前台的activity（调用了onPause），比如前台的activity是透明的或者使用dialog启动了一个新的activity
 - 进程中运行着一个对当前使用者比较重要的service服务，比如动态墙纸或者输入法相关的service
 
 可见进程相比前台进程等级要低了一些，但对系统运行也是相当重要的，所以通常来说，如果不是为了保障前台进程正常运行所必须的话，通常不会轻易的回收它们。
 
#### 3.  服务进程
 
 顾名思义，服务进程中运行着一个service，并且已经调用了startService方法。 正常情况下，系统只有在前台进程和可见进程内存吃紧的时候才会Kill服务进程。启动service的onStartCommmand命令返回的参数能控制service的被kill掉之后的行为
  - START_STICKY : 表示希望系统可用的时候重启该服务
  - START_REDELIVER_INTENT ： 被系统kill之后重启时，重新获得上一次的intent
  - START_NOT_STICKY ：表示被kill也没关系
 
 Service运行一定时间后会自动降级（通常是30min或更久)，降级并缓存到一个LRU的列表中。这样在一定程序上避免了一些有内存泄漏或者因为某些错误占用了大量内存的service长时间运行，浪费系统资源。
 
#### 4. 后台和空进程(Cached Process)

在最新的En文档中，已经没有后台进程和空进程的说法，取而代之的是一个被称之为Cache Process的进程等级，从描述来讲，Cached Process应该是后台进程和空进程的综合体。Cached Process的特点是当前任务不需要，系统在内存不足时随时可能会被kill掉。通常在一个系统中，会存在多个Cached Process，只有极端情况下，所有的Cache Process都被杀死，系统内存仍然不够时，才会考虑Kill掉服务进程。

最常见的情况是，一个对用户不可见的Activity(比如已调用了OnStop方法)所在的进程就属于Cached Prcess。为了不影响用户体验，当它们被系统杀死后，用户想再次回到之前的App，能在新的进程里启动Activity并恢复之前已经保存的数据。

所有的Cached Process都会被保存在一个伪-LRU列表中，通常尾端的进程会被第一个被kill掉。至于其它的Kill规则，通常与平台实现相关，基本原则还是保证跟用户直接相关的进程最后被Kill。

####Tips

要明确的一点是，进程的等级所指的对象是进程，而通常对App来说，一个进程中可能运行着多种组件（Activity,Service)，所以当我们将通过其中一个组件提升了进程等级，那也就意味着这个进程内的其它组件也相应受到影响。如果对这些组件的运行等级有不同的要求，强烈建议把它们分到不同的进程中，让消耗大的进程能被及时回收。所以让组件运行在合适的优先级是一件需要慎重考虑的事情，有所取舍而不是一股脑将所有的进程设置为前台进程。


### 小结
 
知道了进程等级的分类，我们再来看看组件的生命周期内，进程的等级是如何变化的。

#### 1. Activity 
首先看一张从Api Doc扒出来的Activity的生命周期图

![MacDown Screenshot](https://developer.android.com/images/activity_lifecycle.png)

从图中可以看到

- Activity启动执行onResume，进程等级会变成前台进程
- 执行onPause之后，但仍然可见，进程等级处于可见进程 
- Activity执行onStop之后，会进入Cached进程


#### 2. Service

 - 运行在onCreate,onStartCommand,onDestory这几个方法时，Service所在进程是前台进程
 - 如果service被绑定以其它组件上，那么它的进程运行等级就不会低于这些组件
 - 可以通过startForeFround方法将Service的进程运行等级提升到前台进程
 
 
综上所述，对于Activity来说，它的运行等级是跟生命周期相关，而对于Service来说，它是可以通过bind或者startForeground方法来提升它的运行等级。而提升运行等级的作用更多的是保证重要的业务不会被随时Kill掉，从而提高App的体验和运行效率。当然，它还涉及到另外一个争议比较大的话题，那就是进程的保活。
 

