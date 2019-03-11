package io.dropwizard.jersey;


import MediaType.TEXT_PLAIN_TYPE;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class JerseyContentTypeTest extends AbstractJerseyTest {
    @Test
    public void testValidContentType() {
        final Response response = target("/").request(TEXT_PLAIN_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("bar");
    }

    @Test
    public void testInvalidContentType() {
        final Response response = target("/").request("foo").get();
        assertThat(response.getStatus()).isEqualTo(406);
        assertThat(response.hasEntity()).isEqualTo(false);
    }
}

