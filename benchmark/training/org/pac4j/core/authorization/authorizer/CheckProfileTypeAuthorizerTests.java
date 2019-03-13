package org.pac4j.core.authorization.authorizer;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link CheckProfileTypeAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckProfileTypeAuthorizerTests {
    private static class FakeProfile1 extends CommonProfile {
        private static final long serialVersionUID = 593942762996944056L;

        public FakeProfile1() {
        }
    }

    private static class FakeProfile2 extends CommonProfile {
        private static final long serialVersionUID = -7923087937494697612L;

        public FakeProfile2() {
        }
    }

    @Test
    public void testGoodProfile() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(CheckProfileTypeAuthorizerTests.FakeProfile1.class, CheckProfileTypeAuthorizerTests.FakeProfile2.class);
        final List<CommonProfile> profiles = new ArrayList<>();
        profiles.add(new CheckProfileTypeAuthorizerTests.FakeProfile1());
        Assert.assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testBadProfileType() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(CheckProfileTypeAuthorizerTests.FakeProfile1.class);
        final List<CommonProfile> profiles = new ArrayList<>();
        profiles.add(new CheckProfileTypeAuthorizerTests.FakeProfile2());
        Assert.assertFalse(authorizer.isAuthorized(null, profiles));
    }
}

