package uk.ac.york.wsdh500.moea.problems ;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.JMetalException ;

import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoDTLZ4 extends DTLZ4 implements ParetoSet
{
	private static final long serialVersionUID = 2024315002961667813L ;

	public ParetoDTLZ4()
	{
		this( 12 , 2 ) ;
		setName( super.getName() ) ;
	}

	public ParetoDTLZ4( Integer numberOfVariables , Integer numberOfObjectives ) throws JMetalException
	{
		super( numberOfVariables , numberOfObjectives ) ;
	}

	@Override
	public boolean inSet( DoubleSolution solution )
	{
		return inSet( solution , 0.0 ) ;
	}

	@Override
	public boolean inSet( DoubleSolution solution , double ε )
	{
		double f1 = solution.getObjective( 0 ) ;
		double f2 = solution.getObjective( 1 ) ;

		return ParetoUtils.member( this , f1 , f2 , ε ) ;
	}

	@Override
	public double function( double f1 )
	{
		// https://people.ee.ethz.ch/~sop/download/supplementary/testproblems/dtlz4/
		return Math.sqrt( 1 - Math.pow( f1 , 2 ) ) ;
	}
}
