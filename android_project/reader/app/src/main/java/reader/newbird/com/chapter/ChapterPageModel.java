package reader.newbird.com.chapter;

import reader.newbird.com.book.BookModel;

/**
 * 每章分页之后的数据
 */
public class ChapterPageModel {
    public BookModel bookModel;
    public ChapterModel chapterModel;
    public int size;//字符数量
    public int position;//在章节的位置,按字符数量

    public String getPageContent() {
        return chapterModel != null ? chapterModel.content.substring(position, size) : null;
    }




}
