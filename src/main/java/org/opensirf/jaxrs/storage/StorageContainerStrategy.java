package org.opensirf.jaxrs.storage;

import java.io.InputStream;

import javax.ws.rs.core.StreamingOutput;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.model.MagicObject;

public interface StorageContainerStrategy {
	
	public ContainerConfiguration getConfig();
	
	public void setConfig(ContainerConfiguration c);
	
	public MagicObject retrieveMagicObject();

	public void createContainer(String containerName);

	public void pushProvenanceInformation(String authorName);

	public void pushPreservationObject(String poUUID, byte[] b);

	public void pushCatalog(SIRFCatalog catalog);
	
	public void pushProvenanceInformation(String authorName, String containerName);

	public void pushCatalog(SIRFCatalog catalog, String containerName);

	public void deleteContainer();

	public void deletePreservationObject(String poName);
	
	public SIRFCatalog getCatalog();
	
	public StreamingOutput getPreservationObjectStreamingOutput(String poUUID);
	
	public InputStream getPreservationObjectInputStream(String poUUID);
}

