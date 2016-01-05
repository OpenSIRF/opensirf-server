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
//package org.opensirf.format;
//
//import java.util.ArrayList;
//
//import org.opensirf.audit.AuditLogReference;
//import org.opensirf.audit.ContainerAuditLog;
//import org.opensirf.audit.ContainerAuditLogReference;
//import org.opensirf.audit.PreservationObjectAuditLog;
//import org.opensirf.catalog.SIRFCatalog;
//import org.opensirf.container.SIRFContainer;
//import org.opensirf.obj.DigestInformation;
//import org.opensirf.obj.Extension;
//import org.opensirf.obj.ExtensionPair;
//import org.opensirf.obj.FixityInformation;
//import org.opensirf.obj.PreservationObjectIdentifier;
//import org.opensirf.obj.PreservationObjectInformation;
//import org.opensirf.obj.PreservationObjectLogicalIdentifier;
//import org.opensirf.obj.PreservationObjectName;
//import org.opensirf.obj.PreservationObjectParentIdentifier;
//import org.opensirf.obj.PreservationObjectVersionIdentifier;
//import org.opensirf.obj.RelatedObjectReference;
//import org.opensirf.obj.RelatedObjects;
//import org.opensirf.obj.Retention;
//
//public class Test {
//	public static void main(String[] args) throws Exception {
//		String container = "SampleContainer";
//		String poName = "http://snia.org/sirf/" + container + "/UUID-Alone";
//		String objectName = "Poem 'Alone' written by Edgar Allan Poe in 1829";
//
//		SIRFContainer c = new SIRFContainer(container);
//		SIRFCatalog catalog = c.getCatalog();
//
//		ContainerAuditLog cal = new ContainerAuditLog();
//		cal.setContainerAuditLogReference(new ContainerAuditLogReference("internal", "AuditLog", "audit_log_object_version_identifier"));
//		
//		ContainerAuditLog cal2 = new ContainerAuditLog();
//		cal2.setContainerAuditLogReference(new ContainerAuditLogReference("external", "AuditLog", "http://link.to/auditLog"));
//		
//		catalog.getContainerInformation().getContainerAuditLog().add(cal);
//		catalog.getContainerInformation().getContainerAuditLog().add(cal2);
//		
//		PreservationObjectInformation poi = new PreservationObjectInformation("none");
//		PreservationObjectIdentifier poId = new PreservationObjectIdentifier();
//		String logicalIdentifier = poName;
//		String versionIdentifier = logicalIdentifier + "-1.0";
//		poId.setObjectLogicalIdentifier(new PreservationObjectLogicalIdentifier("logicalIdentifier", "en", logicalIdentifier));
//		poId.setObjectParentIdentifier(new PreservationObjectParentIdentifier("parentIdentifier", "en", "null"));
//		poId.setObjectVersionIdentifier(new PreservationObjectVersionIdentifier("versionIdentifier", "en", versionIdentifier));
//		poId.putObjectName(new PreservationObjectName("name", "en", objectName));
//		poi.addObjectIdentifier(poId);
//
//		DigestInformation di = new DigestInformation("ObjectApi", "SHA-1", "E0C3782686641E7D36185C1DE8581E59C2FC4D60");
//		poi.setObjectFixity(new FixityInformation(di));
//
//		// Begin: added functionality (for FVT purposes)
//
//		PreservationObjectIdentifier poId2 = new PreservationObjectIdentifier();
//		String logicalIdentifier2 = "SWIFT-" + container + "-" + poName;
//		String versionIdentifier2 = logicalIdentifier2 + "-1.0-2";
//		poId2.setObjectLogicalIdentifier(new PreservationObjectLogicalIdentifier("logicalIdentifier-2", "en", logicalIdentifier2));
//		poId2.setObjectParentIdentifier(new PreservationObjectParentIdentifier("parentIdentifier-2", "en", "null"));
//		poId2.setObjectVersionIdentifier(new PreservationObjectVersionIdentifier("versionIdentifier-2", "en", versionIdentifier2));
//		poId2.putObjectName(new PreservationObjectName("sample name 1", "en", "'Alone' - a poem written by Edgar Allan Poe"));
//		poId2.putObjectName(new PreservationObjectName("sample name 2", "en", "Poem 'Alone' by Edgar Allan Poe"));
//		poi.addObjectIdentifier(poId2);
//
//		RelatedObjects ros1 = new RelatedObjects();
//		RelatedObjectReference ror1 = new RelatedObjectReference();
//		ror1.setReferenceRole("sample related reference role 1");
//		ror1.setReferenceType("sample related reference type 1");
//		ror1.setReferenceValue("sample related reference value 1");
//		ros1.setObjectRelatedObjectsReference(ror1);
//		RelatedObjects ros2 = new RelatedObjects();
//		RelatedObjectReference ror2 = new RelatedObjectReference();
//		ror2.setReferenceRole("sample related reference role 2");
//		ror2.setReferenceType("sample related reference type 2");
//		ror2.setReferenceValue("sample related reference value 2");
//		ros2.setObjectRelatedObjectsReference(ror2);
//		ArrayList<RelatedObjects> roSet = new ArrayList<RelatedObjects>();
//		roSet.add(ros1);
//		roSet.add(ros2);
//		poi.setObjectRelatedObjects(roSet);
//
//		ArrayList<PreservationObjectAuditLog> alSet = new ArrayList<PreservationObjectAuditLog>();
//		PreservationObjectAuditLog al1 = new PreservationObjectAuditLog();
//		AuditLogReference alr1 = new AuditLogReference();
//		alr1.setReferenceRole("AuditLog");
//		alr1.setReferenceType("sample audit log type 1");
//		alr1.setReferenceValue("sample audit log value 1");
//		al1.setObjectAuditLogReference(alr1);
//		PreservationObjectAuditLog al2 = new PreservationObjectAuditLog();
//		AuditLogReference alr2 = new AuditLogReference();
//		alr2.setReferenceRole("AuditLog");
//		alr2.setReferenceType("sample audit log type 2");
//		alr2.setReferenceValue("sample audit log value 2");
//		al2.setObjectAuditLogReference(alr2);
//		alSet.add(al1);
//		alSet.add(al2);
//		poi.setObjectAuditLog(alSet);
//		
//		Retention r = new Retention("time_period", "10 years");
//		poi.setObjectRetention(r);
//
//		ArrayList<Extension> exSet = new ArrayList<Extension>();
//		Extension e1 = new Extension();
//		e1.setObjectExtensionDescription("sample extension description 1");
//		e1.setObjectExtensionOrganization("sample extension organization 1");
//		ArrayList<ExtensionPair> pairSet = new ArrayList<ExtensionPair>();
//		pairSet.add(new ExtensionPair("extension key example 1", "extension value example 1"));
//		pairSet.add(new ExtensionPair("extension key example 2", "extension value example 2"));
//		e1.setObjectExtensionPairs(pairSet);
//		Extension e2 = new Extension();
//		e2.setObjectExtensionDescription("sample extension description 2");
//		e2.setObjectExtensionOrganization("sample extension organization 2");
//		ArrayList<ExtensionPair> pairSet2 = new ArrayList<ExtensionPair>();
//		pairSet2.add(new ExtensionPair("extension key example 3", "extension value example 3"));
//		pairSet2.add(new ExtensionPair("extension key example 4", "extension value example 4"));
//		e2.setObjectExtensionPairs(pairSet2);
//		exSet.add(e1);
//		exSet.add(e2);
//		poi.setObjectExtension(exSet);
//
//		// End: added functionality
//
//		catalog.getSirfObjects().put(poi);
//		System.out.println(new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
//	}
//}
