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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class SwiftDriver implements ISirfDriver {

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

	public void createContainerAndMagicObject(String containerName) {
		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		CreateContainerOptions options = CreateContainerOptions.Builder.metadata(ImmutableMap.
				of("containerSpecification", "1.0", "sirfLevel", "1", "sirfCatalogId", "catalog.json"));
		System.out.println("Creating container " + containerName);
		boolean returnCode = containerApi.create(containerName, options);
		System.out.println("Return: " + returnCode);
	}

	public MagicObject containerMetadata(String containerName) {
		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		return new MagicObject(containerApi.get(containerName).getMetadata());
	}

//	public void uploadObjectFromFile(String swiftContainerName, String fileName) {
//		try {
//			ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, swiftContainerName);
//			File file = new File(fileName);
//			FileInputStream fis = new FileInputStream(file);
//			byte[] fileBytes = new byte[(int) file.length()];
//			fis.read(fileBytes);
//			Payload payload = newByteSourcePayload(wrap(fileBytes));
//			fis.close();
//
//			objectApi.put(fileName, payload, PutOptions.Builder.metadata(ImmutableMap.of("key1", "value1")));
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//	}

//	public String downloadSmallObjectFromFile(String container, String filename) throws IOException {
//		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, container);
//		SwiftObject fileObject = objectApi.get(filename);
//		InputStream is = fileObject.getPayload().openStream();
//
//		return IOUtils.toString(is);
//	}

	public InputStream getFileInputStream(String container, String filename) throws IOException {
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, container);
		SwiftObject fileObject = objectApi.get(filename);
		return fileObject.getPayload().openStream();
	}

	public void uploadObjectFromString(String containerName, String fileName, String content) {
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, containerName);
		Payload payload = newByteSourcePayload(wrap(content.getBytes()));
		objectApi.put(fileName, payload, PutOptions.Builder.metadata(ImmutableMap.of("key1", "value1")));
	}
	
	public void uploadObjectFromByteArray(String containerName, String fileName, byte[] b) {
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, containerName);
		Payload payload = newByteSourcePayload(wrap(b));
		objectApi.put(fileName, payload);
	}

	public void deleteContainer(String containerName) {
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, containerName);
		ObjectList list = objectApi.list();

		Iterator<SwiftObject> itr = list.iterator();

		while (itr.hasNext()) {
			String objectName = itr.next().getName();
			objectApi.delete(objectName);
		}

		ContainerApi containerApi = swiftApi.getContainerApiForRegion(region);
		containerApi.deleteIfEmpty(containerName);
	}

	public void deleteObject(String containerName, String objectName) {
		ObjectApi objectApi = swiftApi.getObjectApiForRegionAndContainer(region, containerName);
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
