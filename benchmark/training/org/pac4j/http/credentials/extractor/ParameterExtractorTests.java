package org.pac4j.http.credentials.extractor;


import HTTP_METHOD.GET;
import HTTP_METHOD.POST;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * This class tests the {@link ParameterExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ParameterExtractorTests implements TestsConstants {
    private static final String GOOD_PARAMETER = "goodParameter";

    private static final ParameterExtractor getExtractor = new ParameterExtractor(ParameterExtractorTests.GOOD_PARAMETER, true, false);

    private static final ParameterExtractor postExtractor = new ParameterExtractor(ParameterExtractorTests.GOOD_PARAMETER, false, true);

    @Test
    public void testRetrieveGetParameterOk() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(GET.name()).addRequestParameter(ParameterExtractorTests.GOOD_PARAMETER, VALUE);
        final TokenCredentials credentials = ParameterExtractorTests.getExtractor.extract(context).get();
        Assert.assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterOk() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name()).addRequestParameter(ParameterExtractorTests.GOOD_PARAMETER, VALUE);
        final TokenCredentials credentials = ParameterExtractorTests.postExtractor.extract(context).get();
        Assert.assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterNotSupported() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name()).addRequestParameter(ParameterExtractorTests.GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> getExtractor.extract(context), CredentialsException.class, "POST requests not supported");
    }

    @Test
    public void testRetrieveGetParameterNotSupported() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(GET.name()).addRequestParameter(ParameterExtractorTests.GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> postExtractor.extract(context), CredentialsException.class, "GET requests not supported");
    }

    @Test
    public void testRetrieveNoGetParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(GET.name());
        final Optional<TokenCredentials> credentials = ParameterExtractorTests.getExtractor.extract(context);
        Assert.assertFalse(credentials.isPresent());
    }

    @Test
    public void testRetrieveNoPostParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        final Optional<TokenCredentials> credentials = ParameterExtractorTests.postExtractor.extract(context);
        Assert.assertFalse(credentials.isPresent());
    }
}

