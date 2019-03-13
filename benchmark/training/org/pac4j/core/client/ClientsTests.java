package org.pac4j.core.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * This class tests the {@link Clients} class.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class ClientsTests implements TestsConstants {
    @Test
    public void testMissingClient() {
        final Clients clients = new Clients();
        clients.setCallbackUrl(TestsConstants.CALLBACK_URL);
        TestsHelper.initShouldFail(clients, "clients cannot be null");
    }

    @Test
    public void testNoValuesSet() {
        MockIndirectClient facebookClient = newFacebookClient();
        final Clients clients = new Clients(facebookClient);
        clients.init();
        Assert.assertNull(getCallbackUrl());
        Assert.assertNull(getUrlResolver());
        Assert.assertNull(getCallbackUrlResolver());
        Assert.assertNull(getAjaxRequestResolver());
        Assert.assertEquals(0, getAuthorizationGenerators().size());
    }

    @Test
    public void testValuesSet() {
        MockIndirectClient facebookClient = newFacebookClient();
        final Clients clients = new Clients(facebookClient);
        final AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
        final UrlResolver urlResolver = new DefaultUrlResolver();
        final CallbackUrlResolver callbackUrlResolver = new QueryParameterCallbackUrlResolver();
        final AuthorizationGenerator authorizationGenerator = ( context, profile) -> Optional.of(profile);
        clients.setCallbackUrl(TestsConstants.CALLBACK_URL);
        clients.setAjaxRequestResolver(ajaxRequestResolver);
        clients.setUrlResolver(urlResolver);
        clients.setCallbackUrlResolver(callbackUrlResolver);
        clients.addAuthorizationGenerator(authorizationGenerator);
        clients.init();
        Assert.assertEquals(TestsConstants.CALLBACK_URL, getCallbackUrl());
        Assert.assertEquals(urlResolver, getUrlResolver());
        Assert.assertEquals(callbackUrlResolver, getCallbackUrlResolver());
        Assert.assertEquals(ajaxRequestResolver, getAjaxRequestResolver());
        Assert.assertEquals(authorizationGenerator, getAuthorizationGenerators().get(0));
    }

    @Test
    public void testAllClients() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(TestsConstants.CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients();
        Assert.assertEquals(2, clients2.size());
        Assert.assertTrue(clients2.containsAll(clients));
    }

    @Test
    public void testByClass1() {
        internalTestByClass(false);
    }

    @Test
    public void testByClass2() {
        internalTestByClass(true);
    }

    @Test
    public void rejectSameName() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameDifferentCase() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.NAME.toUpperCase(), new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: NAME");
    }

    @Test
    public void testFindByName() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        Assert.assertNotNull(clients.findClient("FacebookClient"));
    }

    @Test
    public void testFindByNameCase() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        Assert.assertNotNull(clients.findClient("FACEBOOKclient"));
    }

    @Test
    public void testFindByNameBlankSpaces() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        Assert.assertNotNull(clients.findClient(" FacebookClient          "));
    }
}

