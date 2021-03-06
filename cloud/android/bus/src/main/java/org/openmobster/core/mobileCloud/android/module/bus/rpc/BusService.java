/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author openmobster@gmail.com
 *
 */
public class BusService extends Service
{
	private IBinder binder;
	
	public BusService()
	{
		
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		try
		{
			if(this.binder == null)
			{
				this.binder = new BusBinder();
			}
			return this.binder;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
