package demo;


import HttpStatus.OK;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import sparklr.common.AbstractResourceOwnerPasswordProviderTests;


/**
 *
 *
 * @author Dave Syer
 */
public class ResourceOwnerPasswordProviderTests extends AbstractResourceOwnerPasswordProviderTests {
    @Test
    @OAuth2ContextConfiguration(ResourceOwnerPasswordProviderTests.OtherResourceOwner.class)
    public void testTokenObtainedWithHeaderAuthenticationAndOtherResource() throws Exception {
        Assert.assertEquals(OK, http.getStatusCode("/"));
    }

    static class OtherResourceOwner extends ResourceOwner implements DoNotOverride {
        public OtherResourceOwner(Object target) {
            super(target);
            setClientId("my-other-client-with-secret");
            setClientSecret("secret");
            setScope(Arrays.asList("trust"));
        }
    }
}

