package org.opensirf.jaxrs.storage.multicontainer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.container.MagicObject;
import org.opensirf.container.ProvenanceInformation;
import org.opensirf.container.SIRFContainer;
import org.opensirf.format.GenericMarshaller;
import org.opensirf.format.GenericUnmarshaller;
import org.opensirf.format.ProvenanceInformationMarshaller;
import org.opensirf.format.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.format.SirfFormatException;
import org.opensirf.jaxrs.api.PreservationObjectNotFoundException;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.storage.AbstractDriverFactory;
import org.opensirf.jaxrs.storage.ISirfDriver;
import org.opensirf.jaxrs.storage.IStorageContainerStrategy;
import org.opensirf.jaxrs.storage.SirfStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiContainerStrategy implements IStorageContainerStrategy {
	
	static final Logger log = LoggerFactory.getLogger(MultiContainerStrategy.class); 

	private InputStream poInputStream;
	
	protected MultiContainerStrategy() {
		poller = new StoragePoller();
	}
	
	public MultiContainerStrategy(ContainerConfiguration c) { 
		this();
		this.config = c;
	}
	
	public void setConfig(ContainerConfiguration c) {
		this.config = c;
	}

	public ContainerConfiguration getConfig() {
		return config;
	}

	private ContainerConfiguration config;

	@Override
	public MagicObject retrieveMagicObject(String containerName) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		ContainerConfiguration sourceStorageContainer = poller.getContainerByName(index.getCatalogContainerName(),
				(MultiContainerConfiguration) config);
		ISirfDriver driver = AbstractDriverFactory.createDriver(sourceStorageContainer);
		return driver.containerMetadata(sourceStorageContainer.getContainerName() + "/" + containerName);
	}

	@Override
	public void createContainer(String containerName) {
		try {
			ContainerConfiguration target = poller.findTargetStorageContainer(
					(MultiContainerConfiguration) config);
			ISirfDriver targetDriver = AbstractDriverFactory.createDriver(target);
			
			targetDriver.createContainerAndMagicObject(containerName);
			targetDriver.close();
			
			MultiContainerIndex index = new MultiContainerIndex();
			index.setCatalogContainerName(target.getContainerName());
			writeMultiContainerIndex(containerName, index);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void writeMultiContainerIndex(String containerName, MultiContainerIndex index) {
		String indexPath = SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "/" + "index.json";
		try {
			String s = GenericMarshaller.marshal("application/json", index);
			Path moPath = Paths.get(indexPath);
			BufferedWriter wri = Files.newBufferedWriter(moPath);
			wri.write(s); wri.flush(); wri.close();
		} catch(JAXBException jbe) {
			throw new SirfFormatException("JAXB exception trying to marshal the index.");
		} catch(IOException ioe) {
			throw new SirfStorageException("IO exception trying to write index to " +	indexPath +
					". Please verify full path, filesystem permissions and capacity.");
		}
	}
	
	private MultiContainerIndex readMultiContainerIndex(String containerName) {
		String indexPath = SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "/" + "index.json";
		return GenericUnmarshaller.unmarshal("application/json",
				Paths.get(indexPath), MultiContainerIndex.class);
	}

	@Override
	public SIRFCatalog getCatalog(String containerName) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		ContainerConfiguration sourceStorageContainer = poller.getContainerByName(index.getCatalogContainerName(),
				(MultiContainerConfiguration) config);
		String catalogPath = containerName + "/" + SIRFContainer.SIRF_DEFAULT_CATALOG_ID;
		ISirfDriver driver = AbstractDriverFactory.createDriver(sourceStorageContainer);
		SIRFCatalog catalog = null;
		
		try {			
			InputStream is = driver.getFileInputStream(sourceStorageContainer.getContainerName(), catalogPath);
			catalog = new SIRFCatalogUnmarshaller("application/json").unmarshalCatalog(is);
			is.close();
			driver.close();
			
			return catalog;
		} catch(JAXBException jbe) {
			jbe.printStackTrace();
		} catch(NullPointerException npe) {
			npe.printStackTrace();
			return null;
		} catch(FileNotFoundException fnfe) {
			log.warn("Failure trying to find catalog. If a catalog is being pushed for the first "
					+ "time it is safe to ignore this message.");
			fnfe.printStackTrace();
		} catch(PreservationObjectNotFoundException ponfe) {
			log.warn("Failure trying to find catalog. If a catalog is being pushed for the first "
					+ "time it is safe to ignore this message.");
			ponfe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return catalog;
	}

	@Override
	public StreamingOutput getPreservationObjectStreamingOutput(String containerName, String poName) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		ContainerConfiguration sourceStorageContainer = poller.getContainerByName(index.getCatalogContainerName(),
				(MultiContainerConfiguration) config);
		
		String poPath = containerName + "/" + poName;
		ISirfDriver sourceDriver = AbstractDriverFactory.createDriver(sourceStorageContainer);
		StreamingOutput so = null;
		
		try {
			poInputStream = sourceDriver.getFileInputStream(sourceStorageContainer.getContainerName(),
					poPath);
			
			so = new StreamingOutput() {
				public void write(OutputStream out) throws IOException, WebApplicationException {
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = poInputStream.read(bytes)) != -1)
                        out.write(bytes, 0, read);
				}
			};
			
			sourceDriver.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return so;
	}

	@Override
	public void deletePreservationObject(String poUUID, String containerName) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		ContainerConfiguration sourceStorageContainer = poller.getContainerByName(
				index.getPoIndex().get(poUUID), (MultiContainerConfiguration) config);
		ISirfDriver sourceDriver = AbstractDriverFactory.createDriver(sourceStorageContainer);
		
		sourceDriver.deleteObject(containerName, poUUID);
		removePreservationObjectFromIndex(containerName, sourceStorageContainer, poUUID);
	}

	@Override
	public InputStream getPreservationObjectInputStream(String containerName, String poUUID) {
		ContainerConfiguration target = poller.findTargetStorageContainer(
				(MultiContainerConfiguration) config);
		ISirfDriver targetDriver = AbstractDriverFactory.createDriver(target);
		
		return null;
	}

	@Override
	public void pushProvenanceInformation(String authorName, String containerName) {
		ContainerConfiguration target = poller.findTargetStorageContainer(
				(MultiContainerConfiguration) config);
		ISirfDriver targetDriver = AbstractDriverFactory.createDriver(target);
		
		try {
			targetDriver.uploadObjectFromString(containerName,
				SIRFContainer.SIRF_DEFAULT_PROVENANCE_MANIFEST_FILE,
				new ProvenanceInformationMarshaller("application/json")
				.marshalProvenanceInformation(new ProvenanceInformation(authorName)));

			targetDriver.close();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void pushCatalog(SIRFCatalog catalog, String containerName) {
		ContainerConfiguration target = poller.findTargetStorageContainer(
				(MultiContainerConfiguration) config);
		ISirfDriver targetDriver = AbstractDriverFactory.createDriver(target);
		
		try {
			SIRFCatalog existingCatalog = getCatalog(containerName);
			
			// Only metadata updates
			if(existingCatalog != null && existingCatalog.getSirfObjects() != null) {					
				if (existingCatalog.getSirfObjects().size() >= 0 &&
						catalog.getSirfObjects().size() == 0) {
					catalog.getSirfObjects().addAll(existingCatalog.getSirfObjects());
				}
			}
			
			targetDriver.uploadObjectFromString(containerName, SIRFContainer.SIRF_DEFAULT_CATALOG_ID,
					new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
			targetDriver.close();
			
		} catch(JAXBException jbe) {
			jbe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void pushPreservationObject(String poUUID, byte[] b, String containerName) {
		try {
			ContainerConfiguration target = poller.findTargetStorageContainer(
					(MultiContainerConfiguration) config);
			ISirfDriver targetDriver = AbstractDriverFactory.createDriver(target);
			targetDriver.uploadObjectFromByteArray(containerName, poUUID, b);
			addPreservationObjectToIndex(containerName, target, poUUID);			
			
			targetDriver.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void addPreservationObjectToIndex(String containerName,
			ContainerConfiguration targetStorageContainer, String poUUID) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		index.getPoIndex().put(poUUID, targetStorageContainer.getContainerName());
		writeMultiContainerIndex(containerName, index);
	}

	private void removePreservationObjectFromIndex(String containerName,
			ContainerConfiguration sourceStorageContainer, String poUUID) {
		MultiContainerIndex index = readMultiContainerIndex(containerName);
		index.getPoIndex().put(poUUID, sourceStorageContainer.getContainerName());
		writeMultiContainerIndex(containerName, index);
	}
	
	@Override
	public void deleteContainer(String containerName) {
		for(ContainerConfiguration c: ((MultiContainerConfiguration) config).getSubconfigurations()) {
			ISirfDriver driver = AbstractDriverFactory.createDriver(c);
			driver.deleteContainer(containerName);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	private StoragePoller poller;
}
