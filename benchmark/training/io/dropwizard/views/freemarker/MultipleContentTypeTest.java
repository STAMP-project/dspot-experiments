package io.dropwizard.views.freemarker;


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.TEXT_HTML_TYPE;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.views.View;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


public class MultipleContentTypeTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Test
    public void testJsonContentType() {
        final Response response = target("/").request().accept(APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("{\"title\":\"Title#TEST\",\"content\":\"Content#TEST\"}");
    }

    @Test
    public void testHtmlContentType() {
        final Response response = target("/").request().accept(TEXT_HTML_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).contains("Breaking news").contains("<h1>Title#TEST</h1>").contains("<p>Content#TEST</p>");
    }

    @Test
    public void testOnlyJsonContentType() {
        final Response response = target("/json").request().accept(APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("{\"title\":\"Title#TEST\",\"content\":\"Content#TEST\"}");
    }

    @Test
    public void testOnlyHtmlContentType() {
        final Response response = target("/html").request().accept(TEXT_HTML_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).contains("Breaking news").contains("<h1>Title#TEST</h1>").contains("<p>Content#TEST</p>");
    }

    @Path("/")
    public static class ExampleResource {
        @GET
        @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
        public Response getInfoCombined() {
            final MultipleContentTypeTest.Info info = new MultipleContentTypeTest.Info("Title#TEST", "Content#TEST");
            return Response.ok(info).build();
        }

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Path("json")
        public Response getInfoJson() {
            final MultipleContentTypeTest.Info info = new MultipleContentTypeTest.Info("Title#TEST", "Content#TEST");
            return Response.ok(info).build();
        }

        @GET
        @Produces(MediaType.TEXT_HTML)
        @Path("html")
        public Response getInfoHtml() {
            final MultipleContentTypeTest.Info info = new MultipleContentTypeTest.Info("Title#TEST", "Content#TEST");
            return Response.ok(info).build();
        }
    }

    public static class Info extends View {
        private final String title;

        private final String content;

        public Info(String title, String content) {
            super("/issue627.ftl");
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    @Provider
    @Produces(MediaType.APPLICATION_JSON)
    public static class InfoMessageBodyWriter implements MessageBodyWriter<MultipleContentTypeTest.Info> {
        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return MultipleContentTypeTest.Info.class.isAssignableFrom(type);
        }

        @Override
        public long getSize(MultipleContentTypeTest.Info info, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(MultipleContentTypeTest.Info info, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            Jackson.newObjectMapper().writeValue(entityStream, info);
        }
    }
}

