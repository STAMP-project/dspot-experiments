package org.springframework.security.oauth2.config.xml;


import java.util.Collection;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ClientDetailsServiceBeanDefinitionParserTests {
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Test
    public void testClientDetailsFromNonPropertyFile() {
        // valid client details NOT from property file
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId("my-client-id-non-property-file");
        Assert.assertNotNull(clientDetailsService);
        Assert.assertEquals("my-client-id-non-property-file", clientDetails.getClientId());
        Assert.assertEquals("my-client-secret-non-property-file", clientDetails.getClientSecret());
        Set<String> grantTypes = clientDetails.getAuthorizedGrantTypes();
        Assert.assertNotNull(grantTypes);
        Assert.assertEquals(2, grantTypes.size());
        Assert.assertTrue(grantTypes.contains("password"));
        Assert.assertTrue(grantTypes.contains("authorization_code"));
        Set<String> scopes = clientDetails.getScope();
        Assert.assertNotNull(scopes);
        Assert.assertEquals(2, scopes.size());
        Assert.assertTrue(scopes.contains("scope1"));
        Assert.assertTrue(scopes.contains("scope2"));
        Collection<GrantedAuthority> authorities = clientDetails.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(2, authorities.size());
        Assert.assertTrue(AuthorityUtils.authorityListToSet(authorities).contains("ROLE_USER"));
        Assert.assertTrue(AuthorityUtils.authorityListToSet(authorities).contains("ROLE_ANONYMOUS"));
    }

    @Test
    public void testClientDetailsFromPropertyFile() {
        // valid client details from property file
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId("my-client-id-property-file");
        Assert.assertNotNull(clientDetailsService);
        Assert.assertEquals("my-client-id-property-file", clientDetails.getClientId());
        Assert.assertEquals("my-client-secret-property-file", clientDetails.getClientSecret());
        Set<String> grantTypes = clientDetails.getAuthorizedGrantTypes();
        Assert.assertNotNull(grantTypes);
        Assert.assertEquals(2, grantTypes.size());
        Assert.assertTrue(grantTypes.contains("password"));
        Assert.assertTrue(grantTypes.contains("authorization_code"));
        Set<String> scopes = clientDetails.getScope();
        Assert.assertNotNull(scopes);
        Assert.assertEquals(2, scopes.size());
        Assert.assertTrue(scopes.contains("scope1"));
        Assert.assertTrue(scopes.contains("scope2"));
        Collection<GrantedAuthority> authorities = clientDetails.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(2, authorities.size());
        Assert.assertTrue(AuthorityUtils.authorityListToSet(authorities).contains("ROLE_USER"));
        Assert.assertTrue(AuthorityUtils.authorityListToSet(authorities).contains("ROLE_ANONYMOUS"));
    }

    @Test
    public void testClientDetailsDefaultFlow() {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId("my-client-id-default-flow");
        Assert.assertNotNull(clientDetailsService);
        Assert.assertEquals("my-client-id-default-flow", clientDetails.getClientId());
        Assert.assertEquals(1, clientDetails.getRegisteredRedirectUri().size());
        Assert.assertEquals("http://mycompany.com", clientDetails.getRegisteredRedirectUri().iterator().next());
        Set<String> grantTypes = clientDetails.getAuthorizedGrantTypes();
        Assert.assertNotNull(grantTypes);
        Assert.assertEquals(2, grantTypes.size());
        Assert.assertTrue(grantTypes.contains("authorization_code"));
        Assert.assertTrue(grantTypes.contains("refresh_token"));
    }
}

