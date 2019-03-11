package org.apereo.cas.support.pac4j.authentication;


import CasProtocol.SAML;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jDelegatedAuthenticationProperties;
import org.apereo.cas.configuration.model.support.pac4j.cas.Pac4jCasClientProperties;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;
import org.apereo.cas.configuration.model.support.pac4j.oidc.Pac4jOidcClientProperties;
import org.apereo.cas.configuration.model.support.pac4j.saml.Pac4jSamlClientProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link DelegatedClientFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
public class DelegatedClientFactoryTests {
    @Test
    public void verifyFactoryForIdentifiableClients() {
        val props = new Pac4jDelegatedAuthenticationProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getBitbucket());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getDropbox());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getFacebook());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getFoursquare());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getGithub());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getGoogle());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getLinkedIn());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getOrcid());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getPaypal());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getTwitter());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getWindowsLive());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getWordpress());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getYahoo());
        DelegatedClientFactoryTests.configureIdentifiableClient(props.getHiOrgServer());
        val factory = new DelegatedClientFactory(props);
        val clients = factory.build();
        Assertions.assertEquals(14, clients.size());
    }

    @Test
    public void verifyFactoryForCasClients() {
        val props = new Pac4jDelegatedAuthenticationProperties();
        val cas = new Pac4jCasClientProperties();
        cas.setLoginUrl("https://cas.example.org/login");
        cas.setProtocol(SAML.name());
        props.getCas().add(cas);
        val factory = new DelegatedClientFactory(props);
        val clients = factory.build();
        Assertions.assertEquals(1, clients.size());
    }

    @Test
    public void verifyFactoryForSamlClients() {
        val props = new Pac4jDelegatedAuthenticationProperties();
        val saml = new Pac4jSamlClientProperties();
        saml.setKeystorePath(FileUtils.getTempDirectoryPath());
        saml.setIdentityProviderMetadataPath(FileUtils.getTempDirectoryPath());
        saml.setServiceProviderMetadataPath(FileUtils.getTempDirectoryPath());
        saml.setServiceProviderEntityId("test-entityid");
        props.getSaml().add(saml);
        val factory = new DelegatedClientFactory(props);
        val clients = factory.build();
        Assertions.assertEquals(1, clients.size());
    }

    @Test
    public void verifyFactoryForOAuthClients() {
        val props = new Pac4jDelegatedAuthenticationProperties();
        val oauth = new Pac4jOAuth20ClientProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(oauth);
        props.getOauth2().add(oauth);
        val factory = new DelegatedClientFactory(props);
        val clients = factory.build();
        Assertions.assertEquals(1, clients.size());
    }

    @Test
    public void verifyFactoryForOidcClients() {
        val props = new Pac4jDelegatedAuthenticationProperties();
        val oidc1 = new Pac4jOidcClientProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(oidc1.getGeneric());
        props.getOidc().add(oidc1);
        val oidc2 = new Pac4jOidcClientProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(oidc2.getGoogle());
        props.getOidc().add(oidc2);
        val oidc3 = new Pac4jOidcClientProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(oidc3.getAzure());
        oidc3.getAzure().setTenant("contoso.onmicrosoft.com");
        oidc3.getAzure().setLogoutUrl("https://example.logout");
        props.getOidc().add(oidc3);
        val oidc4 = new Pac4jOidcClientProperties();
        DelegatedClientFactoryTests.configureIdentifiableClient(oidc4.getKeycloak());
        props.getOidc().add(oidc4);
        val factory = new DelegatedClientFactory(props);
        val clients = factory.build();
        Assertions.assertEquals(4, clients.size());
    }
}

