///*
// * OpenSIRF JAX-RS
// * 
// * Copyright IBM Corporation 2015.
// * All Rights Reserved.
// * 
// * MIT License:
// * 
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// * 
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// * 
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// * 
// * Except as contained in this notice, the name of a copyright holder shall not
// * be used in advertising or otherwise to promote the sale, use or other
// * dealings in this Software without prior written authorization of the
// * copyright holder.
// */
//package org.opensirf.jaxrs.storage.multicontainer;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Set;
//
//import org.jclouds.openstack.swift.v1.domain.Container;
//import org.opensirf.container.MagicObject;
//import org.opensirf.jaxrs.config.ContainerConfiguration;
//import org.opensirf.jaxrs.storage.ISirfDriver;
//
//public class MultiContainerDriver implements ISirfDriver {
//	
//	public MultiContainerDriver(ContainerConfiguration config) {
//	}
//
//	public void createContainerAndMagicObject(String containerName) {
//		
//	}
//
//	public MagicObject containerMetadata(String containerName) {
//		return null;
//	}
//
//	public InputStream getFileInputStream(String container, String filename) throws IOException {
//		return null;
//	}
//
//	public void uploadObjectFromString(String containerName, String fileName, String content) {
//	}
//	
//	public void uploadObjectFromByteArray(String containerName, String fileName, byte[] b) {
//	}
//
//	public void deleteContainer(String containerName) {
//	}
//
//	public void deleteObject(String containerName, String objectName) {
//	}
//
//	public Set<Container> listContainers() {
//		return null;
//	}
//
//	public void close() throws IOException {
//	}
//}
