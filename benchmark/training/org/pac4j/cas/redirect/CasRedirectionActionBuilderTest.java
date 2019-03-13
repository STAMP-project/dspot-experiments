package org.pac4j.cas.redirect;


import CasProtocol.SAML;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link CasRedirectionActionBuilder}.
 *
 * @author Jerome LELEU
 * @since 3.7.0
 */
public final class CasRedirectionActionBuilderTest implements TestsConstants {
    @Test
    public void testRedirect() {
        final CasRedirectionActionBuilder builder = newBuilder(new CasConfiguration());
        final RedirectionAction action = builder.redirect(MockWebContext.create()).get();
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(((LOGIN_URL) + "?service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient"), getLocation());
    }

    @Test
    public void testRedirectWithMethod() {
        final CasConfiguration config = new CasConfiguration();
        config.setMethod("post");
        final CasRedirectionActionBuilder builder = newBuilder(config);
        final RedirectionAction action = builder.redirect(MockWebContext.create()).get();
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(((LOGIN_URL) + "?method=post&service=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient"), getLocation());
    }

    @Test
    public void testRedirectForSAMLProtocol() {
        final CasConfiguration config = new CasConfiguration();
        config.setProtocol(SAML);
        final CasRedirectionActionBuilder builder = newBuilder(config);
        final RedirectionAction action = builder.redirect(MockWebContext.create()).get();
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(((LOGIN_URL) + "?TARGET=http%3A%2F%2Fwww.pac4j.org%2Ftest.html%3Fclient_name%3DCasClient"), getLocation());
    }
}

