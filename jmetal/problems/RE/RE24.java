/**
 * RE24.java
 * 
 * Author: Ryoji Tanabe <rt.ryoji.tanabe@gmail.com>, patched by Sam Hart
 * <wsdh500@york.ac.uk>
 * 
 * This is a two-objective version of the hatch cover design problem.
 * 
 * H. M. Amir and T. Hasegawa,
 * "Nonlinear Mixed-Discrete Structural Optimization,"
 * J. Struct. Eng., vol. 115, no. 3, pp. 626-646, 1989.
 * 
 * Copyright (c) 2018 Ryoji Tanabe, 2021 Sam Hart
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY ; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jmetal.problems.RE ;

import java.util.ArrayList ;

import org.apache.commons.lang3.tuple.Pair ;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem ;
import org.uma.jmetal.solution.doublesolution.DoubleSolution ;

public class RE24 extends AbstractDoubleProblem
{
	private static final long serialVersionUID = -9195338590454570693L ;

	public static final double[] LOWERLIMIT = { 0.5 , 0.5 } ;
	public static final double[] UPPERLIMIT = { 4 , 50 } ;

	private int numberOfOriginalConstraints_ = 4 ;

	public RE24()
	{
		bounds = new ArrayList<Pair<Double,Double>>() ;

		for( int i = 0 ; i < getNumberOfVariables() ; i++ )
			bounds.add( Pair.of( LOWERLIMIT[ i ] , UPPERLIMIT[ i ] ) ) ;
	}  

	@Override
	public void evaluate( DoubleSolution solution )
	{
		double[] x = new double[ getNumberOfVariables() ] ;
		double[] f = new double[ getNumberOfObjectives() ] ;
		double[] g = new double[ numberOfOriginalConstraints_ ] ;

		for( int i = 0 ; i < getNumberOfVariables() ; i++ )
			x[ i ] = solution.getVariable( i ) ;

		double x1 = x[ 0 ] ;
		double x2 = x[ 1 ] ;

		// First original objective function
		f[ 0 ] = x1 + ( 120 * x2 ) ;

		double E = 700000 ;
		double sigmaBMax = 700 ;
		double tauMax = 450 ;
		double deltaMax = 1.5 ;
		double sigmaK = ( E * x1 * x1 ) / 100 ;
		double sigmaB = 4500 / ( x1 * x2 ) ;
		double tau = 1800 / x2 ;
		double delta = ( 56.2 * 10000 ) / ( E * x1 * x2 * x2 ) ;

		g[ 0 ] = 1 - ( sigmaB / sigmaBMax ) ;
		g[ 1 ] = 1 - ( tau / tauMax ) ;
		g[ 2 ] = 1 - ( delta / deltaMax ) ;
		g[ 3 ] = 1 - ( sigmaB / sigmaK ) ;

		for( int i = 0 ; i < numberOfOriginalConstraints_ ; i++ )
		{
			if( g[ i ] < 0.0 )
				g[ i ] = -g[ i ] ;
			else
				g[ i ] = 0 ;
		}

		f[ 1 ] = g[ 0 ] + g[ 1 ] + g[ 2 ] + g[ 3 ] ;

		for( int i = 0 ; i < getNumberOfObjectives() ; i++ )
			solution.setObjective( i , f[ i ] ) ;
	}


	@Override
	public int getNumberOfVariables()
	{
		return 2 ;
	}

	@Override
	public int getNumberOfObjectives()
	{
		return 2 ;
	}

	@Override
	public int getNumberOfConstraints()
	{
		return 0 ;
	}

	@Override
	public String getName()
	{
		return "RE24" ;
	}

	@Override
	public DoubleSolution createSolution()
	{
		return super.createSolution() ;
	}
}
