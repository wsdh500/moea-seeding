package uk.ac.york.wsdh500.moea.problems.zdt ;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT3 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoZDT3 extends ZDT3 implements ParetoSet
{
	private static final long serialVersionUID = 3990455469988084970L ;

	public ParetoZDT3()
	{
		this( 30 ) ;
	}

	public ParetoZDT3( Integer numberOfVariables )
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
		// https://sop.tik.ee.ethz.ch/download/supplementary/testproblems/zdt3/
		double f2 = Double.NaN ;
		if
		(
				( 0 <= f1 && f1 <= 0.0830015349  )
			||  ( 0.1822287280 < f1 && f1 <= 0.2577623634 )
			||  ( 0.4093136748 < f1 && f1 <= 0.4538821041 )
			||  ( 0.6183967944 < f1 && f1 <= 0.6525117038 )
			||  ( 0.8233317983 < f1 && f1 <= 0.8518328654 )
		)
		{
			f2 = 1 - Math.sqrt( f1 ) - f1 * Math.sin( 10 * Math.PI * f1 ) ;
		}
		return f2 ;
	}
}
