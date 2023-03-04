package Manager;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bookmark {
	public String BookmarkName="";
	public String BookmarkTime="";
	public String BookmarkURL="";
	public String BookmarkGroup="";
	public String BookmarkMemo="";
	
	public Bookmark(String BookmarkURL) {
		this.BookmarkURL=BookmarkURL;
		LocalDateTime addtime = LocalDateTime.now();
	    BookmarkTime = addtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"));
	    this.BookmarkName="";
	    this.BookmarkGroup="";
	    this.BookmarkMemo="";
	}
	
	public Bookmark(String BookmarkName,String BookmarkGroup,String BookmarkMemo) {
		BookmarkName=this.BookmarkName;
		BookmarkGroup=this.BookmarkGroup;
		BookmarkMemo=this.BookmarkMemo;
	}
	
	
	public Bookmark() {}

	public void print() {
		System.out.println(BookmarkName.trim()+";"+BookmarkTime.trim()+";"+BookmarkURL.trim()+";"+BookmarkGroup.trim()+";"+BookmarkMemo.trim()+";");
	}
}
