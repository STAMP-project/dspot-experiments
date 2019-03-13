package org.pac4j.core.engine;


import Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;
import Pac4jConstants.DEFAULT_URL_VALUE;
import Pac4jConstants.USER_PROFILES;
import java.util.LinkedHashMap;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests {@link DefaultCallbackLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultCallbackLogicTests implements TestsConstants {
    private DefaultCallbackLogic<Object, JEEContext> logic;

    protected MockHttpServletRequest request;

    protected MockHttpServletResponse response;

    private JEEContext context;

    private Config config;

    private HttpActionAdapter<Object, JEEContext> httpActionAdapter;

    private String defaultUrl;

    private Boolean renewSession;

    private ClientFinder clientFinder;

    private HttpAction action;

    @Test
    public void testNullConfig() {
        config = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "config cannot be null");
    }

    @Test
    public void testNullContext() {
        context = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "context cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        httpActionAdapter = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "httpActionAdapter cannot be null");
    }

    @Test
    public void testBlankDefaultUrl() {
        defaultUrl = "";
        TestsHelper.expectException(() -> call(), TechnicalException.class, "defaultUrl cannot be blank");
    }

    @Test
    public void testNullClients() {
        config.setClients(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testNullClientFinder() {
        clientFinder = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testDirectClient() {
        request.addParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.NAME);
        final MockDirectClient directClient = new MockDirectClient(TestsConstants.NAME, Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(directClient));
        TestsHelper.expectException(() -> call(), TechnicalException.class, ("unable to find one indirect client for the callback: check the callback URL for a client name parameter or" + " suffix path or ensure that your configuration defaults to one indirect client"));
    }

    @Test
    public void testCallback() {
        final String originalSessionId = request.getSession().getId();
        request.setParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.NAME);
        final CommonProfile profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(TestsConstants.NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(TestsConstants.CALLBACK_URL, indirectClient));
        config.getClients().init();
        call();
        final HttpSession session = request.getSession();
        final String newSessionId = session.getId();
        final LinkedHashMap<String, CommonProfile> profiles = ((LinkedHashMap<String, CommonProfile>) (session.getAttribute(USER_PROFILES)));
        Assert.assertTrue(profiles.containsValue(profile));
        Assert.assertEquals(1, profiles.size());
        Assert.assertNotEquals(newSessionId, originalSessionId);
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(DEFAULT_URL_VALUE, getLocation());
    }

    @Test
    public void testCallbackWithOriginallyRequestedUrl() {
        internalTestCallbackWithOriginallyRequestedUrl(302);
    }

    @Test
    public void testCallbackWithOriginallyRequestedUrlAndPostRequest() {
        request.setMethod("POST");
        internalTestCallbackWithOriginallyRequestedUrl(303);
    }

    @Test
    public void testCallbackNoRenew() {
        final String originalSessionId = request.getSession().getId();
        request.setParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.NAME);
        final CommonProfile profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(TestsConstants.NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(TestsConstants.CALLBACK_URL, indirectClient));
        renewSession = false;
        config.getClients().init();
        call();
        final HttpSession session = request.getSession();
        final String newSessionId = session.getId();
        final LinkedHashMap<String, CommonProfile> profiles = ((LinkedHashMap<String, CommonProfile>) (session.getAttribute(USER_PROFILES)));
        Assert.assertTrue(profiles.containsValue(profile));
        Assert.assertEquals(1, profiles.size());
        Assert.assertEquals(newSessionId, originalSessionId);
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(DEFAULT_URL_VALUE, getLocation());
    }
}

