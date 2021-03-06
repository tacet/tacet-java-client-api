package org.tacet.nodeagentapi.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.tacet.nodeagentapi.Root;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class HttpJsonMeasurementSender implements MeasurementSender {

    private final static Logger logger = Logger.getLogger(HttpJsonMeasurementSender.class);

    private final ApacheHttpClient httpClient;
    private final String uri;
    private final ExecutorService executorService;

    public HttpJsonMeasurementSender(ExecutorService executorService, String uri) {
        this.executorService = executorService;
        this.uri = uri;
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);
        httpClient = ApacheHttpClient.create(config);
    }

    private WebResource.Builder createResource(String uri) {
        try {
            return httpClient.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);
        } catch (UniformInterfaceException e) {
            throw new RuntimeException(e.getResponse().getEntity(String.class), e);
        }
    }

    public void send(final Root root) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    createResource(uri).put(root);
                } catch (UniformInterfaceException e) {
                    logger.error("Failed to push call graph. Response: " + e.getResponse().getEntity(String.class), e);
                } catch (Exception e) {
                    logger.error("Failed to push call graph", e);
                }
            }
        });
    }

}
