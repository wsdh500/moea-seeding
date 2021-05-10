package uk.ac.york.wsdh500.moea.problems.dtlz ;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.JMetalException ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoDTLZ3 extends DTLZ3 implements ParetoSet
{
	private static final long serialVersionUID = 2024315002961667813L ;

	public ParetoDTLZ3()
	{
		this( 12 , 2 ) ;
		setName( super.getName() ) ;
	}

	public ParetoDTLZ3( Integer numberOfVariables , Integer numberOfObjectives ) throws JMetalException
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
		// https://sop.tik.ee.ethz.ch/download/supplementary/testproblems/dtlz3/
		return Math.sqrt( 1 - Math.pow( f1 , 2 ) ) ;
	}
}
