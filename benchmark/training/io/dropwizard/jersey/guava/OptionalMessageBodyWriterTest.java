package io.dropwizard.jersey.guava;


import com.google.common.base.Optional;
import io.dropwizard.jersey.AbstractJerseyTest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;


public class OptionalMessageBodyWriterTest extends AbstractJerseyTest {
    @Test
    public void presentOptionalsReturnTheirValue() throws Exception {
        assertThat(target("/optional-return/").queryParam("id", "woo").request().get(String.class)).isEqualTo("woo");
    }

    @Test
    public void absentOptionalsThrowANotFound() throws Exception {
        try {
            target("/optional-return/").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(404);
        }
    }

    @Path("/optional-return/")
    @Produces(MediaType.TEXT_PLAIN)
    public static class OptionalReturnResource {
        @GET
        public Optional<String> showWithQueryParam(@QueryParam("id")
        String id) {
            return Optional.fromNullable(id);
        }

        @POST
        public Optional<String> showWithFormParam(@FormParam("id")
        String id) {
            return Optional.fromNullable(id);
        }
    }
}

