package reader.newbird.com.base;

import android.content.Context;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class ReaderContext {
    private static ReaderContext mReader;
    private Context mContext;

    public static ReaderContext getInstance() {
        if (mReader == null) {
            synchronized (ReaderContext.class) {
                if (mReader == null) {
                    mReader = new ReaderContext();
                }
            }
        }
        return mReader;
    }

    public void init(Context context) {
        mContext = context;
    }

    public Context get() {
        return mContext;
    }
}
