/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package org.opensirf.jaxrs.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

/**
 * @author pviana
 *
 */
public class ConfigTestApi {
	@Test
	public void configTestApi() {
		String endpoint = "200.144.189.109:58081";
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target("http://" + endpoint + "/sirf/config");
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
		Response response = request.get();
		String output = response.readEntity(String.class);
		SIRFConfiguration c = new SIRFConfigurationUnmarshaller().unmarshalConfig(output);
		System.out.println(c.getContainerConfiguration().getDriver());
		System.out.println(c.getContainerConfiguration().getEndpoint());
		System.out.println(c.getContainerConfiguration().getContainerName());
	}
}
