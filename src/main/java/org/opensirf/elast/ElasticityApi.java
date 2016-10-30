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
package org.opensirf.elast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opensirf.format.GenericMarshaller;
import org.opensirf.format.GenericUnmarshaller;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.ContainerConfigurationMarshaller;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SirfConfigurationException;
import org.opensirf.jaxrs.storage.multicontainer.MultiContainerConfiguration;

/**
 * @author pviana
 *
 */
@Path("elast")
public class ElasticityApi {
	@PUT
	@Path("joinMultiConfiguration")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response addStorageContainerToMultiConfig(ContainerConfiguration containerConfig) throws JAXBException, IOException {
		SIRFConfiguration config = GenericUnmarshaller.unmarshal("application/json", Paths.get(
    			SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"), SIRFConfiguration.class);

		try {
			MultiContainerConfiguration multiConfig = (MultiContainerConfiguration) config.
					getContainerConfiguration();
			multiConfig.getSubconfigurations().add(containerConfig);
			System.out.println("NUM OF SUBCONFIGS = " + multiConfig.getSubconfigurations().size());
			FileOutputStream fos = new FileOutputStream(new File(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
			fos.write(GenericMarshaller.marshal("application/json", multiConfig).getBytes());
			fos.flush();
			fos.close();
		} catch(ClassCastException cce) {
			throw new SirfConfigurationException("An elasticity request was sent but the storage"
					+ " configuration does not support elasticity.");
		}
		
		return Response.ok().build();
	}
}
