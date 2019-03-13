package org.pac4j.http.client.direct;


import HttpConstants.HTTP_METHOD.GET;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;


/**
 * This class tests the {@link ParameterClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ParameterClientTests implements TestsConstants {
    private static final String PARAMETER_NAME = "parameterName";

    private static final boolean SUPPORT_GET = true;

    private static final boolean SUPPORT_POST = false;

    @Test
    public void testMissingTokenAuthenticator() {
        final ParameterClient client = new ParameterClient(ParameterClientTests.PARAMETER_NAME, null);
        TestsHelper.expectException(() -> client.getCredentials(MockWebContext.create()), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final ParameterClient client = new ParameterClient(ParameterClientTests.PARAMETER_NAME, new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> client.getUserProfile(new <TOKEN>TokenCredentials(), MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final ParameterClient client = new ParameterClient(null, new SimpleTestTokenAuthenticator());
        client.setParameterName(ParameterClientTests.PARAMETER_NAME);
        client.init();
    }

    @Test
    public void testMissingParameterName() {
        final ParameterClient client = new ParameterClient(null, new SimpleTestTokenAuthenticator());
        TestsHelper.initShouldFail(client, "parameterName cannot be blank");
    }

    @Test
    public void testAuthentication() {
        final ParameterClient client = new ParameterClient(ParameterClientTests.PARAMETER_NAME, new SimpleTestTokenAuthenticator());
        client.setSupportGetRequest(ParameterClientTests.SUPPORT_GET);
        client.setSupportPostRequest(ParameterClientTests.SUPPORT_POST);
        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(ParameterClientTests.PARAMETER_NAME, VALUE);
        context.setRequestMethod(GET.name());
        final TokenCredentials credentials = client.getCredentials(context).get();
        final CommonProfile profile = ((CommonProfile) (client.getUserProfile(credentials, context).get()));
        Assert.assertEquals(VALUE, profile.getId());
    }
}

