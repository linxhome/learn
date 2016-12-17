##基于RxJava的MVP实现第二弹##


上一篇中，简单介绍了一下通过响应式编程框架**RxJava**实现一种MVP的框架结构，因为重点在于如何实现MVP，所以对于RxJava的介绍还是比较粗浅的，后续的实践过程中也遇到了不少问题和一些新的发现，这里统一再整理一下。

###问题一：RxJava的异常处理###

当我们在Presenter层使用RxJava对业务逻辑进行调用时，业务层抛出的异常怎么办？ 

	  Observable.create(new Observable.OnSubscribe<String>() {
	        @Override
	        public void call(Subscriber<? super String> subscriber) {
				//Todo get data from model 
	            throw new RuntimeException("device is not connected");
	        }
	    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
	        @Override
	        public void call(String s) {
	
	        }
	    });

比如上面代码中，在call方法中的调用里，抛出了一个运行时异常，如果完全不处理的话，Rx会抛出一个名为**rx.exceptions.OnErrorNotImplementedException**的异常，意思就是尚未处理的Exception。

如果是旧的编码方式，可能更多的是直接在业务层内部对可能发生的所有Exception进行try catch捕捉。

但实践中发现这种旧的方法有两处不足:一是业务层内部只要涉及到IO相关的操作，可能随处都是try catch；二是如果完全在底层通过try catch吞掉异常，那么发生排查异常问题的时候需要层层递进，并且后端吞掉的异常前端感知不到，无法给出相应的处理和提示。

所以RxJava给我们提供了一个异常处理的方式。翻阅RxJava的文档就会发现，subscribeOn方法本身有很多重载，其中一个就是

**subscribe(Action1<? super T> onNext, Action1<Throwable> onError)**

其中onNext就是对订阅事件发出subscriber.onNext()的响应，而onError就会捕捉一切来自订阅事件抛出的异常。具体的使用方法如下：
  
	 Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //Todo get data from model
                throw new RuntimeException("network is not connected");
            }
        }).subscribeOn(Schedulers.immediate()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

如上所示，我们在subscribeOn方法的第二个参数Action1中对异常进行处理。如果排查bug时，只需在此处打印出Exceptio的栈信息就能很快定位到具体代码行数；如果前端需要异常的具体信息，这里就可以对异常进行分类处理，然后决定前端的显示，比如


###问题二：如何与Activity的生命周期协同###

我们知道Activity是有生命周期的，也就是说它不会一直存在内存中，是会Create和Destory的。什么时候生成，什么时候消失取决于用户的操作，而另一方面，当我们进行IO操作的时候，IO回调并不知道activity是否已经被回收。这时候就需要有一个同步数据的方法来告诉IO回调，activity已经被销毁，不需要进行后面的操作了，否则就会产生很多意想不到的问题，比如内存泄漏，比如空指针问题。 

对于RxJava来说，当我们订阅一个事件后，如果在业务数据（通常是从网络获取）返回之前就已经销毁这个activity的话，就可能会抛出一些异常，所以需要在销毁activity的时候进行取消订阅的操作。 具体作法如下所示:

	//MainActivity.java
    Subscription subscription ;
    public void getData() {
        subscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //Todo get data from network
            }
        }).subscribeOn(Schedulers.immediate()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

可以看到，在onDestory的时候，通过unsubscribe进行了取消订阅的操作。当然这里写在activity里是为了演示，按照前面的MVP规范，当然是写在Presenter层更好。

最佳的实践是写一个通用的BasePresenter层，统一对事件的订阅和取消进行管理，如下

	//BasePresenter.java
	public abstract class BasePresenter {
	    CompositeSubscription subscribeList = new CompositeSubscription();
		    
	    //Rx 的错误处理
	    Action1<Throwable> defaultErrorAction() {
	        return new Action1<Throwable>() {
	            @Override
	            public void call(Throwable throwable) {
	                new LogUtil().e("RxJava Call Exception" + throwable.toString());
	                throwable.printStackTrace();
	            }
	        };
	    }
	
	    public void destroy() {
	        if (subscribeList.hasSubscriptions()) {
	            subscribeList.unsubscribe();
	            subscribeList.clear();
	        }
	    }
	
	    void addSubscribes(Subscription sub) {
	        if (sub != null) {
	            subscribeList.add(sub);
	        }
	    }
	
	 }

每订阅一个事件时，都可以通过addSubscribes方法加到统一的list中进行管理，而当Activity销毁的时候，就可以在OnDestory方法中进行事件的取消。


###问题三：RxJava其它的强大功能，操作符###



###问题四：如何将业务逻辑层与数据层分开进行单元测试###


###问题五：OnCompelte的使用场景是如何###

