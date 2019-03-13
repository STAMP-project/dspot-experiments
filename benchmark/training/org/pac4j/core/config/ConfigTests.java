package org.pac4j.core.config;


import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests the {@link Config}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigTests implements TestsConstants {
    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersSetter() {
        final Config config = new Config();
        config.setAuthorizers(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersConstructor() {
        new Config(((Map<String, Authorizer>) (null)));
    }

    @Test
    public void testAddAuthorizer() {
        final Config config = new Config();
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        config.addAuthorizer(TestsConstants.NAME, authorizer);
        Assert.assertEquals(authorizer, config.getAuthorizers().get(TestsConstants.NAME));
    }

    @Test
    public void testConstructor() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Config config = new Config(TestsConstants.CALLBACK_URL, client);
        Assert.assertEquals(TestsConstants.CALLBACK_URL, config.getClients().getCallbackUrl());
        Assert.assertEquals(client, config.getClients().findAllClients().get(0));
    }
}

