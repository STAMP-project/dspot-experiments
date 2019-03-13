package io.dropwizard.jersey.optional;


import io.dropwizard.jersey.AbstractJerseyTest;
import java.util.Optional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class OptionalMessageBodyWriterTest extends AbstractJerseyTest {
    @Test
    public void presentOptionalsReturnTheirValue() throws Exception {
        assertThat(target("optional-return").queryParam("id", "woo").request().get(String.class)).isEqualTo("woo");
    }

    @Test
    public void presentOptionalsReturnTheirValueWithResponse() throws Exception {
        assertThat(target("optional-return/response-wrapped").queryParam("id", "woo").request().get(String.class)).isEqualTo("woo");
    }

    @Test
    public void absentOptionalsThrowANotFound() throws Exception {
        try {
            target("optional-return").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(404);
        }
    }

    @Path("optional-return")
    @Produces(MediaType.TEXT_PLAIN)
    public static class OptionalReturnResource {
        @GET
        public Optional<String> showWithQueryParam(@QueryParam("id")
        String id) {
            return Optional.ofNullable(id);
        }

        @POST
        public Optional<String> showWithFormParam(@FormParam("id")
        String id) {
            return Optional.ofNullable(id);
        }

        @Path("response-wrapped")
        @GET
        public Response showWithQueryParamResponse(@QueryParam("id")
        String id) {
            return Response.ok(Optional.ofNullable(id)).build();
        }
    }
}

