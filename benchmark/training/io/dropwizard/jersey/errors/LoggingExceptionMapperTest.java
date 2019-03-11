package io.dropwizard.jersey.errors;


import MediaType.APPLICATION_JSON;
import io.dropwizard.jersey.AbstractJerseyTest;
import java.util.Collections;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class LoggingExceptionMapperTest extends AbstractJerseyTest {
    @Test
    public void returnsAnErrorMessage() throws Exception {
        try {
            target("/exception/").request(APPLICATION_JSON).get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            final Response response = e.getResponse();
            assertThat(response.getStatus()).isEqualTo(500);
            assertThat(response.readEntity(String.class)).startsWith(("{\"code\":500,\"message\":" + "\"There was an error processing your request. It has been logged (ID "));
        }
    }

    @Test
    public void handlesJsonMappingException() throws Exception {
        try {
            target("/exception/json-mapping-exception").request(APPLICATION_JSON).get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            final Response response = e.getResponse();
            assertThat(response.getStatus()).isEqualTo(500);
            assertThat(response.readEntity(String.class)).startsWith(("{\"code\":500,\"message\":" + "\"There was an error processing your request. It has been logged (ID "));
        }
    }

    @Test
    public void handlesMethodNotAllowedWithHeaders() {
        final Throwable thrown = catchThrowable(() -> target("/exception/json-mapping-exception").request(MediaType.APPLICATION_JSON).post(Entity.json("A"), .class));
        assertThat(thrown).isInstanceOf(WebApplicationException.class);
        final Response resp = getResponse();
        assertThat(resp.getStatus()).isEqualTo(405);
        assertThat(resp.getHeaders()).contains(entry("Allow", Collections.singletonList("GET,OPTIONS")));
        assertThat(resp.readEntity(String.class)).isEqualTo("{\"code\":405,\"message\":\"HTTP 405 Method Not Allowed\"}");
    }

    @Test
    public void formatsWebApplicationException() throws Exception {
        try {
            target("/exception/web-application-exception").request(APPLICATION_JSON).get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            final Response response = e.getResponse();
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.readEntity(String.class)).isEqualTo("{\"code\":400,\"message\":\"KAPOW\"}");
        }
    }

    @Test
    public void handlesRedirectInWebApplicationException() {
        String responseText = target("/exception/web-application-exception-with-redirect").request(APPLICATION_JSON).get(String.class);
        assertThat(responseText).isEqualTo("{\"status\":\"OK\"}");
    }
}

