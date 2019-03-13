package org.pac4j.http.credentials.extractor;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;


/**
 * This class tests the {@link IpExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpExtractorTests implements TestsConstants {
    private static final String GOOD_IP = "goodIp";

    @SuppressWarnings("PMD")
    private static final String LOCALHOST = "127.0.0.1";

    private static final IpExtractor extractor = new IpExtractor();

    @Test
    public void testRetrieveIpOk() {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(IpExtractorTests.GOOD_IP);
        final TokenCredentials credentials = IpExtractorTests.extractor.extract(context).get();
        Assert.assertEquals(IpExtractorTests.GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderWithProxyIpCheck() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, IpExtractorTests.GOOD_IP).setRemoteAddress(IpExtractorTests.LOCALHOST);
        final IpExtractor ipExtractor = new IpExtractor();
        ipExtractor.setProxyIp(IpExtractorTests.LOCALHOST);
        // test for varargs
        ipExtractor.setAlternateIpHeaders("fooBar", HEADER_NAME, "barFoo");
        final TokenCredentials credentials = ipExtractor.extract(context).get();
        Assert.assertEquals(IpExtractorTests.GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        ipExtractor.setAlternateIpHeaders(HEADER_NAME);
        final TokenCredentials credentials2 = ipExtractor.extract(context).get();
        Assert.assertEquals(IpExtractorTests.GOOD_IP, credentials2.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderUsingConstructor() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, IpExtractorTests.GOOD_IP).setRemoteAddress(IpExtractorTests.LOCALHOST);
        // test for varargs
        final IpExtractor ipExtractor = new IpExtractor("fooBar", HEADER_NAME, "barFoo");
        final TokenCredentials credentials = ipExtractor.extract(context).get();
        Assert.assertEquals(IpExtractorTests.GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        final IpExtractor ipExtractor2 = new IpExtractor(HEADER_NAME);
        final TokenCredentials credentials2 = ipExtractor2.extract(context).get();
        Assert.assertEquals(IpExtractorTests.GOOD_IP, credentials2.getToken());
    }

    @Test(expected = TechnicalException.class)
    public void testSetNullIpHeaderChain() {
        final IpExtractor ipExtractor = new IpExtractor();
        ipExtractor.setAlternateIpHeaders(((String[]) (null)));
    }

    @Test
    public void testNoIp() {
        final MockWebContext context = MockWebContext.create();
        final Optional<TokenCredentials> credentials = IpExtractorTests.extractor.extract(context);
        Assert.assertFalse(credentials.isPresent());
    }
}

