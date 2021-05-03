package uk.ac.york.wsdh500.moea.utils ;

import java.io.File ;
import java.util.Arrays ;
import java.util.List;
import java.util.Map ;
import java.util.Vector ;
import java.util.regex.Pattern ;

import org.uma.jmetal.lab.experiment.util.ExperimentProblem ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.experiment.SeedExperiment ;
import uk.ac.york.wsdh500.moea.experiment.ExperimentPaths ;

public class StatsUtils implements ExperimentPaths
{
	private static boolean CREATE_STATS		= true ;
	private static boolean CREATE_PROBLEMS	= false ;

	private StatsUtils(){}
	
	private static final String BASE_DIR = EXPERIMENT_ROOT ;

	/** Index of first quartile */
	public static final int Q1 = 0 ;
	/** Index of second quartile */
	public static final int Q2 = 1 ;

	private static final String MEAN			= "MEAN" ;
	private static final String MEDIAN			= "MEDIAN" ;
	private static final String SD				= "SD" ;
	private static final String FIRST_QUARTILE	= "Q1" ;
	private static final String SECOND_QUARTILE	= "Q2" ;

	public static double[][] mean_frames( String key , double[][][] frames , boolean write )
	{
		int n = frames[ 0 ].length ;
		int m = frames[ 0 ][ 0 ].length ;
		double[][] averages = new double[ n ][ m ] ;
		for( int j = 0 ; j < n ; j++ )
		{
			for( int i = 0 ; i < m ; i++ )
			{
				double sum = 0 ;
				for( int k = 0 ; k < frames.length ; k++ )
					sum += frames[ k ][ j ][ i ] ;
				averages[ j ][ i ] = sum / frames.length ;
			}
		}

		if( write )
			FileUtils.write_quality_indicators( averages , generatePath( key ) + "#" + MEAN + ".csv" ) ;
		
		return averages ;
	}

	public static double[][] median_frames( String key , double[][][] frames , boolean write )
	{
		int n = frames[ 0 ].length ;
		int m = frames[ 0 ][ 0 ].length ;
		double[][] averages = new double[ n ][ m ] ;
		double[] locate = new double[ frames.length ] ;
		for( int j = 0 ; j < n ; j++ )
		{
			for( int i = 0 ; i < m ; i++ )
			{
				for( int k = 0 ; k < frames.length ; k++ )
					locate[ k ] = frames[ k ][ j ][ i ] ;
				averages[ j ][ i ] = median( locate )  ;
			}
		}

		if( write )
			FileUtils.write_quality_indicators( averages , generatePath( key )  + "#" + MEDIAN + ".csv" ) ;
		
		return averages ;
	}
	
	public static double[][] sd_frames( String key , double[][][] frames , boolean write )
	{
		int n = frames[ 0 ].length ;
		int m = frames[ 0 ][ 0 ].length ;
		double[][] averages = new double[ n ][ m ] ;
		double[] locate = new double[ frames.length ] ;
		for( int j = 0 ; j < n ; j++ )
		{
			for( int i = 0 ; i < m ; i++ )
			{
				for( int k = 0 ; k < frames.length ; k++ )
					locate[ k ] = frames[ k ][ j ][ i ] ;
				averages[ j ][ i ] = sd( locate )  ;
			}
		}

		if( write )
			FileUtils.write_quality_indicators( averages , generatePath( key ) + "#" + SD + ".csv" ) ;
		
		return averages ;
	}

	public static String generatePath( String key )
	{
		return BASE_DIR + SeedExperiment.date() + "_" + key ;
	}

	public static double[][][] interquartile_frames( String key , double[][][] frames , boolean write )
	{
		int n = frames[ 0 ].length ;
		int m = frames[ 0 ][ 0 ].length ;
		double[][][] iqs = new double[ 2 ][ n ][ m ] ;
		double[] locate = new double[ frames.length ] ;
		for( int j = 0 ; j < n ; j++ )
		{
			for( int i = 0 ; i < m ; i++ )
			{
				for( int k = 0 ; k < frames.length ; k++ )
					locate[ k ] = frames[ k ][ j ][ i ] ;
				double[] interquartile = interquartile( locate ) ;
				iqs[ Q1 ][ j ][ i ] = interquartile[ Q1 ]  ;
				iqs[ Q2 ][ j ][ i ] = interquartile[ Q2 ]  ;
			}
		}

		if( write )
		{
			FileUtils.write_quality_indicators( iqs[ Q1 ] , generatePath( key ) + "#" + FIRST_QUARTILE + ".csv" ) ;
			FileUtils.write_quality_indicators( iqs[ Q2 ] , generatePath( key ) + "#" + SECOND_QUARTILE + ".csv" ) ;
		}
		
		return iqs ;
	}

	public static double arithmetic_mean( double[] values )
	{
		double mean = 0 ;

		for( double value : values )
			mean += value ;
		mean /= ( double )values.length ;

		return mean ;
	}

	public static double median( double[] locate )
	{
		double median = 0 ;

		Arrays.sort( locate ) ;
		int halfway = locate.length / 2 ;
		if( locate.length % 2 == 0 )
			median = ( locate[ halfway - 1 ] + locate[ halfway ] ) / 2.0  ;
		else
			median = locate[ halfway ]  ;

		return median ;
	}

	public static double[] interquartile( double[] locate )
	{
		double[] interquartile = new double[ 2 ] ;

		Arrays.sort( locate ) ;
		int halfway = locate.length / 2 ;

		int i = 0 ;
		double[] first = new double[ halfway ] ;
		for( ; i < first.length ; i++ )
			first[ i ] = locate[ i ] ;
		interquartile[ Q1 ] = median( first ) ;

		double[] second = new double[ locate.length - first.length ] ;
		for( int j = 0 ; j < second.length ; j++ , i++ )
			second[ j ] = locate[ i ] ;
		interquartile[ Q2 ] = median( second ) ;

		return interquartile ;
	}

	public static double variance( double[] values )
	{
		double variance = 0 ;
		
		double mean = arithmetic_mean( values ) ;

		for( double value : values )
		{
			value -= mean ;
			value *= value ;
			variance += value ;
		}
		variance /= values.length ;

		return variance ;
	}

	public static double sd( double[] values )
	{
		return Math.sqrt( variance( values ) ) ;
	}

	public static void test( String[] args )
	{
		double[] values = new double[ args.length ] ;
		int i = 0 ;
		for( String arg : args )
			values[ i++ ] = Double.parseDouble( arg ) ;

		double mean = arithmetic_mean( values ) ;
		double median = median( values ) ;
		double[] iqs = interquartile( values ) ;
		double q1 = iqs[ Q1 ] ;
		double q2 = iqs[ Q2 ] ;
		double variance = variance( values ) ;
		double sd = sd( values ) ;

		for( double instance : values )
			System.out.print( instance + "\t" ) ;
		System.out.println() ;

		System.out.println( "mean : " + mean ) ;
		System.out.println( "median : " + median ) ;
		System.out.println( "q1 : " + q1 ) ;
		System.out.println( "q2 : " + q2 ) ;
		System.out.println( "variance : " + variance ) ;
		System.out.println( "sd : " + sd ) ;
	}

	public static void main( String[] args )
	{	
		if( CREATE_STATS )
		{
			create_stats( BASE_DIR ) ;
		}
		else if( CREATE_PROBLEMS )
		{
			List<ExperimentProblem<DoubleSolution>> problems = SeedExperiment.getProblems() ;
			for( ExperimentProblem<DoubleSolution> problem : problems )
				stats_for_problem( problem.getProblem()  , BASE_DIR ) ;
		}
		System.out.println( "Done." ) ;
	}
	
	public static void create_stats( String search_dir )
	{
		Pattern pattern = FileUtils.file_pattern() ;
		Map<String,Vector<File>> map = FileUtils.get_files( search_dir , pattern ) ;
		for( String key : map.keySet() )
		{
			System.out.println( "Processing : " + key ) ;

			Vector<File> files = map.get( key ) ;
			double[][][] frames = new double[ files.size() ][][] ;
			for( int i = 0 ; i < files.size() ; i++ )
			{
				CSV csv = FileUtils.parse_csv( files.elementAt( i ) ) ;
				frames[ i ] = csv.values ;
			}

			System.out.println( MEDIAN ) ;
			StatsUtils.median_frames( key , frames , true ) ;

			System.out.println( FIRST_QUARTILE + " + " + SECOND_QUARTILE ) ;
			StatsUtils.interquartile_frames( key , frames , true ) ;

			System.out.println( MEAN ) ;
			StatsUtils.mean_frames( key , frames , true ) ;

			System.out.println( SD ) ;
			StatsUtils.sd_frames( key , frames , true ) ;
		}
		System.out.println( "Done." ) ;
	}

	public static void stats_for_problem( Problem<DoubleSolution> problem , String search_dir )
	{
		System.out.println( "Loading files for " + problem.getName() + "..." ) ;
		Pattern pattern = FileUtils.file_pattern( problem ) ;
		Map<String,Vector<File>> map = FileUtils.get_files( search_dir , pattern ) ;
		System.out.println( "Files loaded." ) ;

		double[][][] complete_history = new double[ map.keySet().size() ][][] ;
		String[] names = new String[ map.keySet().size() ] ;
		String[] headers = null ;
		int p = 0 ;
		for( String key : map.keySet() )
		{
			Vector<File> files = map.get( key ) ;
			double[][][] frames = new double[ files.size() ][][] ;
			for( int i = 0 ; i < files.size() ; i++ )
			{
				File file = files.elementAt( i ) ;
				CSV csv = FileUtils.parse_csv( file ) ;
				frames[ i ] = csv.values ;
				if( headers == null )
					headers = csv.headers ;
			}

			double[][] median = StatsUtils.median_frames( key , frames , false ) ;
			complete_history[ p ] = median ;

			String[] split = key.split( "-" ) ;
			names[ p ] = split[ 0 ] ;
			p++ ;
		}
		
		//

		for( int i = 0 ; i < headers.length ; i++ )
		{
			String header = headers[ i ] ;
    		StringBuffer buffer = new StringBuffer() ;
   
    		for( String name : names )
    			buffer.append( name + "," ) ;	
    		buffer.deleteCharAt( buffer.length() - 1 ) ; // fix up stray comma
    		buffer.append( "\n" ) ;
    
    		for( int j = 0 ; j < complete_history[ 0 ].length ; j++ )
    		{
    			for( int k = 0 ; k < complete_history.length ; k++ )
    				buffer.append( ( float )complete_history[ k ][ j ][ i ] + "," ) ;
    			buffer.deleteCharAt( buffer.length() - 1 ) ; // fix up stray comma
    			buffer.append( "\n" ) ;
    		}

    		System.out.print( "Writing " + problem.getName() + "~" + header + " ... " ) ;
    		FileUtils.write_lines( buffer.toString() , generatePath( problem.getName() ) + "~" + header + ".csv" ) ;
    		System.out.println( "Done." ) ;
		}

		System.out.println( "Done." ) ;
	}
}
