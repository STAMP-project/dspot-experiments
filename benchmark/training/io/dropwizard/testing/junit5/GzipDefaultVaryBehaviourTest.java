package io.dropwizard.testing.junit5;


import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.TestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class GzipDefaultVaryBehaviourTest {
    private DropwizardAppExtension<TestConfiguration> extension = new DropwizardAppExtension(TestApplication.class, ResourceHelpers.resourceFilePath("gzip-vary-test-config.yaml"));

    @Test
    public void testDefaultVaryHeader() {
        final Response clientResponse = extension.client().target((("http://localhost:" + (extension.getLocalPort())) + "/test")).request().header(ACCEPT_ENCODING, "gzip").get();
        assertThat(clientResponse.getHeaders().get(VARY)).isEqualTo(Collections.singletonList(((Object) (ACCEPT_ENCODING))));
        assertThat(clientResponse.getHeaders().get(CONTENT_ENCODING)).isEqualTo(Collections.singletonList(((Object) ("gzip"))));
    }
}

