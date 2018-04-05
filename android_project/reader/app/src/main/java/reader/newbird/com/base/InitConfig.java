package reader.newbird.com.base;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import reader.newbird.com.config.PathConstant;

public class InitConfig {

    public static void initDir(Context context) {
        if(TextUtils.isEmpty(PathConstant.ROOT_DIR)) {
            File cache = context.getExternalCacheDir();
            File root = cache == null ? null : cache.getParentFile();
            if(root != null) {
                if(!root.exists() && !root.isDirectory()) {
                    root.mkdir();
                }
                PathConstant.ROOT_DIR = root.getAbsolutePath();
            }
        }

        File bookStoreContent = new File(PathConstant.ROOT_DIR + File.separator + PathConstant.LOCAL_BOOK_DIR_NAME);
        if(!bookStoreContent.exists() || !bookStoreContent.isDirectory()) {
            bookStoreContent.mkdir();
        }
        PathConstant.BOOK_STORE_DIR = bookStoreContent.getAbsolutePath();
    }



}
