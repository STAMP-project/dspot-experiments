package io.dropwizard.jersey.params;


import io.dropwizard.jersey.AbstractJerseyTest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.junit.jupiter.api.Test;


public class NonEmptyStringParamProviderTest extends AbstractJerseyTest {
    @Test
    public void shouldReturnDefaultMessageWhenNonExistent() {
        String response = target("/non-empty/string").request().get(String.class);
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    public void shouldReturnDefaultMessageWhenEmptyString() {
        String response = target("/non-empty/string").queryParam("message", "").request().get(String.class);
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    public void shouldReturnDefaultMessageWhenNull() {
        String response = target("/non-empty/string").queryParam("message").request().get(String.class);
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    public void shouldReturnMessageWhenSpecified() {
        String response = target("/non-empty/string").queryParam("message", "Goodbye").request().get(String.class);
        assertThat(response).isEqualTo("Goodbye");
    }

    @Path("/non-empty")
    public static class NonEmptyStringParamResource {
        @GET
        @Path("/string")
        public String getMessage(@QueryParam("message")
        NonEmptyStringParam message) {
            return message.get().orElse("Hello");
        }
    }
}

