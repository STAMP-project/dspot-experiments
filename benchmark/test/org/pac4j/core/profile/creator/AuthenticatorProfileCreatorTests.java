package org.pac4j.core.profile.creator;


import AuthenticatorProfileCreator.INSTANCE;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;


/**
 * This class tests the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorProfileCreatorTests implements TestsConstants {
    @Test
    public void testReturnNoProfile() {
        Assert.assertFalse(INSTANCE.create(new TokenCredentials(TestsConstants.TOKEN), null).isPresent());
    }

    @Test
    public void testReturnProfile() {
        final CommonProfile profile = new CommonProfile();
        final Credentials credentials = new TokenCredentials(TestsConstants.TOKEN);
        credentials.setUserProfile(profile);
        final CommonProfile profile2 = ((CommonProfile) (INSTANCE.create(credentials, null).get()));
        Assert.assertEquals(profile, profile2);
    }
}

