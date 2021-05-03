package uk.ac.york.wsdh500.moea.random ;

import org.uncommons.maths.random.MersenneTwisterRNG ;

public class MersennePRNG extends PRNGenerator
{
	private static final long serialVersionUID = 757643619215980980L ;

	public MersennePRNG()
	{
		random = new MersenneTwisterRNG() ;
	}
}
