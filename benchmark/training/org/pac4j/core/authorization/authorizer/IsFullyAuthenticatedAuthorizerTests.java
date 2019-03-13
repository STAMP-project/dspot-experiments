package org.pac4j.core.authorization.authorizer;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link IsFullyAuthenticatedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class IsFullyAuthenticatedAuthorizerTests extends IsAuthenticatedAuthorizerTests {
    @Override
    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        setRedirectionUrl(TestsConstants.PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), HttpAction.class, "Performing a 302 HTTP action");
    }

    @Test
    public void testCommonRmeProfile() {
        profile.setRemembered(true);
        profiles.add(profile);
        Assert.assertFalse(authorizer.isAuthorized(null, profiles));
    }
}

