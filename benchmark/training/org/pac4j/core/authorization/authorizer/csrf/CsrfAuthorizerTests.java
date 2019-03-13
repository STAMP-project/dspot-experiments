package org.pac4j.core.authorization.authorizer.csrf;


import HttpConstants.HTTP_METHOD.DELETE;
import HttpConstants.HTTP_METHOD.PATCH;
import HttpConstants.HTTP_METHOD.POST;
import HttpConstants.HTTP_METHOD.PUT;
import Pac4jConstants.CSRF_TOKEN;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link CsrfAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class CsrfAuthorizerTests implements TestsConstants {
    private CsrfAuthorizer authorizer;

    @Test
    public void testParameterOk() {
        final WebContext context = MockWebContext.create().addRequestParameter(CSRF_TOKEN, TestsConstants.VALUE).addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testParameterOkNewName() {
        final WebContext context = MockWebContext.create().addRequestParameter(TestsConstants.NAME, TestsConstants.VALUE).addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        authorizer.setParameterName(TestsConstants.NAME);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOk() {
        final WebContext context = MockWebContext.create().addRequestHeader(CSRF_TOKEN, TestsConstants.VALUE).addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOkNewName() {
        final WebContext context = MockWebContext.create().addRequestHeader(TestsConstants.NAME, TestsConstants.VALUE).addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        authorizer.setHeaderName(TestsConstants.NAME);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoToken() {
        final WebContext context = MockWebContext.create().addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        Assert.assertFalse(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoTokenCheckAll() {
        final MockWebContext context = MockWebContext.create().addSessionAttribute(CSRF_TOKEN, TestsConstants.VALUE);
        authorizer.setCheckAllRequests(false);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoTokenRequest() {
        internalTestNoTokenRequest(POST);
        internalTestNoTokenRequest(PUT);
        internalTestNoTokenRequest(PATCH);
        internalTestNoTokenRequest(DELETE);
    }

    @Test
    public void testHeaderOkButNoTokenInSession() {
        final WebContext context = MockWebContext.create().addRequestHeader(CSRF_TOKEN, TestsConstants.VALUE);
        Assert.assertFalse(authorizer.isAuthorized(context, null));
    }
}

