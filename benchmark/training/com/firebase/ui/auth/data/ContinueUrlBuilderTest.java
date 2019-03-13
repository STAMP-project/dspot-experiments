package com.firebase.ui.auth.data;


import com.firebase.ui.auth.util.data.ContinueUrlBuilder;
import com.firebase.ui.auth.util.data.EmailLinkParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link ContinueUrlBuilder}.
 */
@RunWith(RobolectricTestRunner.class)
public class ContinueUrlBuilderTest {
    private static final String ENCODED_EMAIL_LINK = "https://www.fake.com?link=https://fake.firebaseapp.com/__/auth/action?" + "%26mode%3DsignIn%26continueUrl%3Dhttps://google.com";

    private static final String DECODED_EMAIL_LINK = "https://fake.com/__/auth/action?apiKey=apiKey" + "&mode=signIn&continueUrl=https://google.com";

    private static final String SESSION_ID = "sessionId";

    private static final String ANONYMOUS_USER_ID = "anonymousUserId";

    private static final String PROVIDER_ID = "providerId";

    private static final boolean FORCE_SAME_DEVICE = true;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullUrl_expectsThrows() {
        /* url= */
        new ContinueUrlBuilder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_emptyUrl_expectsThrows() {
        /* url= */
        new ContinueUrlBuilder(null);
    }

    @Test
    public void testAppendParams_encodedLink_expectSuccess() {
        String continueUrlBuilder = new ContinueUrlBuilder(ContinueUrlBuilderTest.ENCODED_EMAIL_LINK).appendSessionId(ContinueUrlBuilderTest.SESSION_ID).appendAnonymousUserId(ContinueUrlBuilderTest.ANONYMOUS_USER_ID).appendProviderId(ContinueUrlBuilderTest.PROVIDER_ID).appendForceSameDeviceBit(ContinueUrlBuilderTest.FORCE_SAME_DEVICE).build();
        EmailLinkParser parser = new EmailLinkParser(continueUrlBuilder);
        assertThat(parser.getSessionId()).isEqualTo(ContinueUrlBuilderTest.SESSION_ID);
        assertThat(parser.getAnonymousUserId()).isEqualTo(ContinueUrlBuilderTest.ANONYMOUS_USER_ID);
        assertThat(parser.getProviderId()).isEqualTo(ContinueUrlBuilderTest.PROVIDER_ID);
        assertThat(parser.getForceSameDeviceBit()).isEqualTo(ContinueUrlBuilderTest.FORCE_SAME_DEVICE);
    }

    @Test
    public void testAppendParams_decodedLink_expectSuccess() {
        String continueUrlBuilder = new ContinueUrlBuilder(ContinueUrlBuilderTest.DECODED_EMAIL_LINK).appendSessionId(ContinueUrlBuilderTest.SESSION_ID).appendAnonymousUserId(ContinueUrlBuilderTest.ANONYMOUS_USER_ID).appendProviderId(ContinueUrlBuilderTest.PROVIDER_ID).appendForceSameDeviceBit(ContinueUrlBuilderTest.FORCE_SAME_DEVICE).build();
        EmailLinkParser parser = new EmailLinkParser(continueUrlBuilder);
        assertThat(parser.getSessionId()).isEqualTo(ContinueUrlBuilderTest.SESSION_ID);
        assertThat(parser.getAnonymousUserId()).isEqualTo(ContinueUrlBuilderTest.ANONYMOUS_USER_ID);
        assertThat(parser.getProviderId()).isEqualTo(ContinueUrlBuilderTest.PROVIDER_ID);
        assertThat(parser.getForceSameDeviceBit()).isEqualTo(ContinueUrlBuilderTest.FORCE_SAME_DEVICE);
    }

    @Test
    public void testAppendParams_nullValues_expectNoParamsAdded() {
        String continueUrl = new ContinueUrlBuilder(ContinueUrlBuilderTest.ENCODED_EMAIL_LINK).appendSessionId(null).appendAnonymousUserId(null).appendProviderId(null).build();
        assertThat(continueUrl).isEqualTo(ContinueUrlBuilderTest.ENCODED_EMAIL_LINK);
    }
}

