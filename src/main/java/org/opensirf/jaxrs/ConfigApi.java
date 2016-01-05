package org.opensirf.jaxrs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SIRFConfigurationMarshaller;
import org.opensirf.jaxrs.config.SIRFConfigurationUnmarshaller;

@Path("sirf")
public class ConfigApi {
	@PUT
	@Path("config/singleContainer")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response configureSingleContainer(SIRFConfiguration config) throws JAXBException, IOException, URISyntaxException {

		FileOutputStream fos = new FileOutputStream(new File(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
		fos.write(new SIRFConfigurationMarshaller("application/json").marshalConfig(config).getBytes());
		fos.flush();
		fos.close();
		
		return Response.ok().build();
	}
	
	@GET
	@Path("config")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SIRFConfiguration getConfiguration() throws JAXBException, IOException, URISyntaxException {
		return new SIRFConfigurationUnmarshaller("application/json").unmarshalConfig(new FileInputStream(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
	}
}
