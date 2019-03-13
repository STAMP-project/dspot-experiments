package org.pac4j.oauth.client;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * This class tests the {@link OAuth20Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuth20ClientTests implements TestsConstants {
    @Test
    public void testState() throws MalformedURLException {
        FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        setStateData("OK");
        final FoundAction action = ((FoundAction) (client.redirect(MockWebContext.create()).get()));
        URL url = new URL(action.getLocation());
        Assert.assertTrue(url.getQuery().contains("state=OK"));
    }

    @Test
    public void testSetState() throws MalformedURLException {
        FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        setStateData("oldstate");
        final MockWebContext mockWebContext = MockWebContext.create();
        FoundAction action = ((FoundAction) (client.redirect(mockWebContext).get()));
        URL url = new URL(action.getLocation());
        final Map<String, String> stringMap = TestsHelper.splitQuery(url);
        Assert.assertEquals(stringMap.get("state"), "oldstate");
        action = ((FoundAction) (client.redirect(mockWebContext).get()));
        URL url2 = new URL(action.getLocation());
        final Map<String, String> stringMap2 = TestsHelper.splitQuery(url2);
        Assert.assertEquals(stringMap2.get("state"), "oldstate");
    }

    @Test
    public void testStateRandom() throws MalformedURLException {
        OAuth20Client client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        FoundAction action = ((FoundAction) (client.redirect(MockWebContext.create()).get()));
        URL url = new URL(action.getLocation());
        final Map<String, String> stringMap = TestsHelper.splitQuery(url);
        Assert.assertNotNull(stringMap.get("state"));
        action = ((FoundAction) (client.redirect(MockWebContext.create()).get()));
        URL url2 = new URL(action.getLocation());
        final Map<String, String> stringMap2 = TestsHelper.splitQuery(url2);
        Assert.assertNotNull(stringMap2.get("state"));
        Assert.assertNotEquals(stringMap.get("state"), stringMap2.get("state"));
    }

    @Test
    public void testGetRedirectionGithub() {
        final FoundAction action = ((FoundAction) (getClient().redirect(MockWebContext.create()).get()));
        final String url = action.getLocation();
        Assert.assertTrue(((url != null) && (!(url.isEmpty()))));
    }

    @Test
    public void testDefaultName20() {
        final OAuth20Client client = new FacebookClient();
        Assert.assertEquals("FacebookClient", client.getName());
    }

    @Test
    public void testDefinedName() {
        final OAuth20Client client = new FacebookClient();
        client.setName(TYPE);
        Assert.assertEquals(TYPE, client.getName());
    }

    @Test
    public void testMissingKey() {
        final OAuth20Client client = getClient();
        client.setKey(null);
        TestsHelper.expectException(() -> client.redirect(MockWebContext.create()), TechnicalException.class, "key cannot be blank");
    }

    @Test
    public void testMissingSecret() {
        final OAuth20Client client = getClient();
        client.setSecret(null);
        TestsHelper.expectException(() -> client.redirect(MockWebContext.create()), TechnicalException.class, "secret cannot be blank");
    }

    @Test
    public void testMissingFieldsFacebook() {
        final FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    @Test
    public void testMissingScopeGoogle() {
        final Google2Client client = getGoogleClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    @Test
    public void testDefaultScopeGoogle() {
        getGoogleClient().redirect(MockWebContext.create());
    }

    @Test
    public void testMissingFieldsOk() {
        final OkClient client = new OkClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "publicKey cannot be blank");
    }

    @Test
    public void testMissingScopeLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }

    @Test
    public void testMissingFieldsLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    @Test
    public void testMissingFieldsPaypal() {
        final PayPalClient client = new PayPalClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
}

