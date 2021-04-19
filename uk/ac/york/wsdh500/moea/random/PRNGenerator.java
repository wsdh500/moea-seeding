package uk.ac.york.wsdh500.moea.random ;

import java.util.Random ;

import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator ;

public abstract class PRNGenerator implements PseudoRandomGenerator
{
	private static final long serialVersionUID = -1611293154232991733L ;

	protected Random random ;
	private long seed ;

	@Override
	public int nextInt( int lowerBound , int upperBound )
	{
		int range = upperBound - lowerBound ;
		return lowerBound + random.nextInt( range ) ;
	}

	@Override
	public double nextDouble( double lowerBound , double upperBound )
	{
		double range = upperBound - lowerBound ;
		return lowerBound + nextDouble() * range ;
	}

	@Override
	public double nextDouble()
	{
		return random.nextDouble() ;
	}

	@Override
	public void setSeed( long seed )
	{
		this.seed  = seed ;
		random.setSeed( seed ) ;
	}

	@Override
	public long getSeed()
	{
		return seed ;
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() ;
	}

}
