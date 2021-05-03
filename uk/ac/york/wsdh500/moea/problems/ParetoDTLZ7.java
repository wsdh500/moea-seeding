package uk.ac.york.wsdh500.moea.problems ;

import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7 ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.JMetalException ;

import uk.ac.york.wsdh500.moea.utils.ParetoUtils ;

public class ParetoDTLZ7 extends DTLZ7 implements ParetoSet
{
	private static final long serialVersionUID = 2024315002961667813L ;

	public ParetoDTLZ7()
	{
		this( 22 , 2 ) ;
		setName( super.getName() ) ;
	}

	public ParetoDTLZ7( Integer numberOfVariables , Integer numberOfObjectives ) throws JMetalException
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
		// https://sop.tik.ee.ethz.ch/download/supplementary/testproblems/dtlz7/
		double f2 = Double.NaN ;
		if
		(
				( 0 <= f1 && f1 <= 0.2514118360 )
			||	( 0.6316265307 < f1 && f1 <= 0.8594008566 )
			||	( 1.3596178367 < f1 && f1 <= 1.5148392681 )
			||	( 2.0518383519 < f1 && f1 <= 2.116426807 )
		)
			f2 = 4 - f1 * ( 1 + Math.sin(  3 * Math.PI * f1  ) ) ;
		return f2 ;
	}
}
