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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SIRFConfigurationUnmarshaller;
import org.opensirf.jaxrs.storage.AbstractStrategyFactory;
import org.opensirf.jaxrs.storage.IStorageContainerStrategy;
import org.opensirf.obj.DigestInformation;
import org.opensirf.obj.FixityInformation;
import org.opensirf.obj.PreservationObjectInformation;

@Path("sirf")
public class ObjectApi {	
	@GET
	@Path("container/{containername}/{po}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PreservationObjectInformation getPOMetadata(@PathParam("containername") String containerName, @PathParam("po") String poUUID) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));

		IStorageContainerStrategy strat = AbstractStrategyFactory.createStrategy(config);
		
		SIRFCatalog c = strat.getCatalog();
		PreservationObjectInformation poi = null;
		poi = c.getSirfObjects().get(poUUID);
		return poi;
	}
	
	@GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("container/{containername}/{po}/data")
	public Response getPreservationObjectData(@PathParam("containername") String containerName, @PathParam("po") String poName) throws IOException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
    	
		IStorageContainerStrategy strat = AbstractStrategyFactory.createStrategy(config);
		
		StreamingOutput so = strat.getPreservationObjectStreamingOutput(poName);
		
		return Response.ok(so)
				.header("content-disposition","attachment;filename=" + poName).build();
	}

	@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("container/{containername}/po")
	public Response submitPO(@PathParam("containername") String container,
			@FormDataParam("poi") FormDataBodyPart poiBodyPart,
			@FormDataParam("inputstream") InputStream inputStream)
					throws IOException, URISyntaxException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(
    			SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
		
		poiBodyPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    PreservationObjectInformation poi = poiBodyPart.getValueAs(PreservationObjectInformation.class);
	    
	    poi.setVersionIdentifierUUID(poi.getObjectIdentifiers().get(0).
	    		getObjectVersionIdentifier().getObjectIdentifierValue());
	     
    	IStorageContainerStrategy strat = AbstractStrategyFactory.createStrategy(config);
		
		try {
			SIRFCatalog catalog = strat.getCatalog();
			
			byte[] b = IOUtils.toByteArray(inputStream);
			
			String sha1Hex = getSHA1(b);
			DigestInformation di = new DigestInformation("ObjectApi", "SHA-1", sha1Hex);
			poi.setObjectFixity(new FixityInformation(di));
			catalog.getSirfObjects().put(poi);
			strat.pushCatalog(catalog);
			strat.pushPreservationObject(poi.getVersionIdentifierUUID(), b);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return Response.created(new URI("sirf/container/" + container + "/" +
				poi.getVersionIdentifierUUID())).build();
	}
	
	private String getSHA1(byte[] b) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return bytesToHex(md.digest(b));
		}
		catch(NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return null;
		}
	}
	
	private static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}
	
	@DELETE
	@Path("container/{containername}/{po}")
	public Response deletePO(@PathParam("containername") String containerName, @PathParam("po") String poName) throws IOException, URISyntaxException {
		
		// TODO: overload with other DELETE
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
    	IStorageContainerStrategy strat = AbstractStrategyFactory.createStrategy(config);
		SIRFCatalog catalog = strat.getCatalog();
		catalog.getSirfObjects().remove(poName);
		strat.pushCatalog(catalog);

		return Response.ok().build();
	}
	
	@DELETE
	@Path("container/{containername}/{po}/data")
	public Response deletePOAndMetadata(@PathParam("containername") String containerName, @PathParam("po") String poName) throws IOException, URISyntaxException {
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().
    			unmarshalConfig(new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"))));
    	IStorageContainerStrategy strat = AbstractStrategyFactory.createStrategy(config);
		SIRFCatalog catalog = strat.getCatalog();
		catalog.getSirfObjects().remove(poName);
		strat.pushCatalog(catalog);
		strat.deletePreservationObject(poName);

		return Response.ok().build();
	}
}
