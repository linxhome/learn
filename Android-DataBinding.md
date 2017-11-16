##Android DataBinding的源码跟读

Data-binding 是google官方提供的一种数据绑定技术，目的主要是帮助开发者提高开发效率，大大降低View的维护成本，对代码结构的提升也很有帮助。

虽然用起来非常方便，但如果对data-binding的运作机制不了解的话，很容易踩到坑。特别是通过databinding设置了某些值却不知道什么时候生效，或者搞不清楚为什么没有生效，碰到这些情况就比较麻烦了，也是很多人弃坑的原因。

本文从databinding的源码出发，带大家了解几个databinding开发过程中需要了解的问题。

###针对Databinding执行几个关键的问题：

- DataBindingUtil.setContentView与activity.setContentView有什么关系和区别
- executePendingBindings 使用场景是什么
- 通过ViewModel.setValue的值最终是如何生效的，相对于直接的view.setValue有多久的延迟


>ps: 本文默认您已经很熟悉data-binding 的使用方法，所以后文不会涉及到Databinding的使用方法


先构造一个最简单的data-binding 的项目

	@Overide   	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentMainBinding databinding = DataBindingUtil.setContentView(this,R.layout.content_main);
        UserModel model = new UserModel();
        databinding.setUser(model);
    }



###一切的开始: 不一样的setContentView
	
从例子中可以看到，使用DataBinding的第一句代码就是
 
	DataBindingUtil.setContentView(this,R.layout.content_main);

databinding重写了setContentView方法用来替换activity内置的setContentView方法

我们知道 activity的SetContentView 方法的主要解析是解析layoutId指向的xml文件并生成View，将生成的View添加到PhoneWindow中的DecorView。那么DatabindingUtils重新定义了这个方法，又做了那些工作呢？ 

分析源码，如下所示：

	//DataBindingUtils.java文件
	public static <T extends ViewDataBinding> T setContentView(Activity activity, int layoutId,
            DataBindingComponent bindingComponent) {
        activity.setContentView(layoutId);
        View decorView = activity.getWindow().getDecorView();
        ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
        return bindToAddedViews(bindingComponent, contentView, 0, layoutId);
    }

DataBindingUtil重写的setContentView主要做了两件事情：

1. 调用activity.setContentView来解析xml生成View
1. 调用bindToAddedViews 获得一个与页面layout对应的ViewDataBinding子类

主要看第二条，ViewDataBinding子类是如何获得的，下面是这一过程的调用链，具体的代码就不贴了

 - DataBindingUtils.setContentView()
 - DataBindingUtils.bindToAddedViews()
 - DataBindingUtils.bind()
 - sMapper.getDataBinder()

看到最后一步，通过sMapper获得相应的ViewDataBinding子类。sMapper是一个DataBinderMapper类的对象，管理了一个类似Map的结构，key是layoutId,Value就是对应的ViewDataBinding子类,来看它的getDataBinder方法

	//DataBindingUtils.java文件	
	public android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent 	bindingComponent, 	android.view.View view, int layoutId) {
        switch(layoutId) {
                case com.example.administrator.databindingapplication.R.layout.content_main:
                    return com.example.administrator.databindingapplication.databinding.ContentMainBinding.bind(view, bindingComponent);
        }
        return null;
    }

返回的就是ContentMainBinding对象，它是ViewDataBinding的子类。这个类是怎么来的呢，其实在编写databinding相应的layout文件时，IDE就会自动生成一个名称结构相同的ViewDataBinding的继承类。在本例中，由content_main.xml自动生成一个叫做ContentMainBinding.java的文件。

ContentMainBinding.bind()的这个静态方法其实是new了一个ContentMainBinding对象返回，看这个子类的构造器函数

	//ContentMainBinding.java
	public ContentMainBinding(@NonNull android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        super(bindingComponent, root, 2);
        final Object[] bindings = mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds);
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.TextView) bindings[1];
        this.mboundView1.setTag(null);
        this.mboundView2 = (android.widget.TextView) bindings[2];
        this.mboundView2.setTag(null);
        this.mboundView3 = (android.widget.Button) bindings[3];
        this.mboundView3.setTag(null);
        setRootTag(root);
        // listeners
		invalidateAll();
    }
    
这个构造器函数里面做了两件事情

 1. 通过mapBindings方法获得一个view[]的数组，然后赋值给了ViewDataBinding子类的Vi
 2. 调用invalidateAll方法

从上面的代码可以看到，ContentMainBinding这个子类其实是接管了对View的控制权，以前我们在activity中维护的View变量都被转移到这个类里面来进行操作。也就是说databinding替你做完了一大堆的findviewById的工作并将view统一放到ContentMainBinding这样的子类中进行管理。

最后一句调用了invalidatAll()这个方法

	//ContentMainBinding.java文件 	
	@Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
        }
        requestRebind();
    }

重置了脏字标记位，然后调用了调用了父类ViewDataBinding中实现的requestRebind()

	//ViewDataBinding.java文件
	boolean USE_CHOREOGRAPHER = SDK_INT >= 16	
	protected void requestRebind() {
        if (mContainingBinding != null) {
            mContainingBinding.requestRebind();
        } else {
            synchronized (this) {
                if (mPendingRebind) {
                    return;
                }
                mPendingRebind = true;
            }
            if (USE_CHOREOGRAPHER) {
                mChoreographer.postFrameCallback(mFrameCallback);
            } else {
				// 这个分支可以忽略了
                mUIThreadHandler.post(mRebindRunnable);
            }
        }
    }

这一段代码里首先定义了USE_CHOREOGRAPHER这个常量，我们知道，Android系统从4.1(API 16)开始加入Choreographe。Choreographer主要作用是协调动画，输入和绘制的时间，它从显示子系统接收定时脉冲（例如垂直同步），然后安排渲染下一个frame的一部分工作，FrameCallback是在下一个frame被渲染时触发的接口类。关于Choreographer的相关内容，大家可以参考这篇文章--[简书：Android Choreographer 源码分析](http://www.jianshu.com/p/996bca12eb1d)

简单说来，通过postFrameCallback可以在下一帧渲染前做一些回调中指定的操作，那么我们继续到底做了那些事情
 
	//ViewDatabinding.java文件 	
	mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            mRebindRunnable.run();
        }
    };

继续看mRebindRunnable

	//ViewDatabinding.java文件 	
	private final Runnable mRebindRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                mPendingRebind = false;
            }
            processReferenceQueue();

            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                // Nested so that we don't get a lint warning in IntelliJ
                if (!mRoot.isAttachedToWindow()) {
                    // Don't execute the pending bindings until the View
                    // is attached again.
                    mRoot.removeOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                    mRoot.addOnAttachStateChangeListener(ROOT_REATTACHED_LISTENER);
                    return;
                }
            }
            executePendingBindings();
        }
    };

顺着executePendingBindings方法一路看下去，最终执行到了executeBindings()，这个方法在ViewDataBinding.java中是abstract修饰的抽象方法，具体实现在ViewDataBinding子类中，也就是例子中的ContentMainBinding.java文件.

看到这里，大家应该就明白了，构造函数中的invalidateAll的实现是通过Choreographer.postFrameCallback指定在下一帧执行当前页面对应的executeBindings。

>下一帧到底是延迟多久呢？我们知道android系统的刷新频率是60Hz，那么每一帧的间隔就是16ms。延迟也就最多16ms了。

这一阶段的整个调用过程如下

1. "android.databinding.DataBindingUtil.setContentView(DataBindingUtil.java:279)"
1. "android.databinding.DataBindingUtil.bindToAddedViews(DataBindingUtil.java:299)"
1. "android.databinding.DataBindingUtil.bind(DataBindingUtil.java:185)"
1. "android.databinding.DataBinderMapper.getDataBinder(DataBinderMapper.java:11)"
1. "com.example.dai.databinding.ContentMainBinding.bind(ActivityDataBindingBinding.java:226)"
1. "com.example.dai.databinding.ContentMainBinding.<init>(ActivityDataBindingBinding.java:35)"
1. "com.example.dai.databinding.ContentMainBinding.invalidateAll(ActivityDataBindingBinding.java:43)"
1. "android.databinding.ViewDataBinding.requestRebind(ViewDataBinding.java:452)"
2. "android.databinding.ViewDataBinding.executePendingBindings()"
3. "com.example.dai.databinding.ContentMainBinding.executeBindings()"


### 神特码的executeBindings执行过程 ###

executeBindings是整个databinding过程比较重要的一环，executeBindings方法的实现是在子类中，看下面代码
	
	//ContentMainDataBinding.java 
 	@Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        java.lang.String userLastNameGet = null;
        android.databinding.ObservableField<java.lang.String> userFirstName = null;
        com.example.administrator.databindingapplication.model.SecondModel user = mUser;
        java.lang.String userFirstNameGet = null;
        android.databinding.ObservableField<java.lang.String> userLastName = null;
        android.view.View.OnClickListener userChangeClickAndroidViewViewOnClickListener = null;

        if ((dirtyFlags & 0xfL) != 0) {
            //第一步：完成与ObservableField的监听绑定
			if ((dirtyFlags & 0xdL) != 0) {
			       if (user != null) {
                        // read user.firstName
                        userFirstName = user.firstName;
                    }
                    updateRegistration(0, userFirstName);


                    if (userFirstName != null) {
                        // read user.firstName.get()
                        userFirstNameGet = userFirstName.get();
                    }
            }
            if ((dirtyFlags & 0xeL) != 0) {

                    if (user != null) {
                        // read user.lastName
                        userLastName = user.lastName;
                    }
                    updateRegistration(1, userLastName);


                    if (userLastName != null) {
                        // read user.lastName.get()
                        userLastNameGet = userLastName.get();
                    }
            }
            if ((dirtyFlags & 0xcL) != 0) {
                    if (user != null) {
                        // read user::changeClick
                        userChangeClickAndroidViewViewOnClickListener = (((mUserChangeClickAndroidViewViewOnClickListener == null) ? (mUserChangeClickAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mUserChangeClickAndroidViewViewOnClickListener).setValue(user));
                    }
            }
        }
        
		//第二步对View的属性进行初始化赋值
        if ((dirtyFlags & 0xdL) != 0) {	
            android.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, userFirstNameGet);
        }
        if ((dirtyFlags & 0xeL) != 0) {
            android.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView2, userLastNameGet);
        }
        if ((dirtyFlags & 0xcL) != 0) {
            this.mboundView3.setOnClickListener(userChangeClickAndroidViewViewOnClickListener);
        }
    }


逻辑写的很清楚，针对每一个View属性，做了两件事，一个是完成与ObservableField数据的监听绑定，一个是对View的属性进行初始化赋值。初始化赋值的逻辑很简单，我们着重看第一步数据的监听绑定。

	//第一步
	updateRegistration(0, userFirstName);

	//CREATE_PROPERTY_LISTENER 是一个listener的构造器
	protected boolean updateRegistration(int localFieldId, Observable observable) {
		//第二步        
		return updateRegistration(localFieldId, observable, CREATE_PROPERTY_LISTENER);
    }

	private static final CreateWeakListener CREATE_PROPERTY_LISTENER = new CreateWeakListener() {
        @Override
        public WeakListener create(ViewDataBinding viewDataBinding, int localFieldId) {
            return new WeakPropertyListener(viewDataBinding, localFieldId).getListener();
        }
    };

	//做一些判空检查然后调用registerto进行绑定
	private boolean updateRegistration(int localFieldId, Object observable,
            CreateWeakListener listenerCreator) {
        if (observable == null) {
            return unregisterFrom(localFieldId);
        }
        WeakListener listener = mLocalFieldObservers[localFieldId];
        if (listener == null) {
            //第三步
			registerTo(localFieldId, observable, listenerCreator);
            return true;
        }
        if (listener.getTarget() == observable) {
            return false;//nothing to do, same object
        }
        unregisterFrom(localFieldId);
        registerTo(localFieldId, observable, listenerCreator);
        return true;
    }

	//根据fieldId创建一个listener，通过setTarget完成数据的绑定
	protected void registerTo(int localFieldId, Object observable,
            CreateWeakListener listenerCreator) {
        if (observable == null) {
            return;
        }
        WeakListener listener = mLocalFieldObservers[localFieldId];
        if (listener == null) {
            listener = listenerCreator.create(this, localFieldId);
            mLocalFieldObservers[localFieldId] = listener;
        }
        //第四步
		listener.setTarget(observable);
    }

	public void setTarget(T object) {
        unregister();
        mTarget = object;
        if (mTarget != null) {
			//第五步：添加监听            
			mObservable.addListener(mTarget);
        }
    }

	@Override
    public void addListener(Observable target) {
		第六步：这里target是ObservableField,this指代的是刚才根据fieldId创建的listener        
		target.addOnPropertyChangedCallback(this);
    }

	@Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (mCallbacks == null) {
                mCallbacks = new PropertyChangeRegistry();
            }
        }
        //第七步
		mCallbacks.add(callback);
    }

看起来七个步骤挺复杂，其实总结起来就是下面两点：

 1. 根据View获得fieldId，然后创建一个WeakReference的Listener
 1. 将这个listener添加到ObservableField的mCallbacks中

弯弯绕绕很多圈，其实就是将View的属性包装成listener，添加到了ObservableField的mCallback列表中。mCallback是ObservableField维护的一个监听的列表，当值发生变化时就挨个回调mCallback里面保存的listener。这个模式看起来是不是很熟悉，对了，就是观察者模式。

excutePendingBinding这一部分就算是走读完了，下面我们就来说一下是如何通过ObservableField.setXXX来修改View的相应属性值的。

###ObservableField.setXXX 的执行过程

从之前的绑定过程其实可以猜出这一部分的内容，前面提到在executebinding的过程中把View和ObservableField进行了监听绑定，所以当通过ObservableField.setXXX进行值的修改时，就会通过绑定的listener来通知View来修改属性。

整个调用流程如下：

1. "android.databinding.ObservableField.set(ObservableField.java:66)"
1. "android.databinding.BaseObservable.notifyChange(BaseObservable.java:49)"
1. "android.databinding.CallbackRegistry.notifyCallbacks(CallbackRegistry.java:91)"
1. "android.databinding.CallbackRegistry.notifyRecurse(CallbackRegistry.java:145)"
1. "android.databinding.CallbackRegistry.notifyRemainder(CallbackRegistry.java:169)"
1. "android.databinding.CallbackRegistry.notifyFirst64(CallbackRegistry.java:122)"
1. "android.databinding.CallbackRegistry.notifyCallbacks(CallbackRegistry.java:201)"
1. "android.databinding.PropertyChangeRegistry$1.onNotifyCallback(PropertyChangeRegistry.java:24)"
1. "android.databinding.PropertyChangeRegistry$1.onNotifyCallback(PropertyChangeRegistry.java:28)"
1. "android.databinding.ViewDataBinding$WeakPropertyListener.onPropertyChanged(ViewDataBinding.java:1224)"
1. "android.databinding.ViewDataBinding.access$500(ViewDataBinding.java:51)"
1. "android.databinding.ViewDataBinding.handleFieldChange(ViewDataBinding.java:427)"
1. "android.databinding.ViewDataBinding.requestRebind(ViewDataBinding.java:453)"


这里需要注意的一点是，当WeakPropertyListener.onPropertyChanged 被调用时，databinding并没有直接的去修改View的属性值，而是通过修改dirtyFlag值，然后发出一个requestRebind的请求，最后重新执行executeBinding来完成的。下面我们着重看这一块。

从第12步开始，先看ViewDataBinding.handleFieldChange()

	private void handleFieldChange(int mLocalFieldId, Object object, int fieldId) {
		// 第一步：相应相应field 值的变化        
		boolean result = onFieldChange(mLocalFieldId, object, fieldId);
        if (result) {
            //第四步，通过mChoreographer.postFrameCallback在下一帧调用executeBinding方法
			requestRebind();
        }
    }

	//第二步，根据不同的fieldId，做不同的相应	
	@Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeUserFirstName((android.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 1 :
                return onChangeUserLastName((android.databinding.ObservableField<java.lang.String>) object, fieldId);
        }
        return false;
    }

	//第三步，修改当前fieldId对应的mDirtyFlags位	
	private boolean onChangeUserFirstName(android.databinding.ObservableField<java.lang.String> UserFirstName, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;

    }

	@Override
    protected void executeBindings() {
        long dirtyFlags = 0;
         ....
        
		//第五步，判断dirty位有没有变化，然后修改相应的View的属性值
        if ((dirtyFlags & 0xdL) != 0) {	
            android.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, userFirstNameGet);
        }
        ...
    }


这样整个ObservableField.setValue的过程我们就基本搞清楚了，与传统用法相比，databinding带来的便利性还是毋庸置疑的。但仍需要注意的一点是，databinding中修改属性值之后，至少有一帧（16ms）的延迟，这是它本身机制决定的。不要小看这一点，知道这个原理，能避免很多使用上的坑，是对于executePendBinding这个方法也知道什么时候该调，什么时候不需要调用。

上面分析的，只是databinding的主线流程，databinding本身还有其他的内容，比如双向绑定，databinding插件，自动代码的生成等等，有时间的话，下次在逐个来分析下源码。
