package org.opensirf.jaxrs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.opensirf.jaxrs.config.ContainerConfiguration;
import org.opensirf.jaxrs.config.ContainerConfigurationMarshaller;
import org.opensirf.jaxrs.config.SwiftConfiguration;
import org.opensirf.storage.SwiftStrategy;


@Path("sirf")
public class ConfigApi {
	@PUT
	@Path("config/singleContainer")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response configureSingleContainer(ContainerConfiguration config) throws JAXBException, IOException, URISyntaxException {

		FileOutputStream fos = new FileOutputStream(new File(ContainerConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json"));
		fos.write(new ContainerConfigurationMarshaller("application/json").marshalConfig(config).getBytes());
		fos.flush();
		fos.close();
		
		return Response.ok().build();
	}
	
	@GET
	@Path("config")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SwiftConfiguration getConfiguration() throws JAXBException, IOException, URISyntaxException {
		SwiftConfiguration s = (SwiftConfiguration) new SwiftStrategy().unmarshalConfig(ContainerConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json");
		return s;
	}
	
	@GET
	@Path("configp")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getConfiguration2() throws JAXBException, IOException, URISyntaxException {
		SwiftStrategy strat = new SwiftStrategy();		
		SwiftConfiguration s = (SwiftConfiguration) strat.unmarshalConfig(ContainerConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json");
		// JSONP response
		return "configCallback(" + strat.marshalConfig(s) + ");";
	}
	

	@OPTIONS
	@Path("config")
	public Response configOptions() {
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS, DELETE, HEAD")
				.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
}
