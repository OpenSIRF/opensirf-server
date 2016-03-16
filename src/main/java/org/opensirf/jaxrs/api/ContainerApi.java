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
package org.opensirf.jaxrs.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.container.SIRFContainer;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SIRFConfigurationUnmarshaller;
import org.opensirf.jaxrs.model.MagicObject;

@Path("sirf")
public class ContainerApi {

	@OPTIONS
	@Path("container/{containername}")
	public Response containerOptions(@PathParam("containername") String containerName) {
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS, DELETE, HEAD")
				.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}

	@GET
	@Path("container/{containername}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public MagicObject getMagicObject(@PathParam("containername") String containerName) {
		try {
			SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
			StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
			MagicObject c = strat.retrieveMagicObject();
			
			return c;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	@PUT
	@Path("container/{containername}")
	public Response createContainer(@PathParam("containername") String containerName) throws IOException, URISyntaxException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(
    			SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
    	StorageContainerStrategy strat = StrategyFactory.createStrategy(config);

		SIRFContainer container = new SIRFContainer(containerName);
		strat.createContainer(containerName);
		strat.pushProvenanceInformation("SNIA LTR TWG", containerName);
		SIRFCatalog catalog = container.getCatalog();
		strat.pushCatalog(catalog, containerName);

		return Response.created(new URI("sirf/container/" + containerName)).build();
	}

	@DELETE
	@Path("container/{containername}")
	public Response deleteContainer(@PathParam("containername") String containerName) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
		StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
		strat.deleteContainer();

		return Response.ok().build();
	}

	@GET
	@Path("container/{containername}/catalog")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SIRFCatalog getCatalog(@PathParam("containername") String containerName) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
		StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
		SIRFCatalog c = strat.getCatalog();
		return c;
	}
}
