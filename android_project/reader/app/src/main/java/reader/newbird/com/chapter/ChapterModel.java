package reader.newbird.com.chapter;

import java.util.List;

import reader.newbird.com.book.BookModel;

/**
 * 每个章节的相关信息
 */
public class ChapterModel {
    public BookModel bookModel;//书籍相关信息
    public List<ChapterPageModel> pageList;//章节分页
    public String chapterTitle;//章节标题
    public String historyPosition; //阅读历史位置
    public int chapterSeq;//章节序号
    public String chapterFileName;//章节对应的文件名
    public String content;//章节内容

    public static ChapterModel obtain(BookModel bookInfo,int chapterSeq) {
        ChapterModel info =  new ChapterModel();
        info.bookModel = bookInfo;
        info.chapterSeq = chapterSeq;
        info.chapterFileName = bookInfo.chapterSeqs.get(chapterSeq);
        return info;
    }
}
