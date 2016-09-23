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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.opensirf.container.MagicObject;
import org.opensirf.format.GenericMarshaller;
import org.opensirf.format.GenericUnmarshaller;
import org.opensirf.format.SirfFormatException;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.storage.SirfStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesystemDriver implements Closeable {	

	static final Logger log = LoggerFactory.getLogger(FilesystemDriver.class); 

	public FilesystemDriver(ContainerConfiguration config) {
		fsConfig = (FilesystemConfiguration) config;
	}

	public void close() {
		
	}
	
	private void createDirectory(String dirPath) {
		new File(dirPath).mkdir();
	}

	public void createContainerAndMagicObject(String containerPath) {
		createDirectory(containerPath);
		createMagicObjectFile(containerPath, "1.0", "1", "catalog.json");
	}

	private void createMagicObjectFile(String containerPath, String containerSpecification,
			String sirfLevel, String sirfCatalogId) {
		MagicObject mo = new MagicObject(containerSpecification, sirfLevel, sirfCatalogId);
		String magicObjectPath = containerPath + "/" + "magic.json";
		try {
			String s = GenericMarshaller.marshal("application/json", mo);
			Path moPath = Paths.get(magicObjectPath);
			BufferedWriter wri = Files.newBufferedWriter(moPath);
			wri.write(s);
			wri.flush();
			wri.close();
		} catch(JAXBException jbe) {
			throw new SirfFormatException("JAXB exception trying to marshal the magic object.");
		} catch(IOException ioe) {
			throw new SirfStorageException("IO exception trying to write magic object to " +
				magicObjectPath + ". Please verify full path, filesystem permissions and capacity.");
		}
	}

	public MagicObject getMagicObject(String containerPath) {
		String magicObjectPath = containerPath + "/" + "magic.json";
		Path moPath = Paths.get(magicObjectPath);
		return GenericUnmarshaller.unmarshal("application/json", moPath, MagicObject.class);
	}

	public InputStream getFileInputStream(String container, String filename) throws IOException {
		return new FileInputStream(new File(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "/storage/" +
				container + "/" + filename));
	}

	public void uploadObjectFromString(String containerPath, String fileName, String content) {
		String objectLocation = containerPath + "/" + fileName;
		Path objectPath = Paths.get(objectLocation);
		BufferedWriter wri;
		try {
			wri = Files.newBufferedWriter(objectPath);
			wri.write(content);
			wri.flush();
			wri.close();
		} catch(IOException ioe) {
			throw new SirfStorageException("IO exception trying to write object " + fileName + " to " +
				objectLocation + ". Please verify full path, filesystem permissions and capacity.");
		}
	}

	public void uploadObjectFromByteArray(String containerPath, String fileName, byte[] b) {
		String objectLocation = containerPath + "/" + fileName;
		try {
			FileOutputStream fos = new FileOutputStream(objectLocation);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(b);
			bos.flush();
			bos.close();
		} catch(FileNotFoundException fnfe) {
			throw new SirfStorageException("File not found and could not be created: " + objectLocation 
					+ ", while trying to upload a new preservation object. Please verify full path, "
					+ "filesystem permissions and capacity.");
		} catch(IOException ioe) {
			throw new SirfStorageException("IO exception trying to write object " + fileName + " to " +
				objectLocation + ". Please verify full path, filesystem permissions and capacity.");
		} 
	}

	public void deleteContainer(String containerPath) {
		try {
			log.debug("Deleting container on " + containerPath);
			FileUtils.deleteDirectory(new File(containerPath));
		} catch(IOException ioe) {
			throw new SirfStorageException("IO exception trying to delete container on " +
				containerPath + ". Please verify full path and the filesystem contents.");
		}
	}

	public void deleteFile(String containerPath, String objectName) {
		new File(containerPath + "/" + objectName).delete();
	}

	private final FilesystemConfiguration fsConfig;
}
