package org.opensirf.jaxrs.config;

import org.junit.Test;
import org.opensirf.format.GenericMarshaller;
import org.opensirf.format.GenericUnmarshaller;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftConfiguration;
 
public class MultiConfigTest {

	@Test
    public void configTest() throws Exception {
    	String json = "{\"containerName\":\"philContainer\",\"driver\":\"multi\",\"endpoint\":\"localhost\",\"distributionPolicy\":\"serial\",\"subconfigurations\":[{\"containerName\":\"lv1\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage/lv1\"},{\"containerName\":\"lv2\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage/lv2\"}]}";
    	MultiContainerConfiguration config = GenericUnmarshaller.unmarshal("application/json", json, MultiContainerConfiguration.class);
    	
    	System.out.println("MultiContainerConfig policy == " + config.getDistributionPolicy());   
    	System.out.println("MultiContainerConfig driver == " + config.getDriver());
    	System.out.println("MultiContainerConfig number of subconfigs == " + config.getSubconfigurations().size());    	
    	System.out.println("MultiContainerConfig config0 driver == " + config.getSubconfigurations().get(0).getDriver());
    	
    	String configS = GenericMarshaller.marshal("application/json", config);
    	System.out.println(configS);
    	
//        "identity" : "sirf:sirfadmin",
//        "credential" : "100years",
//        "provider" : "openstack-swift",
//        "region" : "RegionOne",
//        "containerName" : "swift1",
//        "driver" : "swift",
//        "endpoint" : "http://172.28.128.3:5000/v2.0/"
    	
    	SwiftConfiguration swiftC = new SwiftConfiguration("swift2", "swift",
    			"http://172.28.128.7:5000/v2.0/", "sirf:sirfadmin", "100years", "openstack-swift",
    			"RegionOne");
    	
    	System.out.println("ELAST NODE: " + GenericMarshaller.marshal("application/json", swiftC));
    }
	
	@Test
    public void sirfConfigWithMultiConfigTest() throws Exception {
    	String json = "{\"containerConfiguration\":{\"containerName\":\"philContainer\",\"driver\":\"multi\",\"endpoint\":\"localhost\",\"distributionPolicy\":\"serial\",\"subconfigurations\":[{\"containerName\":\"lv1\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage/lv1\"},{\"containerName\":\"lv2\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage/lv2\"}]}}";
    	//SIRFConfiguration sirfConfig = GenericUnmarshaller.unmarshal("application/json", json, SIRFConfiguration.class);
    	SIRFConfiguration sirfConfig = new SIRFConfigurationUnmarshaller().unmarshalConfig(json);
    	MultiContainerConfiguration config = (MultiContainerConfiguration) sirfConfig.getContainerConfiguration();    	
    	
    	System.out.println("MultiContainerConfig policy == " + config.getDistributionPolicy());   
    	System.out.println("MultiContainerConfig driver == " + config.getDriver());
    	System.out.println("MultiContainerConfig number of subconfigs == " + config.getSubconfigurations().size());    	
    	System.out.println("MultiContainerConfig config0 driver == " + config.getSubconfigurations().get(0).getDriver());
    	
    	String configS = GenericMarshaller.marshal("application/json", config);
    	System.out.println(configS);
    }
}