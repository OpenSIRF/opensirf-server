/*
 * OpenSIRF JAX-RS
 * 
 * Copyright IBM Corporation 2015.
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
package org.opensirf.jaxrs.storage.swift;

import static com.google.common.io.ByteSource.wrap;
import static org.jclouds.io.Payloads.newByteSourcePayload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.io.Payload;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.PutOptions;
import org.opensirf.container.MagicObject;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.storage.ISirfDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class SwiftDriver implements ISirfDriver {

	static final Logger log = LoggerFactory.getLogger(SwiftDriver.class); 

	private SwiftApi swiftApi;
	private BlobStoreContext blobStoreContext;
	private final String region;
	
	public SwiftDriver(ContainerConfiguration config) {
		SwiftConfiguration swiftConfig = (SwiftConfiguration) config;
		String endpoint = swiftConfig.getEndpoint();
		String identity = swiftConfig.getIdentity();
		String credential = swiftConfig.getCredential();
		String provider = swiftConfig.getProvider();
		region = swiftConfig.getRegion();
		
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());
		swiftApi = ContextBuilder.newBuilder(provider).endpoint(endpoint).
				credentials(identity, credential).modules(modules).buildApi(SwiftApi.class);
		blobStoreContext = ContextBuilder.newBuilder(provider).credentials(identity, credential).
				buildView(BlobStoreContext.class);
	}

	public void createContainerAndMagicObject(String sirfContainerName) {
		log.info("Creating container and magic object for " + sirfContainerName);
		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		CreateContainerOptions options = CreateContainerOptions.Builder.metadata(ImmutableMap.
				of("containerSpecification", "1.0", "sirfLevel", "1", "sirfCatalogId", "catalog.json"));
		System.out.println("Creating container " + sirfContainerName);
		boolean returnCode = containerApi.create(sirfContainerName, options);
		System.out.println("Return: " + returnCode);
	}

	public MagicObject containerMetadata(String storageContainer, String sirfContainer) {
		log.info("Returning magic object for " + sirfContainer);
		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		return new MagicObject(containerApi.get(sirfContainer).getMetadata());
	}

	public InputStream getFileInputStream(String storageContainerName, String filepath) throws IOException {
		String sirfContainerName = filepath.substring(0, filepath.indexOf('/'));
		String fileName = filepath.substring(filepath.indexOf('/') + 1);
		log.debug("Get file: Container = " + sirfContainerName + " File = " + fileName);
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, sirfContainerName);
		SwiftObject fileObject = objectApi.get(fileName);
		if(fileObject == null || fileObject.getPayload() == null)
			return null;		
		return fileObject.getPayload().openStream();
	}

	public void uploadObjectFromString(String storageContainerName, String filename, String content) {
		log.debug("Upload obj: Container = " + storageContainerName + " File = " + filename);
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, storageContainerName);
		Payload payload = newByteSourcePayload(wrap(content.getBytes()));
		objectApi.put(filename, payload, PutOptions.Builder.metadata(ImmutableMap.of("key1", "value1")));
	}
	
	public void uploadObjectFromByteArray(String storageContainerName, String filename, byte[] b) {
		log.debug("Upload obj from b[]: Container = " + storageContainerName + " File = " + filename);
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, storageContainerName);
		Payload payload = newByteSourcePayload(wrap(b));
		objectApi.put(filename, payload);
	}

	public void deleteContainer(String storageContainerName) {
		log.info("Deleting storage container: " + storageContainerName);
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, storageContainerName);
		ObjectList list = objectApi.list();

		Iterator<SwiftObject> itr = list.iterator();

		while (itr.hasNext()) {
			String objectName = itr.next().getName();
			objectApi.delete(objectName);
		}

		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		containerApi.deleteIfEmpty(storageContainerName);
	}

	public void deleteObject(String storageContainerName, String objectName) {
		log.info("Deleting object: " + objectName);
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, storageContainerName);
		objectApi.delete(objectName);
	}

	public Set<Container> listContainers() {
		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		Set<Container> containers = containerApi.list().toSet();

		return containers;
	}

	public void close() throws IOException {
		Closeables.close(swiftApi, true);
		Closeables.close(blobStoreContext, true);
	}
}
