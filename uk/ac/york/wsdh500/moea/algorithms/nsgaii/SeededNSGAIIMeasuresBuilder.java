package uk.ac.york.wsdh500.moea.algorithms.nsgaii ;

import java.util.Comparator ;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII ;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.comparator.DominanceComparator ;

public class SeededNSGAIIMeasuresBuilder extends NSGAIIBuilder<DoubleSolution>
{
	private Comparator<DoubleSolution> dominanceComparator ;

	protected int SEED_TYPE = -1 ;

	public SeededNSGAIIMeasuresBuilder( Problem<DoubleSolution> problem , CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator , int populationSize )
	{
		super( problem , crossoverOperator , mutationOperator , populationSize ) ;
	}

	public NSGAII<DoubleSolution> build()
	{
		SeededNSGAIIMeasures algorithm = null ;

		algorithm = new SeededNSGAIIMeasures
		(
				getProblem() ,
				getMaxIterations() , getPopulationSize() , matingPoolSize , offspringPopulationSize ,
				getCrossoverOperator() , getMutationOperator() , getSelectionOperator() ,
				dominanceComparator != null ? dominanceComparator : new DominanceComparator<>() , getSolutionListEvaluator()
		) ;

		algorithm.setSeedStrategy( SEED_TYPE ) ;

		return algorithm ;
	}

	public NSGAIIBuilder<DoubleSolution> setDominanceComparator( Comparator<DoubleSolution> dominanceComparator )
	{
		this.dominanceComparator = dominanceComparator ;
		return setDominanceComparator( dominanceComparator ) ;
	}

	public SeededNSGAIIMeasuresBuilder setSeedStrategy( int type )
	{
		SEED_TYPE = type ;

		return this ;
	}
}