package io.dropwizard.jersey.optional;


import io.dropwizard.jersey.AbstractJerseyTest;
import java.util.OptionalInt;
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


public class OptionalIntMessageBodyWriterTest extends AbstractJerseyTest {
    @Test
    public void presentOptionalsReturnTheirValue() throws Exception {
        assertThat(target("optional-return").queryParam("id", "1").request().get(Integer.class)).isEqualTo(1);
    }

    @Test
    public void presentOptionalsReturnTheirValueWithResponse() throws Exception {
        assertThat(target("optional-return/response-wrapped").queryParam("id", "1").request().get(Integer.class)).isEqualTo(1);
    }

    @Test
    public void absentOptionalsThrowANotFound() throws Exception {
        try {
            target("optional-return").request().get(Integer.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(404);
        }
    }

    @Path("optional-return")
    @Produces(MediaType.TEXT_PLAIN)
    public static class OptionalIntReturnResource {
        @GET
        public OptionalInt showWithQueryParam(@QueryParam("id")
        OptionalInt id) {
            return id;
        }

        @POST
        public OptionalInt showWithFormParam(@FormParam("id")
        OptionalInt id) {
            return id;
        }

        @Path("response-wrapped")
        @GET
        public Response showWithQueryParamResponse(@QueryParam("id")
        OptionalInt id) {
            return Response.ok(id).build();
        }
    }
}

