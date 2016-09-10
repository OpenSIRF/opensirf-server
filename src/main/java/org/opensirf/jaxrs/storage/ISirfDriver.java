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

package org.opensirf.jaxrs.storage;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.jclouds.openstack.swift.v1.domain.Container;
import org.opensirf.jaxrs.model.MagicObject;

/**
 * @author pviana
 *
 */
public interface ISirfDriver extends Closeable {

	void createContainer(String containerName);

	MagicObject containerMetadata(String containerName);

	void uploadObjectFromFile(String swiftContainerName, String fileName);

	String downloadSmallObjectFromFile(String container, String filename) throws IOException;

	InputStream getFileInputStream(String container, String filename) throws IOException;

	void uploadObjectFromString(String containerName, String fileName, String content);

	void uploadObjectFromByteArray(String containerName, String fileName, byte[] b);

	void deleteContainer(String containerName);

	void deleteObject(String containerName, String objectName);

	Set<Container> listContainers();

	void close() throws IOException;

}