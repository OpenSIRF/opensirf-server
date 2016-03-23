package org.opensirf.jaxrs.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;

/** This class represents a simple REST client.
 * Created by dekozo on 3/22/16.
 */
public class RestClient {
    /**
     * Endpoint to which all requests should be sent.
     */
    private URL endpoint;

    /**
     * URI to which the request should be sent.
     */
    private URL target;
    private Client client;

    /**
     * 
     */
    private WebTarget resource;

    /**
     * Builder of requests.
     */
    private Invocation.Builder request;

    public RestClient() {
        this.client = ClientBuilder.newClient();
    }

    public RestClient(String endpoint) throws MalformedURLException {
        this.endpoint = new URL(endpoint);
    }

    public void setTarget(String target) {
        this.resource = this.client.target(target);
    }

    public void setMediaType(MediaType mediaType) {
        this.request.accept(mediaType);
    }

    public void buildRequest() {
        this.request = this.resource.request();
    }

}

