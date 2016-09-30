package org.opensirf.jaxrs.config;

import org.junit.Test;
import org.opensirf.format.GenericUnmarshaller;
import org.opensirf.jaxrs.storage.fs.FilesystemConfiguration;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerConfiguration;
 
public class MultiConfigTest {

	@Test
    public void configTest()  {
    	String json = "{\"containerName\":\"philContainer\",\"driver\":\"multi\",\"endpoint\":\"localhost\",\"distributionPolicy\":\"serial\",\"subconfigurations\":[{\"containerName\":\"philContainer\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage\"}]}";
    	MultiContainerConfiguration config = GenericUnmarshaller.unmarshal("application/json", json, MultiContainerConfiguration.class);
    	
    	System.out.println("MultiContainerConfig policy == " + config.getDistributionPolicy());   
    	System.out.println("MultiContainerConfig driver == " + config.getDriver());
    	System.out.println("MultiContainerConfig number of subconfigs == " + config.getSubconfigurations().size());    	
    	System.out.println("MultiContainerConfig config0 driver == " + config.getSubconfigurations().get(0).getDriver());
    }
	
//	@Test
//    public void configTest2()  {
//    	String json = "{\"containerName\":\"philContainer\",\"driver\":\"fs\",\"endpoint\":\"localhost\",\"mountPoint\":\"/var/lib/sirf/storage\"}";
//    	FilesystemConfiguration config = GenericUnmarshaller.unmarshal("application/json", json, FilesystemConfiguration.class);
//
//    	System.out.println("FilesystemConfiguration mountpoint == " + config.getMountPoint());   
//    	System.out.println("FilesystemConfiguration driver == " + config.getDriver());    	
//    }
}