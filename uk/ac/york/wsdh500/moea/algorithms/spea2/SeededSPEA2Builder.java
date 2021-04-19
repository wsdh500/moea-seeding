package uk.ac.york.wsdh500.moea.algorithms.spea2 ;

import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

public class SeededSPEA2Builder extends SPEA2Builder<DoubleSolution>
{
	protected int SEED_TYPE = -1 ;

	public SeededSPEA2Builder( Problem<DoubleSolution> problem , CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator )
	{
		super( problem , crossoverOperator , mutationOperator ) ;
	}

	public SeededSPEA2 build()
	{
		SeededSPEA2 algorithm = null ;

		algorithm = new SeededSPEA2
		(
				getProblem() ,
				getMaxIterations() , getPopulationSize() ,
				getCrossoverOperator() , getMutationOperator() , getSelectionOperator() ,
				getSolutionListEvaluator() , k
		) ;

		algorithm.setSeedStrategy( SEED_TYPE ) ;

		return algorithm ;
	}

	public SeededSPEA2Builder setSeedStrategy( int type )
	{
		SEED_TYPE  = type ;

		return this ;
	}
}
