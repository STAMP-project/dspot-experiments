package org.pac4j.core.client;


import HttpConstants.AJAX_HEADER_NAME;
import HttpConstants.AJAX_HEADER_VALUE;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX;


/**
 * This class tests the {@link BaseClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class BaseClientTests implements TestsConstants {
    @Test
    public void testDirectClient() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (redirect(context).get()));
        final String redirectionUrl = action.getLocation();
        Assert.assertEquals(TestsConstants.LOGIN_URL, redirectionUrl);
        final Optional<Credentials> credentials = getCredentials(context);
        Assert.assertFalse(credentials.isPresent());
    }

    @Test
    public void testIndirectClientWithImmediate() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (redirect(context).get()));
        final String redirectionUrl = action.getLocation();
        Assert.assertEquals(TestsConstants.LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        Assert.assertFalse(getUserProfile(null, context).isPresent());
    }

    @Test
    public void testAjaxRequest() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestHeader(AJAX_HEADER_NAME, AJAX_HEADER_VALUE);
        final HttpAction e = ((HttpAction) (TestsHelper.expectException(() -> client.redirect(context))));
        Assert.assertEquals(401, e.getCode());
    }

    @Test
    public void testAlreadyTried() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, ((getName()) + (ATTEMPTED_AUTHENTICATION_SUFFIX)), "true");
        final HttpAction e = ((HttpAction) (TestsHelper.expectException(() -> client.redirect(context))));
        Assert.assertEquals(401, e.getCode());
    }

    @Test
    public void testSaveAlreadyTried() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        setCallbackUrl(TestsConstants.CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        Assert.assertEquals("true", context.getSessionStore().get(context, ((getName()) + (ATTEMPTED_AUTHENTICATION_SUFFIX))).get());
    }

    @Test
    public void testStateParameter() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.TYPE, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockWebContext context = MockWebContext.create();
        TestsHelper.expectException(() -> client.redirect(context));
    }
}

