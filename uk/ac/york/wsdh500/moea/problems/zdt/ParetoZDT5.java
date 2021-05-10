package uk.ac.york.wsdh500.moea.problems.zdt ;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT5 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoZDT5 extends ZDT5 implements ParetoSet
{
	private static final long serialVersionUID = 3990455469988084970L ;

	public ParetoZDT5()
	{
		this( 11 ) ;
	}

	public ParetoZDT5( Integer numberOfVariables )
	{
		super( numberOfVariables ) ;
		setName( super.getName() ) ;
	}

	@Override
	public boolean inSet( DoubleSolution solution , double ε )
	{
		double f1 = solution.getObjective( 0 ) ;
		double f2 = solution.getObjective( 1 ) ;

		return ParetoUtils.member( this , f1 , f2 , ε ) ;
	}

	@Override
	public boolean inSet( DoubleSolution solution )
	{
		return inSet( solution , 0.0 ) ;
	}

	@Override
	public double function( double f1 )
	{
		return 1 / f1 ;
	}
}
