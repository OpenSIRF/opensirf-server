package org.opensirf.jaxrs.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamSource;

import org.opensirf.storage.SwiftStrategy;

public class Test {
	public static void main(String[] args) throws Exception {
		SwiftConfiguration c = new SwiftConfiguration("myContainer", "swift", "172.17.0.3", 
				"services:swift", "swift", "openstack-swift", "regionOne");

		StringWriter w = new StringWriter();
		new SwiftStrategy().getMarshaller().marshal(c,w);
		String s = w.toString();
		System.out.println(s);
		
		FileOutputStream fos = new FileOutputStream("/tmp/config.json");
		fos.write(s.getBytes());
		fos.flush();
		fos.close();
		
		SwiftConfiguration c2 = new SwiftStrategy().getUnmarshaller().unmarshal(
				new StreamSource(new FileInputStream("/tmp/config.json")), SwiftConfiguration.class).getValue(); 
				
		StringWriter w2 = new StringWriter();
		new SwiftStrategy().getMarshaller().marshal(c2,w2);
		String s2 = w2.toString();		
		System.out.println(s2);
	}
}
