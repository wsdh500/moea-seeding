package uk.ac.york.wsdh500.moea.algorithms.spea2 ;

import java.util.List ;

import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.operator.selection.SelectionOperator ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator ;

import uk.ac.york.wsdh500.moea.utils.TraceUtils ;

public class TraceSPEA2 extends SeededSPEA2
{
	private static final long serialVersionUID = -7593945700739899524L ;

	private TraceUtils trace ;

	public TraceSPEA2
	(
			Problem<DoubleSolution> problem , int maxIterations , int populationSize ,
			CrossoverOperator<DoubleSolution> crossoverOperator , MutationOperator<DoubleSolution> mutationOperator ,
			SelectionOperator<List<DoubleSolution>,DoubleSolution> selectionOperator , SolutionListEvaluator<DoubleSolution> evaluator , 
			int k , String referenceParetoFront , boolean dump_fronts
	)
	{
		super
		(
				problem , maxIterations , populationSize ,
				crossoverOperator , mutationOperator ,
				selectionOperator , evaluator ,
				k
		) ;

		trace = new TraceUtils( problem , referenceParetoFront , populationSize , maxIterations , dump_fronts ) ;
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

		if( iterations == maxIterations )
			trace.dump( this.getName() ) ;
	}
}
