package reader.newbird.com.base.thread;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class ThreadManager {
    private static ThreadManager mThreadManager;
    private Handler mMainHandler;

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

    public void postUI(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        mMainHandler.post(runnable);
    }


}
