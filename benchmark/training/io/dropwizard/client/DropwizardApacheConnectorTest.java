package io.dropwizard.client;


import ClientProperties.FOLLOW_REDIRECTS;
import ClientProperties.READ_TIMEOUT;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.JerseyClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardApacheConnectorTest {
    private static final int SLEEP_TIME_IN_MILLIS = 1000;

    private static final int DEFAULT_CONNECT_TIMEOUT_IN_MILLIS = 500;

    private static final int ERROR_MARGIN_IN_MILLIS = 300;

    private static final int INCREASE_IN_MILLIS = 100;

    private static final URI NON_ROUTABLE_ADDRESS = URI.create("http://10.255.255.1");

    public static final DropwizardAppExtension<Configuration> APP_RULE = new DropwizardAppExtension(DropwizardApacheConnectorTest.TestApplication.class, ResourceHelpers.resourceFilePath("yaml/dropwizardApacheConnectorTest.yml"));

    private final URI testUri = URI.create(("http://localhost:" + (DropwizardApacheConnectorTest.APP_RULE.getLocalPort())));

    private JerseyClient client;

    private Environment environment;

    @Test
    public void when_no_read_timeout_override_then_client_request_times_out() {
        assertThatThrownBy(() -> client.target(((testUri) + "/long_running")).request().get()).isInstanceOf(ProcessingException.class).hasCauseInstanceOf(SocketTimeoutException.class);
    }

    @Test
    public void when_read_timeout_override_created_then_client_requests_completes_successfully() {
        client.target(((testUri) + "/long_running")).property(READ_TIMEOUT, ((DropwizardApacheConnectorTest.SLEEP_TIME_IN_MILLIS) * 2)).request().get();
    }

    @Test
    public void when_no_override_then_redirected_request_successfully_redirected() {
        assertThat(client.target(((testUri) + "/redirect")).request().get(String.class)).isEqualTo("redirected");
    }

    @Test
    public void when_configuration_overridden_to_disallow_redirects_temporary_redirect_status_returned() {
        assertThat(client.target(((testUri) + "/redirect")).property(FOLLOW_REDIRECTS, false).request().get(Response.class).getStatus()).isEqualTo(HttpStatus.SC_TEMPORARY_REDIRECT);
    }

    @Test
    public void when_jersey_client_runtime_is_garbage_collected_apache_client_is_not_closed() {
        for (int j = 0; j < 5; j++) {
            System.gc();// We actually want GC here

            final String response = client.target(((testUri) + "/long_running")).property(READ_TIMEOUT, ((DropwizardApacheConnectorTest.SLEEP_TIME_IN_MILLIS) * 2)).request().get(String.class);
            assertThat(response).isEqualTo("success");
        }
    }

    @Test
    public void multiple_headers_with_the_same_name_are_processed_successfully() throws Exception {
        final CloseableHttpClient client = Mockito.mock(CloseableHttpClient.class);
        final DropwizardApacheConnector dropwizardApacheConnector = new DropwizardApacheConnector(client, null, false);
        final Header[] apacheHeaders = new Header[]{ new BasicHeader("Set-Cookie", "test1"), new BasicHeader("Set-Cookie", "test2") };
        final CloseableHttpResponse apacheResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(apacheResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        Mockito.when(apacheResponse.getAllHeaders()).thenReturn(apacheHeaders);
        Mockito.when(client.execute(Mockito.any())).thenReturn(apacheResponse);
        final ClientRequest jerseyRequest = Mockito.mock(ClientRequest.class);
        Mockito.when(jerseyRequest.getUri()).thenReturn(URI.create("http://localhost"));
        Mockito.when(jerseyRequest.getMethod()).thenReturn("GET");
        Mockito.when(jerseyRequest.getHeaders()).thenReturn(new javax.ws.rs.core.MultivaluedHashMap());
        final ClientResponse jerseyResponse = dropwizardApacheConnector.apply(jerseyRequest);
        assertThat(jerseyResponse.getStatus()).isEqualTo(apacheResponse.getStatusLine().getStatusCode());
    }

    @Path("/")
    public static class TestResource {
        @GET
        @Path("/long_running")
        public String getWithSleep() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(DropwizardApacheConnectorTest.SLEEP_TIME_IN_MILLIS);
            return "success";
        }

        @GET
        @Path("redirect")
        public Response getWithRedirect() {
            return Response.temporaryRedirect(URI.create("/redirected")).build();
        }

        @GET
        @Path("redirected")
        public String redirectedGet() {
            return "redirected";
        }
    }

    public static class TestApplication extends Application<Configuration> {
        public static void main(String[] args) throws Exception {
            new DropwizardApacheConnectorTest.TestApplication().run(args);
        }

        @Override
        public void run(Configuration configuration, Environment environment) {
            environment.jersey().register(DropwizardApacheConnectorTest.TestResource.class);
            environment.healthChecks().register("dummy", new HealthCheck() {
                @Override
                protected Result check() {
                    return Result.healthy();
                }
            });
        }
    }
}

