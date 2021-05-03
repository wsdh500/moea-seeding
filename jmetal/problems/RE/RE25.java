/**
 * RE25.java
 * 
 * Author: Ryoji Tanabe <rt.ryoji.tanabe@gmail.com>, patched by Sam Hart
 * <wsdh500@york.ac.uk>
 * 
 * This is a two-objective version of the coil compression spring design problem.
 * 
 * Reference: J. Lampinen and I. Zelinka,
 * "Mixed integer-discrete-continuous optimization by differential evolution, part 2: a practical example,"
 * in International Conference on Soft Computing, 1999, pp. 77-81.
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

public class RE25 extends AbstractDoubleProblem
{
	private static final long serialVersionUID = -2314723511629014290L ;

	public static final double[] LOWERLIMIT = { 1 , 0.6 , 0.09 } ;
	public static final double[] UPPERLIMIT = { 70 , 3 , 0.5 } ;

	/** Since two out of the original 8 constraint functions can be expressed by the bound constraints, they are removed. */
	private int numberOfOriginalConstraints_ = 6 ;

	public static final double[] diameterFeasibleIntergers =
	{
		0.009 , 0.0095 , 0.0104 , 0.0118 , 0.0128 , 0.0132 , 0.014 , 0.015 , 0.0162 , 0.0173 , 0.018 ,
		0.02 , 0.023 , 0.025 , 0.028 , 0.032 , 0.035 , 0.041 , 0.047 , 0.054 , 0.063 , 0.072 , 0.08 , 0.092 ,
		0.105 , 0.12 , 0.135 , 0.148 , 0.162 , 0.177 , 0.192 ,
		0.207 , 0.225 , 0.244 , 0.263 , 0.283 ,
		0.307 , 0.331 , 0.362 , 0.394 ,
		0.4375 , 0.5
	} ;

	public RE25()
	{
		bounds = new ArrayList<Pair<Double,Double>>() ;

		for( int i = 0 ; i < getNumberOfVariables() ; i++ )
			bounds.add( Pair.of( LOWERLIMIT[ i ] , UPPERLIMIT[ i ] ) ) ;
	}   

	public double getClosestValue( double[] targetArray , double compValue )
	{
		double closestValue = targetArray[ 0 ] ;
		double minDiffValue = Math.abs( targetArray[ 0 ] - compValue ) ;
		double tmpDiffValue = 0 ;

		for( int i = 1 ; i < targetArray.length ; i++ )
		{
			tmpDiffValue = Math.abs( targetArray[ i ] - compValue ) ;
			if( tmpDiffValue < minDiffValue )
			{
				minDiffValue = tmpDiffValue ;
				closestValue = targetArray[ i ] ;
			}
		}

		return closestValue ;
	}

	@Override
	public void evaluate( DoubleSolution solution )
	{
		double[] x = new double[ getNumberOfVariables() ] ;
		double[] f = new double[ getNumberOfObjectives() ] ;
		double[] g = new double[ numberOfOriginalConstraints_ ] ;

		for( int i = 0 ; i < getNumberOfVariables() ; i++ )
			x[ i ] = solution.getVariable( i ) ;

		double x1 = Math.rint( x[ 0 ] ) ;
		double x2 = x[ 1 ] ;
		double x3 = getClosestValue( diameterFeasibleIntergers , x[ 2 ] ) ;

		// first original objective function
		f[ 0 ] = ( Math.PI * Math.PI * x2 * x3 * x3 * ( x1 + 2 ) ) / 4.0 ;

		// constraint functions
		double Cf = ( ( 4.0 * ( x2 / x3 ) - 1 ) / ( 4.0 * ( x2 / x3 ) - 4 ) ) + ( 0.615 * x3 / x2 ) ;
		double Fmax = 1000.0 ;
		double S = 189000.0 ;
		double G = 11.5 * 1e+6 ;
		double K = ( G * x3 * x3 * x3 * x3 ) / ( 8 * x1 * x2 * x2 * x2 ) ;
		double lmax = 14.0 ;
		double lf = ( Fmax / K ) + 1.05 * ( x1 + 2 ) * x3 ;

		double Fp = 300.0 ;
		double sigmaP = Fp / K ;
		double sigmaPM = 6 ;
		double sigmaW = 1.25 ;

		g[ 0 ] = -( ( 8 * Cf * Fmax * x2 ) / ( Math.PI * x3 * x3 * x3 ) ) + S ;
		g[ 1 ] = -lf + lmax ;
		g[ 2 ] = -3 + ( x2 / x3 ) ;
		g[ 3 ] = -sigmaP + sigmaPM ;
		g[ 4 ] = -sigmaP - ( ( Fmax - Fp ) / K ) - 1.05 * ( x1 + 2 ) * x3 + lf ;
		g[ 5 ] = sigmaW - ( ( Fmax - Fp ) / K ) ;

		for( int i = 0 ; i < numberOfOriginalConstraints_ ; i++ )
		{
			if( g[ i ] < 0.0 )
				g[ i ] = -g[ i ] ;
			else
				g[ i ] = 0 ;
		}

		f[ 1 ] = g[ 0 ] + g[ 1 ] + g[ 2 ] + g[ 3 ] + g[ 4 ] + g[ 5 ] ;

		for( int i = 0 ; i < getNumberOfObjectives() ; i++ )
			solution.setObjective( i , f[ i ] ) ;
	}


	@Override
	public int getNumberOfVariables()
	{
		return 3 ;
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
		return "RE25" ;
	}
}
