package io.dropwizard.views.mustache;


import ViewRenderExceptionMapper.TEMPLATE_ERROR_MSG;
import io.dropwizard.logging.BootstrapLogging;
import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


public class MustacheViewRendererTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Path("/test/")
    @Produces(MediaType.TEXT_HTML)
    public static class ExampleResource {
        @GET
        @Path("/absolute")
        public AbsoluteView showAbsolute() {
            return new AbsoluteView("yay");
        }

        @GET
        @Path("/relative")
        public RelativeView showRelative() {
            return new RelativeView();
        }

        @GET
        @Path("/bad")
        public BadView showBad() {
            return new BadView();
        }

        @GET
        @Path("/error")
        public ErrorView showError() {
            return new ErrorView();
        }
    }

    @Test
    public void rendersViewsWithAbsoluteTemplatePaths() throws Exception {
        final String response = target("/test/absolute").request().get(String.class);
        assertThat(response).isEqualTo("Woop woop. yay\n");
    }

    @Test
    public void rendersViewsWithRelativeTemplatePaths() throws Exception {
        final String response = target("/test/relative").request().get(String.class);
        assertThat(response).isEqualTo("Ok.\n");
    }

    @Test
    public void returnsA500ForViewsWithBadTemplatePaths() throws Exception {
        try {
            target("/test/bad").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(500);
            assertThat(e.getResponse().readEntity(String.class)).isEqualTo(TEMPLATE_ERROR_MSG);
        }
    }

    @Test
    public void returnsA500ForViewsThatCantCompile() throws Exception {
        try {
            target("/test/error").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(500);
            assertThat(e.getResponse().readEntity(String.class)).isEqualTo(TEMPLATE_ERROR_MSG);
        }
    }

    @Test
    public void cacheByDefault() {
        MustacheViewRenderer mustacheViewRenderer = new MustacheViewRenderer();
        mustacheViewRenderer.configure(Collections.emptyMap());
        assertThat(mustacheViewRenderer.isUseCache()).isTrue();
    }

    @Test
    public void canDisableCache() {
        MustacheViewRenderer mustacheViewRenderer = new MustacheViewRenderer();
        mustacheViewRenderer.configure(Collections.singletonMap("cache", "false"));
        assertThat(mustacheViewRenderer.isUseCache()).isFalse();
    }
}

