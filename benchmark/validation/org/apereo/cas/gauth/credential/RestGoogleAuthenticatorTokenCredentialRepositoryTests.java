package org.apereo.cas.gauth.credential;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.val;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.otp.repository.credentials.OneTimeTokenCredentialRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;


/**
 * This is {@link RestGoogleAuthenticatorTokenCredentialRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { AopAutoConfiguration.class, RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class }, properties = { "cas.authn.mfa.gauth.rest.endpointUrl=http://example.com" })
@Tag("RestfulApi")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Getter
public class RestGoogleAuthenticatorTokenCredentialRepositoryTests extends BaseOneTimeTokenCredentialRepositoryTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final Map<String, OneTimeTokenCredentialRepository> repositoryMap = new HashMap<>();

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired(required = false)
    @Qualifier("googleAuthenticatorAccountRegistry")
    private OneTimeTokenCredentialRepository registry;

    @Test
    @Override
    public void verifyGet() throws Exception {
        val repository = ((RestGoogleAuthenticatorTokenCredentialRepository) (getRegistry("verifyGet")));
        Assertions.assertNotNull(repository, "Repository is null");
        val mockServer = MockRestServiceServer.createServer(repository.getRestTemplate());
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.GET)).andRespond(withNoContent());
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(RestGoogleAuthenticatorTokenCredentialRepositoryTests.MAPPER.writeValueAsString(getAccount("verifyGet", BaseOneTimeTokenCredentialRepositoryTests.CASUSER)), MediaType.APPLICATION_JSON));
        super.verifyGet();
        mockServer.verify();
    }

    @Test
    @Override
    public void verifyGetWithDecodedSecret() throws Exception {
        val repository = ((RestGoogleAuthenticatorTokenCredentialRepository) (getRegistry("verifyGetWithDecodedSecret")));
        Assertions.assertNotNull(repository, "Repository is null");
        val acct = getAccount("verifyGetWithDecodedSecret", BaseOneTimeTokenCredentialRepositoryTests.CASUSER).clone();
        acct.setSecretKey(BaseOneTimeTokenCredentialRepositoryTests.PLAIN_SECRET);
        val mockServer = MockRestServiceServer.createServer(repository.getRestTemplate());
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(RestGoogleAuthenticatorTokenCredentialRepositoryTests.MAPPER.writeValueAsString(acct), MediaType.APPLICATION_JSON));
        super.verifyGetWithDecodedSecret();
        mockServer.verify();
    }

    @Test
    @Override
    public void verifySaveAndUpdate() throws Exception {
        val repository = ((RestGoogleAuthenticatorTokenCredentialRepository) (getRegistry("verifySaveAndUpdate")));
        Assertions.assertNotNull(repository, "Repository is null");
        val acct = getAccount("verifySaveAndUpdate", BaseOneTimeTokenCredentialRepositoryTests.CASUSER).clone();
        val mockServer = MockRestServiceServer.createServer(repository.getRestTemplate());
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(RestGoogleAuthenticatorTokenCredentialRepositoryTests.MAPPER.writeValueAsString(acct), MediaType.APPLICATION_JSON));
        acct.setSecretKey("newSecret");
        acct.setValidationCode(999666);
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://example.com")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(RestGoogleAuthenticatorTokenCredentialRepositoryTests.MAPPER.writeValueAsString(acct), MediaType.APPLICATION_JSON));
        super.verifySaveAndUpdate();
        mockServer.verify();
    }
}

