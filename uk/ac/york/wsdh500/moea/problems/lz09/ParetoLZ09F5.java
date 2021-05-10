package uk.ac.york.wsdh500.moea.problems.lz09 ;

import org.uma.jmetal.problem.multiobjective.lz09.LZ09F5 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;
import uk.ac.york.wsdh500.moea.utils.ParetoUtils;

public class ParetoLZ09F5 extends LZ09F5 implements ParetoSet
{
	private static final long serialVersionUID = 5917715835055806155L ;

	public ParetoLZ09F5()
	{
		this( 21 , 1 , 26 ) ;
	}

	public ParetoLZ09F5( Integer ptype , Integer dtype , Integer ltype )
	{
		super( ptype , dtype , ltype ) ;
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
		// org.uma.jmetal.problem.multiobjective.lz09.LZ09	type = 21
		return 1 - Math.sqrt( f1 ) ;
	}
}