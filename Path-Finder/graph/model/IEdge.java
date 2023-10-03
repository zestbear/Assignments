package model;

public interface IEdge {

	INode getNodeA();
	
	INode getNodeB();

	INode getOpposite(INode node);

	int getWeight();

}
