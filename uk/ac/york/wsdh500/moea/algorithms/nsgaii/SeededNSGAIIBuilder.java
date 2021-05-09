package uk.ac.york.wsdh500.moea.algorithms.nsgaii ;

import java.util.Comparator ;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII ;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.comparator.DominanceComparator ;

public class SeededNSGAIIBuilder extends NSGAIIBuilder<DoubleSolution>
{
	private Comparator<DoubleSolution> dominanceComparator ;

	protected int[] SEED_TYPES ;

	public SeededNSGAIIBuilder( Problem<DoubleSolution> problem , CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator , int populationSize )
	{
		super( problem , crossoverOperator , mutationOperator , populationSize ) ;
	}

	public NSGAII<DoubleSolution> build()
	{
		SeededNSGAII algorithm = null ;

		algorithm = new SeededNSGAII
		(
				getProblem() ,
				getMaxIterations() , getPopulationSize() , matingPoolSize , offspringPopulationSize ,
				getCrossoverOperator() , getMutationOperator() , getSelectionOperator() ,
				dominanceComparator != null ? dominanceComparator : new DominanceComparator<>() , getSolutionListEvaluator()
		) ;

		algorithm.setSeedStrategy( SEED_TYPES ) ;

		return algorithm ;
	}

	public SeededNSGAIIBuilder setDominanceComparator( Comparator<DoubleSolution> dominanceComparator )
	{
		this.dominanceComparator = dominanceComparator ;
		return setDominanceComparator( dominanceComparator ) ;
	}

	public SeededNSGAIIBuilder setSeedStrategy( int[] types )
	{
		SEED_TYPES = types ;

		return this ;
	}
}