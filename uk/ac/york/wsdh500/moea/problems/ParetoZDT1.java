package uk.ac.york.wsdh500.moea.problems ;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT1 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoZDT1 extends ZDT1 implements ParetoSet
{
	private static final long serialVersionUID = 3990455469988084970L ;

	public ParetoZDT1()
	{
		this( 30 ) ;
	}

	public ParetoZDT1( Integer numberOfVariables )
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
		// https://sop.tik.ee.ethz.ch/download/supplementary/testproblems/zdt1/
		return 1 - Math.sqrt( f1 ) ;
	}
}
