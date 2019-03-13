package org.pac4j.core.engine;


import Pac4jConstants.URL;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link DefaultLogoutLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultLogoutLogicTests implements TestsConstants {
    private LogoutLogic<Object, WebContext> logic;

    private MockWebContext context;

    private Config config;

    private HttpActionAdapter<Object, WebContext> httpActionAdapter;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean centralLogout;

    private LinkedHashMap<String, CommonProfile> profiles;

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
    public void testBlankLogoutUrlPattern() {
        logoutUrlPattern = "";
        TestsHelper.expectException(() -> call(), TechnicalException.class, "logoutUrlPattern cannot be blank");
    }

    @Test
    public void testLogoutPerformed() {
        profiles.put(TestsConstants.NAME, new CommonProfile());
        addProfilesToContext();
        call();
        Assert.assertEquals(204, action.getCode());
        expectedNProfiles(0);
    }

    @Test
    public void testLogoutNotPerformedBecauseLocalLogoutIsFalse() {
        profiles.put(TestsConstants.NAME, new CommonProfile());
        addProfilesToContext();
        localLogout = false;
        call();
        Assert.assertEquals(204, action.getCode());
        expectedNProfiles(1);
    }

    @Test
    public void testLogoutPerformedBecauseLocalLogoutIsFalseButMultipleProfiles() {
        profiles.put(TestsConstants.NAME, new CommonProfile());
        profiles.put(TestsConstants.VALUE, new CommonProfile());
        addProfilesToContext();
        localLogout = false;
        call();
        Assert.assertEquals(204, action.getCode());
        expectedNProfiles(0);
    }

    @Test
    public void testCentralLogout() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(TestsConstants.NAME);
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.NAME);
        setCallbackUrl(TestsConstants.PAC4J_BASE_URL);
        setLogoutActionBuilder(( ctx, p, targetUrl) -> Optional.of(new FoundAction((((TestsConstants.CALLBACK_URL) + "?p=") + targetUrl))));
        config.setClients(new Clients(client));
        profiles.put(TestsConstants.NAME, profile);
        addProfilesToContext();
        centralLogout = true;
        context.addRequestParameter(URL, TestsConstants.CALLBACK_URL);
        logoutUrlPattern = ".*";
        call();
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals((((TestsConstants.CALLBACK_URL) + "?p=") + (TestsConstants.CALLBACK_URL)), getLocation());
        expectedNProfiles(0);
    }

    @Test
    public void testCentralLogoutWithRelativeUrl() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(TestsConstants.NAME);
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.NAME);
        setCallbackUrl(TestsConstants.PAC4J_BASE_URL);
        setLogoutActionBuilder(( ctx, p, targetUrl) -> Optional.of(new FoundAction((((TestsConstants.CALLBACK_URL) + "?p=") + targetUrl))));
        config.setClients(new Clients(client));
        profiles.put(TestsConstants.NAME, profile);
        addProfilesToContext();
        centralLogout = true;
        context.addRequestParameter(URL, TestsConstants.PATH);
        call();
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(((TestsConstants.CALLBACK_URL) + "?p=null"), getLocation());
        expectedNProfiles(0);
    }

    @Test
    public void testLogoutWithDefaultUrl() {
        defaultUrl = TestsConstants.CALLBACK_URL;
        call();
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(TestsConstants.CALLBACK_URL, getLocation());
    }

    @Test
    public void testLogoutWithGoodUrl() {
        context.addRequestParameter(URL, TestsConstants.PATH);
        call();
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(TestsConstants.PATH, getLocation());
    }

    @Test
    public void testLogoutWithBadUrlNoDefaultUrl() {
        context.addRequestParameter(URL, TestsConstants.PATH);
        logoutUrlPattern = TestsConstants.VALUE;
        call();
        Assert.assertEquals(204, action.getCode());
        Assert.assertEquals("", context.getResponseContent());
    }

    @Test
    public void testLogoutWithBadUrlButDefaultUrl() {
        context.addRequestParameter(URL, TestsConstants.PATH);
        defaultUrl = TestsConstants.CALLBACK_URL;
        logoutUrlPattern = TestsConstants.VALUE;
        call();
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(TestsConstants.CALLBACK_URL, getLocation());
    }
}

