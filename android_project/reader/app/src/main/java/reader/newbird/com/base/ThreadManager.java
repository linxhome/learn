package reader.newbird.com.base;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class ThreadManager {
    private static ThreadManager mThreadManager;
    private static final int IO_THREAD_POOL_SIZE = 5;
    private Handler mMainHandler;
    private ExecutorService mIOThreadPool;

    public static ThreadManager getInstance() {
        if (mThreadManager == null) {
            synchronized (ThreadManager.class) {
                if (mThreadManager == null) {
                    mThreadManager = new ThreadManager();
                }
            }
        }
        return mThreadManager;
    }


    private ThreadManager() {

    }

    public ExecutorService getIOThread() {
        if (mIOThreadPool == null) {
            mIOThreadPool = Executors.newFixedThreadPool(IO_THREAD_POOL_SIZE);
        }
        return mIOThreadPool;
    }

    public void postUI(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        mMainHandler.post(runnable);
    }


    public void PostIOHandler(Runnable postRunnable) {
        ExecutorService service = getIOThread();
        service.execute(postRunnable);
    }

    public Future postIOHandle(Callable callable) {
        return getIOThread().submit(callable);
    }




}
