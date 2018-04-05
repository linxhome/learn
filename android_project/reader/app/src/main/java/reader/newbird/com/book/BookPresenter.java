package reader.newbird.com.book;

import android.os.HandlerThread;

import java.util.List;
import java.util.concurrent.Callable;

import reader.newbird.com.base.ThreadManager;

/**
 * 提供书籍相关数据的Presenter层
 */
public class BookPresenter implements IGetBook {

    private IGetBook iBookGetListener;
    private BookFileManager mFileManager;

    public BookPresenter(IGetBook bookGetListener) {
        this.iBookGetListener = bookGetListener;
        mFileManager = new BookFileManager();
    }

    public void getBook() {
        ThreadManager.getInstance().PostIOHandler(() -> {
            final List<BookModel> books = mFileManager.getLocalBooks();
            ThreadManager.getInstance().postUI(() -> updateBooks(books));
        });
        //更新asset中的文件
        mFileManager.initAssetBook(this).executeOnExecutor(ThreadManager.getInstance().getIOThread());
    }

    @Override
    public void updateBook(BookModel data) {
        if(iBookGetListener != null) {
            iBookGetListener.updateBook(data);
        }
    }

    @Override
    public void updateBooks(List<BookModel> list) {
        if(iBookGetListener != null) {
            iBookGetListener.updateBooks(list);
        }
    }

    public void destroy() {
        this.iBookGetListener = null;
    }
}
