package org.pac4j.http.client.direct;


import HttpConstants.AUTHORIZATION_HEADER;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;


/**
 * This class tests the {@link DirectBearerAuthClient} class.
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
public final class DirectBearerAuthClientTests implements TestsConstants {
    @Test
    public void testMissingTokenAuthenticator() {
        final DirectBearerAuthClient bearerAuthClient = new DirectBearerAuthClient(null);
        TestsHelper.expectException(() -> bearerAuthClient.getCredentials(MockWebContext.create()), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectBearerAuthClient bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> bearerAuthClient.getUserProfile(new <TOKEN>TokenCredentials(), MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectBearerAuthClient bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        bearerAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        final DirectBearerAuthClient client = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(AUTHORIZATION_HEADER, ((HttpConstants.BEARER_HEADER_PREFIX) + (TOKEN)));
        final TokenCredentials credentials = client.getCredentials(context).get();
        final CommonProfile profile = ((CommonProfile) (client.getUserProfile(credentials, context).get()));
        Assert.assertEquals(TOKEN, profile.getId());
    }
}

