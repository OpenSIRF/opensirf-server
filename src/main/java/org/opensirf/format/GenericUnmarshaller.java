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
package org.opensirf.format;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.opensirf.jaxrs.storage.SirfStorageException;

public class GenericUnmarshaller {
	private static <T> Unmarshaller createUnmarshaller(String mediaType, Class<T> clazz) {
		try	{
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		    jaxbUnmarshaller.setListener(new VersionIdentifierListener());
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
			return jaxbUnmarshaller;
		} catch(JAXBException je) {
			je.printStackTrace();
			return null;
		}
	}

	public static <T> T unmarshal(String mediaType, InputStream is, Class<T> clazz) {
		Unmarshaller u = createUnmarshaller(mediaType, clazz);
		try {
			return (T) u.unmarshal(new StreamSource(is), clazz).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new SirfFormatException("JAXB exception unmarshalling " + clazz.getSimpleName() + 
					". Please check the request and the contents of the requested object.");
		}
	}
	
	public static <T> T unmarshal(String mediaType, Path p, Class<T> clazz) {
		try {
			return unmarshal(mediaType, new FileInputStream(p.toFile()), clazz);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SirfStorageException("FileNotFound exception unmarshalling " +
					clazz.getSimpleName() + ". Please check that the file exists.");
		}
	}
	
	public static <T> T unmarshal(String mediaType, String s, Class<T> clazz) {
		System.out.println("STRING UNMARSHALLED = " + s);
		
		return unmarshal(mediaType, new ByteArrayInputStream(s.getBytes()), clazz);
	}
}
