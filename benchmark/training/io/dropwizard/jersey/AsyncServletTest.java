package io.dropwizard.jersey;


import MediaType.TEXT_PLAIN_TYPE;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class AsyncServletTest extends AbstractJerseyTest {
    @Test
    public void testAsyncResponse() {
        final Response response = target("/async").request(TEXT_PLAIN_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("foobar");
    }
}

