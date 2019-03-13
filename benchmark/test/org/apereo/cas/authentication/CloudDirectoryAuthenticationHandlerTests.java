package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.clouddirectory.CloudDirectoryRepository;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link CloudDirectoryAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.cloudDirectory.usernameAttributeName=username", "cas.authn.cloudDirectory.passwordAttributeName=password" })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CloudDirectoryAuthenticationHandlerTests {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyAction() throws Exception {
        val repository = Mockito.mock(CloudDirectoryRepository.class);
        Mockito.when(repository.getUser(ArgumentMatchers.anyString())).thenReturn(CollectionUtils.wrap("username", "casuser", "password", "Mellon"));
        val h = new CloudDirectoryAuthenticationHandler("", Mockito.mock(ServicesManager.class), PrincipalFactoryUtils.newPrincipalFactory(), repository, casProperties.getAuthn().getCloudDirectory());
        Assertions.assertNotNull(h.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon")));
    }
}

