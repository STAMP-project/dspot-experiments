package org.pac4j.core.client.finder;


import Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link DefaultSecurityClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultSecurityClientFinderTests implements TestsConstants {
    private final DefaultSecurityClientFinder finder = new DefaultSecurityClientFinder();

    @Test
    public void testBlankClientName() {
        final List<Client> currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        Assert.assertEquals(0, currentClients.size());
    }

    @Test
    public void testClientOnRequestAllowed() {
        internalTestClientOnRequestAllowedList(TestsConstants.NAME, TestsConstants.NAME);
    }

    @Test
    public void testBadClientOnRequest() {
        final MockIndirectClient client = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.FAKE_VALUE);
        Assert.assertTrue(finder.find(clients, context, TestsConstants.NAME).isEmpty());
    }

    @Test
    public void testClientOnRequestAllowedList() {
        internalTestClientOnRequestAllowedList(TestsConstants.NAME, (((TestsConstants.FAKE_VALUE) + ",") + (TestsConstants.NAME)));
    }

    @Test
    public void testClientOnRequestAllowedListCaseTrim() {
        internalTestClientOnRequestAllowedList("NaMe  ", ((TestsConstants.FAKE_VALUE.toUpperCase()) + "  ,       nAmE"));
    }

    @Test
    public void testClientOnRequestNotAllowed() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.CLIENT_NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.NAME);
        Assert.assertTrue(finder.find(clients, context, TestsConstants.CLIENT_NAME).isEmpty());
    }

    @Test
    public void testClientOnRequestNotAllowedList() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.CLIENT_NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.NAME);
        Assert.assertTrue(finder.find(clients, context, (((TestsConstants.CLIENT_NAME) + ",") + (TestsConstants.FAKE_VALUE))).isEmpty());
    }

    @Test
    public void testNoClientOnRequest() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.CLIENT_NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, TestsConstants.CLIENT_NAME);
        Assert.assertEquals(1, currentClients.size());
        Assert.assertEquals(client2, currentClients.get(0));
    }

    @Test
    public void testNoClientOnRequestBadDefaultClient() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.CLIENT_NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        Assert.assertTrue(finder.find(clients, context, TestsConstants.FAKE_VALUE).isEmpty());
    }

    @Test
    public void testNoClientOnRequestList() {
        internalTestNoClientOnRequestList((((TestsConstants.CLIENT_NAME) + ",") + (TestsConstants.NAME)));
    }

    @Test
    public void testNoClientOnRequestListBlankSpaces() {
        internalTestNoClientOnRequestList((((TestsConstants.CLIENT_NAME) + " ,") + (TestsConstants.NAME)));
    }

    @Test
    public void testNoClientOnRequestListDifferentCase() {
        internalTestNoClientOnRequestList((((TestsConstants.CLIENT_NAME.toUpperCase()) + ",") + (TestsConstants.NAME)));
    }

    @Test
    public void testNoClientOnRequestListUppercase() {
        internalTestNoClientOnRequestList((((TestsConstants.CLIENT_NAME.toUpperCase()) + ",") + (TestsConstants.NAME)));
    }

    @Test
    public void testDefaultSecurityClients() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(TestsConstants.CLIENT_NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        clients.setDefaultSecurityClients(TestsConstants.CLIENT_NAME);
        final List<Client> result = finder.find(clients, MockWebContext.create(), null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(client2, result.get(0));
    }

    @Test
    public void testOneClientAsDefault() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1);
        final List<Client> result = finder.find(clients, MockWebContext.create(), null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(client1, result.get(0));
    }

    @Test
    public void testBlankClientRequested() {
        final MockIndirectClient client1 = new MockIndirectClient(TestsConstants.NAME, new FoundAction(TestsConstants.LOGIN_URL), Optional.empty(), new CommonProfile());
        final Clients clients = new Clients(client1);
        final List<Client> result = finder.find(clients, MockWebContext.create(), "");
        Assert.assertEquals(0, result.size());
    }
}

