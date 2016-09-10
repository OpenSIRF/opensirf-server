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
package org.opensirf.jaxrs.storage.fs;


import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.jclouds.openstack.swift.v1.domain.Container;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.model.MagicObject;
import org.opensirf.jaxrs.storage.ISirfDriver;

public class FilesystemDriver implements ISirfDriver {	
	public FilesystemDriver(ContainerConfiguration config) {
		fsConfig = (FilesystemConfiguration) config;
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#createContainer(java.lang.String)
	 */
	@Override
	public void createContainer(String containerName) {
		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#containerMetadata(java.lang.String)
	 */
	@Override
	public MagicObject containerMetadata(String containerName) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#uploadObjectFromFile(java.lang.String, java.lang.String)
	 */
	@Override
	public void uploadObjectFromFile(String swiftContainerName, String fileName) {
		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#downloadSmallObjectFromFile(java.lang.String, java.lang.String)
	 */
	@Override
	public String downloadSmallObjectFromFile(String container, String filename) throws IOException {
		return null;
		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#getFileInputStream(java.lang.String, java.lang.String)
	 */
	@Override
	public InputStream getFileInputStream(String container, String filename) throws IOException {
		return null;
		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#uploadObjectFromString(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void uploadObjectFromString(String containerName, String fileName, String content) {		
	}
	
	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#uploadObjectFromByteArray(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void uploadObjectFromByteArray(String containerName, String fileName, byte[] b) {		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#deleteContainer(java.lang.String)
	 */
	@Override
	public void deleteContainer(String containerName) {		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#deleteObject(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteObject(String containerName, String objectName) {
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#listContainers()
	 */
	@Override
	public Set<Container> listContainers() {
		return null;
		
	}

	/* (non-Javadoc)
	 * @see org.opensirf.jaxrs.driver.ISirfDriver#close()
	 */
	@Override
	public void close() throws IOException {
		
	}
	
	private final FilesystemConfiguration fsConfig;
}
