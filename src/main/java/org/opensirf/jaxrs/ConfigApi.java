package org.opensirf.jaxrs;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opensirf.jaxrs.config.SingleContainerConfiguration;

@Path("sirf")
public class ConfigApi {
	@PUT
	@Path("config/singleContainer")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response configureSingleContainer(SingleContainerConfiguration config) throws IOException, URISyntaxException {

		System.out.println(config.getDriver().getName() + " " + config.getEndpoint());

		return Response.ok().build();
	}
}
