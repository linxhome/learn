package reader.newbird.com.base;

import android.app.Application;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class ReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ReaderContext.getInstance().init(getApplicationContext());
    }
}
