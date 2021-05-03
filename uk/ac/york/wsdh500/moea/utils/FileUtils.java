package uk.ac.york.wsdh500.moea.utils ;

import java.io.BufferedInputStream ;
import java.io.BufferedOutputStream ;
import java.io.BufferedReader ;
import java.io.BufferedWriter ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileNotFoundException ;
import java.io.FileOutputStream ;
import java.io.FileReader ;
import java.io.FileWriter ;
import java.io.FilenameFilter ;
import java.io.IOException ;
import java.util.Comparator ;
import java.util.HashMap ;
import java.util.List;
import java.util.Map ;
import java.util.Vector ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;
import java.util.zip.ZipEntry ;
import java.util.zip.ZipOutputStream ;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

import uk.ac.york.wsdh500.moea.experiment.SeedExperiment ;

public class FileUtils
{
	private static final String algorithm_pattern = "([\\w\\d +]+)" ;
	private static final String problem_pattern = "([\\w\\d]+)" ;

	public static Pattern file_pattern( AbstractGeneticAlgorithm<DoubleSolution,List<DoubleSolution>> algorithm )
	{
		return file_pattern( algorithm.getName() , problem_pattern ) ;
	}

	public static Pattern file_pattern( Problem<DoubleSolution> problem )
	{
		return file_pattern( algorithm_pattern , "(" + problem.getName() + ")" ) ;
	}

	public static Pattern file_pattern( Problem<DoubleSolution> problem , String type )
	{
		return file_pattern( algorithm_pattern , "(" + problem.getName() + ")" + type ) ;
	}

	public static Pattern file_pattern( AbstractGeneticAlgorithm<DoubleSolution,List<DoubleSolution>> algorithm , Problem<DoubleSolution> problem )
	{
		return file_pattern( "(" + algorithm.getName() + ")" , "(" + problem.getName() + ")" ) ;
	}

	private static Pattern file_pattern( String algorithm , String problem )
	{
		String pattern_string = "\\d+" + "_" + algorithm + "_" + problem + ".csv" ;
		Pattern pattern = Pattern.compile( pattern_string ) ;

		return pattern ;
	}
	
	public static Pattern file_pattern()
	{
		String pattern_string = "\\d+" + "_" + algorithm_pattern + "_" + problem_pattern + ".csv" ;
		Pattern pattern = Pattern.compile( pattern_string ) ;

		return pattern ;
	}

	private static final Comparator<File> ordered_comparison = new Comparator<File>()
	{
		@Override
		public int compare( File o1 , File o2 )
		{
			return o1.getName().compareTo( o2.getName() ) ;
		}
	} ;

	private FileUtils(){}

	public static void zip( String path , boolean delete )
	{
		if( path.endsWith( "/" ) )
			path = path.substring( 0 , path.length() - 1 ) ;

		File zip_dir = new File( path ) ;
		if( zip_dir.exists() && zip_dir.canRead() && zip_dir.isDirectory() )
		{
			File zip_file = new File( path + ".zip" ) ;

			FileOutputStream fos = null ;
			try
			{
				fos = new FileOutputStream( zip_file ) ;
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace() ;
			}

			if( fos != null )
			{
				BufferedOutputStream bos = new BufferedOutputStream( fos ) ;
				ZipOutputStream zos = new ZipOutputStream( bos ) ;
				zos.setLevel( 9 ) ;
				try
				{
					zip_file( zip_dir , "" , zos ) ;
					zos.flush() ;
					bos.flush() ;
					fos.flush() ;
					zos.close() ; // essential !
					bos.close() ;
					fos.close() ;
				}
				catch( IOException e )
				{
					e.printStackTrace() ;
				}

				if( delete )
					delete( zip_dir ) ;
			}
		}
	}

	private static void delete( File file )
	{
		if( file.isDirectory() )
		{
			for( File sub : file.listFiles() )
				delete( sub ) ;
		}

		file.delete() ;
	}

	private static void zip_file( File file , String path , ZipOutputStream zos ) throws IOException
	{
		if( file.isDirectory() )
		{
			String file_name = path + file.getName() + "/" ;
			zos.putNextEntry( new ZipEntry( file_name ) ) ;
			zos.closeEntry() ;

			File[] files = file.listFiles() ;
			for( File zip_file : files )
				zip_file( zip_file , file_name , zos ) ;
		}
		else
		{
			FileInputStream fis = new FileInputStream( file ) ;
			BufferedInputStream bis = new BufferedInputStream( fis ) ;
			zos.putNextEntry( new ZipEntry( path + file.getName() ) ) ;

			byte[] buffer = new byte[ 512 ] ;
			int count = 0 ;
			while( ( count = bis.read( buffer ) ) >= 0 )
				zos.write( buffer , 0 , count ) ;
			zos.flush() ;
			fis.close() ;

			zos.closeEntry() ;
		}
	}

	public static String doubles_to_csv( double[][] history )
	{
		StringBuffer buffer = new StringBuffer() ;

		for( int j = 0 ; j < history.length ; j++ )
		{
			double[] row = history[ j ] ;
			for( int i = 0 ; i < row.length ; i++ )
				buffer.append( ( float )row[ i ] + "," ) ;
			buffer.deleteCharAt( buffer.length() - 1 ) ; // fix up stray comma
			buffer.append( "\n" ) ;
		}

		return buffer.toString() ;
	}

	public static void write_quality_indicators( double[][] history , String path )
	{
		StringBuffer buffer = new StringBuffer() ;
		List<GenericIndicator<DoubleSolution>> indicators = SeedExperiment.indicators() ;
		for( GenericIndicator<DoubleSolution> indicator : indicators )
			buffer.append( indicator.getClass().getSimpleName() + "," ) ;	
		if( history[ 0 ].length > indicators.size() )
			buffer.append( "Membership" + "," ) ;
		buffer.deleteCharAt( buffer.length() - 1 ) ; // fix up stray comma
		buffer.append( "\n" ) ;

		buffer.append( doubles_to_csv( history ) ) ;
		write_lines( buffer.toString() , path ) ;
	}

	public static void write_csv( double[][] history , String path )
	{
		write_lines( doubles_to_csv( history ) , path ) ;
	}

	public static void write_lines( String buffer , String path )
	{

		File file = new File( path ) ;
		try
		{
			FileWriter writer = new FileWriter( file ) ;
			BufferedWriter bw = new BufferedWriter( writer ) ;
			bw.write( buffer ) ;
			bw.flush() ;
			bw.close() ;
		}
		catch( IOException e )
		{
			e.printStackTrace() ;
		}
	}

	public static CSV parse_csv( File file )
	{
		return parse_csv( file , false ) ;
	}

	public static CSV parse_csv( File file , boolean skip_headers )
	{
		Vector<String> lines = lines( file.getAbsolutePath() ) ;
		String[] headers = lines.remove( 0 ).split( "," ) ;

		// this method needs rewriting, obviously :D
		if( skip_headers )
			lines.insertElementAt( String.join( "," , headers ), 0 ) ;

		double[][] values = new double[ lines.size() ][ headers.length ] ;

		for( int j = 0 ; j < lines.size() ; j++ )
		{
			String line = lines.get( j ) ;
			String[] split = line.split( "," ) ;
			for( int i = 0 ; i < split.length ; i++ )
				values[ j ][ i ] = Double.parseDouble( split[ i ] ) ;
		}

		return new CSV( headers , values ) ;
	}

	private static Vector<String> lines( String path )
	{
		Vector<String> lines = new Vector<String>() ;

		BufferedReader br = null ;

		try
		{
			br = new BufferedReader( new FileReader( path ) ) ;
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace() ;
		}

		String buffer ;
		while( br != null )
		{
			try
			{
				if( ( buffer = br.readLine() ) != null )
					lines.add( buffer ) ;
				else
					break ;
			}
			catch( IOException e )
			{
				e.printStackTrace() ;
			}
		}

		if( br != null )
		{
			try
			{
				br.close() ;
			}
			catch( IOException e )
			{
				e.printStackTrace() ;
			}
		}

		return lines ;
	}

	public static Map<String,Vector<File>> get_files( String path , Pattern pattern )
	{
		Map<String,Vector<File>> map = new HashMap<String,Vector<File>>() ;

		File file = new File( path ) ;
		if( file.exists() && file.isDirectory() && file.canRead() )
		{
			File[] files = file.listFiles( new FilenameFilter()
			{
				@Override
				public boolean accept( File dir , String name )
				{
					Matcher matcher = pattern.matcher( name ) ;
					boolean matches = matcher.matches() ;
					if( matches )
					{
						String key = matcher.group( 1 ) + "-" + matcher.group( 2 ) ;
						Vector<File> files ;
						if( map.containsKey( key ) )
						{
							files = map.get( key ) ;
						}
						else
						{
							files = new Vector<File>() ;
							map.put( key , files ) ;
						}
						File file = new File( dir.getAbsolutePath() + File.separatorChar + name ) ;
						if( file.exists() && file.canRead() && file.isFile() )
							files.add( file ) ;

					}

					return matches ;
				}
			} ) ;

			for( String key : map.keySet() )
				map.get( key ).sort( ordered_comparison ) ;

		}

		return map ;
	}
}
