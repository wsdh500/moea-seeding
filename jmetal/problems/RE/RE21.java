/**
 * RE21.java
 * 
 * Author:
 * Ryoji Tanabe <rt.ryoji.tanabe@gmail.com>, patched by Sam Hart <wsdh500@york.ac.uk>
 * 
 * This is a four bar truss design problem.
 * 
 * Reference:
 * F. Y. Cheng and X. S. Li:
 * Generalized center method for multiobjective engineering optimization.
 * Engineering Optimization, 31(5), pp. 641-661 (1999)
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

/**
 * Class representing problem Speed reducer design
 */
public class RE21 extends AbstractDoubleProblem
{
	private static final long serialVersionUID = -6371343148323081688L ;

	private static final double F = 10 ;
	private static final double sigma = 10 ;

	public RE21()
	{
		bounds = new ArrayList<Pair<Double,Double>>() ;

		double tmpVar = ( F / sigma ) ;

		bounds.add( Pair.of( tmpVar , 3 * tmpVar ) ) ;
		bounds.add( Pair.of( Math.sqrt( 2.0 ) * tmpVar , 3 * tmpVar ) ) ;
		bounds.add( Pair.of( Math.sqrt( 2.0 ) * tmpVar , 3 * tmpVar ) ) ;
		bounds.add( Pair.of( tmpVar , 3 * tmpVar ) ) ;
	}  

	@Override
	public void evaluate( DoubleSolution solution )
	{
		double[] x = new double[ getNumberOfVariables() ] ;
		double[] f = new double[ getNumberOfObjectives() ] ;

		for( int i = 0 ; i < getNumberOfVariables() ; i++ )
			x[ i ] = solution.getVariable( i ) ;

		double x1 = x[ 0 ] ;
		double x2 = x[ 1 ] ;
		double x3 = x[ 2 ] ;
		double x4 = x[ 3 ] ;

		double E = 200000 ;
		double L = 200 ;

		f[ 0 ] = L * ( ( 2 * x1 ) + Math.sqrt( 2.0 ) * x2 + Math.sqrt( x3 ) + x4 ) ;
		f[ 1 ] = ( ( F * L ) / E ) * ( ( 2.0 / x1 ) + ( 2.0 * Math.sqrt( 2.0 ) / x2 ) - ( 2.0 * Math.sqrt( 2.0 ) / x3 ) + ( 2.0 / x4 ) ) ;

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
		return "RE21" ;
	}

	@Override
	public DoubleSolution createSolution()
	{
		return super.createSolution() ;
	}
}
