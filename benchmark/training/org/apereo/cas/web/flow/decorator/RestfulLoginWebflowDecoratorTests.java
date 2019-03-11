package org.apereo.cas.web.flow.decorator;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.apereo.cas.configuration.model.webapp.WebflowLoginDecoratorProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link RestfulLoginWebflowDecoratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("RestfulApi")
public class RestfulLoginWebflowDecoratorTests {
    @Test
    public void verifyOperation() {
        val props = new WebflowLoginDecoratorProperties.Rest();
        props.setUrl("http://localhost:9465");
        val rest = new RestfulLoginWebflowDecorator(props);
        val requestContext = new MockRequestContext();
        try (val webServer = new org.apereo.cas.util.MockWebServer(9465, new ByteArrayResource(RestfulLoginWebflowDecoratorTests.getJsonData().getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            rest.decorate(requestContext, Mockito.mock(ApplicationContext.class));
            Assertions.assertTrue(requestContext.getFlowScope().contains("decoration"));
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

