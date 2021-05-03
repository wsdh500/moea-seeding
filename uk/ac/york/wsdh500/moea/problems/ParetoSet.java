package uk.ac.york.wsdh500.moea.problems ;

import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

public interface ParetoSet
{
	/**
	 * Is this solution in the set of points in the true Pareto set ?
	 * @param solution	-	DoubleSolution to evaluate,
	 * @return			-	boolean indicating membership, almost certainly false due to binary quantisation.
	 */
	public boolean inSet( DoubleSolution solution ) ;

	/**
	 * Is this solution in the set of points close to the Pareto front ?
	 * @param solution	-	DoubleSolution to evaluate,
	 * @param ε			-	margin of error from true Pareto set,
	 * @return			-	boolean indicating membership.
	 */
	public boolean inSet( DoubleSolution solution , double ε ) ;

	/**
	 * Function representing the true Pareto set.
	 * @param f1		-	argument
	 * @return			-	f2, or Double.NaN if f2 is not in the Pareto set.
	 */
	public double function( double f1 ) ;
}
