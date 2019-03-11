package io.dropwizard.jersey.errors;


import MediaType.TEXT_HTML_TYPE;
import io.dropwizard.jersey.AbstractJerseyTest;
import io.dropwizard.jersey.DropwizardResourceConfig;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class ErrorEntityWriterTest extends AbstractJerseyTest {
    public static class ErrorEntityWriterTestResourceConfig extends DropwizardResourceConfig {
        public ErrorEntityWriterTestResourceConfig() {
            super();
            property(TestProperties.CONTAINER_PORT, "0");
            register(DefaultLoggingExceptionMapper.class);
            register(DefaultJacksonMessageBodyProvider.class);
            register(ExceptionResource.class);
            register(new ErrorEntityWriter<ErrorMessage, String>(MediaType.TEXT_HTML_TYPE, String.class) {
                @Override
                protected String getRepresentation(ErrorMessage entity) {
                    return ("<!DOCTYPE html><html><body>" + (entity.getMessage())) + "</body></html>";
                }
            });
        }
    }

    @Test
    public void formatsErrorsAsHtml() {
        try {
            target("/exception/html-exception").request(TEXT_HTML_TYPE).get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            final Response response = e.getResponse();
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getMediaType()).isEqualTo(TEXT_HTML_TYPE);
            assertThat(response.readEntity(String.class)).isEqualTo("<!DOCTYPE html><html><body>BIFF</body></html>");
        }
    }
}

