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
package org.opensirf.jaxrs;

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
import org.opensirf.storage.StorageContainerStrategy;
import org.opensirf.storage.StrategyFactory;

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

			strat.close();
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
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
    	StorageContainerStrategy strat = StrategyFactory.createStrategy(config);

		SIRFContainer container = new SIRFContainer(containerName);
		strat.createContainer(containerName);
		strat.pushProvenanceInformation("SNIA LTR TWG");

		SIRFCatalog catalog = container.getCatalog();

		// Unit test for some categories
		// ContainerAuditLogReference cal = new ContainerAuditLogReference();
		// cal.setReferenceRole("Container-audit-log");
		// cal.setReferenceType("container ref type");
		// cal.setReferenceValue("ref value");
		// ContainerAuditLogReference cal2 = new ContainerAuditLogReference();
		// cal2.setReferenceRole("Container-audit-log2");
		// cal2.setReferenceType("container ref type2");
		// cal2.setReferenceValue("ref value2");
		// catalog.getContainerInformation().getContainerAuditLogs().add(cal);
		// catalog.getContainerInformation().getContainerAuditLogs().add(cal2);
		// End of unit test

		strat.pushCatalog(catalog);

		strat.close();

		return Response.created(new URI("sirf/container/" + containerName)).build();
	}

	@DELETE
	@Path("container/{containername}")
	public Response deleteContainer(@PathParam("containername") String containerName) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
		StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
		strat.deleteContainer();
		strat.close();

		return Response.ok().build();
	}

	// Swift specific??
	// @GET
	// @Path("container")
	// @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	// public HashSet<Container> listContainers() throws IOException {
	//
	// try {
	// StorageContainerStrategy strat = StrategyFactory.createStrategy();
	// HashSet<Container> containers = new HashSet<Container>();
	//
	// for(org.jclouds.openstack.swift.v1.domain.Container c :
	// jcloudsSwift.listContainers())
	// containers.add(new Container(c.getName()));
	//
	// jcloudsSwift.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	//
	// return containers;
	// }

	@GET
	@Path("container/{containername}/catalog")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SIRFCatalog getCatalog(@PathParam("containername") String containerName) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
		StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
		SIRFCatalog c = strat.getCatalog();
		strat.close();
		return c;
	}
}
