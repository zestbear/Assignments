package Manager;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BookmarkManager {
	
	JFrame frame=new JFrame();
	JPanel p1=new JPanel();
	JPanel p2=new JPanel();
	BookmarkListPanel ap=new BookmarkListPanel();
	
	public BookmarkManager() throws IOException {
	
		JButton add=new JButton("ADD");
		JButton del=new JButton("DELETE");
		JButton up=new JButton("UP");
		JButton down=new JButton("DOWN");
		JButton save=new JButton("SAVE");
		p1.setLayout(new GridLayout(5,1));
		p1.add(add);
		p1.add(del);
		p1.add(up);
		p1.add(down);
		p1.add(save);
		ADDListenerClass addlistener = new ADDListenerClass();
		DELListenerClass dellistener = new DELListenerClass();
		SAVEListenerClass savelistener=new SAVEListenerClass();
		UPListenerClass uplistener=new UPListenerClass();
		DOWNListenerClass downlistener=new DOWNListenerClass();
	    add.addActionListener(addlistener);
	    del.addActionListener(dellistener);
	    del.addMouseListener(new delmouse());
	    save.addActionListener(savelistener);
	    up.addActionListener(uplistener);
	    down.addActionListener(downlistener);
	    p1.setBackground(Color.LIGHT_GRAY);
	    
	    p2=ap.newp;
	    frame.add(p2,BorderLayout.CENTER);
	    frame.add(p1,BorderLayout.EAST);
		
		frame.setTitle("BookmarkManager");
		frame.setSize(800,400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	class ADDListenerClass implements ActionListener {
		
		JFrame addf=new JFrame();
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		String[] addtitle= {"Group","Name","URL","Memo"};
		String[][] adding= {{"","","",""}};
		DefaultTableModel model=new DefaultTableModel(adding,addtitle) {
			public boolean isCellEditable(int rowindex,int mColindex) {
				return true;
			}
		};
		JTable addtable=new JTable(model);
		
		public ADDListenerClass() {
			
			JButton input=new JButton("INPUT");
			JScrollPane addscroll=new JScrollPane(addtable);
			p2.add(addscroll);
			addscroll.setPreferredSize(new Dimension(550,40));
			INPUTListenerClass listener=new INPUTListenerClass();
			input.addActionListener(listener);
			p1.add(input);
			addf.add(p1,BorderLayout.EAST);
			addf.add(p2,BorderLayout.CENTER);
			
			addf.setTitle("Input New Bookmark");
			addf.setSize(700,100);
			addf.setLocationRelativeTo(null);
		}
		
		public void actionPerformed(ActionEvent e) {
			new ADDListenerClass();
			addf.setVisible(true);
		}
		
		class INPUTListenerClass implements ActionListener {
			
			public INPUTListenerClass() {
				String group=(String) addtable.getValueAt(0, 0);
				String name=(String) addtable.getValueAt(0, 1);
				String url=(String) addtable.getValueAt(0, 2);
				String memo=(String) addtable.getValueAt(0, 3);
				LocalDateTime addtime = LocalDateTime.now();
			    String time = addtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"));
				String[] intable= {"",group,name,url,time,memo};
				Bookmark bk=new Bookmark(url);
				bk.BookmarkGroup=group;
				bk.BookmarkMemo=memo;
				bk.BookmarkName=name;
				bk.BookmarkURL=url;
				if(!url.equals("")) {
					ap.model.addRow(intable);
					ap.list.bookmarklist.add(bk);
					ap.ord.bookmarklist.add(bk);
				}
				addtable.repaint();
				addf.dispose();
			}

			public void actionPerformed(ActionEvent e) {
				new INPUTListenerClass();
			}
		}
	}

	class DELListenerClass implements ActionListener {
		
		int row=ap.table.getSelectedRow();
		
		public DELListenerClass() {
			
			if(row!=-1 && !ap.table.getValueAt(row,0).equals("V")) {
				Bookmark rem=new Bookmark();
				rem.BookmarkGroup=(String)ap.table.getValueAt(row, 1);
				rem.BookmarkName=(String)ap.table.getValueAt(row, 2);
				rem.BookmarkURL=(String)ap.table.getValueAt(row, 3);
				rem.BookmarkTime=(String)ap.table.getValueAt(row, 4);
				rem.BookmarkMemo=(String)ap.table.getValueAt(row, 5);
				
				if(!rem.BookmarkTime.equals("")) {
					for(int i=0;i<ap.ord.numBookmarks();i++) {
						if(ap.ord.getBookmark(i).BookmarkGroup.equals(rem.BookmarkGroup)) {
							if(ap.ord.getBookmark(i).BookmarkName.equals(rem.BookmarkName)) {
								if(ap.ord.getBookmark(i).BookmarkURL.equals(rem.BookmarkURL)) {
									if(ap.ord.getBookmark(i).BookmarkTime.equals(rem.BookmarkTime)) {
										if(ap.ord.getBookmark(i).BookmarkMemo.equals(rem.BookmarkMemo)) {
											ap.ord.bookmarklist.remove(i);
										}
									}
								}
							}
						}
					}
				}
				else {
					for(int i=ap.ord.numBookmarks()-1;i>0;i--) {
						if(ap.ord.getBookmark(i).BookmarkGroup.equals(rem.BookmarkGroup)) {
							ap.ord.bookmarklist.remove(i);
						}
					}
				}
				
			}
		}

		public void actionPerformed(ActionEvent e) {
			new DELListenerClass();
		}
	}
	
	class delmouse implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			int row=ap.table.getSelectedRow();
			if(row==-1) {
				JOptionPane.showMessageDialog(null, "NOT SELECTED");
			}
			else if(ap.table.getValueAt(row,0).equals("V")) {
				JOptionPane.showMessageDialog(null, "CANT DELETE");
			}
			else {
				ap.model.removeRow(row);
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
	
	class UPListenerClass implements ActionListener {
		
		public UPListenerClass() {
			int row=ap.table.getSelectedRow();
			int col=ap.table.getSelectedColumn();
			
			if(row>0) {
				int swap1 = 0,swap2 = 0;
				String[] temp=new String[6];
				temp[0]=(String) ap.table.getValueAt(row-1, 0);
				temp[1]=(String) ap.table.getValueAt(row-1, 1);
				temp[2]=(String) ap.table.getValueAt(row-1, 2);
				temp[3]=(String) ap.table.getValueAt(row-1, 3);
				temp[4]=(String) ap.table.getValueAt(row-1, 4);
				temp[5]=(String) ap.table.getValueAt(row-1, 5);
				for(int i=0;i<ap.ord.numBookmarks();i++) {
					if(ap.ord.getBookmark(i).BookmarkGroup.equals(temp[1])) {
						if(ap.ord.getBookmark(i).BookmarkName.equals(temp[2])) {
							if(ap.ord.getBookmark(i).BookmarkURL.equals(temp[3])) {
								if(ap.ord.getBookmark(i).BookmarkTime.equals(temp[4])) {
									if(ap.ord.getBookmark(i).BookmarkMemo.equals(temp[5])) {
										swap1=i;
									}
								}
							}
						}
					}
				}
				
				String[] next=new String[6];
				next[0]=(String) ap.table.getValueAt(row, 0);
				next[1]=(String) ap.table.getValueAt(row, 1);
				next[2]=(String) ap.table.getValueAt(row, 2);
				next[3]=(String) ap.table.getValueAt(row, 3);
				next[4]=(String) ap.table.getValueAt(row, 4);
				next[5]=(String) ap.table.getValueAt(row, 5);
				for(int i=0;i<ap.ord.numBookmarks();i++) {
					if(ap.ord.getBookmark(i).BookmarkGroup.equals(next[1])) {
						if(ap.ord.getBookmark(i).BookmarkName.equals(next[2])) {
							if(ap.ord.getBookmark(i).BookmarkURL.equals(next[3])) {
								if(ap.ord.getBookmark(i).BookmarkTime.equals(next[4])) {
									if(ap.ord.getBookmark(i).BookmarkMemo.equals(next[5])) {
										swap2=i;
									}
								}
							}
						}
					}
				}
				
				if(!ap.table.getValueAt(row, col+3).equals("")) {
					if(!ap.table.getValueAt(row-1, col+3).equals("")) {
						Collections.swap(ap.ord.bookmarklist, swap1, swap2);
					}
				}
				
				for(int i=0;i<6;i++) {
					ap.table.setValueAt((String)ap.table.getValueAt(row, i), row-1, i);
					ap.table.setValueAt(temp[i], row, i);
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			new UPListenerClass();
		}
	}

	class DOWNListenerClass implements ActionListener {
	
		public DOWNListenerClass() {
			int row=ap.table.getSelectedRow();
			int col=ap.table.getSelectedColumn();
			
			if(row<ap.table.getRowCount()-1 && row!=-1) {
				int swap1 = 0,swap2 = 0;
				String[] temp=new String[6];
				temp[0]=(String) ap.table.getValueAt(row+1, 0);
				temp[1]=(String) ap.table.getValueAt(row+1, 1);
				temp[2]=(String) ap.table.getValueAt(row+1, 2);
				temp[3]=(String) ap.table.getValueAt(row+1, 3);
				temp[4]=(String) ap.table.getValueAt(row+1, 4);
				temp[5]=(String) ap.table.getValueAt(row+1, 5);
				for(int i=0;i<ap.ord.numBookmarks();i++) {
					if(ap.ord.getBookmark(i).BookmarkGroup.equals(temp[1])) {
						if(ap.ord.getBookmark(i).BookmarkName.equals(temp[2])) {
							if(ap.ord.getBookmark(i).BookmarkURL.equals(temp[3])) {
								if(ap.ord.getBookmark(i).BookmarkTime.equals(temp[4])) {
									if(ap.ord.getBookmark(i).BookmarkMemo.equals(temp[5])) {
										swap1=i;
									}
								}
							}
						}
					}
				}
				
				String[] next=new String[6];
				next[0]=(String) ap.table.getValueAt(row, 0);
				next[1]=(String) ap.table.getValueAt(row, 1);
				next[2]=(String) ap.table.getValueAt(row, 2);
				next[3]=(String) ap.table.getValueAt(row, 3);
				next[4]=(String) ap.table.getValueAt(row, 4);
				next[5]=(String) ap.table.getValueAt(row, 5);
				for(int i=0;i<ap.ord.numBookmarks();i++) {
					if(ap.ord.getBookmark(i).BookmarkGroup.equals(next[1])) {
						if(ap.ord.getBookmark(i).BookmarkName.equals(next[2])) {
							if(ap.ord.getBookmark(i).BookmarkURL.equals(next[3])) {
								if(ap.ord.getBookmark(i).BookmarkTime.equals(next[4])) {
									if(ap.ord.getBookmark(i).BookmarkMemo.equals(next[5])) {
										swap2=i;
									}
								}
							}
						}
					}
				}
				
				if(!ap.table.getValueAt(row, col+3).equals("")) {
					if(!ap.table.getValueAt(row+1, col+3).equals("")) {
						Collections.swap(ap.ord.bookmarklist, swap1, swap2);
					}
				}
				
				for(int i=0;i<6;i++) {
					ap.table.setValueAt((String)ap.table.getValueAt(row, i), row+1, i);
					ap.table.setValueAt(temp[i], row, i);
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			new DOWNListenerClass();
		}
	}

	class SAVEListenerClass implements ActionListener  {
	
		public SAVEListenerClass() throws IOException {
			File file=new File("Bookmark.txt");
		    PrintWriter output=new PrintWriter(file);
		    output.println("// Bookmark information - saved by GUI ");
			for(int i=0;i<ap.ord.bookmarklist.size();i++) {
				if(!ap.ord.bookmarklist.get(i).BookmarkTime.equals("")) {
					Bookmark wrbk=new Bookmark();
					wrbk.BookmarkGroup=ap.ord.bookmarklist.get(i).BookmarkGroup;
					wrbk.BookmarkName=ap.ord.bookmarklist.get(i).BookmarkName;
					wrbk.BookmarkURL=ap.ord.bookmarklist.get(i).BookmarkURL;
					wrbk.BookmarkTime=ap.ord.bookmarklist.get(i).BookmarkTime;
					wrbk.BookmarkMemo=ap.ord.bookmarklist.get(i).BookmarkMemo;
				    output.print(wrbk.BookmarkName.trim()+";");
				    output.print(wrbk.BookmarkTime.trim()+";");
				    output.print(wrbk.BookmarkURL.trim()+";");
				    output.print(wrbk.BookmarkGroup.trim()+";");
				    output.println(wrbk.BookmarkMemo.trim());
				}
			}
			output.print("// end of Bookmark information saved by GUI ");
			output.close();
		}

		public void actionPerformed(ActionEvent e) {
			try {
				new SAVEListenerClass();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
