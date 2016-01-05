//package org.opensirf.jaxrs.config;
//
//import java.io.ByteArrayInputStream;
//
//import org.opensirf.jaxrs.config.SingleContainerConfiguration.Driver;
//
//public class Test {
//	public static void main(String[] args) throws Exception {
//		SingleContainerConfiguration c = new SingleContainerConfiguration(Driver.SWIFT, "myEndpoint");
//		String s = new SingleContainerConfigurationMarshaller("application/json").marshalConfig(c);
//		System.out.println(s);
//		
//		SingleContainerConfiguration c2 = 
//				new SingleContainerConfigurationUnmarshaller("application/json").
//				unmarshalCatalog(new ByteArrayInputStream(s.getBytes()));
//		
//		String s2 = new SingleContainerConfigurationMarshaller("application/json").marshalConfig(c2);
//		System.out.println(s2);
//	}
//}
