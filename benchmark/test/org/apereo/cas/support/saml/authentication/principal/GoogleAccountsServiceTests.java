package org.apereo.cas.support.saml.authentication.principal;


import DefaultResponse.ResponseType.POST;
import SamlProtocolConstants.PARAMETER_SAML_RELAY_STATE;
import SamlProtocolConstants.PARAMETER_SAML_RESPONSE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.ResponseBuilder;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.config.support.EnvironmentConversionServiceInitializer;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.apereo.cas.support.saml.config.SamlGoogleAppsConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Import(SamlGoogleAppsConfiguration.class)
@TestPropertySource(locations = "classpath:/gapps.properties")
@ContextConfiguration(initializers = EnvironmentConversionServiceInitializer.class)
public class GoogleAccountsServiceTests extends AbstractOpenSamlTests {
    private static final File FILE = new File(FileUtils.getTempDirectoryPath(), "service.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    @Qualifier("googleAccountsServiceFactory")
    private ServiceFactory factory;

    @Autowired
    @Qualifier("googleAccountsServiceResponseBuilder")
    private ResponseBuilder<GoogleAccountsService> googleAccountsServiceResponseBuilder;

    private GoogleAccountsService googleAccountsService;

    @Test
    public void verifyResponse() {
        val resp = googleAccountsServiceResponseBuilder.build(googleAccountsService, "SAMPLE_TICKET", CoreAuthenticationTestUtils.getAuthentication());
        Assertions.assertEquals(POST, resp.getResponseType());
        val response = resp.getAttributes().get(PARAMETER_SAML_RESPONSE);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.contains("NotOnOrAfter"));
        val pattern = Pattern.compile("NotOnOrAfter\\s*=\\s*\"(.+Z)\"");
        val matcher = pattern.matcher(response);
        val now = ZonedDateTime.now(ZoneOffset.UTC);
        while (matcher.find()) {
            val onOrAfter = matcher.group(1);
            val dt = ZonedDateTime.parse(onOrAfter);
            Assertions.assertTrue(dt.isAfter(now));
        } 
        Assertions.assertTrue(resp.getAttributes().containsKey(PARAMETER_SAML_RELAY_STATE));
    }

    @Test
    public void serializeGoogleAccountService() throws Exception {
        val service = getGoogleAccountsService();
        GoogleAccountsServiceTests.MAPPER.writeValue(GoogleAccountsServiceTests.FILE, service);
        val service2 = GoogleAccountsServiceTests.MAPPER.readValue(GoogleAccountsServiceTests.FILE, GoogleAccountsService.class);
        Assertions.assertEquals(service, service2);
    }
}

