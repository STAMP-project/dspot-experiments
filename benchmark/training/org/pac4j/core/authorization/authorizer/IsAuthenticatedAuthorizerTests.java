package org.pac4j.core.authorization.authorizer;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link IsAuthenticatedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizerTests implements TestsConstants {
    protected Authorizer authorizer;

    protected List<CommonProfile> profiles;

    protected CommonProfile profile;

    @Test
    public void testNoProfile() {
        Assert.assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testAnonymousProfile() {
        profiles.add(new AnonymousProfile());
        Assert.assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfileTwoProfiles() {
        profiles.add(new AnonymousProfile());
        profiles.add(profile);
        Assert.assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfile() {
        profiles.add(profile);
        Assert.assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        setRedirectionUrl(TestsConstants.PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), HttpAction.class, "Performing a 302 HTTP action");
    }
}

