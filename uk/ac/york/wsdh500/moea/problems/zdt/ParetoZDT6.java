package uk.ac.york.wsdh500.moea.problems.zdt ;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT6 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoZDT6 extends ZDT6 implements ParetoSet
{
	private static final long serialVersionUID = 3990455469988084970L ;

	public ParetoZDT6()
	{
		this( 10 ) ;
	}

	public ParetoZDT6( Integer numberOfVariables )
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
		// https://sop.tik.ee.ethz.ch/download/supplementary/testproblems/zdt6/
		double f2 = Double.NaN ;
		if( 0.2807753191 <= f1 && f1 <= 1 )
			f2 = 1 - Math.pow( f1 , 2 ) ;

		return f2 ;
	}
}
