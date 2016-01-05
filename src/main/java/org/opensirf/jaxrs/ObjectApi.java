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
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.obj.DigestInformation;
import org.opensirf.obj.FixityInformation;
import org.opensirf.obj.PreservationObjectIdentifier;
import org.opensirf.obj.PreservationObjectInformation;
import org.opensirf.obj.PreservationObjectLogicalIdentifier;
import org.opensirf.obj.PreservationObjectName;
import org.opensirf.obj.PreservationObjectParentIdentifier;
import org.opensirf.obj.PreservationObjectVersionIdentifier;
import org.opensirf.obj.Retention;
import org.opensirf.storage.StorageContainerStrategy;
import org.opensirf.storage.StrategyFactory;

@Path("sirf")
public class ObjectApi {

	@GET
	@Path("container/{containername}/{po}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PreservationObjectInformation getPOMetadata(@PathParam("containername") String containerName, @PathParam("po") String poUUID) throws IOException {
		StorageContainerStrategy strat = StrategyFactory.createStrategy(containerName);
		
		SIRFCatalog c = strat.getCatalog();
		PreservationObjectInformation poi = null;
		poi = c.getSirfObjects().get(poUUID);
		strat.close();
		
		return poi;
	}
	
	@GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("container/{containername}/{po}/data")
	public Response getPreservationObjectData(@PathParam("containername") String containerName, @PathParam("po") String poName) throws IOException {
		StorageContainerStrategy strat = StrategyFactory.createStrategy(containerName);
		
		StreamingOutput so = strat.getPreservationObjectStreamingOutput(poName);
		
		return Response.ok(so)
				.header("content-disposition","attachment;filename=" + poName).build();
	}
	
	@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("container/{containername}/{po}")
	public Response submitPO(@PathParam("containername") String container, @PathParam("po") String poName, @FormDataParam("objectName") String objectName,
			@FormDataParam("inputstream") InputStream inputStream) throws IOException, URISyntaxException {
		StorageContainerStrategy strat = StrategyFactory.createStrategy(container);
		
		try {
			SIRFCatalog catalog = strat.getCatalog();
		
			PreservationObjectInformation poi = new PreservationObjectInformation("none");
			PreservationObjectIdentifier poId = new PreservationObjectIdentifier();
			String logicalIdentifier = container + "-" + poName;
			String versionIdentifier = logicalIdentifier + "-1.0";
			poId.setObjectLogicalIdentifier(new PreservationObjectLogicalIdentifier("logicalIdentifier", "en", logicalIdentifier));
			poId.setObjectParentIdentifier(new PreservationObjectParentIdentifier("parentIdentifier", "en", "null"));
			poId.setObjectVersionIdentifier(new PreservationObjectVersionIdentifier("versionIdentifier", "en", versionIdentifier));
			poId.putObjectName(new PreservationObjectName("name", "en", objectName));
			poi.addObjectIdentifier(poId);
			
			byte[] b = IOUtils.toByteArray(inputStream);
			
			String sha1Hex = getSHA1(b);
			System.out.println("SHA-1 sum: " + sha1Hex);
			DigestInformation di = new DigestInformation("ObjectApi", "SHA-1", sha1Hex);
			poi.setObjectFixity(new FixityInformation(di));
			
			poi.setObjectRetention(new Retention("time_period", "default"));
			
			// Begin: added functionality (for FVT purposes)
			
//			PreservationObjectIdentifier poId2 = new PreservationObjectIdentifier();
//			String logicalIdentifier2 = "2 SWIFT-" + container + "-" + poName;
//			String versionIdentifier2 = logicalIdentifier2 + "-1.0 2";
//			poId2.setObjectLogicalIdentifier(new PreservationObjectLogicalIdentifier("logicalIdentifier2", "en2", logicalIdentifier2));
//			poId2.setObjectParentIdentifier(new PreservationObjectParentIdentifier("parentIdentifier2", "en2", "null2"));
//			poId2.setObjectVersionIdentifier(new PreservationObjectVersionIdentifier("versionIdentifier2", "en2", versionIdentifier2));
//			poId2.putObjectName(new PreservationObjectName("name2", "en", objectName));
//			poId2.putObjectName(new PreservationObjectName("name3", "en", objectName));
//			poi.addObjectIdentifier(poId2);
//			
//			RelatedObjects ros1 = new RelatedObjects();
//			RelatedObjectReference ror1 = new RelatedObjectReference();
//			ror1.setReferenceRole("sample related reference role 1");
//			ror1.setReferenceType("sample related reference type 1");
//			ror1.setReferenceValue("sample related reference value 1");
//			ros1.setObjectRelatedObjectsReference(ror1);
//			RelatedObjects ros2 = new RelatedObjects();			
//			RelatedObjectReference ror2 = new RelatedObjectReference();
//			ror2.setReferenceRole("sample related reference role 2");
//			ror2.setReferenceType("sample related reference type 2");
//			ror2.setReferenceValue("sample related reference value 2");
//			ros2.setObjectRelatedObjectsReference(ror2);
//			HashSet<RelatedObjects> roSet = new HashSet<RelatedObjects>();
//			roSet.add(ros1); roSet.add(ros2);			
//			poi.setObjectRelatedObjects(roSet);
//			
//			HashSet<PreservationObjectAuditLog> alSet = new HashSet<PreservationObjectAuditLog>();
//			PreservationObjectAuditLog al1 = new PreservationObjectAuditLog();
//			AuditLogReference alr1 = new AuditLogReference();
//			alr1.setReferenceRole("sample audit log role 1");
//			alr1.setReferenceType("sample audit log type 1");
//			alr1.setReferenceValue("sample audit log value 1");
//			al1.setObjectAuditLogReference(alr1);
//			PreservationObjectAuditLog al2 = new PreservationObjectAuditLog();
//			AuditLogReference alr2 = new AuditLogReference();
//			alr2.setReferenceRole("sample audit log role 2");
//			alr2.setReferenceType("sample audit log type 2");
//			alr2.setReferenceValue("sample audit log value 2");
//			al2.setObjectAuditLogReference(alr2);
//			alSet.add(al1); alSet.add(al2);
//			poi.setObjectAuditLogObjectIds(alSet);
//			
//			HashSet<Extension> exSet = new HashSet<Extension>();
//			Extension e1 = new Extension();
//			e1.setObjectExtensionDescription("sample ext descr 1");
//			e1.setObjectExtensionOrganization("sample ext org 1");
//			HashSet<ExtensionPair> pairSet = new HashSet<ExtensionPair>();
//			pairSet.add(new ExtensionPair("ext key example1", "ext value example1"));
//			pairSet.add(new ExtensionPair("ext key example2", "ext value example2"));
//			e1.setObjectExtensionPairs(pairSet);
//			Extension e2 = new Extension();
//			e2.setObjectExtensionDescription("sample ext descr 2");
//			e2.setObjectExtensionOrganization("sample ext org 3");
//			HashSet<ExtensionPair> pairSet2 = new HashSet<ExtensionPair>();
//			pairSet2.add(new ExtensionPair("ext key example3", "ext value example3"));
//			pairSet2.add(new ExtensionPair("ext key example4", "ext value example4"));
//			e2.setObjectExtensionPairs(pairSet2);
//			exSet.add(e1); exSet.add(e2);			
//			poi.setObjectExtension(exSet);

			// End: added functionality
			
			catalog.getSirfObjects().put(poi);
			
			strat.pushCatalog(catalog);
			strat.pushPreservationObject(versionIdentifier, b);
			strat.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return Response.created(new URI("sirf/container/" + container + "/" + poName))
				.build();
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
		
		StorageContainerStrategy strat = StrategyFactory.createStrategy(containerName);
		
		try {
			SIRFCatalog catalog = strat.getCatalog();
			catalog.getSirfObjects().remove(poName);
			strat.pushCatalog(catalog);
			strat.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return Response.ok().build();
	}
	
	@DELETE
	@Path("container/{containername}/{po}/data")
	public Response deletePOAndMetadata(@PathParam("containername") String containerName, @PathParam("po") String poName) throws IOException, URISyntaxException {
		

		StorageContainerStrategy strat = StrategyFactory.createStrategy(containerName);
		
		try {
			SIRFCatalog catalog = strat.getCatalog();
			catalog.getSirfObjects().remove(poName);
			strat.pushCatalog(catalog);
			strat.deletePreservationObject(poName);
			strat.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return Response.ok().build();
	}
}
