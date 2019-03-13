package org.pac4j.core.credentials.extractor;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * This class tests the {@link HeaderExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class HeaderExtractorTests implements TestsConstants {
    private static final String GOOD_HEADER = "goodHeader";

    private static final String BAD_HEADER = "badHeader";

    private static final String GOOD_PREFIX = "goodPrefix ";

    private static final String BAD_PREFIX = "badPrefix ";

    private static final HeaderExtractor extractor = new HeaderExtractor(HeaderExtractorTests.GOOD_HEADER, HeaderExtractorTests.GOOD_PREFIX);

    @Test
    public void testRetrieveHeaderOk() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HeaderExtractorTests.GOOD_HEADER, ((HeaderExtractorTests.GOOD_PREFIX) + (TestsConstants.VALUE)));
        final TokenCredentials credentials = HeaderExtractorTests.extractor.extract(context).get();
        Assert.assertEquals(TestsConstants.VALUE, credentials.getToken());
    }

    @Test
    public void testBadHeader() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HeaderExtractorTests.BAD_HEADER, ((HeaderExtractorTests.GOOD_PREFIX) + (TestsConstants.VALUE)));
        final Optional<TokenCredentials> credentials = HeaderExtractorTests.extractor.extract(context);
        Assert.assertFalse(credentials.isPresent());
    }

    @Test
    public void testBadPrefix() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HeaderExtractorTests.GOOD_HEADER, ((HeaderExtractorTests.BAD_PREFIX) + (TestsConstants.VALUE)));
        TestsHelper.expectException(() -> HeaderExtractorTests.extractor.extract(context), CredentialsException.class, ("Wrong prefix for header: " + (HeaderExtractorTests.GOOD_HEADER)));
    }
}

