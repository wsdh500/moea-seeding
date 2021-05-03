package uk.ac.york.wsdh500.moea.utils ;

import uk.ac.york.wsdh500.moea.experiment.ExperimentPaths ;

import java.io.FileNotFoundException ;
import java.util.List ;

import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.front.Front ;
import org.uma.jmetal.util.front.impl.ArrayFront ;
import org.uma.jmetal.util.front.util.FrontNormalizer ;
import org.uma.jmetal.util.front.util.FrontUtils ;

import uk.ac.york.wsdh500.moea.experiment.SeedExperiment ;
import uk.ac.york.wsdh500.moea.problems.ParetoSet ;

public class TraceUtils implements ExperimentPaths
{
	private Problem<DoubleSolution> problem ;

	private List<GenericIndicator<DoubleSolution>> indicators ;
	private double[][] quality_indicators ;
	private double[][][] fronts ;
	private int ticker = 0 ;

	private boolean dump_fronts = false ;
	
	private FrontNormalizer frontNormalizer ;

	public TraceUtils( Problem<DoubleSolution> problem , String referenceParetoFront , int populationSize , int iterations , boolean dump_fronts )
	{
		this.problem = problem ;

		Front referenceFront = null ;
		try
		{
			referenceFront = new ArrayFront( referenceParetoFront , "," ) ;
		}
		catch( FileNotFoundException e )
		{
			throw new RuntimeException( e ) ;
		}
		frontNormalizer = new FrontNormalizer( referenceFront ) ;
		Front normalizedReferenceFront = frontNormalizer.normalize( referenceFront ) ;

		indicators = SeedExperiment.indicators() ;
		for( GenericIndicator<DoubleSolution> indicator : indicators )
			indicator.setReferenceParetoFront( normalizedReferenceFront ) ;

		int size = problem instanceof ParetoSet ? indicators.size() + 1 : indicators.size() ;
		quality_indicators = new double[ iterations ][ size ] ;

		this.dump_fronts = dump_fronts ;
		if( dump_fronts )
			fronts  = new double[ iterations ][ populationSize ][ problem.getNumberOfObjectives() ] ;
	}

	public void trace( List<DoubleSolution> population )
	{
		Front normalizedFront = frontNormalizer.normalize( new ArrayFront( population ) ) ;
		List normalizedPopulation = FrontUtils.convertFrontToSolutionList( normalizedFront ) ;
		for( int i = 0 ; i < indicators.size() ; i++ )
		{
			GenericIndicator<DoubleSolution> indicator = indicators.get( i ) ;
			quality_indicators[ ticker ][ i ] = indicator.evaluate( normalizedPopulation ) ;
		}

		if( problem instanceof ParetoSet )
		{
			ParetoSet fs = ( ParetoSet )problem ;
			double score = 0 ;
			for( DoubleSolution solution : population )
			{
				if( fs.inSet( solution , 0.01 ) )
					score++ ;
			}
			score /= ( double )population.size() ;
			quality_indicators[ ticker ][ indicators.size() ] = score ;
		}

		if( dump_fronts )
			for( int j = 0 ; j < population.size() ; j++ )
				for( int i = 0 ; i < problem.getNumberOfObjectives() ; i++ )
					fronts[ ticker ][ j ][ i ] = population.get( j ).getObjective( i ) ;

		ticker++ ;
	}

	public void dump( String algorithm )
	{
		String path = "_" + algorithm + "_" + problem.getName() ;
		FileUtils.write_quality_indicators( quality_indicators , EXPERIMENT_ROOT + System.currentTimeMillis() + path + ".csv" ) ;

		if( dump_fronts )
		{
			for( double[][] front : fronts )
			{
				FileUtils.write_csv( front , EXPERIMENT_ROOT + System.nanoTime() + path + "#FRONT.csv" ) ;
				try
				{
					Thread.sleep( 1 ) ;
				}
				catch( InterruptedException e )
				{
					e.printStackTrace() ;
				}
			}
		}
	}
}
