package SIRmodel;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public interface AgentGeneric {
	
	Parameters params = RunEnvironment.getInstance().getParameters();
	
	int maxMove = (Integer)params.getValue("movement");
	int minMove = (-1)*maxMove;
	
	public void agentMove(GridPoint point, int xAdjustment , int yAdjustment);
	public void step();
}
