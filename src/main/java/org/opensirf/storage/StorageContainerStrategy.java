package org.opensirf.storage;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.StreamingOutput;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.model.MagicObject;

public interface StorageContainerStrategy extends Closeable {
	
	public void close() throws IOException;
	
	public SIRFConfiguration getConfig();
	
	public void setConfig(SIRFConfiguration c);
	
	public MagicObject retrieveMagicObject();

	public void createContainer(String containerName);

	public void pushProvenanceInformation(String authorName);

	public void pushPreservationObject(String poUUID, byte[] b);

	public void pushCatalog(SIRFCatalog catalog);

	public void deleteContainer();

	public void deletePreservationObject(String poName);
	
	public SIRFCatalog getCatalog();
	
	public StreamingOutput getPreservationObjectStreamingOutput(String poUUID);
	
	public InputStream getPreservationObjectInputStream(String poUUID);
}

