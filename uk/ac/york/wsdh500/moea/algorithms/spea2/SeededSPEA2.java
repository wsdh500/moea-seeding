package uk.ac.york.wsdh500.moea.algorithms.spea2 ;

import static uk.ac.york.wsdh500.moea.utils.SeedUtils.LINEAR ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.LOGARITHMIC ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.ORTHOGONAL ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.RANDOM ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.TWISTED ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.createLinearPopulation ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.createLogarithmicPopulation ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.createOrthogonalPopulation ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.createStochasticPopulation ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.createTwistedPopulation ;

import java.util.List ;

import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2 ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.operator.selection.SelectionOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator ;

public class SeededSPEA2 extends SPEA2<DoubleSolution>
{
	private static final long serialVersionUID = -1813030140735081087L ;

	private int SEED_TYPE = -1 ;

	public SeededSPEA2
	( 
			Problem<DoubleSolution> problem , int maxIterations , int populationSize ,
			CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator ,
			SelectionOperator<List<DoubleSolution>,DoubleSolution> selectionOperator , SolutionListEvaluator<DoubleSolution> evaluator , 
			int k
	)
	{
		super
		(
				problem , maxIterations , populationSize ,
				crossoverOperator , mutationOperator ,
				selectionOperator , evaluator ,
				k
		) ;
	}

	protected void setSeedStrategy( int type )
	{
		if( SEED_TYPE == -1 )
			SEED_TYPE = type ;
	}

	@Override
	public String getName()
	{
		String name = "SPEA2" ;

		switch( SEED_TYPE & 3 )
		{
			case RANDOM :
				break ;
			case LINEAR :
				name += " + linear" ;
				break ;
			case LOGARITHMIC :
				name += " + log" ;
				break ;
		}

		if( ( SEED_TYPE & TWISTED ) == TWISTED )
			name += " + twist" ;

		if( ( SEED_TYPE & ORTHOGONAL ) == ORTHOGONAL )
			name += " + orthogonal" ; ;

		return name ;
	}

	@Override
	protected List<DoubleSolution> createInitialPopulation()
	{
		List<DoubleSolution> population = null ;

		switch( SEED_TYPE )
		{
			case RANDOM :
				population = createStochasticPopulation( getProblem() , getMaxPopulationSize() ) ;
				break ;
			case LINEAR :
				population = createLinearPopulation( getProblem() , getMaxPopulationSize() ) ;
				break ;
			case LOGARITHMIC :
				population = createLogarithmicPopulation( getProblem() , getMaxPopulationSize() ) ;
				break ;
			case TWISTED :
				population = createTwistedPopulation( getProblem() , getMaxPopulationSize() ) ;
				break ;
			case ORTHOGONAL :
				population = createOrthogonalPopulation( getProblem() , getMaxPopulationSize() ) ;
				break ;
			default :
				throw new IllegalArgumentException( "Unknown seeding stratgey : " + SEED_TYPE ) ;
		}

		return population ;
	}
}
