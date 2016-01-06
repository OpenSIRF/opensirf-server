package org.opensirf.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.opensirf.catalog.SIRFCatalog;
import org.opensirf.container.ProvenanceInformation;
import org.opensirf.container.SIRFContainer;
import org.opensirf.format.ProvenanceInformationMarshaller;
import org.opensirf.format.SIRFCatalogMarshaller;
import org.opensirf.format.SIRFCatalogUnmarshaller;
import org.opensirf.jaxrs.SwiftDriver;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SwiftConfiguration;
import org.opensirf.jaxrs.model.MagicObject;

public class SwiftStrategy implements StorageContainerStrategy {
	public void setConfig(SIRFConfiguration c) {
		this.config = c;
	}

	public SIRFConfiguration getConfig() {
		return config;
	}

	private SIRFConfiguration config;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public MagicObject retrieveMagicObject() {
		try {
			SwiftDriver driver = new SwiftDriver(config);
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
			SwiftDriver driver = new SwiftDriver(config);
			driver.createContainer(config.getContainerName());
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void pushProvenanceInformation(String authorName) {
		SwiftDriver driver = new SwiftDriver(config);

		try {
			driver.uploadObjectFromString(config.getContainerName(), SIRFContainer.SIRF_DEFAULT_PROVENANCE_MANIFEST_FILE,
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
	public void pushCatalog(SIRFCatalog catalog) {
		SwiftDriver driver = new SwiftDriver(config);

		try {
			driver.uploadObjectFromString(config.getContainerName(), SIRFContainer.SIRF_DEFAULT_CATALOG_ID,
					new SIRFCatalogMarshaller("application/json").marshalCatalog(catalog));
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	@Override
	public void deleteContainer() {
		SwiftDriver driver = new SwiftDriver(config);

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
			SwiftDriver driver = new SwiftDriver(config);
			InputStream is = driver.getFileInputStream(config.getContainerName(), SIRFContainer.SIRF_DEFAULT_CATALOG_ID);
			
			catalog = new SIRFCatalogUnmarshaller("application/json").unmarshalCatalog(is);
			driver.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}

		return catalog;
	}

	@Override
	public StreamingOutput getPreservationObjectStreamingOutput(String poName) {
		SwiftDriver driver = new SwiftDriver(config);
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
	public void pushPreservationObject(String poUUID, byte[] b) {
		try {
			SwiftDriver driver = new SwiftDriver(config);
			driver.uploadObjectFromByteArray(config.getContainerName(), poUUID, b);
			driver.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

	@Override
	public void deletePreservationObject(String poName) {
		try {
			SwiftDriver driver = new SwiftDriver(config);
			driver.deleteObject(config.getContainerName(), poName);
			driver.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public InputStream getPreservationObjectInputStream(String poUUID) {
		try {
			SwiftDriver driver = new SwiftDriver(config);
			return driver.getFileInputStream(config.getContainerName(), poUUID);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return null;
	}

	public Marshaller getMarshaller() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SwiftConfiguration.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			return jaxbMarshaller;
		} catch(JAXBException je) {
			je.printStackTrace();
		}
		
		return null;
	}
	
	public Unmarshaller getUnmarshaller() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SwiftConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			jaxbUnmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			return jaxbUnmarshaller;
		} catch(JAXBException je) {
			je.printStackTrace();
		}
		
		return null;
	}
	
}
