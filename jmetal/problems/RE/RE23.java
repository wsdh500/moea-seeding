/**
 * RE23.java
 * 
 * Author:
 * Ryoji Tanabe <rt.ryoji.tanabe@gmail.com>, patched by Sam Hart <wsdh500@york.ac.uk>
 * 
 * This is a two-objective version of the pressure vessel design problem.
 * 
 * Reference:
 * B. K. Kannan and S. N. Kramer,
 * An Augmented Lagrange Multiplier Based Method for Mixed Integer Discrete Continuous Optimization and Its Applications to Mechanical Design,
 * Journal of Mechanical Design, vol. 116, no. 2, pp. 405-411, 1994.
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

public class RE23 extends AbstractDoubleProblem
{
	private static final long serialVersionUID = 7723076637790930392L ;

	public static final double[] LOWERLIMIT = { 1 , 1 , 10 , 10 } ;
	public static final double[] UPPERLIMIT = { 100 , 100 , 200 , 240 } ;

	private static final int numberOfOriginalConstraints_ = 3 ;

	public RE23()
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

		double x1 = 0.0625 * Math.rint( x[ 0 ] ) ;
		double x2 = 0.0625 * Math.rint( x[ 1 ] ) ;
		double x3 = x[ 2 ] ;
		double x4 = x[ 3 ] ;

		// First original objective function
		f[ 0 ] = ( 0.6224 * x1 * x3 * x4 ) + ( 1.7781 * x2 * x3 * x3 ) + ( 3.1661 * x1 * x1 * x4 ) + ( 19.84 * x1 * x1 * x3 ) ;

		g[ 0 ] = x1 - ( 0.0193 * x3 ) ;
		g[ 1 ] = x2 - ( 0.00954 * x3 ) ;
		g[ 2 ] = ( Math.PI * x3 * x3 * x4 ) + ( ( 4.0 / 3.0 ) * ( Math.PI * x3 * x3 * x3 ) ) - 1296000 ;

		for( int i = 0 ; i < numberOfOriginalConstraints_ ; i++ )
		{
			if( g[ i ] < 0.0 )
				g[ i ] = -g[ i ] ;
			else
				g[ i ] = 0 ;
		}

		f[ 1 ] = g[ 0 ] + g[ 1 ] + g[ 2 ] ;

		for( int i = 0 ; i < getNumberOfObjectives() ; i++ )
			solution.setObjective( i , f[ i ] ) ;
	}

	@Override
	public int getNumberOfVariables()
	{
		return 4 ;
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
		return "RE23" ;
	}

	@Override
	public DoubleSolution createSolution()
	{
		return super.createSolution() ;
	} 
}
