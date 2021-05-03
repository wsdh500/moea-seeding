package uk.ac.york.wsdh500.moea.algorithms.nsgaii ;

import java.util.Comparator ;
import java.util.List ;

import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.operator.selection.SelectionOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator ;

import uk.ac.york.wsdh500.moea.utils.TraceUtils ;

public class TraceNSGAII extends SeededNSGAII
{
	private static final long serialVersionUID = -612288807106694163L ;

	private TraceUtils trace ;

	public TraceNSGAII
	(
			Problem<DoubleSolution> problem ,
			int maxEvaluations , int populationSize , int matingPoolSize , int offspringPopulationSize ,
			CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator , SelectionOperator<List<DoubleSolution>,DoubleSolution> selectionOperator ,
			Comparator<DoubleSolution> dominanceComparator , SolutionListEvaluator<DoubleSolution> evaluator ,
			String referenceParetoFront , boolean dump_fronts
	)
	{
		super
		(
				problem ,
				maxEvaluations , populationSize , matingPoolSize , offspringPopulationSize ,
				crossoverOperator , mutationOperator , selectionOperator ,
				dominanceComparator , evaluator
		) ;

		int iterations = maxEvaluations / populationSize ;
		trace = new TraceUtils( problem , referenceParetoFront , populationSize , iterations , dump_fronts ) ;
	}

	@Override
	protected List<DoubleSolution> evaluatePopulation( List<DoubleSolution> population )
	{
		population = super.evaluatePopulation( population ) ;

		trace.trace( population ) ;

		return population ;
	}

	@Override
	protected void updateProgress()
	{
		super.updateProgress() ;

		if( evaluations == maxEvaluations )
			trace.dump( this.getName() ) ;
	}
}
