package SIRmodel;

import java.util.List;
import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class AgentSusceptible implements AgentGeneric {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	public AgentSusceptible(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		GridPoint pt = grid.getLocation(this);

		//randomise direction of movement
		int xAdj = ranInt(minMove, maxMove);
		int yAdj = ranInt(minMove, maxMove);
		//System.out.println(xAdj + " " + yAdj);	
		agentMove(pt, xAdj, yAdj);
	}

	public void agentMove(GridPoint nPt, int xAdj, int yAdj) {
		NdPoint myPoint = space.getLocation(this);
		
		NdPoint otherPoint = new NdPoint((nPt.getX()+xAdj) , (nPt.getY()+yAdj));
		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
				otherPoint);
		
		space.moveByVector(this, 1, angle, 0);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
	}
	
	public static int ranInt(int min, int max){
		Random rn = new Random();
		int rnInt = rn.nextInt((max-min)+1) + min;
		return rnInt;
	}
}
