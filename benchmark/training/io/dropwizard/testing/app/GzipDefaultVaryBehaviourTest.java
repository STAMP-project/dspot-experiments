package io.dropwizard.testing.app;


import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;


public class GzipDefaultVaryBehaviourTest {
    @SuppressWarnings("deprecation")
    @ClassRule
    public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule(TestApplication.class, ResourceHelpers.resourceFilePath("gzip-vary-test-config.yaml"));

    @Test
    public void testDefaultVaryHeader() {
        final Response clientResponse = GzipDefaultVaryBehaviourTest.RULE.client().target((("http://localhost:" + (GzipDefaultVaryBehaviourTest.RULE.getLocalPort())) + "/test")).request().header(ACCEPT_ENCODING, "gzip").get();
        assertThat(clientResponse.getHeaders().get(VARY)).isEqualTo(Collections.singletonList(((Object) (ACCEPT_ENCODING))));
        assertThat(clientResponse.getHeaders().get(CONTENT_ENCODING)).isEqualTo(Collections.singletonList(((Object) ("gzip"))));
    }
}

