package uk.ac.york.wsdh500.moea.algorithms.nsgaii ;

import static uk.ac.york.wsdh500.moea.utils.SeedUtils.LINEAR ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.LOGARITHMIC ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.ORTHOGONAL ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.RANDOM ;
import static uk.ac.york.wsdh500.moea.utils.SeedUtils.TWISTED ;

import java.util.ArrayList ;
import java.util.Comparator ;
import java.util.List ;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.operator.selection.SelectionOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator ;

import uk.ac.york.wsdh500.moea.utils.SeedUtils ;

public class SeededNSGAIIMeasures extends NSGAIIMeasures<DoubleSolution>
{
	private static final long serialVersionUID = -4334447327011803270L ;

	private int[] SEED_TYPES ;

	public static final String CURRENT_EXECUTION_TIME = "currentExecutionTime" ; 
	public static final String CURRENT_EVALUATION = "currentEvaluation" ;
	public static final String NUMBER_OF_NONDOM_SOLUTIONS = "numberOfNonDominatedSolutionsInPopulation" ;
	public static final String CURRENT_POPULATION = "currentPopulation" ;
	public static final String HYPERVOLUME = "hypervolume" ;

	public SeededNSGAIIMeasures
	(
			Problem<DoubleSolution> problem ,
			int maxEvaluations , int populationSize , int matingPoolSize , int offspringPopulationSize ,
			CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator , SelectionOperator<List<DoubleSolution>,DoubleSolution> selectionOperator ,
			Comparator<DoubleSolution> dominanceComparator , SolutionListEvaluator<DoubleSolution> evaluator
	)
	{
		super
		(
				problem ,
				maxEvaluations , populationSize , matingPoolSize , offspringPopulationSize ,
				crossoverOperator , mutationOperator , selectionOperator ,
				dominanceComparator , evaluator
		) ;
	}

	protected void setSeedStrategy( int[] types )
	{
		if( SEED_TYPES == null )
			SEED_TYPES = types ;
	}

	@Override
	public String getName()
	{
		String name = "NSGAII" ;

		for( int SEED_TYPE : SEED_TYPES )
		{
			switch( SEED_TYPE & 3 )
			{
				case RANDOM :
					name += " + random" ;
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
				name += " + orthogonal" ;
		}

		return name ;
	}

	@Override
	protected List<DoubleSolution> createInitialPopulation()
	{
		List<DoubleSolution> population = new ArrayList<DoubleSolution>() ;
		for( int SEED_TYPE : SEED_TYPES )
			population.addAll( SeedUtils.createPopulation( getProblem() , getMaxPopulationSize() , SEED_TYPE ) ) ;

		return population ;
	}
}
