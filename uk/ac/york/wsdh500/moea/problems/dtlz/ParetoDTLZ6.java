package uk.ac.york.wsdh500.moea.problems.dtlz ;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.JMetalException ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoDTLZ6 extends DTLZ6 implements ParetoSet
{
	private static final long serialVersionUID = 2024315002961667813L ;

	public ParetoDTLZ6()
	{
		this( 12 , 2 ) ;
		setName( super.getName() ) ;
	}

	public ParetoDTLZ6( Integer numberOfVariables , Integer numberOfObjectives ) throws JMetalException
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
		return Math.sqrt( 1 - Math.pow( f1 , 2 ) ) ;
	}
}
