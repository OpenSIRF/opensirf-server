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
package org.opensirf.jaxrs.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.opensirf.jaxrs.storage.fs.FilesystemConfiguration;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerConfiguration;
import org.opensirf.jaxrs.storage.swift.SwiftConfiguration;

public class ContainerConfigurationUnmarshaller {
	public ContainerConfigurationUnmarshaller(String mediaType) {
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(ContainerConfiguration.class,
					SwiftConfiguration.class,
					FilesystemConfiguration.class,
					MultiContainerConfiguration.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
			jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);			
		}
		catch(JAXBException je) {
			je.printStackTrace();
		}
	}
	
	public ContainerConfiguration unmarshalConfig(InputStream is) throws JAXBException {
		return jaxbUnmarshaller.unmarshal(new StreamSource(is), ContainerConfiguration.class).getValue();
	}
	
	private Unmarshaller jaxbUnmarshaller;
}
