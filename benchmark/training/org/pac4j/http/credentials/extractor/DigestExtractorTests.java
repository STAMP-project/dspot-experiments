package org.pac4j.http.credentials.extractor;


import HttpConstants.AUTHORIZATION_HEADER;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.DigestCredentials;


/**
 * This class tests the {@link DigestAuthExtractor}
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DigestExtractorTests implements TestsConstants {
    private static final DigestAuthExtractor digestExtractor = new DigestAuthExtractor();

    @Test
    public void testRetrieveDigestHeaderComponents() {
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(AUTHORIZATION_HEADER, DIGEST_AUTHORIZATION_HEADER_VALUE);
        final DigestCredentials credentials = DigestExtractorTests.digestExtractor.extract(context).get();
        Assert.assertEquals(DIGEST_RESPONSE, credentials.getToken());
        Assert.assertEquals(USERNAME, credentials.getUsername());
    }

    @Test
    public void testNotDigest() {
        final MockWebContext context = MockWebContext.create();
        final Optional<DigestCredentials> credentials = DigestExtractorTests.digestExtractor.extract(context);
        Assert.assertFalse(credentials.isPresent());
    }
}

