package org.springframework.security.oauth2.config.xml;


import AuthenticationScheme.form;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ResourceBeanDefinitionParserTests {
    @Autowired
    @Qualifier("one")
    private OAuth2ProtectedResourceDetails one;

    @Autowired
    @Qualifier("two")
    private OAuth2ProtectedResourceDetails two;

    @Autowired
    @Qualifier("three")
    private AuthorizationCodeResourceDetails three;

    @Autowired
    @Qualifier("four")
    private ImplicitResourceDetails four;

    @Autowired
    @Qualifier("five")
    private ClientCredentialsResourceDetails five;

    @Autowired
    @Qualifier("six")
    private AuthorizationCodeResourceDetails six;

    @Autowired
    @Qualifier("seven")
    private ResourceOwnerPasswordResourceDetails seven;

    @Autowired
    @Qualifier("template")
    private OAuth2RestTemplate template;

    @Test
    public void testResourceFromNonPropertyFile() {
        Assert.assertEquals("my-client-id-non-property-file", one.getClientId());
        Assert.assertEquals("my-client-secret-non-property-file", one.getClientSecret());
        Assert.assertEquals("http://somewhere.com", one.getAccessTokenUri());
        Assert.assertEquals(2, one.getScope().size());
        Assert.assertEquals("[none, some]", one.getScope().toString());
    }

    @Test
    public void testResourceFromPropertyFile() {
        Assert.assertEquals("my-client-id-property-file", two.getClientId());
        Assert.assertEquals("my-client-secret-property-file", two.getClientSecret());
        Assert.assertEquals("http://myhost.com", two.getAccessTokenUri());
        Assert.assertEquals(2, two.getScope().size());
        Assert.assertEquals("[none, all]", two.getScope().toString());
    }

    @Test
    public void testResourceWithRedirectUri() {
        Assert.assertEquals("my-client-id", three.getClientId());
        Assert.assertNull(three.getClientSecret());
        Assert.assertEquals("http://somewhere.com", three.getAccessTokenUri());
        Assert.assertEquals("http://anywhere.com", three.getPreEstablishedRedirectUri());
        Assert.assertFalse(three.isUseCurrentUri());
    }

    @Test
    public void testResourceWithImplicitGrant() {
        Assert.assertEquals("my-client-id", four.getClientId());
        Assert.assertNull(four.getClientSecret());
        Assert.assertEquals("http://somewhere.com", four.getUserAuthorizationUri());
    }

    @Test
    public void testResourceWithClientCredentialsGrant() {
        Assert.assertEquals("my-secret-id", five.getClientId());
        Assert.assertEquals("secret", five.getClientSecret());
        Assert.assertEquals("http://somewhere.com", five.getAccessTokenUri());
        Assert.assertNotNull(template.getOAuth2ClientContext().getAccessTokenRequest());
    }

    @Test
    public void testResourceWithCurrentUriHint() {
        Assert.assertEquals("my-client-id", six.getClientId());
        Assert.assertFalse(six.isUseCurrentUri());
        Assert.assertEquals(form, six.getClientAuthenticationScheme());
    }

    @Test
    public void testResourceWithPasswordGrant() {
        Assert.assertEquals("my-client-id", seven.getClientId());
        Assert.assertEquals("secret", seven.getClientSecret());
        Assert.assertEquals("http://somewhere.com", seven.getAccessTokenUri());
        Assert.assertEquals("admin", seven.getUsername());
        Assert.assertEquals("long-and-strong", seven.getPassword());
    }
}

