package uk.ac.york.wsdh500.moea.algorithms.nsgaii ;

import java.util.Comparator ;

import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.comparator.DominanceComparator ;

public class TraceNSGAIIBuilder extends SeededNSGAIIBuilder
{
	private Comparator<DoubleSolution> dominanceComparator ;
	private String pareto_front ;
	private boolean dump_fronts ;

	public TraceNSGAIIBuilder
	(
		Problem<DoubleSolution> problem ,
		CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator ,
		int populationSize , String pareto_front , boolean dump_fronts
	)
	{
		super( problem , crossoverOperator , mutationOperator , populationSize ) ;

		this.pareto_front = pareto_front ;
		this.dump_fronts = dump_fronts ;
	}

	public TraceNSGAII build()
	{
		TraceNSGAII algorithm = null ;

		algorithm = new TraceNSGAII
		(
				getProblem() ,
				getMaxIterations() , getPopulationSize() , matingPoolSize , offspringPopulationSize ,
				getCrossoverOperator() , getMutationOperator() , getSelectionOperator() ,
				dominanceComparator != null ? dominanceComparator : new DominanceComparator<>() , getSolutionListEvaluator() ,
				pareto_front , dump_fronts
		) ;

		algorithm.setSeedStrategy( SEED_TYPES ) ;

		return algorithm ;
	}

	public TraceNSGAIIBuilder setDominanceComparator( Comparator<DoubleSolution> dominanceComparator )
	{
		this.dominanceComparator = dominanceComparator ;
		return setDominanceComparator( dominanceComparator ) ;
	}
}