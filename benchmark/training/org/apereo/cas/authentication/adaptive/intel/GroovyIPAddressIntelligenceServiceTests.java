package org.apereo.cas.authentication.adaptive.intel;


import lombok.val;
import org.apereo.cas.configuration.model.core.authentication.AdaptiveAuthenticationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link GroovyIPAddressIntelligenceServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("Groovy")
public class GroovyIPAddressIntelligenceServiceTests {
    @Test
    public void verifyOperation() {
        val script = new ClassPathResource("GroovyIPAddressIntelligenceService.groovy");
        val props = new AdaptiveAuthenticationProperties();
        props.getIpIntel().getGroovy().setLocation(script);
        val service = new GroovyIPAddressIntelligenceService(props);
        val response = service.examine(new MockRequestContext(), "1.2.3.4");
        Assertions.assertTrue(response.isBanned());
    }
}

