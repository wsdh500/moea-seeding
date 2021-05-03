package uk.ac.york.wsdh500.moea.experiment ;

import java.io.IOException ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.uma.jmetal.algorithm.Algorithm ;
import org.uma.jmetal.algorithm.multiobjective.gwasfga.GWASFGA ;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder ;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD ;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder ;
import org.uma.jmetal.algorithm.multiobjective.smpso.jmetal5version.SMPSOBuilder ;
import org.uma.jmetal.lab.experiment.Experiment ;
import org.uma.jmetal.lab.experiment.ExperimentBuilder ;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators ;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms ;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR ;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanTestTables ;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages ;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics ;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR ;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm ;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem ;
import org.uma.jmetal.operator.crossover.CrossoverOperator ;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover ;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover ;
import org.uma.jmetal.operator.mutation.MutationOperator ;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation ;
import org.uma.jmetal.operator.selection.SelectionOperator ;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection ;
import org.uma.jmetal.problem.Problem ;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem ;
import org.uma.jmetal.qualityindicator.impl.Epsilon ;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance ;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus ;
import org.uma.jmetal.qualityindicator.impl.Spread ;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume ;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;
import org.uma.jmetal.util.JMetalLogger ;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive ;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator ;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator ;
import org.uma.jmetal.util.pseudorandom.JMetalRandom ;

import uk.ac.york.wsdh500.moea.algorithms.nsgaii.SeededNSGAIIBuilder ;
import uk.ac.york.wsdh500.moea.algorithms.nsgaii.TraceNSGAIIBuilder ;
import uk.ac.york.wsdh500.moea.algorithms.spea2.SeededSPEA2Builder ;
import uk.ac.york.wsdh500.moea.algorithms.spea2.TraceSPEA2Builder ;
import uk.ac.york.wsdh500.moea.random.MersennePRNG ;
import uk.ac.york.wsdh500.moea.random.PRNGenerator ;
import uk.ac.york.wsdh500.moea.random.SecurePRNG ;
import uk.ac.york.wsdh500.moea.utils.FileUtils ;
import uk.ac.york.wsdh500.moea.utils.SeedTypes ;

/**
 * <a href="https://jmetal.readthedocs.io/en/latest/experimentation.html">
 * https://jmetal.readthedocs.io/en/latest/experimentation.html
 * </a>
 */
public class SeedExperiment implements SeedTypes , ExperimentPaths
{
	private static final int INDEPENDENT_RUNS	= 25 ;
	private static final int CORES				= 6 ;

	// algorithms
	private final static int NSGAII				= 1 ;
	private final static int SMPSO				= 2 ;
	private final static int MOEAD				= 4 ; // see POPULATION_SIZE
	private final static int SPEA2				= 8 ;
	private final static int MOCell				= 16 ;
	private final static int GWASFGA			= 32 ; // implementation issue

	// problems
	private final static int LZ09				= 1 ;
	private final static int WFG				= 2 ;
	private final static int ZDT				= 4 ;
	private final static int DTLZ				= 8 ;
	private final static int RE					= 16 ;

	// configuration
	private static int ALGORITHMS				= NSGAII | SPEA2 ;
	private static int PROBLEMS					= DTLZ | LZ09 | RE | WFG | ZDT ;
	private static boolean TRACE				= true ;
	private static boolean PISA_HV				= true ;
	private static boolean SECURE				= false ;

	private final static int POPULATION_SIZE	= 100 ;
	private final static int ITERATIONS			= 250 ;

//////////////////////////////////////////////////////////// MAIN

	public static void main( String[] args ) throws IOException
	{
		new SeedExperiment() ;
	}

	public SeedExperiment() throws IOException
	{
		PRNGenerator prng = SECURE ? new SecurePRNG() : new MersennePRNG() ;
		JMetalRandom.getInstance().setRandomGenerator( prng ) ;

		String name = "SeedExperiment-" + date() ;
		Experiment<DoubleSolution,List<DoubleSolution>> experiment = new ExperimentBuilder<DoubleSolution,List<DoubleSolution>>( name )
				.setExperimentBaseDirectory( EXPERIMENT_ROOT )
				.setAlgorithmList( getAlgorithms() )
				.setProblemList( getProblems() )
				.setReferenceFrontDirectory( FRONTS_DIR )
				.setOutputParetoFrontFileName( "FUN" )
				.setOutputParetoSetFileName( "VAR" )
				.setIndicatorList( indicators() )
				.setIndependentRuns( INDEPENDENT_RUNS ).setNumberOfCores( CORES ).build() ;

		int a_size = experiment.getAlgorithmList().size() ;
		int p_size = experiment.getProblemList().size() ;
		boolean do_html = ( ( a_size / p_size ) / INDEPENDENT_RUNS ) >= 2 ;

		new ExecuteAlgorithms<DoubleSolution,List<DoubleSolution>>( experiment ).run() ;
		new ComputeQualityIndicators<DoubleSolution,List<DoubleSolution>>( experiment ).run() ;

		new GenerateLatexTablesWithStatistics( experiment ).run() ;
		new GenerateWilcoxonTestTablesWithR<List<DoubleSolution>>( experiment ).run() ;
		new GenerateFriedmanTestTables<List<DoubleSolution>>( experiment ).run() ;
		new GenerateBoxplotsWithR<List<DoubleSolution>>( experiment ).setRows( 3 ).setColumns( 3 ).run() ;
		// ... and, if more than one algorithm ...
		if( do_html )
			new GenerateHtmlPages<List<DoubleSolution>>( experiment ).run() ;

		System.out.println( "Zipping ..." ) ;
		FileUtils.zip( EXPERIMENT_ROOT + name + "/data/" , true ) ;
		FileUtils.zip( EXPERIMENT_ROOT + name + "/r/" , true ) ;

		JMetalLogger.logger.info( "Done !" ) ;
	}

	public static String date()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm") ;
		return sdf.format( new Date() ) ;
	}

//////////////////////////////////////////////////////////// INDICATOR SELECTION

	public static List<GenericIndicator<DoubleSolution>> indicators()
	{
		return List.of
		(
				new Epsilon<DoubleSolution>() ,
				new Spread<DoubleSolution>() ,
				new GenerationalDistance<DoubleSolution>() ,
				PISA_HV ? new PISAHypervolume<DoubleSolution>() : new WFGHypervolume<DoubleSolution>() , // https://www.youtube.com/watch?v=cR4r1aNPBkQ
				new InvertedGenerationalDistancePlus<DoubleSolution>()
		) ;
	}

//////////////////////////////////////////////////////////// PROBLEM SELECTION

	public static List<ExperimentProblem<DoubleSolution>> getProblems()
	{		
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;

		if( ( PROBLEMS & DTLZ ) == DTLZ )
			problems.addAll( ExperimentProblems.PARETO_DTLZ_PROBLEMS() ) ;
		if( ( PROBLEMS & LZ09 ) == LZ09 )
			problems.addAll( ExperimentProblems.LZ09_PROBLEMS() ) ;
		if( ( PROBLEMS & RE ) == RE )
			problems.addAll( ExperimentProblems.RE_PROBLEMS() ) ;
		if( ( PROBLEMS & WFG ) == WFG )
			problems.addAll( ExperimentProblems.WFG_PROBLEMS() ) ;
		if( ( PROBLEMS & ZDT ) == ZDT )
			problems.addAll( ExperimentProblems.PARETO_ZDT_PROBLEMS() ) ;

		return problems ;
	}
	
//////////////////////////////////////////////////////////// OPERATORS

    public static PolynomialMutation getPolynomialMutation( Problem< DoubleSolution > problem )
    {
    	double mutationProbability = 1.0 / problem.getNumberOfVariables();
    	double mutationDistributionIndex = 20.0;

    	return new PolynomialMutation( mutationProbability , mutationDistributionIndex );
    }
    
    public static CrossoverOperator< DoubleSolution > getCrossoverOperator()
    {
    	double crossoverProbability = 0.9;
    	double crossoverDistributionIndex = 20.0;
    	CrossoverOperator<DoubleSolution> crossover = new SBXCrossover( crossoverProbability , crossoverDistributionIndex );

    	return crossover;
    }
    
    public static SelectionOperator< List< DoubleSolution > , DoubleSolution > getSelectionOperator()
    {
    	return new BinaryTournamentSelection< DoubleSolution >( new RankingAndCrowdingDistanceComparator< DoubleSolution >() );
    }

//////////////////////////////////////////////////////////// ALGORITHM SELECTION

	private static List<ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>> getAlgorithms()
	{
		List<ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>> algorithms = new ArrayList<ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>>() ;

		for( int i = 0 ; i < INDEPENDENT_RUNS ; i++ )
		{
			for( ExperimentProblem<DoubleSolution> problem : getProblems() )
			{
				if( ( ALGORITHMS & NSGAII ) == NSGAII )
				{
					if( TRACE )
					{
						algorithms.add( getTraceNSGAII( problem , i , RANDOM ) ) ;
						algorithms.add( getTraceNSGAII( problem , i , LINEAR ) ) ;
						algorithms.add( getTraceNSGAII( problem , i , LOGARITHMIC ) ) ;

						algorithms.add( getTraceNSGAII( problem , i , RANDOM | ORTHOGONAL ) ) ;
						algorithms.add( getTraceNSGAII( problem , i , LINEAR | ORTHOGONAL ) ) ;
						algorithms.add( getTraceNSGAII( problem , i , LOGARITHMIC | ORTHOGONAL ) ) ;
					}
					else
					{
						algorithms.add( getSeededNSGAII( problem , i , RANDOM ) ) ;
						algorithms.add( getSeededNSGAII( problem , i , LINEAR ) ) ;
						algorithms.add( getSeededNSGAII( problem , i , LOGARITHMIC ) ) ;

						algorithms.add( getSeededNSGAII( problem , i , RANDOM | ORTHOGONAL ) ) ;
						algorithms.add( getSeededNSGAII( problem , i , LINEAR | ORTHOGONAL ) ) ;
						algorithms.add( getSeededNSGAII( problem , i , LOGARITHMIC | ORTHOGONAL ) ) ;
					}
				}

				if( ( ALGORITHMS & SPEA2 ) == SPEA2 )
				{
					if( TRACE )
					{
						algorithms.add( getTraceSPEA2( problem , i , RANDOM ) ) ;
						algorithms.add( getTraceSPEA2( problem , i , LINEAR ) ) ;
						algorithms.add( getTraceSPEA2( problem , i , LOGARITHMIC ) ) ;
						/*
						algorithms.add( getTraceSPEA2( problem , i , RANDOM | ORTHOGONAL ) ) ;
						algorithms.add( getTraceSPEA2( problem , i , LINEAR | ORTHOGONAL ) ) ;
						algorithms.add( getTraceSPEA2( problem , i , LOGARITHMIC | ORTHOGONAL ) ) ;
						*/
					}
					else
					{
						algorithms.add( getSeededSPEA2( problem , i , RANDOM ) ) ;
						algorithms.add( getSeededSPEA2( problem , i , LINEAR ) ) ;
						algorithms.add( getSeededSPEA2( problem , i , LOGARITHMIC ) ) ;
						/*
						algorithms.add( getSeededSPEA2( problem , i , RANDOM | ORTHOGONAL ) ) ;
						algorithms.add( getSeededSPEA2( problem , i , LINEAR | ORTHOGONAL ) ) ;
						algorithms.add( getSeededSPEA2( problem , i , LOGARITHMIC | ORTHOGONAL ) ) ;
						*/
					}
				}

				if( ( ALGORITHMS & SMPSO ) == SMPSO )
					algorithms.add( getSMPSO( problem , i ) ) ;

				if( ( ALGORITHMS & MOEAD ) == MOEAD )
					algorithms.add( getMOEAD( problem , i ) ) ;

				if( ( ALGORITHMS & GWASFGA ) == GWASFGA )
					algorithms.add( getGWASFGA( problem , i ) ) ;

				if( ( ALGORITHMS & MOCell ) == MOCell )
					algorithms.add( getMOCell( problem , i ) ) ;

			}
		}

		return algorithms ;
	}

//////////////////////////////////////////////////////////// NSGAII

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getSeededNSGAII( ExperimentProblem<DoubleSolution> problem , int i  , int seed )
	{
		CrossoverOperator<DoubleSolution> crossover = getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = getSelectionOperator() ;

		SeededNSGAIIBuilder builder = new SeededNSGAIIBuilder
		( problem.getProblem() , crossover , mutation , POPULATION_SIZE ) ;
		builder = ( SeededNSGAIIBuilder )builder.setSelectionOperator( selection ) ;
		builder = ( SeededNSGAIIBuilder )builder.setMaxEvaluations( ITERATIONS * POPULATION_SIZE ) ;
		builder = ( SeededNSGAIIBuilder )builder.setSeedStrategy( seed ) ;

		Algorithm<List<DoubleSolution>> algorithm = builder.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getTraceNSGAII( ExperimentProblem<DoubleSolution> problem , int i  , int seed )
	{
		CrossoverOperator<DoubleSolution> crossover = getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = getSelectionOperator() ;

		TraceNSGAIIBuilder builder = new TraceNSGAIIBuilder
		( problem.getProblem() , crossover , mutation , POPULATION_SIZE , FRONTS_DIR + problem.getReferenceFront() , false ) ;
		builder = ( TraceNSGAIIBuilder )builder.setSelectionOperator( selection ) ;
		builder = ( TraceNSGAIIBuilder )builder.setMaxEvaluations( ITERATIONS * POPULATION_SIZE ) ;
		builder = ( TraceNSGAIIBuilder )builder.setSeedStrategy( seed ) ;

		Algorithm<List<DoubleSolution>> algorithm = builder.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

//////////////////////////////////////////////////////////// SPEA2

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getSeededSPEA2( ExperimentProblem<DoubleSolution> problem , int i , int seed )
	{
		CrossoverOperator<DoubleSolution> crossover = getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = getSelectionOperator() ;

		SeededSPEA2Builder builder = new SeededSPEA2Builder
		( problem.getProblem() , crossover , mutation ) ;
		builder = ( SeededSPEA2Builder )builder.setSelectionOperator( selection ) ;
		builder = ( SeededSPEA2Builder )builder.setMaxIterations( ITERATIONS ) ;
		builder = ( SeededSPEA2Builder )builder.setPopulationSize( POPULATION_SIZE ) ;
		builder = ( SeededSPEA2Builder )builder.setK( 1 ) ;
		builder = ( SeededSPEA2Builder )builder.setSeedStrategy( seed ) ;

		Algorithm<List<DoubleSolution>> algorithm = builder.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getTraceSPEA2( ExperimentProblem<DoubleSolution> problem , int i , int seed )
	{
		CrossoverOperator<DoubleSolution> crossover = SeedExperiment.getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = SeedExperiment.getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = SeedExperiment.getSelectionOperator() ;

		TraceSPEA2Builder builder = new TraceSPEA2Builder
		( problem.getProblem() , crossover , mutation , FRONTS_DIR + problem.getReferenceFront() , false ) ;
		builder = ( TraceSPEA2Builder )builder.setSelectionOperator( selection ) ;
		builder = ( TraceSPEA2Builder )builder.setMaxIterations( ITERATIONS ) ;
		builder = ( TraceSPEA2Builder )builder.setPopulationSize( POPULATION_SIZE ) ;
		builder = ( TraceSPEA2Builder )builder.setK( 1 ) ;
		builder = ( TraceSPEA2Builder )builder.setSeedStrategy( seed ) ;

		Algorithm<List<DoubleSolution>> algorithm = builder.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}


	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getMOCell( ExperimentProblem<DoubleSolution> problem , int i )
	{
		CrossoverOperator<DoubleSolution> crossover = getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = getSelectionOperator() ;

		Algorithm<List<DoubleSolution>> algorithm = new MOCellBuilder<DoubleSolution>( problem.getProblem() , crossover , mutation )
				.setSelectionOperator( selection )
				.setMaxEvaluations( 25000 )
				.setPopulationSize( POPULATION_SIZE )
				.setArchive( new CrowdingDistanceArchive<DoubleSolution>( 100 ) )
				.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

	@SuppressWarnings( "unused" )
	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getGWASFGA( ExperimentProblem<DoubleSolution> problem , int i )
	{
		if( problem == null || problem != null )
			throw new RuntimeException( "Not implemented correctly." ) ;

		CrossoverOperator<DoubleSolution> crossover = getCrossoverOperator() ;

		MutationOperator<DoubleSolution> mutation = getPolynomialMutation( problem.getProblem() ) ;

		SelectionOperator<List<DoubleSolution>,DoubleSolution> selection = getSelectionOperator() ;

	    double epsilon = 0.01 ;
		Algorithm<List<DoubleSolution>> algorithm = new GWASFGA<DoubleSolution>
		( problem.getProblem() , POPULATION_SIZE , 250 , crossover , mutation , selection , new SequentialSolutionListEvaluator<DoubleSolution>() , epsilon ) ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getSMPSO( ExperimentProblem<DoubleSolution> problem , int i )
	{
		Algorithm<List<DoubleSolution>> algorithm ;
		algorithm = new SMPSOBuilder( ( DoubleProblem )problem.getProblem() , new CrowdingDistanceArchive<DoubleSolution>( 100 ) )
				.setMutation( getPolynomialMutation( problem.getProblem() ) )
				.setMaxIterations( 250 )
				.setSwarmSize( POPULATION_SIZE )
				.setSolutionListEvaluator( new SequentialSolutionListEvaluator<DoubleSolution>() ).build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}

	private static ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>> getMOEAD( ExperimentProblem<DoubleSolution> problem , int i )
	{
		Algorithm<List<DoubleSolution>> algorithm ;
		CrossoverOperator<DoubleSolution> crossover ;
		MutationOperator<DoubleSolution> mutation ;

		double cr = 1.0 ;
	    double f = 0.5 ;
	    crossover = new DifferentialEvolutionCrossover( cr , f , DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN ) ;

	    mutation = getPolynomialMutation( problem.getProblem() ) ;

		algorithm = new MOEADBuilder( problem.getProblem() , MOEADBuilder.Variant.MOEAD )
				.setCrossover( crossover )
				.setMutation( mutation )
				.setMaxEvaluations( 150000 )
				.setPopulationSize( 300 )
				.setResultPopulationSize( 100 )
				.setNeighborhoodSelectionProbability( 0.9 )
				.setMaximumNumberOfReplacedSolutions( 2 )
				.setNeighborSize( 20 )
				.setFunctionType( AbstractMOEAD.FunctionType.TCHE )
				.setDataDirectory( RESOURCE_DIR + "weightVectorFiles/moead/" )
				.build() ;

		return new ExperimentAlgorithm<DoubleSolution,List<DoubleSolution>>( algorithm , problem , i ) ;
	}
}
