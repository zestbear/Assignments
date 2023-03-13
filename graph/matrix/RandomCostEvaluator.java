package matrix;

import model.IEdge;

import java.util.Random;
import model.INode;

public class RandomCostEvaluator extends AStarCostEvaluator {
	
	static int sum=0;
	
	@Override
	public int evaluateHeuristic(INode node, INode start, INode end) {
		return 0;
	}
	
	@Override
	public int evaluateCost(INode candidate, IEdge edge, INode start, INode end) {
		if (isEnabled()) {
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			return random.nextInt(100);
		}
		throw new EvaluatorDisabledException();
	}
}
