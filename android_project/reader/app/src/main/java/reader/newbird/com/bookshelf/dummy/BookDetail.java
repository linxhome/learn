package reader.newbird.com.bookshelf.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class BookDetail {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<BookDetailItem> ITEMS = new ArrayList<BookDetailItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Long, BookDetailItem> ITEM_MAP = new HashMap<Long, BookDetailItem>();

    private static final int COUNT = 7;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(BookDetailItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.bookId, item);
    }

    private static BookDetailItem createDummyItem(int position) {
        String name = "金瓶梅";
        String author = "毛陵笑笑生";
        String cover_url = "https://qidian.qpic.cn/qdbimg/349573/1011207893/0";
        return new BookDetailItem(position, author, name, cover_url);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class BookDetailItem {
        public final long bookId;
        public final String authorName;
        public final String bookName;
        public final String coverUrl;

        public BookDetailItem(long bookId, String authorName, String bookName, String coverUrl) {
            this.bookId = bookId;
            this.authorName = authorName;
            this.bookName = bookName;
            this.coverUrl = coverUrl;
        }
    }
}
