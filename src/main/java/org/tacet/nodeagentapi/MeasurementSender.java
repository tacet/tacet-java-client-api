package org.tacet.nodeagentapi;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.DisposableBean;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MeasurementSender implements DisposableBean {
    
    private final ApacheHttpClient httpClient;
    private final String uri;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MeasurementSender(String uri) {
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

    public void send(final CallNode callGraph) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                createResource(uri).put(String.class, callGraph);
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

}
