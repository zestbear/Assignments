package Manager;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BookmarkListPanel extends JPanel {
	
	JPanel newp=new JPanel();
	String[] title= {"","Group","Name","URL","Created Time","Memo"};
	DefaultTableModel model=new DefaultTableModel(setArr(),title) {
		public boolean isCellEditable(int rowindex,int mColindex) {
			return false;
		}
	};
	JTable table = new JTable(model);
	
	BookmarkList list=listing();
	BookmarkList ord=new BookmarkList("Bookmark.txt");
	
	public BookmarkListPanel() throws IOException {
		
		JScrollPane scroll=new JScrollPane(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.getColumnModel().getColumn(3).setPreferredWidth(240);
		table.getColumnModel().getColumn(4).setPreferredWidth(180);
		table.getColumnModel().getColumn(5).setPreferredWidth(100);
		scroll.setPreferredSize(new Dimension(600,300));
		table.setFillsViewportHeight(true);
		newp.add(scroll);
		table.addMouseListener(new OpenListener());
		newp.setBackground(new Color(210,210,210));
	}

	public BookmarkList listing() throws IOException {
		BookmarkList arr=new BookmarkList("Bookmark.txt");
		int k=1;
		while(k<arr.bookmarklist.size()-1) {
			Bookmark bk=new Bookmark();
			if(arr.bookmarklist.get(0).BookmarkGroup.equals(arr.bookmarklist.get(1).BookmarkGroup)) {
				if(!arr.bookmarklist.get(0).BookmarkGroup.equals("")) {
					bk.BookmarkGroup=arr.bookmarklist.get(0).BookmarkGroup;
					bk.BookmarkName="";
					bk.BookmarkURL="";
					bk.BookmarkTime="";
					bk.BookmarkMemo="";
					arr.bookmarklist.add(0, bk);
				}
			}
			
			if(!arr.bookmarklist.get(k).BookmarkGroup.equals("")) {
				if(!arr.bookmarklist.get(k).BookmarkGroup.equals(arr.bookmarklist.get(k-1).BookmarkGroup)) {
					if(arr.bookmarklist.get(k).BookmarkGroup.equals(arr.bookmarklist.get(k+1).BookmarkGroup)) {
						bk.BookmarkGroup=arr.bookmarklist.get(k).BookmarkGroup;
						bk.BookmarkName="";
						bk.BookmarkURL="";
						bk.BookmarkTime="";
						bk.BookmarkMemo="";
						arr.bookmarklist.add(k, bk);
					}
				}
			}
			k++;
		}
		
		return arr;
	}
	
	public String[][] setArr() throws IOException{
		BookmarkList bml=listing();
		
		String[][] arr=new String[bml.bookmarklist.size()][6];
		for(int i=0;i<bml.bookmarklist.size();i++) {
			arr[i][0]="";
		}
		for(int i=0;i<bml.bookmarklist.size();i++) {
			arr[i][1]=bml.bookmarklist.get(i).BookmarkGroup;
			arr[i][2]=bml.bookmarklist.get(i).BookmarkName;
			arr[i][3]=bml.bookmarklist.get(i).BookmarkURL;
			arr[i][4]=bml.bookmarklist.get(i).BookmarkTime;
			arr[i][5]=bml.bookmarklist.get(i).BookmarkMemo;
		}
		for(int i=0;i<bml.bookmarklist.size();i++) {
			if(arr[i][3].equals("") && arr[i][4].equals("")) {
				arr[i][0]="V";
			}
		}
		
		return arr;
	}
	
	public JPanel panel() {
		return newp;
	}

	class OpenListener implements MouseListener{
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int row=table.getSelectedRow();
			int col=table.getSelectedColumn();
			
			if(table.getValueAt(row, col).equals(">")) { // Open
				int loc=ord.bookmarklist.size()-1;
				while(loc!=0) {
					Bookmark bk=new Bookmark();
					if(ord.bookmarklist.get(loc).BookmarkGroup.equals(table.getValueAt(row, col+1))) {
						String t1=ord.bookmarklist.get(loc).BookmarkGroup;
						String t2=ord.bookmarklist.get(loc).BookmarkName;
						String t3=ord.bookmarklist.get(loc).BookmarkURL;
						String t4=ord.bookmarklist.get(loc).BookmarkTime;
						String t5=ord.bookmarklist.get(loc).BookmarkMemo;
						String[] temp= {"",t1,t2,t3,t4,t5};
						model.insertRow(row+1, temp);
						
						bk.BookmarkGroup=t1;
						bk.BookmarkMemo=t5;
						bk.BookmarkName=t2;
						bk.BookmarkTime=t4;
						bk.BookmarkURL=t3;
						list.bookmarklist.add(bk);
					}
					loc--;
				}
				table.setValueAt("V", row, col);
			}
			else if(table.getValueAt(row, col).equals("V")) { // Close
				int loc=table.getRowCount()-1;
				while(loc!=0) {
					if(table.getValueAt(loc, col+1).equals(table.getValueAt(row, col+1))) {
						if(!table.getValueAt(loc, col+4).equals("")) {
							model.removeRow(loc);
						}
					}
					loc--;
				}
				for(int i=list.bookmarklist.size()-1;i>=0;i--) {
					if(list.bookmarklist.get(i).BookmarkGroup.equals(table.getValueAt(row, col+1))) {
						if(!list.bookmarklist.get(i).BookmarkTime.equals("")) {
							list.bookmarklist.remove(i);
						}
					}
				}
				table.setValueAt(">", row, col);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
