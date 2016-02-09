package org.opensirf.jaxrs.config;

import java.io.ByteArrayInputStream;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.catalog.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.obj.PreservationObjectInformation;
 
public class Test {

    public static void main(String[] args) throws Exception  {
    	String json = "{\"catalogId\":\"catalog.json\",\"containerInformation\":{\"containerSpecification\":{\"containerSpecificationIdentifier\":\"SIRF-1.0\",\"containerSpecificationSirfLevel\":\"1\",\"containerSpecificationVersion\":\"1.0\"},\"containerIdentifier\":{\"containerIdentifierLocale\":\"en\",\"containerIdentifierType\":\"containerIdentifier\",\"containerIdentifierValue\":\"philContainer4\"},\"containerState\":{\"containerStateType\":\"ready\",\"containerStateValue\":\"true\"},\"containerProvenanceReference\":{\"referenceRole\":\"Provenance\",\"referenceType\":\"internal\",\"referenceValue\":\"http://snia.org/sirf/philContainer4-provenance.po.json-1.0\"},\"containerAuditLog\":[]},\"objectsSet\":{\"objectInformation\":[{\"objectIdentifiers\":[{\"objectName\":[{\"objectIdentifierLocale\":\"en\",\"objectIdentifierType\":\"name\",\"objectIdentifierValue\":\"provenance.po.json\"}],\"objectLogicalIdentifier\":{\"objectIdentifierLocale\":\"en\",\"objectIdentifierType\":\"logicalIdentifier\",\"objectIdentifierValue\":\"http://snia.org/sirf/philContainer4-provenance.po.json\"},\"objectParentIdentifier\":{\"objectIdentifierLocale\":\"en\",\"objectIdentifierType\":\"parentIdentifier\",\"objectIdentifierValue\":\"null\"},\"objectVersionIdentifier\":{\"objectIdentifierLocale\":\"en\",\"objectIdentifierType\":\"versionIdentifier\",\"objectIdentifierValue\":\"http://snia.org/sirf/philContainer4-provenance.po.json-1.0\"}}],\"objectCreationDate\":\"2016-02-03T19:36Z\",\"objectLastModifiedDate\":\"2016-02-03T19:36Z\",\"objectLastAccessedDate\":\"2016-02-03T19:36Z\",\"objectRelatedObjects\":[],\"packagingFormat\":{\"packagingFormatName\":\"none\"},\"objectFixity\":{\"digestInformation\":[{\"digestAlgorithm\":\"SHA-1\",\"digestOriginator\":\"ObjectApi\",\"digestValue\":\"7bec3092783ac1cffe4ff4b0c98958e2b776a4e2\"}],\"lastCheckDate\":\"20160203193642\"},\"objectRetention\":{\"retentionType\":\"time_period\",\"retentionValue\":\"forever\"},\"objectAuditLog\":[],\"objectExtension\":[],\"versionIdentifierUUID\":\"http://snia.org/sirf/philContainer4-provenance.po.json-1.0\"}]}}";
    	SIRFCatalogUnmarshaller u = new SIRFCatalogUnmarshaller("application/json");
    	SIRFCatalog c = u.unmarshalCatalog(new ByteArrayInputStream(json.getBytes()));
    	System.out.println("Num of POs=" + c.getSirfObjects().size());
    	
    	for(PreservationObjectInformation poi : c.getSirfObjects().getMap().values()) {
    		System.out.println(poi.getVersionIdentifierUUID());
    	}
    	
    	SIRFCatalogMarshaller m = new SIRFCatalogMarshaller("application/json");
    	System.out.println(m.marshalCatalog(c));
    }
 
}