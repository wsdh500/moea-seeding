package uk.ac.york.wsdh500.moea.algorithms.spea2 ;

import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

public class TraceSPEA2Builder extends SeededSPEA2Builder
{
	private String pareto_front ;
	private boolean dump_fronts ;

	public TraceSPEA2Builder
	(
		Problem<DoubleSolution> problem ,
		CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator ,
		String pareto_front , boolean dump_fronts
	)
	{
		super( problem , crossoverOperator , mutationOperator ) ;

		this.pareto_front = pareto_front ;
		this.dump_fronts = dump_fronts ;
	}

	public TraceSPEA2 build()
	{
		TraceSPEA2 algorithm = null ;

		algorithm = new TraceSPEA2
		(
				getProblem() ,
				getMaxIterations() , getPopulationSize() ,
				getCrossoverOperator() , getMutationOperator() , getSelectionOperator() ,
				getSolutionListEvaluator() , k ,
				pareto_front , dump_fronts
		) ;

		algorithm.setSeedStrategy( SEED_TYPES ) ;

		return algorithm ;
	}
}