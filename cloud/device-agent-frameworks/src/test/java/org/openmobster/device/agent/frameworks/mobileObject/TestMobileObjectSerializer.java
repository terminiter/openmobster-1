/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;

import test.openmobster.device.agent.frameworks.mobileObject.MockPOJO;
import test.openmobster.device.agent.frameworks.mobileObject.email.MockChild;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileObjectSerializer extends TestCase 
{
	private static Logger log = Logger.getLogger(TestMobileObjectSerializer.class);
	
	private MobileObjectSerializer serializer;
	
	protected void setUp() throws Exception 
	{		
		ServiceManager.bootstrap();
		
		this.serializer = (MobileObjectSerializer)ServiceManager.
		locate("mobileObject://MobileObjectSerializer");				
	}	
		
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	
	public void testMockPOJO() throws Exception
	{
		MockPOJO pojo = new MockPOJO("top-level");	
		
		MockChild child = new MockChild("child");
		MockPOJO embedded = new MockPOJO("embedded");
		child.setParent(embedded);
		embedded.setChildArray(new String[]{"child://blah0", "child://blah1"});
		pojo.setChild(child);
		
		List<MockChild> children = new ArrayList<MockChild>();
		for(int i=0; i<5; i++)
		{
			MockChild localChild = new MockChild("child://"+i);
			localChild.setParent(this.createPOJOWithStrings("embedded://"+i, false));
			children.add(localChild);
		}
		pojo.setChildren(children);
		
		String deviceXml = this.serializer.serialize(pojo);
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");
		
		MobileObjectReader reader = new MobileObjectReader();
		MobileObject mobileObject = reader.parse(deviceXml);
		
								
		//Assert the MobileObject state
		assertEquals("POJO Value does not match!!", "top-level", mobileObject.getValue("value"));
		assertEquals("Child Value does not match!!", "child", mobileObject.getValue("child.value"));
		assertEquals("Parent Value does not match!!", "embedded", mobileObject.getValue("child.parent.value"));
		assertEquals("Child Array Value does not match!!", "child://blah0", mobileObject.getValue("child.parent.childArray[0]"));
		assertEquals("Child Array Value does not match!!", "child://blah1", mobileObject.getValue("child.parent.childArray[1]"));
		
		for(int i=0; i<5; i++)
		{
			
			assertEquals("Child Value does not match!!!", "child://"+i, 
			mobileObject.getValue("children["+i+"].value"));
			
			assertEquals("Parent Value does not match!!!", "embedded://"+i, 
			mobileObject.getValue("children["+i+"].parent.value"));
			
			for(int j=0; j<5; j++)
			{
				assertEquals("string://"+j, mobileObject.
				getValue("children["+i+"].parent.strings["+j+"]"));
			}
			
			assertEquals("blah0", 
			mobileObject.getValue("children["+i+"].parent.childArray[0]"));
			
			assertEquals("blah1", 
			mobileObject.getValue("children["+i+"].parent.childArray[1]"));
		}
		
		deviceXml = DeviceSerializer.getInstance().serialize(mobileObject);
		pojo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, deviceXml);
		
		this.assertEquals("MockPOJO Value must match!!!", "top-level", pojo.getValue());
		
		this.assertEquals("MockChild Value must match!!!", "child", pojo.getChild().getValue());
		this.assertEquals("MockChild Parent Value must match!!!", "embedded", pojo.getChild().getParent().getValue());		
		String[] childArray = pojo.getChild().getParent().getChildArray();
		this.assertTrue("Child Array must have 2 strings", childArray.length == 2);
		this.assertEquals("Parent Child Array must match!!", childArray[0], "child://blah0");
		this.assertEquals("Parent Child Array must match!!", childArray[1], "child://blah1");
		
				
		children = pojo.getChildren();
		this.assertTrue("Children must not be empty", children != null && !children.isEmpty());
		int index = 0;
		for(MockChild assertChild: children)
		{			
			this.assertEquals("Child Value must match!!", "child://"+index, assertChild.getValue());			
			
			//Deeper assertions
			MockPOJO parent = assertChild.getParent();
			this.assertEquals("MockChild Parent Value must match!!!", "embedded://"+index, parent.getValue());
			
			int strIndex=0;
			for(String parentString: parent.getStrings())
			{
				this.assertEquals("Parent String Value must match!!!", "string://"+strIndex, parentString);
				strIndex++;
			}
			
			String[] assertChildArray = parent.getChildArray();
			this.assertTrue("Parent Child Array must have 2 strings", assertChildArray.length == 2);
			this.assertEquals("Parent Child Array must match!!", assertChildArray[0], "blah0");
			this.assertEquals("Parent Child Array must match!!", assertChildArray[1], "blah1");
			
			index++;
		}
	}
	//------------------------------------------------------------------------------------------------------
	private MockPOJO createPOJOWithStrings(String name, boolean leaveChildArrayNull)
	{
		MockPOJO mockPOJO = new MockPOJO(name);
		
		List<String> strings = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			strings.add("string://"+i);
		}
		mockPOJO.setStrings(strings);
		
		if(!leaveChildArrayNull)
		{
			mockPOJO.setChildArray(new String[]{"blah0", "blah1"});
		}
		
		return mockPOJO;
	}
}
