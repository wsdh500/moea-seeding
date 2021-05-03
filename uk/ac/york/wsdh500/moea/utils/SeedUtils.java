package uk.ac.york.wsdh500.moea.utils ;

import java.util.ArrayList ;
import java.util.List ;

import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.random.SecurePRNG ;

public class SeedUtils implements SeedTypes
{
	/** from SBXCrossOver, EPS defines the minimum difference allowed between real values */
	private static final double EPS = 1.0e-14 ;
	/** The next value up from EPS *should* be the minimum required to get crossed over. */
	private static final double NEPS = Math.nextUp( EPS ) ;

//////////////////////////////////////////////////////////////// PRIVATE CONSTRUCTOR

	private SeedUtils(){}

//////////////////////////////////////////////////////////////// HELPER FUNCTIONS

	public static List<DoubleSolution> createStochasticPopulation( Problem<DoubleSolution> problem , int populationSize )
	{
		return createPopulation( problem , populationSize , RANDOM ) ;
	}

	public static List<DoubleSolution> createLinearPopulation( Problem<DoubleSolution> problem , int populationSize )
	{
		return createPopulation( problem , populationSize , LINEAR ) ;
	}

	public static List<DoubleSolution> createLogarithmicPopulation( Problem<DoubleSolution> problem , int populationSize )
	{
		return createPopulation( problem , populationSize , LOGARITHMIC ) ;
	}

	public static List<DoubleSolution> createTwistedPopulation( Problem<DoubleSolution> problem , int populationSize )
	{
		return createPopulation( problem , populationSize , LOGARITHMIC | TWISTED ) ;
	}

	public static List<DoubleSolution> createOrthogonalPopulation( Problem<DoubleSolution> problem , int populationSize )
	{
		return createPopulation( problem , populationSize , RANDOM | ORTHOGONAL ) ;
	}

//////////////////////////////////////////////////////////////// LOGARITHM

	private static double[] logarithmic_fill_internal( int n , double min , double max )
	{
		double[] w = new double[ n ] ;

		if( max < min )
			return logarithmic_fill_internal( n , max , min ) ;
		if( max == min )
			return w ;

		double log_min = Math.log( min ) ;
		double log_max = Math.log( max ) ;
		double log_range = log_max - log_min ;
		double log_step = log_range / n ;

		double position = log_max ;
		for( int j = 0 ; j < n ; j++ )
		{
			double x = Math.exp( position ) ;
			x = x > max ? max : x ;
			x = x < min ? min : x ;
			w[ j ] = x ;
			position -= log_step ;
		}

		return w ;
	}

	private static double[] logarithmic_fill( int n , double min , double max )
	{
		double[] w = new double[ n ] ;

		if( max < min )
			return logarithmic_fill( n , max , min ) ;
		if( max == min )
			return w ;

		if( min < 0 && max < 0 )
		{
			w = logarithmic_fill( n , -max , -min ) ;
			for( int i = 0 ; i < n ; i++ )
				w[ i ] = -w[ i ] ;

			return w ;
		}

		if( min < 0 )
		{
			double range = Math.abs( max - min ) ;
			int m = ( int )Math.round( ( -min * n ) / range ) ;
			double[] u = logarithmic_fill( m , NEPS , -min ) ;
			for( int i = 0 ; i < m ; i++ )
				w[ i ] = -u[ i ] ;
			double[] v = logarithmic_fill( n - m , NEPS , max ) ;
			for( int i = 0 ; i < v.length ; i++ )
				w[ m + i ] = v[ i ] ;

			return w ;
		}

		if( min <= EPS )
			min = NEPS ;

		return logarithmic_fill_internal( n , min , max ) ;
	}

//////////////////////////////////////////////////////////////// TWIST

	public static void twist( double[][] w )
	{
		int n = w.length ;			// population size
		int m = w[ 0 ].length ;		// number of variables

		int steps = n / m ;

		for( int i = 1 ; i < m ; i++ )
			twist_variable( w , i , i * steps ) ;
	}

	public static void twist_variable( double[][] w , int column , int steps )
	{
		int n = w.length ;

		for( int k = 0 ; k < steps ; k++ )
		{
			for( int j = 0 ; j < ( n - 1 ) ; j++ )
				w[ j ][ column ] = w[ j + 1 ][ column ] ;

			w[ n - 1 ][ column ] = w[ 0 ][ column ] ;
		}
	}

//////////////////////////////////////////////////////////////// ORTHOGONAL

	public static double[][] orthogonalise( double[][] w )
	{
		int n = w.length ;
		int m = w[ 0 ].length ;
		double[][] v = new double[ n ][ m ] ;

		for( int j = 0 ; j < n ; j++ )
		{
			for( int i = 0 ; i < m ; i++ )
			{
				v[ j ][ i ] = w[ j ][ i ] ;

				for( int k = 0 ; k < j ; k++ )
				{
					double dividend = inner_product( v[ k ] , w[ j ] ) ;
					double divisor = inner_product( v[ k ] , v[ k ] ) ;
					double quotient = dividend / divisor ;
					v[ j ][ i ] -= quotient * v[ k ][ i ] ;
				}
			}
		}

		return v ;
	}

	private static double inner_product( double[] a , double[] b )
	{
		double sum = 0 ;

		for( int i = 0 ; i < a.length ; i++ )
			sum += a[ i ] * b[ i ] ;

		return sum ;
	}

	public static double map( double value , double istart , double istop , double ostart , double ostop )
    {
            double i = istop - istart ;
            double j = 0 ;
            if( i != 0 )
            	j =  ( value - istart ) / i ;
            double m = ostart + ( ostop - ostart ) * j ;

            return m ;
    }

	private static double[][] remap( DoubleSolution solution , double[][] w )
	{
		int n = w.length ;
		int m = w[ 0 ].length ;

		double[][] v = new double[ n ][ m ] ;

		double[][] u = new double[ m ][ 2 ] ;
		for( int i = 0 ; i < m ; i++ )
		{
			double min = Double.MAX_VALUE ;
			double max = Double.MIN_NORMAL ;
			for( int j = 0 ; j < n ; j++ )
			{
				if( w[ j ][ i ] < min )
					min = w[ j ][ i ] ;
				if( w[ j ][ i ] > max )
					max = w[ j ][ i ] ;
			}
			u[ i ][ 0 ] = min ;
			u[ i ][ 1 ] = max ;
		}

		for( int j = 0 ; j < n ; j++ )
			for( int i = 0 ; i < m ; i++ )
				v[ j ][ i ] =
				map
				(
						w[ j ][ i ] ,
						u[ i ][ 0 ] , u[ i ][ 1 ] ,
						solution.getLowerBound( i ) , solution.getUpperBound( i )
				) ;

		return v ;
	}

//////////////////////////////////////////////////////////////// LINEAR

	private static double[] linear_fill( int n , double min , double max )
	{
		double[] w = new double[ n ] ;

		if( max < min )
			return linear_fill( n , max , min ) ;
		if( max == min )
			return w ;

		if( min < 0 && max > 0 )
		{
			double range = Math.abs( max - min ) ;
			int m = ( int )Math.round( ( -min * n ) / range ) ;
			double[] u = linear_fill( m , min , -NEPS ) ;
			for( int i = 0 ; i < m ; i++ )
				w[ i ] = u[ i ] ;
			double[] v = linear_fill( n - m , NEPS , max ) ;
			for( int i = 0 ; i < v.length ; i++ )
				w[ m + i ] = v[ i ] ;

			return w ;
		}

		if( min >=0 && min <= EPS )
			min = NEPS ;
		else if( max >=0 && max <= EPS )
			max = NEPS ;

		double range = max - min ;
		double step = range / n ;

		double x = min ;
		for( int j = 0 ; j < n ; j++ )
		{
			x = x > max ? max : x ;
			x = x < min ? min : x ;
			w[ j ] = x ;
			x += step ;
		}

		return w ;
	}

//////////////////////////////////////////////////////////////// RANDOM

	private static double[] random_fill( int n , double min , double max )
	{
		double[] w = new double[ n ] ;

		SecurePRNG random = new SecurePRNG() ;
		for( int j = 0 ; j < n ; j++ )
			w[ j ] = random.nextDouble( min , max ) ;

		return w ;
	}

//////////////////////////////////////////////////////////////// CREATE POPULATION

	public static List<DoubleSolution> createPopulation( Problem<DoubleSolution> problem , int populationSize , int type )
	{
		List<DoubleSolution> population = new ArrayList<>( populationSize ) ;

		int n_variables = problem.getNumberOfVariables() ;

		DoubleSolution solution = problem.createSolution() ;
		double[][] u = new double[ n_variables ][] ;

		for( int i = 0 ; i < n_variables ; i++ )
		{
			double min = solution.getLowerBound( i ) ;
			double max = solution.getUpperBound( i ) ;
			double[] w = null ;
			int fill_type = type & 3 ;
			switch( fill_type )
			{
				case RANDOM :
					w = random_fill( populationSize , min , max ) ;
					break ;
				case LINEAR :
					w = linear_fill( populationSize , min , max ) ;
					break ;
				case LOGARITHMIC :
					w = logarithmic_fill( populationSize , min , max ) ;
					break ;
				default :
					throw new IllegalArgumentException( "Unknown seed fill type." ) ;
			}
			u[ i ] = w ;
		}

		u = SeedUtils.transpose( u ) ;

		if( ( type & TWISTED ) == TWISTED )
			twist( u ) ;

		if( ( type & ORTHOGONAL ) == ORTHOGONAL )
		{
			u = orthogonalise( u ) ;
			u = remap( solution , u ) ;
		}

		for( int j = 0 ; j <  populationSize ; j++ )
		{
			double[] w = u[ j ] ;
			solution = problem.createSolution() ;
			for( int i = 0 ; i < n_variables ; i++ )
				solution.setVariable( i , w[ i ] ) ;
			population.add( solution ) ;
		}

		return population ;
	}

	public static double[][] transpose( double[][] input )
	{
		int n = input.length ;
		int m = input[ 0 ].length ;

		double[][] output = new double[ m ][ n ] ;

		for( int j = 0 ; j < n ; j++ )
			for( int i = 0 ; i < m ; i++ )
				output[ i ][ j ] = input[ j ][ i ] ;

		return output ;
	}
}
