package uk.ac.york.wsdh500.moea.experiment ;

import java.lang.reflect.InvocationTargetException ;
import java.util.ArrayList ;
import java.util.List ;

import org.uma.jmetal.lab.experiment.util.ExperimentProblem ;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem ;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

public class ExperimentProblems
{
	private static final String BASE = "org.uma.jmetal.problem.multiobjective." ;

	public static final List<ExperimentProblem<DoubleSolution>> LZ09_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 9 ; i++ )
		{
			problem = getProblem( BASE + "lz09.LZ09F" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "LZ09_F" + i + ".csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> WFG_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 9 ; i++ )
		{
			problem = getProblem( BASE + "wfg.WFG" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "WFG" + i + ".2D.csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> ZDT_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 6 ; i++ )
		{
			if( i == 5 )
				continue ;

			problem = getProblem( BASE + "zdt.ZDT" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "ZDT" + i + ".csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> PARETO_ZDT_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 6 ; i++ )
		{
			if( i == 5 )
				continue ;

			problem = getProblem( "uk.ac.york.wsdh500.moea.problems.ParetoZDT" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "ZDT" + i + ".csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> DTLZ_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 7 ; i++ )
		{
			problem = getProblem( BASE + "dtlz.DTLZ" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "DTLZ" + i + ".2D.csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> PARETO_DTLZ_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 7 ; i++ )
		{
			problem = getProblem( "uk.ac.york.wsdh500.moea.problems.ParetoDTLZ" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "DTLZ" + i + ".2D.csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} ;

	public static final List<ExperimentProblem<DoubleSolution>> RE_PROBLEMS()
	{
		List<ExperimentProblem<DoubleSolution>> problems = new ArrayList<ExperimentProblem<DoubleSolution>>() ;
		ExperimentProblem<DoubleSolution> problem ;
		for( int i = 1 ; i <= 5 ; i++ )
		{
			problem = getProblem( "jmetal.problems.RE.RE2" + i ) ;
			if( problem != null )
			{
				problem.setReferenceFront( "RE2" + i + ".csv" ) ;
				problems.add( problem ) ;
			}
		}

		return problems ;
	} 

	@SuppressWarnings( "unchecked" )
	private static ExperimentProblem<DoubleSolution> getProblem( String classname )
	{
		ExperimentProblem<DoubleSolution> problem ;
		Class<? extends AbstractDoubleProblem> klass = null ;
		DoubleProblem p = null ;

			try
			{
				klass = ( Class<? extends AbstractDoubleProblem> )Class.forName( classname ) ;
			}
			catch( ClassNotFoundException e )
			{
				e.printStackTrace() ;
			}

			try
			{
				p = klass.getConstructor().newInstance() ;
			}
			catch
			(
					InstantiationException
				|	IllegalAccessException
				|	IllegalArgumentException
				|	InvocationTargetException
				|	NoSuchMethodException
				|	SecurityException e
			)
			{
				e.printStackTrace() ;
			}
			problem = new ExperimentProblem<DoubleSolution>( p ) ;
			
			return problem ;
	}
}
