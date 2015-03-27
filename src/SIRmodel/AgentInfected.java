package SIRmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;
import SIRmodel.AgentSusceptible;
import SIRmodel.AgentInfected;

public class AgentInfected implements AgentGeneric{
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int infTick;
	private int recoverPeriod = 20;
	private int infectionChance = 80;
	
	public AgentInfected(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		this.infTick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		getParameters();
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		GridPoint pt = grid.getLocation(this);

		//randomise direction of movement
		int xAdj = ranInt(minMove, maxMove);
		int yAdj = ranInt(minMove, maxMove);
		
		agentMove(pt, xAdj, yAdj);
		infect();
		recover(infTick);
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
	
	public void infect() {
		GridPoint pt = grid.getLocation(this);
		
		List<Object> susceptible = new ArrayList<Object>();
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof AgentSusceptible) {
				susceptible.add(obj);
			}
		}
		
		if (susceptible.size() > 0) {
			if (ranInt(0,100) < infectionChance){
				int index = RandomHelper.nextIntFromTo(0, susceptible.size() - 1);
				Object obj = susceptible.get(index);
				NdPoint spacePt = space.getLocation(obj);
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
				
				AgentInfected agnInf = new AgentInfected(space, grid);
				context.add(agnInf);
				
				space.moveTo(agnInf, spacePt.getX(), spacePt.getY());
				grid.moveTo(agnInf, pt.getX(), pt.getY());
			}
		}
	}
	
	public void recover(int infTick){
		int currTick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		GridPoint pt = grid.getLocation(this);
		List<Object> infected = new ArrayList<Object>();
		
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof AgentInfected) {
				if ((infTick + recoverPeriod) < currTick){
					infected.add(obj);
				}
			}
		}
		
		if (infected.size() > 0) {
			if (ranInt(0,100) < recoverPeriod){
				int index = RandomHelper.nextIntFromTo(0, infected.size() - 1);
				Object obj = infected.get(index);
				NdPoint spacePt = space.getLocation(obj);
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
				
				AgentRecovered agnInf = new AgentRecovered(space, grid);
				context.add(agnInf);
				
				space.moveTo(agnInf, spacePt.getX(), spacePt.getY());
				grid.moveTo(agnInf, pt.getX(), pt.getY());
			}
		}
	}
	
	public static int ranInt(int min, int max){
		Random rn = new Random();
		int rnInt = rn.nextInt((max-min)+1) + min;
		return rnInt;
	}
	
	private void getParameters(){
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.recoverPeriod = (Integer)params.getValue("recover_period");
		this.infectionChance = (Integer)params.getValue("infection_chance");
	}

}
