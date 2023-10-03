package model;

import java.util.Collection;

public interface INode {

	Collection<IEdge> getEdges();

	INode getPredecessor();
	
	void setPredecessor(INode node);
	
	public boolean isOpen();
	
	public void setOpen(boolean open);

	public boolean isVisited();

	public void setVisited(boolean visited);

	public boolean isSelected();

	public void setSelected(boolean selected);

	int getHeuristic();

	void setHeuristic(int heuristic);

	int getCost();

	void setCost(int cost);

}
