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
import org.opensirf.container.ProvenanceInformation;
import org.opensirf.container.SIRFContainer;
import org.opensirf.format.ProvenanceInformationMarshaller;
import org.opensirf.format.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.model.MagicObject;
import org.opensirf.jaxrs.storage.StorageContainerStrategy;

public class FilesystemStrategy implements StorageContainerStrategy {
	protected FilesystemStrategy() {
		
	}
	
	public FilesystemStrategy(ContainerConfiguration c) { 
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
	public MagicObject retrieveMagicObject() {
		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			MagicObject mo = driver.containerMetadata(config.getContainerName());
			driver.close();
			return mo;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		
		return null;
	}

	@Override
	public void createContainer(String containerName) {
		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			System.out.println("Calling driver, container name = " + containerName);
			driver.createContainer(containerName);
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void pushProvenanceInformation(String authorName) {
		pushProvenanceInformation(authorName, config.getContainerName());
	}

	@Override
	public void pushCatalog(SIRFCatalog catalog) {
		pushCatalog(catalog, config.getContainerName());
	}

	@Override
	public void deleteContainer() {
		FilesystemDriver driver = new FilesystemDriver(config);

		try {
			driver.deleteContainer(config.getContainerName());
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public SIRFCatalog getCatalog() {
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
			return null;
		}

		return catalog;
	}

	@Override
	public StreamingOutput getPreservationObjectStreamingOutput(String poName) {
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
		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			driver.deleteObject(config.getContainerName(), poName);
			driver.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public InputStream getPreservationObjectInputStream(String poUUID) {
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
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FilesystemConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			return (FilesystemConfiguration) jaxbUnmarshaller.unmarshal(new StreamSource(new FileInputStream(configPath)), FilesystemConfiguration.class).getValue();
		} catch(JAXBException je) {
			je.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	@Override
	public void pushProvenanceInformation(String authorName, String containerName) {
		FilesystemDriver driver = new FilesystemDriver(config);

		try {
			driver.uploadObjectFromString(containerName, SIRFContainer.SIRF_DEFAULT_PROVENANCE_MANIFEST_FILE,
					new ProvenanceInformationMarshaller("application/json")
							.marshalProvenanceInformation(new ProvenanceInformation(authorName)));

			driver.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	@Override
	public void pushCatalog(SIRFCatalog catalog, String containerName) {
		FilesystemDriver driver = new FilesystemDriver(config);

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
			
			driver.uploadObjectFromString(containerName, SIRFContainer.SIRF_DEFAULT_CATALOG_ID,
					new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	@Override
	public void pushPreservationObject(String poUUID, byte[] b) {
		try {
			FilesystemDriver driver = new FilesystemDriver(config);
			driver.uploadObjectFromByteArray(config.getContainerName(), poUUID, b);
			driver.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
