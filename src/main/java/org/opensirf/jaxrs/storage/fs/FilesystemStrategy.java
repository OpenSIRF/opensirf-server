package org.opensirf.jaxrs.storage.fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.container.MagicObject;
import org.opensirf.container.ProvenanceInformation;
import org.opensirf.container.SIRFContainer;
import org.opensirf.format.ProvenanceInformationMarshaller;
import org.opensirf.format.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.jaxrs.api.ContainerApi;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.SirfConfigurationException;
import org.opensirf.jaxrs.storage.IStorageContainerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesystemStrategy implements IStorageContainerStrategy {

	static final Logger log = LoggerFactory.getLogger(FilesystemStrategy.class); 

	protected FilesystemStrategy() {
	}
	
	public FilesystemStrategy(ContainerConfiguration c) {
		setConfig(c);
	}
	
	public void setConfig(ContainerConfiguration c) {
		log.debug("ENTER setConfig()");
		try {
			this.config = (FilesystemConfiguration) c;
		} catch(ClassCastException cce) {
			throw new SirfConfigurationException("Object casting exception: filesystem configuration "
				+ "expected, but " + c.getClass().getSimpleName() + " was given. Please check the "
				+ "configuration.");
		}
	}

	public ContainerConfiguration getConfig() {
		log.debug("ENTER getConfig()");
		return config;
	}

	@Override
	public MagicObject retrieveMagicObject() {
		log.debug("ENTER retrieveMagicObject()");
		FilesystemDriver driver = new FilesystemDriver(config);
		MagicObject mo = driver.getMagicObject(config.getMountPoint() + "/" + config.getContainerName());
		driver.close();
		return mo;
	}

	@Override
	public void createContainer(String containerName) {
		log.debug("ENTER createContainer()");
		FilesystemDriver driver = new FilesystemDriver(config);
		driver.createContainerAndMagicObject(config.getMountPoint() + "/" + containerName);
		driver.close();
	}

	@Override
	public void pushProvenanceInformation(String authorName) {
		log.debug("ENTER pushProvenanceInformation()");
		pushProvenanceInformation(authorName, config.getContainerName());
	}

	@Override
	public void pushCatalog(SIRFCatalog catalog) {
		log.debug("ENTER pushCatalog()");
		pushCatalog(catalog, config.getContainerName());
	}

	@Override
	public void deleteContainer() {
		log.debug("ENTER deleteContainer()");
		FilesystemDriver driver = new FilesystemDriver(config);

		driver.deleteContainer(config.getContainerName());
		driver.close();
	}

	@Override
	public SIRFCatalog getCatalog() {
		log.debug("ENTER getCatalog()");
		SIRFCatalog catalog = null;

		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			InputStream is = driver.getFileInputStream(config.getContainerName(), SIRFContainer.SIRF_DEFAULT_CATALOG_ID);
			
			catalog = new SIRFCatalogUnmarshaller("application/json").unmarshalCatalog(is);
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		} catch(NullPointerException npe) {
			npe.printStackTrace();
			return null;
		}

		return catalog;
	}

	@Override
	public StreamingOutput getPreservationObjectStreamingOutput(String poName) {
		log.debug("ENTER getPreservationObjectStreamingOutput()");
		FilesystemDriver driver = new FilesystemDriver(config);
		StreamingOutput so = null;
		
		try {
			final InputStream is = driver.getFileInputStream(config.getContainerName(), poName);
			
			so = new StreamingOutput() {
				public void write(OutputStream out) throws IOException, WebApplicationException {
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = is.read(bytes)) != -1)
                        out.write(bytes, 0, read);
				}
			};
			
			driver.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return so;
	}


	@Override
	public void deletePreservationObject(String poName) {
		log.debug("ENTER deletePreservationObject()");
		FilesystemDriver driver = new FilesystemDriver(config);
		driver.deleteObject(config.getContainerName(), poName);
		driver.close();
	}

	@Override
	public InputStream getPreservationObjectInputStream(String poUUID) {
		log.debug("ENTER getPreservationObjectInputStream()");
		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			InputStream is = driver.getFileInputStream(config.getContainerName(), poUUID);
			driver.close();
			return is;
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return null;
	}

	public String marshalConfig(FilesystemConfiguration config) {
		log.debug("ENTER marshalConfig()");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FilesystemConfiguration.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			StringWriter w = new StringWriter();
			jaxbMarshaller.marshal(config,w);			
			return w.toString();
			
		} catch(JAXBException je) {
			je.printStackTrace();
		}
		
		return null;
	}
	
	public FilesystemConfiguration unmarshalConfig(String configPath) {
		log.debug("ENTER pushProvenanceInformation()");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FilesystemConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			return (FilesystemConfiguration) jaxbUnmarshaller.unmarshal(new StreamSource(
				new FileInputStream(configPath)), FilesystemConfiguration.class).getValue();
		} catch(JAXBException je) {
			je.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	@Override
	public void pushProvenanceInformation(String authorName, String containerName) {
		log.debug("ENTER pushProvenanceInformation()");
		System.out.println("Pushing provenance..." + config.getMountPoint());
		String containerPath = config.getMountPoint() + "/" + containerName;
		System.out.println("Pushing provenance..." + containerPath);
		FilesystemDriver driver = new FilesystemDriver(config);

		try {
			driver.uploadObjectFromString(containerPath,
				SIRFContainer.SIRF_DEFAULT_PROVENANCE_MANIFEST_FILE,
				new ProvenanceInformationMarshaller("application/json")
				.marshalProvenanceInformation(new ProvenanceInformation(authorName)));

			driver.close();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	@Override
	public void pushCatalog(SIRFCatalog catalog, String containerName) {
		log.debug("ENTER pushCatalog()");
		FilesystemDriver driver = new FilesystemDriver(config);
		String containerPath = config.getMountPoint() + "/" + containerName;

		try {
			SIRFCatalog existingCatalog = getCatalog();
			
			// Only metadata updates
			if(existingCatalog != null && existingCatalog.getSirfObjects() != null) {					
				if (existingCatalog.getSirfObjects().size() >= 0 &&
						catalog.getSirfObjects().size() == 0) {
					catalog.getSirfObjects().addAll(existingCatalog.getSirfObjects());
				}
			}
			
			// TODO: else throw exception; number of POs can only change via a PO upload
			
			driver.uploadObjectFromString(containerPath, SIRFContainer.SIRF_DEFAULT_CATALOG_ID,
					new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
			driver.close();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	@Override
	public void pushPreservationObject(String poUUID, byte[] b) {
		log.debug("ENTER pushPreservationObject()");
		FilesystemDriver driver = new FilesystemDriver(config);
		driver.uploadObjectFromByteArray(config.getContainerName(), poUUID, b);
		driver.close();
	}

	private FilesystemConfiguration config;
}
