package uk.ac.york.wsdh500.moea.random ;

import java.security.SecureRandom ;

public class SecurePRNG  extends PRNGenerator
{
	private static final long serialVersionUID = 1408735160631307049L ;

	public SecurePRNG()
	{
		random = new SecureRandom() ;
	}
}
