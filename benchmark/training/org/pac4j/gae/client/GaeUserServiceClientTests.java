package org.pac4j.gae.client;


import HttpConstants.FOUND;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the {@link GaeUserServiceClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class GaeUserServiceClientTests implements TestsConstants {
    private static final Logger logger = LoggerFactory.getLogger(GaeUserServiceClientTests.class);

    private final LocalServiceTestHelper helper = setEnvIsAdmin(true).setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain("");

    private GaeUserServiceClient client;

    private MockWebContext context;

    @Test(expected = TechnicalException.class)
    public void testCallbackMandatory() {
        final GaeUserServiceClient localClient = new GaeUserServiceClient();
        localClient.redirect(context);
    }

    @Test
    public void testRedirect() {
        final HttpAction action = client.redirect(context).get();
        Assert.assertEquals(FOUND, action.getCode());
        Assert.assertEquals(("/_ah/login?continue=" + (CommonHelper.urlEncode((((((CALLBACK_URL) + "?") + (Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER)) + "=") + (client.getName()))))), getLocation());
    }

    @Test
    public void testGetCredentialsUserProfile() {
        final GaeUserCredentials credentials = client.getCredentials(context).get();
        final User user = credentials.getUser();
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals("", user.getAuthDomain());
        final GaeUserServiceProfile profile = ((GaeUserServiceProfile) (client.getUserProfile(credentials, context).get()));
        GaeUserServiceClientTests.logger.debug("userProfile: {}", profile);
        Assert.assertEquals(EMAIL, profile.getId());
        Assert.assertEquals((((GaeUserServiceProfile.class.getName()) + (CommonProfile.SEPARATOR)) + (EMAIL)), profile.getTypedId());
        Assert.assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GaeUserServiceProfile.class));
        Assert.assertEquals("test", profile.getDisplayName());
        Assert.assertTrue(profile.getRoles().contains("GLOBAL_ADMIN"));
        Assert.assertEquals(2, profile.getAttributes().size());
    }
}

