##基于RxJava的MVP实现第二弹##

上一篇[基于RxJava的一种MVP实现](https://github.com/linxhome/learn/blob/master/Android%20MVP%20with%20RxJava.md)中，简单介绍了一下通过响应式编程框架**RxJava**实现一种MVP的框架结构，因为重点在于如何实现MVP，所以对于RxJava的介绍还是比较粗浅的，后续的实践过程中也遇到了不少问题和更多更丰富的使用方法，这里统一再整理一下。

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

每订阅一个事件时，都可以通过addSubscribes方法加到统一的list中进行管理，而当Activity销毁的时候，就可以在OnDestory方法中进行事件的取消，如果是在Fragment中也是同样处理。

###问题三：RxJava功能强大的操作符###
RxJava另一大特性就是有着极其丰富的操作符，可以通过操作符的变换将各种各样不同的事件组合起来，极大的丰富它的应用场景。

简单举几个例子：
1、后一个网络请求需要等待前一个网络请求返回的结果
2、先从服务端加载，在本地进行排序或者其它处理后再返回给UI层
3、需要同时从DB、服务器获取数据拼接之后再返回给UI层
4、从服务端返回的数据格式需要转换成另一种格式才能返回给UI层

针对上面的三个例子，如果是传统的回调函数写法，很容易遇到callback Hell问题，导致代码不清晰，逻辑变得非常复杂。下面依次使用RxJava来实现上面三个场景

**场景1：**后一个网络请求需要等待前一个请求的返回结果

	Observable<String> firstOne = Observable.create(new Observable.OnSubscribe<String>() {
	    @Override
	    public void call(Subscriber<? super String> subscriber) {
	        //get String from network
	        String strFromNet = "hello";
	        subscriber.onNext(strFromNet);
	    }
	});
	
	Observable<Boolean> secondOne = Observable.create(new Observable.OnSubscribe<Boolean>() {
	    @Override
	    public void call(Subscriber<? super Boolean> subscriber) {
	        //get bool result from network
	        boolean boolFromNet = true;
	        subscriber.onNext(boolFromNet);
	    }
	});
	
	firstOne.flatMap(new Func1<String, Observable<Boolean>>() {
	    @Override
	    public Observable<Boolean> call(String s) {
	        if(s == null) {
	            return Observable.just(false);
	        } else {
	            return secondOne;
	        }
	    }
	}).subscribeOn(Schedulers.io()).subscribe(new Action1<Boolean>() {
	    @Override
	    public void call(Boolean aBoolean) {
	        //show the boolean result
	    }
	});



如上所示，我们将两个网络请求都定义为可被观察的Observable事件，第一个请求FristFromNet是从服务端获取字符串，第二个请求SecondOne是根据FirstOne的结果再发出另外一个请求，最终会将结果传递事件的所有订阅者。
这里的一个关键点就是使用到了RxJava的一个重要变换操作符flatMap ，关于flatmap的介绍可以参照以下:

**场景2**：先从服务端加载，在本地进行排序或者其它处理后再返回给UI层

	Observable.create(new Observable.OnSubscribe<String>() {
	    @Override
	    public void call(Subscriber<? super String> subscriber) {
	        //get String from network
	        String strFromNet = "hello";
	        subscriber.onNext(strFromNet);
	    }
	}).doOnNext(new Action1<String>() {
	    @Override
	    public void call(String s) {
	        //save string to local
	    }
	}).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
	    @Override
	    public void call(String resultStr) {
	        //show the boolean result
	    }
	});


这里用到的就是doOnNext操作符来对返回的结果进行一些额外的处理，处理完成后再交由UI层来处理。

**场景3：**需要同时从DB、服务器获取数据拼接之后再返回给UI层

	Observable<String> dbData = Observable.create(new Observable.OnSubscribe<String>() {
	    @Override
	    public void call(Subscriber<? super String> subscriber) {
	        //get String from network
	        String strFromNet = "hello";
	        subscriber.onNext(strFromNet);
	    }
	});
	
	Observable<String> netData = Observable.create(new Observable.OnSubscribe<String>() {
	    @Override
	    public void call(Subscriber<? super String> subscriber) {
	        //get bool result from network
	        subscriber.onNext("RxJava");
	    }
	});
	
	Observable.zip(dbData, netData, new Func2<String, String, String>() {
	    @Override
	    public String call(String s, String s2) {
	        //do the combine action
	        return s + s2;
	    }
	}).subscribe(new Action1<String>() {
	    @Override
	    public void call(String s) {
	        //show the final combined data
	    }
	});


这里使用到的关键操作符就是合并操作zip，与contact 操作符不同的地方在于它是严格按照顺序进行合并的。

**场景四：**从服务端返回的数据格式需要转换成另一种格式才能返回给UI层

	Observable<String> netData = Observable.create(new Observable.OnSubscribe<String>() {
	    @Override
	    public void call(Subscriber<? super String> subscriber) {
	        //get bool result from network
	        subscriber.onNext("RxJava");
	    }
	});
	
	netData.map(new Func1<String, Boolean>() {
	    @Override
	    public Boolean call(String netData) {
	        //对服务端返回的数据进行格式变换
	        return netData != null;
	    }
	}).subscribe(new Action1<Boolean>() {
	    @Override
	    public void call(Boolean aBoolean) {
	
	    }
	});


这里使用的是Map操作符来对服务端返回的数据进行格式上的转换，然后再将转换后的结果传递给UI层。

RxJava的操符还有很多，这里并不能完全的介绍所有的操作符，具体的操作符的使用方法可以参照下面的文档

 - [Rxjava实战](http://www.jianshu.com/p/64aa976a46be "Rxjava实战")
 - [RxJava操作符大全/](http://www.maplejaw.com/2016/08/31/RxJava%E6%93%8D%E4%BD%9C%E7%AC%A6%E5%A4%A7%E5%85%A8 "RxJava操作符大全/")


###问题四：如何将业务逻辑层与数据层分开进行单元测试###
我们知道Android的单元测试通常有两种，一种是依赖于真机环境的Instrumented tests，另一种只需要java环境的Local unit tests。关于这两种测试环境的搭建和使用，[官方文档](https://developer.android.com/studio/test/index.html)上有着比较详细的说明。

通常Instrumented tests需要真实的手机运行环境（模拟器也可以），测试的时候需要打一个完整的包，并且测试运行起来耗时也比较长，所以仅当测试代码强依赖于真机环境时才使用这种方式。而Local unit tests的测试方式无论是效率和速度都要明显高于前一种方式，但它的局限性在于无法调用任何Android Api，简单说来，就是只能测试纯Java代码。

通过之前我们搭建的RxJava+MVP的框架结构，UI层的行为抽象出来行为接口，业务层行为也抽象出来数据接口。 这样的话，除了解耦使代码和逻辑变得清晰这个优点之外，也使得我们的测试可以脱离android运行环境，实现纯Java代码级别的测试。 下面来简单说明一下，在RxJava+MVP的模式下如何进行单元测试。
	
**src/main/java 目录下的ICacheData.java**

	public interface ICacheData {
	    Observable<String> getStringValue(String key);
	}

**src/main/java 目录下的SpCacheDataImpl.java**

	public class SpCacheDataImpl implements ICacheData {

		public Observable<String> getStringValue(String key) {
	        return Observable.create(new Observable.OnSubscribe<String>() {
	            @Override
	            public void call(Subscriber<? super String> subscriber) {
					try {
					    String ret = SharedPreferencesUtil.readString(key, "");
					    subscriber.onNext(ret);
					} catch (Exception e) {
					    return null;
					}
	            }
	        });
	    }
	} 


**src/main/java CachePresenter.java**

	public class CachePresenter {
	    ICacheData dataSource;
	    ViewListener listener;
	
	    interface ViewListener {
	        void showString(String value);
	    }
	
	    public CachePresenter() {
	        dataSource = new SpCacheDataImpl();
	    }
	
	    public CachePresenter(ICacheData dataSource) {
	        this.dataSource = dataSource;
	    }
	
	    public void setListener(ViewListener listener) {
	        this.listener = listener;
	    }
	
	    public void getCacheFromSp(String key) {
	        dataSource.getStringValue(key);
	
	        Observable.create(new Observable.OnSubscribe<String>() {
	            @Override
	            public void call(Subscriber<? super String> subscriber) {
	                subscriber.onNext("let's go");
	            }
	        }).subscribe(new Action1<String>() {
	            @Override
	            public void call(String s) {
	                listener.showString(s);
	            }
	        }, new Action1<Throwable>() {
	            @Override
	            public void call(Throwable throwable) {
	                throwable.printStackTrace();
	            }
	        });
	    }
	}
	
	


**src/test/java/javaTest/SpCacheDataImplTest.java** 

	public class SpCacheDataImplTest implements ICacheData {
	
	    @Override
	    public Observable<String> getStringValue(String key) {
	        return Observable.create(new Observable.OnSubscribe<String>() {
	            @Override
	            public void call(Subscriber<? super String> subscriber) {
	                subscriber.onNext("world");
	            }
	        });
	    }
	} 

**src/test/java/javaTest/DataTest.java**
	
	//测试data层
    @Test
    public void testData() {
        ICacheData data = new SpCacheDataImpl();
        data.getStringValue("hello");

        data.getStringValue.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Assert.assertEquals(s,"world");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                Assert.assertTrue(false);
            }
        });
    }

	//测试presenter层
    @Test
    public void testPresenter() {
        CachePresenter presenter = new CachePresenter(new SpCacheDataImplTest());
        presenter.setListener(new CachePresenter.ViewListener() {
            @Override
            public void showString(String value) {
                Assert.assertNotNull(value);
                Assert.assertEquals(value,"world");
            }
        });
        presenter.getCacheFromSp("hello");
    }
	


从上面DataTest.java文件可以看到，除了UI层，其它业务逻辑或者数据层都可以通过纯java环境的Local unit tests来进行单元测试，涉及到依赖Android环境的代码，可以通过本地模拟数据或者通过Mock的形式来完成测试，这也极大的提高了测试的方便性和效率。

###总结###
以上是基于RxJava的MVP架构的一些具体实践，相比传统的MVC，这种方式增强了代码的灵活性，可以适应更为复杂的业务场景，并且可做到大部分代码的本地测试。当然，也可以发挥想象力，发掘出更多的使用方式。




