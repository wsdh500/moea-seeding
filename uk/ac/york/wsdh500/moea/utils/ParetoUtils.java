package uk.ac.york.wsdh500.moea.utils ;

import uk.ac.york.wsdh500.moea.problems.ParetoSet ;

public class ParetoUtils
{
	private ParetoUtils(){}

	public static boolean member( ParetoSet front , double x , double y , double ε )
	{
		double radians = Math.atan2( y , x ) ;

		double f1 = 0 ;
		boolean in_set = false ;
		for( ; f1 < 1 ; f1 += 0.001 )
		{
			double f2 = front.function( f1 ) ;
			if( f2 == Double.NaN )
				continue ;
			double theta = Math.atan2( f2 , f1 ) ;

			if( radians >= theta - 0.01 && radians <= theta + 0.01 && !in_set )
			{
				double magnitude = Math.sqrt( f1 * f1 + f2 * f2 ) ;
				double comparison = Math.sqrt( y * y + x * x ) ;
				in_set |= comparison >= magnitude - ε && comparison <= magnitude + ε ;
			}
		}

		return in_set ;
	}

}
