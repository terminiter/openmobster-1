/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.perf.framework;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;

/**
 * @author openmobster@gmail.com
 *
 */
public final class RunCloudServer
{
	private static Logger log = Logger.getLogger(RunCloudServer.class);
	
	private void setUp(int numberOfUsers)
	{
		ServiceManager.bootstrap();
		Provisioner provisioner = Provisioner.getInstance();
		
		for(int i=0; i<numberOfUsers; i++)
		{
			if(i == 2)
			{
				continue;
			}
			String username = "blah"+i+"@gmail.com";
			provisioner.registerIdentity(username, "blahblah");
		}
	}
	
	private void tearDown()
	{
		ServiceManager.shutdown();
	}
		
	private void runServer() throws Exception
	{
		log.info("RunServer starting..............");		
		this.blockTest("Press [Enter] to finish the Server");				
	}
	
	
	private void blockTest(String message) throws Exception
	{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		do
		{
			log.info(message);
		}while(!bf.readLine().equals(""));		
	}
	
	public static void main(String[] args) throws Exception
	{
		RunCloudServer server = new RunCloudServer();
		
		int numberOfUsers = 1;
		if(args != null)
		{
			numberOfUsers = Integer.parseInt(args[1]);
		}
		
		server.setUp(numberOfUsers);
		
		server.runServer();
		
		server.tearDown();
	}
}
