package reader.newbird.com.book;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.net.URI;
import java.util.List;

public class BookModel implements Parcelable {
    public String bookName;
    public List<String> chapterSeqs;
    public String authorName;
    public String bookDir;//书籍存储的目录
    public String detail;
    public Uri cover;

    public BookModel() {

    }

    protected BookModel(Parcel in) {
        bookName = in.readString();
        chapterSeqs = in.createStringArrayList();
        authorName = in.readString();
        bookDir = in.readString();
        detail = in.readString();
        cover = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<BookModel> CREATOR = new Creator<BookModel>() {
        @Override
        public BookModel createFromParcel(Parcel in) {
            return new BookModel(in);
        }

        @Override
        public BookModel[] newArray(int size) {
            return new BookModel[size];
        }
    };

    public static BookModel obtain() {
        return new BookModel();
    }


    public boolean isValid() {
        return !TextUtils.isEmpty(bookName) && chapterSeqs != null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookName);
        dest.writeStringList(chapterSeqs);
        dest.writeString(authorName);
        dest.writeString(bookDir);
        dest.writeString(detail);
        dest.writeParcelable(cover, flags);
    }
}
