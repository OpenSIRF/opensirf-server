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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.opensirf.format.ProvenanceInformationMarshaller;
import org.opensirf.format.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.jaxrs.model.Container;
import org.opensirf.jaxrs.model.MagicObject;

import com.ibm.opensirf.catalog.SIRFCatalog;
import com.ibm.opensirf.container.ProvenanceInformation;
import com.ibm.opensirf.container.SIRFContainer;

@Path("sirf")
public class ContainerApi {

	@OPTIONS
	@Path("container/{containername}")
	public Response containerOptions(@PathParam("containername") String containerName) {
		return Response
			.ok()
			.header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Methods",	"POST, GET, PUT, UPDATE, OPTIONS, DELETE, HEAD")
			.header("Access-Control-Allow-Headers",	"Content-Type, Accept, X-Requested-With")
			.build();
	}
	
	@GET
	@Path("container/{containername}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public MagicObject getMagicObject(@PathParam("containername") String containerName) {
		JCloudsApi jcloudsSwift = new JCloudsApi();
		MagicObject c = jcloudsSwift.containerMetadata(containerName);
		
		try {
			jcloudsSwift.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return c;
	}

	@PUT
	@Path("container/{containername}")
	public Response createContainer(@PathParam("containername") String containerName) throws IOException, URISyntaxException {
		JCloudsApi jcloudsSwift = new JCloudsApi();

		try {
			SIRFContainer container = new SIRFContainer(containerName);
			jcloudsSwift.createContainer(containerName);
			ProvenanceInformation pi = new ProvenanceInformation("SNIA LTR TWG");
			jcloudsSwift.uploadObjectFromString(containerName, SIRFContainer.SIRF_DEFAULT_PROVENANCE_MANIFEST_FILE,	new ProvenanceInformationMarshaller("application/json").marshalProvenanceInformation(pi));
			SIRFCatalog catalog = container.getCatalog();

			// Unit test for some categories
//			ContainerAuditLogReference cal = new ContainerAuditLogReference();
//			cal.setReferenceRole("Container-audit-log");
//			cal.setReferenceType("container ref type");
//			cal.setReferenceValue("ref value");
//			ContainerAuditLogReference cal2 = new ContainerAuditLogReference();
//			cal2.setReferenceRole("Container-audit-log2");
//			cal2.setReferenceType("container ref type2");
//			cal2.setReferenceValue("ref value2");
//			catalog.getContainerInformation().getContainerAuditLogs().add(cal);
//			catalog.getContainerInformation().getContainerAuditLogs().add(cal2);
			// End of unit test
			
			jcloudsSwift.uploadObjectFromString(containerName, SIRFContainer.SIRF_DEFAULT_CATALOG_ID, new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
			jcloudsSwift.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch(JAXBException jbe) {
			jbe.printStackTrace();
		}

		return Response.created(new URI("sirf/container/" + containerName))
				.build();
	}
	
	@DELETE
	@Path("container/{containername}")
	public Response deleteContainer(@PathParam("containername") String containerName)
			throws IOException, URISyntaxException {
		
		JCloudsApi jcloudsSwift = new JCloudsApi();
		jcloudsSwift.deleteContainer(containerName);
		jcloudsSwift.close();

		try {
			jcloudsSwift.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return Response.ok().build();
	}
	
	@GET
	@Path("container")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public HashSet<Container> listContainers() throws IOException {
		JCloudsApi jcloudsSwift = new JCloudsApi();
		HashSet<Container> containers = new HashSet<Container>();
		
		for(org.jclouds.openstack.swift.v1.domain.Container c : jcloudsSwift.listContainers())
			containers.add(new Container(c.getName()));

		try {
			jcloudsSwift.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return containers;
	}

	@GET
	@Path("container/{containername}/catalog")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SIRFCatalog getCatalog(@PathParam("containername") String containerName)	throws IOException {
		JCloudsApi jcloudsSwift = new JCloudsApi();
		InputStream is = jcloudsSwift.getFileInputStream(containerName, SIRFContainer.SIRF_DEFAULT_CATALOG_ID);
		SIRFCatalog catalog = null;
		
		try {
			catalog = new SIRFCatalogUnmarshaller("application/json").unmarshalCatalog(is);
			jcloudsSwift.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
		
		return catalog;
	}
}
