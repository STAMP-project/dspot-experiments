package client;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.test.context.ActiveProfiles;
import sparklr.common.AbstractIntegrationTests;


/**
 *
 *
 * @author Dave Syer
 */
@SpringApplicationConfiguration(classes = { ClientApplication.class, CombinedApplication.class })
@ActiveProfiles("combined")
public class ClientServerInteractionTests extends AbstractIntegrationTests {
    @Autowired
    private AuthorizationCodeResourceDetails resource;

    private OAuth2RestOperations template;

    @Test
    public void testForRedirectWithNoToken() throws Exception {
        try {
            template.getForObject(http.getUrl("/"), String.class);
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("A redirect is required to get the users approval"));
        }
    }
}

