package org.apereo.cas.web.view;


import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import lombok.val;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.thymeleaf.IEngineConfiguration;


/**
 * This is {@link RestfulUrlTemplateResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
public class RestfulUrlTemplateResolverTests {
    @Test
    public void verifyAction() {
        try (val webServer = new org.apereo.cas.util.MockWebServer(9302, new ByteArrayResource("template".getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            val props = new CasConfigurationProperties();
            props.getView().getRest().setUrl("http://localhost:9302");
            val r = new RestfulUrlTemplateResolver(props);
            val res = r.resolveTemplate(Mockito.mock(IEngineConfiguration.class), "cas", "template", new LinkedHashMap());
            Assertions.assertNotNull(res);
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

