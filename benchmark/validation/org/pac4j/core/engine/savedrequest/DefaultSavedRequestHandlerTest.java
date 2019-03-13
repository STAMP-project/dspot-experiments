package org.pac4j.core.engine.savedrequest;


import Pac4jConstants.REQUESTED_URL;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link DefaultSavedRequestHandler}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultSavedRequestHandlerTest implements TestsConstants {
    private static final String FORM_DATA = "<html>\n" + ((((((("<body>\n" + "<form action=\"http://www.pac4j.org/test.html\" name=\"f\" method=\"post\">\n") + "<input type=\'hidden\' name=\"key\" value=\"value\" />\n") + "<input value=\'POST\' type=\'submit\' />\n") + "</form>\n") + "<script type=\'text/javascript\'>document.forms[\'f\'].submit();</script>\n") + "</body>\n") + "</html>\n");

    private DefaultSavedRequestHandler handler = new DefaultSavedRequestHandler();

    @Test
    public void testSaveGet() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(TestsConstants.PAC4J_URL);
        handler.save(context);
        final MockSessionStore sessionStore = ((MockSessionStore) (context.getSessionStore()));
        final FoundAction action = ((FoundAction) (sessionStore.get(context, REQUESTED_URL).get()));
        Assert.assertEquals(TestsConstants.PAC4J_URL, action.getLocation());
    }

    @Test
    public void testSavePost() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(TestsConstants.PAC4J_URL).setRequestMethod("POST");
        context.addRequestParameter(TestsConstants.KEY, TestsConstants.VALUE);
        handler.save(context);
        final MockSessionStore sessionStore = ((MockSessionStore) (context.getSessionStore()));
        final OkAction action = ((OkAction) (sessionStore.get(context, REQUESTED_URL).get()));
        Assert.assertEquals(DefaultSavedRequestHandlerTest.FORM_DATA, action.getContent());
    }

    @Test
    public void testRestoreNoRequestedUrl() {
        final MockWebContext context = MockWebContext.create();
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(TestsConstants.LOGIN_URL, getLocation());
        Assert.assertFalse(context.getSessionStore().get(context, REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreEmptyString() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, REQUESTED_URL, "");
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(TestsConstants.LOGIN_URL, getLocation());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }

    @Test
    public void testRestoreStringURL() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, REQUESTED_URL, TestsConstants.PAC4J_URL);
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(TestsConstants.PAC4J_URL, getLocation());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }

    @Test
    public void testRestoreFoundAction() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, REQUESTED_URL, new FoundAction(TestsConstants.PAC4J_URL));
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof FoundAction));
        Assert.assertEquals(TestsConstants.PAC4J_URL, getLocation());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }

    @Test
    public void testRestoreFoundActionAfterPost() {
        final MockWebContext context = MockWebContext.create();
        context.setRequestMethod("POST");
        context.getSessionStore().set(context, REQUESTED_URL, new FoundAction(TestsConstants.PAC4J_URL));
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof SeeOtherAction));
        Assert.assertEquals(TestsConstants.PAC4J_URL, getLocation());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }

    @Test
    public void testRestoreOkAction() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, REQUESTED_URL, OkAction.buildFormContentFromUrlAndData(TestsConstants.PAC4J_URL, buildParameters()));
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof OkAction));
        Assert.assertEquals(DefaultSavedRequestHandlerTest.FORM_DATA, getContent());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }

    @Test
    public void testRestoreOkActionAfterPost() {
        final MockWebContext context = MockWebContext.create();
        context.setRequestMethod("POST");
        context.getSessionStore().set(context, REQUESTED_URL, OkAction.buildFormContentFromUrlAndData(TestsConstants.PAC4J_URL, buildParameters()));
        final HttpAction action = handler.restore(context, TestsConstants.LOGIN_URL);
        Assert.assertTrue((action instanceof TemporaryRedirectAction));
        Assert.assertEquals(DefaultSavedRequestHandlerTest.FORM_DATA, getContent());
        Assert.assertEquals("", context.getSessionStore().get(context, REQUESTED_URL).get());
    }
}

