package org.opensirf.jaxrs.storage;

import java.io.Closeable;
import java.io.InputStream;

import javax.ws.rs.core.StreamingOutput;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.container.MagicObject;
import org.opensirf.jaxrs.config.ContainerConfiguration;

public interface IStorageContainerStrategy extends Closeable {
	
	public ContainerConfiguration getConfig();
	
	public void setConfig(ContainerConfiguration c);

	public MagicObject retrieveMagicObject(String containerName);

	public void createContainer(String containerName);

	public void pushPreservationObject(String poUUID, byte[] b, String containerName);

	public void pushProvenanceInformation(String authorName, String containerName);

	public void pushCatalog(SIRFCatalog catalog, String containerName);

	public void deleteContainer(String containerName);

	public void deletePreservationObject(String poName, String containerName);
	
	public SIRFCatalog getCatalog(String containerName);
	
	public StreamingOutput getPreservationObjectStreamingOutput(String poUUID, String containerName);
	
	public InputStream getPreservationObjectInputStream(String poUUID, String containerName);
}

