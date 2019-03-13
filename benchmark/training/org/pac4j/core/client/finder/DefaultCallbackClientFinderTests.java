package org.pac4j.core.client.finder;


import Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link DefaultCallbackClientFinder}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class DefaultCallbackClientFinderTests implements TestsConstants {
    @Test
    public void testQueryParameter() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final DirectClient basicAuth = new MockDirectClient("BasicAuth");
        final IndirectClient cas = new MockIndirectClient("cas");
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, facebook, basicAuth, cas);
        clients.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, "facebook   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, context, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(facebook, result.get(0));
    }

    @Test
    public void testPathParameter() {
        final IndirectClient azure = new MockIndirectClient("azure");
        azure.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, azure);
        clients.init();
        final MockWebContext context = MockWebContext.create().setPath("/   AZURE   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, context, null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(azure, result.get(0));
    }

    @Test
    public void testDefaultClientDirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final DirectClient basicAuth = new MockDirectClient("BasicAuth");
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, basicAuth, facebook);
        clients.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, "basicauth");
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, context, "Facebook");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(facebook, result.get(0));
    }

    @Test
    public void testDefaultClientIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, twitter, facebook);
        clients.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, "Twitter");
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, context, "Twitter");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(twitter, result.get(0));
    }

    @Test
    public void testDefaultClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, twitter, facebook);
        clients.init();
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, MockWebContext.create(), "Facebook");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(facebook, result.get(0));
    }

    @Test
    public void testOneIndirectClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final Clients clients = new Clients(TestsConstants.CALLBACK_URL, facebook);
        clients.init();
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client> result = finder.find(clients, MockWebContext.create(), null);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(facebook, result.get(0));
    }
}

