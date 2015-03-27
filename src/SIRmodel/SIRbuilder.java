package SIRmodel;

import repast.simphony.context.Context;
//import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
//import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import SIRmodel.AgentSusceptible;
import SIRmodel.AgentInfected;

public class SIRbuilder implements ContextBuilder<Object>{
	
	public int infectedCount = 3;
	public int susceptibleCount = 15000;
	public int gridX = 150;
	public int gridY = 150;

	public Context build(Context<Object> context) {
		context.setId("SIRmodel");
		
		getParameters();
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = 
				spaceFactory.createContinuousSpace("space", context, 
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						100, 100);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, 
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.WrapAroundBorders(),
						new SimpleGridAdder<Object>(),
						true,  gridX, gridY));
		
		for (int i = 0; i < infectedCount; i++){
			context.add(new AgentInfected(space, grid));
		}
		
		for (int i = 0; i < susceptibleCount; i++) {
			context.add(new AgentSusceptible(space, grid));
		}
		
		for (Object obj : context){
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(),(int)pt.getY());
		}
		
		return context;
	}
	
	private void getParameters(){
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		infectedCount = (Integer)params.getValue("infected_count");
		susceptibleCount = (Integer)params.getValue("population_count");
		gridX = (Integer)params.getValue("grid_x");
		gridY = (Integer)params.getValue("grid_y");
	}

}
