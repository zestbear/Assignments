package Manager;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class BookmarkList {
	public String bookmarkFileName;
	ArrayList<Bookmark> bookmarklist=new ArrayList<Bookmark>();
	
	BookmarkList(String bookmarkFileName) throws IOException  {
		this.bookmarkFileName=bookmarkFileName;
		File file=new File(bookmarkFileName);
	    if (!file.exists()) {
	    	System.out.println("Unknwon BookmarkList data File");
	    }
	    else {
	    	BookmarkRead(bookmarkFileName);
	    	finderror();
	    	mergeByGroup();
	    }
	}

	public void BookmarkRead(String bookmarkFileName) throws IOException {
	    File file1=new File(bookmarkFileName);
		Scanner input1=new Scanner(file1);
		while(input1.hasNext()) {
			String str=input1.nextLine();
			if(!str.isBlank()) {
				String str1=str.substring(0, 2);
				String[] str2=str.split(",",5);
				String[] str3=str.split(";",5);
		    	if(str1.equals("//")) {
		    		continue;
		    	}
		    	else {
		    		if(str2.length==5) {
		    			int cnt=0;
		    			bookmarklist.add(new Bookmark(str2[2].trim()));
		    			for(int i=0;i<bookmarklist.size()-1;i++) {
		    				if(bookmarklist.get(i).equals(str2[2].trim())) {
		    					if((bookmarklist.get(i).BookmarkTime).equals(str2[1].trim())) {
		    						cnt++;
		    					}
		    					
		    				}
		    			}
		    			if(cnt>0) {
		    				bookmarklist.remove(bookmarklist.size()-1);
		    			}
		    			else if(cnt==0) {
							bookmarklist.get(bookmarklist.size()-1).BookmarkName=str2[0].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkTime=str2[1].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkURL=str2[2].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkGroup=str2[3].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkMemo=str2[4].trim();
		    			}
		    		}
		    		else if(str3.length==5){
		    			int cnt=0;
		    			bookmarklist.add(new Bookmark(str3[2].trim()));
		    			for(int i=0;i<bookmarklist.size()-1;i++) {
		    				if(bookmarklist.get(i).equals(str3[2].trim())) {
		    					if((bookmarklist.get(i).BookmarkTime).equals(str3[1].trim())) {
		    						cnt++;
		    					}
		    					
		    				}
		    			}
		    			if(cnt>0) {
		    				bookmarklist.remove(bookmarklist.size()-1);
		    			}
		    			else if(cnt==0) {
		    				bookmarklist.get(bookmarklist.size()-1).BookmarkName=str3[0].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkTime=str3[1].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkURL=str3[2].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkGroup=str3[3].trim();
							bookmarklist.get(bookmarklist.size()-1).BookmarkMemo=str3[4].trim();
		    			}
		    		}
		    	}
			}
		}
		input1.close();
	}
	
	public void finderror() {
		
		for(int i=0;i<numBookmarks();i++) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");

			if(bookmarklist.get(i).equals("")) {
				System.out.print("Date Format Error -> No Created Time invalid Bookmark info line: ");
				bookmarklist.get(i).print();
			}
			else if(!bookmarklist.get(i).equals("")){
				try {
					@SuppressWarnings("unused")
					LocalDate date = LocalDate.parse(bookmarklist.get(i).BookmarkTime, formatter);
				} catch(DateTimeParseException e){
					System.out.print("Date Format Error -> Wrong Time Format invalid Bookmark info line: ");
					bookmarklist.get(i).print();
				}
			}
		}
		for(int i=0;i<numBookmarks();i++) {
			if(bookmarklist.get(i).equals("")) {
				System.out.print("MalformedURLException: wrong URL - No URL ; invalid Bookmark info line: ");
				bookmarklist.get(i).print();
			}
		}
	}
	
	public void BookmarkWrite(String bookmarkFileName) throws IOException {
		File file=new File(bookmarkFileName);
	    PrintWriter output=new PrintWriter(file);
	    output.write("\n");
	    output.write(bookmarklist.get(bookmarklist.size()-1).BookmarkName.trim()+"; ");
	    output.write(bookmarklist.get(bookmarklist.size()-1).BookmarkTime.trim()+"; ");
	    output.write(bookmarklist.get(bookmarklist.size()-1).BookmarkURL.trim()+"; ");
	    output.write(bookmarklist.get(bookmarklist.size()-1).BookmarkGroup.trim()+"; ");
	    output.write(bookmarklist.get(bookmarklist.size()-1).BookmarkMemo.trim()+";");
	    output.close();
	}
	
	public int numBookmarks() {
		return bookmarklist.size();
	}
	
	public Bookmark getBookmark(int i) {
		return bookmarklist.get(i);
	}
	
	public void mergeByGroup() {
		for(int i=0;i<bookmarklist.size();i++) {
			for(int j=i+1;j<bookmarklist.size();j++) {
				if(!bookmarklist.get(j).BookmarkGroup.equals("")) {
					if(bookmarklist.get(i).BookmarkGroup.equals(bookmarklist.get(j).BookmarkGroup)) {
						for(int k=j;k>i+1;k--) {
							if(!bookmarklist.get(k).BookmarkGroup.equals(bookmarklist.get(k-1).BookmarkGroup)) {
								Collections.swap(bookmarklist, k, k-1);
							}
							else if(bookmarklist.get(k).BookmarkGroup.equals(bookmarklist.get(k-1).BookmarkGroup)){
								break;
							}
						}
					}	
				}
			}
		}
	}
}
