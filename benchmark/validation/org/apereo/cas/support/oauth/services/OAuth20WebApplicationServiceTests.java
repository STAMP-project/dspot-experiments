package org.apereo.cas.support.oauth.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class OAuth20WebApplicationServiceTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "oAuthWebApplicationService.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @SneakyThrows
    public void verifySerializeACompletePrincipalToJson() {
        val service = new OAuthRegisteredService();
        service.setName("checkCloning");
        service.setServiceId("testId");
        service.setTheme("theme");
        service.setDescription("description");
        val factory = new WebApplicationServiceFactory();
        val serviceWritten = factory.createService(service.getServiceId());
        OAuth20WebApplicationServiceTests.MAPPER.writeValue(OAuth20WebApplicationServiceTests.JSON_FILE, serviceWritten);
        val serviceRead = OAuth20WebApplicationServiceTests.MAPPER.readValue(OAuth20WebApplicationServiceTests.JSON_FILE, WebApplicationService.class);
        Assertions.assertEquals(serviceWritten, serviceRead);
    }
}

