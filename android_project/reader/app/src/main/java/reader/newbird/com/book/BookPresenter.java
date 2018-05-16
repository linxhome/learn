package reader.newbird.com.book;

import com.newbird.parse.config.FontConfig;

import java.util.List;

import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.base.ThreadManager;

/**
 * 提供书籍相关数据的Presenter层
 */
public class BookPresenter implements IGetBook {

    private IGetBook iBookGetListener;
    private BookManager mFileManager;

    public BookPresenter(IGetBook bookGetListener) {
        this.iBookGetListener = bookGetListener;
        mFileManager = new BookManager();
    }


    public void getBook() {
        ThreadManager.getInstance().PostIOHandler(() -> {
            final List<BookModel> books = mFileManager.getLocalBooks();
            ThreadManager.getInstance().postUI(() -> updateBooks(books));
        });
        //更新asset中的文件
        mFileManager.readAssetBook(this).executeOnExecutor(ThreadManager.getInstance().getIOThread());
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
