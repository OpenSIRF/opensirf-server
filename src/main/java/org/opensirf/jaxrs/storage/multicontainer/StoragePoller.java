/*
 * OpenSIRF JAX-RS
 * 
 * Copyright IBM Corporation 2016.
 * All Rights Reserved.
 * 
 * MIT License:
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization of the
 * copyright holder.
 */
package org.opensirf.jaxrs.storage.multicontainer;

import org.opensirf.client.SirfClient;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.ContainerConfiguration.Driver;
import org.opensirf.jaxrs.storage.StorageContainerNotFoundException;
import org.opensirf.jaxrs.storage.fs.FilesystemConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftDriver;
import org.opensirf.storage.monitor.api.DiskApi;
import org.opensirf.storage.monitor.model.StorageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pviana
 *
 */
public class StoragePoller {

	static final Logger log = LoggerFactory.getLogger(StoragePoller.class); 

	public ContainerConfiguration findTargetStorageContainer(MultiContainerConfiguration config) {
		if(config.getDistributionPolicy().equalsIgnoreCase(
				MultiContainerConfiguration.EVENLY_FREE_POLICY)) {
			log.debug("Finding target storage container, policy = evenlyFree");
			float mostFreeDiskSpace = 0F;
			ContainerConfiguration targetContainer = null;
			
			for(ContainerConfiguration c: config.getSubconfigurations()) {
				if(c.getDriver().equalsIgnoreCase(Driver.FILESYSTEM.toString())) {
					SirfClient cli = new SirfClient(c.getEndpoint() + ":" + 
							FilesystemConfiguration.DEFAULT_STORAGE_MONITOR_PORT + STORAGE_MONITOR_URI);
					StorageMetadata meta = cli.getStorageMetadata(((FilesystemConfiguration) c).getMountPoint());
					if(meta.getFreeDiskSpace() > mostFreeDiskSpace) {
						targetContainer = c;
						mostFreeDiskSpace = meta.getFreeDiskSpace();
					}
					
					log.debug(c.getContainerName() + ": " + meta.getFreeDiskSpace() + "/1 free (" +
							meta.getSpaceInMegabytes() + " MB)");
					
				} else if(c.getDriver().equalsIgnoreCase(Driver.SWIFT.toString())) {
					String endpoint = c.getEndpoint().substring(
							c.getEndpoint().indexOf("http://") + "http://".length(), // beginIndex
							c.getEndpoint().indexOf(":" + SwiftConfiguration.DEFAULT_IDENTITY_PORT)); // endIndex
					endpoint += ":" + FilesystemConfiguration.DEFAULT_STORAGE_MONITOR_PORT;
					endpoint += STORAGE_MONITOR_URI;
					
					SirfClient cli = new SirfClient(endpoint);
					StorageMetadata meta = cli.getStorageMetadata(DiskApi.DEFAULT_SWIFT_FILESYSTEM_LOCATION);

					log.debug(c.getContainerName() + ": " + meta.getFreeDiskSpace() + "/1 free (" +
							meta.getSpaceInMegabytes() + " MB)");
					
					if(meta.getFreeDiskSpace() > mostFreeDiskSpace) {
						targetContainer = c;
						mostFreeDiskSpace = meta.getFreeDiskSpace();
					}
				}
			}
			
			log.debug("Target container: " + targetContainer.getContainerName());
			
			return targetContainer;
		}
		
		return null;
	}
	
	public ContainerConfiguration getContainerByName(String containerName, MultiContainerConfiguration config) {
		for(ContainerConfiguration c: config.getSubconfigurations())
			if(c.getContainerName().equalsIgnoreCase(containerName))
				return c;
		
		throw new StorageContainerNotFoundException("The storage container with name " + containerName
				+ " could not be found.");
	}
	
	public static final String STORAGE_MONITOR_URI = "/opensirf-storage-monitor";
}
