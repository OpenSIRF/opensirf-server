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

//	@Deprecated
//	public MagicObject retrieveMagicObject();
	
	public MagicObject retrieveMagicObject(String containerName);

	public void createContainer(String containerName);

//	@Deprecated
//	public void pushProvenanceInformation(String authorName);

//	@Deprecated
//	public void pushPreservationObject(String poUUID, byte[] b);

	public void pushPreservationObject(String poUUID, byte[] b, String containerName);

//	@Deprecated
//	public void pushCatalog(SIRFCatalog catalog);
	
	public void pushProvenanceInformation(String authorName, String containerName);

	public void pushCatalog(SIRFCatalog catalog, String containerName);

//	@Deprecated
//	public void deleteContainer();
	
	public void deleteContainer(String containerName);

//	@Deprecated
//	public void deletePreservationObject(String poName);

	public void deletePreservationObject(String poName, String containerName);

//	@Deprecated
//	public SIRFCatalog getCatalog();
	
	public SIRFCatalog getCatalog(String containerName);
	
//	@Deprecated
//	public StreamingOutput getPreservationObjectStreamingOutput(String poUUID);
	
//	@Deprecated
//	public InputStream getPreservationObjectInputStream(String poUUID);
	
	public StreamingOutput getPreservationObjectStreamingOutput(String poUUID, String containerName);
	
	public InputStream getPreservationObjectInputStream(String poUUID, String containerName);
}

