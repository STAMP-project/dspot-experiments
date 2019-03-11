package com.example.forms;


import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class FormsAppTest {
    public static final DropwizardAppExtension<Configuration> RULE = new DropwizardAppExtension(FormsApp.class, ResourceHelpers.resourceFilePath("app1/config.yml"));

    private final JerseyClientConfiguration config = new JerseyClientConfiguration();

    @Test
    public void canSubmitFormAndReceiveResponse() throws IOException {
        config.setChunkedEncodingEnabled(false);
        final Client client = new io.dropwizard.client.JerseyClientBuilder(FormsAppTest.RULE.getEnvironment()).using(config).build("test client 1");
        try (final FormDataMultiPart fdmp = new FormDataMultiPart()) {
            final MultiPart mp = fdmp.bodyPart(new org.glassfish.jersey.media.multipart.FormDataBodyPart(FormDataContentDisposition.name("file").fileName("fileName").build(), "CONTENT"));
            final String url = String.format("http://localhost:%d/uploadFile", FormsAppTest.RULE.getLocalPort());
            final String response = client.target(url).register(MultiPartFeature.class).request().post(Entity.entity(mp, mp.getMediaType()), String.class);
            assertThat(response).isEqualTo("fileName:\nCONTENT");
        }
    }

    /**
     * Test confirms that chunked encoding has to be disabled in order for
     * sending forms to work. Maybe someday this requirement will be relaxed and
     * this test can be updated for the new behavior. For more info, see issues
     * #1013 and #1094
     *
     * @throws IOException
     * 		
     */
    @Test
    public void failOnNoChunkedEncoding() throws IOException {
        final Client client = new io.dropwizard.client.JerseyClientBuilder(FormsAppTest.RULE.getEnvironment()).using(config).build("test client 2");
        try (final FormDataMultiPart fdmp = new FormDataMultiPart()) {
            final MultiPart mp = fdmp.bodyPart(new org.glassfish.jersey.media.multipart.FormDataBodyPart(FormDataContentDisposition.name("file").fileName("fileName").build(), "CONTENT"));
            final String url = String.format("http://localhost:%d/uploadFile", FormsAppTest.RULE.getLocalPort());
            final Response response = client.target(url).register(MultiPartFeature.class).request().post(Entity.entity(mp, mp.getMediaType()));
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.readEntity(ErrorMessage.class)).isEqualTo(new ErrorMessage(400, "HTTP 400 Bad Request"));
        }
    }
}

