package org.apereo.cas.util.http;


import lombok.val;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link SimpleHttpClient}.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class SimpleHttpClientTests {
    @Test
    public void verifyOkayUrl() {
        Assertions.assertTrue(SimpleHttpClientTests.getHttpClient().isValidEndPoint("http://www.google.com"));
    }

    @Test
    public void verifyBadUrl() {
        Assertions.assertFalse(SimpleHttpClientTests.getHttpClient().isValidEndPoint("https://www.abc1234.org"));
    }

    @Test
    public void verifyInvalidHttpsUrl() {
        val client = SimpleHttpClientTests.getHttpClient();
        Assertions.assertFalse(client.isValidEndPoint("https://wrong.host.badssl.com/"));
    }

    @Test
    public void verifyBypassedInvalidHttpsUrl() {
        val clientFactory = new SimpleHttpClientFactoryBean();
        clientFactory.setSslSocketFactory(SimpleHttpClientTests.getFriendlyToAllSSLSocketFactory());
        clientFactory.setHostnameVerifier(new NoopHostnameVerifier());
        clientFactory.setAcceptableCodes(CollectionUtils.wrapList(200, 403));
        val client = clientFactory.getObject();
        Assertions.assertNotNull(client);
        Assertions.assertTrue(client.isValidEndPoint("https://wrong.host.badssl.com/"));
    }
}

