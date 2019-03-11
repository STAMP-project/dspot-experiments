package com.firebase.ui.auth.data;


import com.firebase.ui.auth.util.data.EmailLinkParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link EmailLinkParser}.
 */
@RunWith(RobolectricTestRunner.class)
public class EmailLinkParserTest {
    private static final String SESSION_ID = "sessionId";

    private static final String ANONYMOUS_USER_ID = "anonymousUserId";

    private static final String PROVIDER_ID = "providerId";

    private static final boolean FORCE_SAME_DEVICE = true;

    private static final String CONTINUE_URL = ((((((("https://google.com" + "?ui_sid=") + (EmailLinkParserTest.SESSION_ID)) + "&ui_auid=") + (EmailLinkParserTest.ANONYMOUS_USER_ID)) + "&ui_pid=") + (EmailLinkParserTest.PROVIDER_ID)) + "&ui_sd=") + (EmailLinkParserTest.FORCE_SAME_DEVICE ? "1" : "0");

    private static final String OOB_CODE = "anOobCode";

    private static final String ENCODED_EMAIL_LINK = ((("https://www.fake.com?link=https://fake.firebaseapp.com/__/auth/action?" + "%26mode%3DsignIn%26oobCode%3D") + (EmailLinkParserTest.OOB_CODE)) + "%26continueUrl%3D") + (EmailLinkParserTest.CONTINUE_URL);

    private static final String DECODED_EMAIL_LINK = ((("https://fake.com/__/auth/action?apiKey=apiKey&mode=signIn" + "&oobCode=") + (EmailLinkParserTest.OOB_CODE)) + "&continueUrl=") + (EmailLinkParserTest.CONTINUE_URL);

    private static final String MALFORMED_LINK = "not_a_hierarchical_link:";

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_validStringWithNoParams_expectThrows() {
        new EmailLinkParser(EmailLinkParserTest.MALFORMED_LINK);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullLink_expectThrows() {
        /* link= */
        new EmailLinkParser(null);
    }

    @Test
    public void testGetters_encodedLink() {
        EmailLinkParser parser = new EmailLinkParser(EmailLinkParserTest.ENCODED_EMAIL_LINK);
        assertThat(parser.getOobCode()).isEqualTo(EmailLinkParserTest.OOB_CODE);
        assertThat(parser.getSessionId()).isEqualTo(EmailLinkParserTest.SESSION_ID);
        assertThat(parser.getAnonymousUserId()).isEqualTo(EmailLinkParserTest.ANONYMOUS_USER_ID);
        assertThat(parser.getProviderId()).isEqualTo(EmailLinkParserTest.PROVIDER_ID);
        assertThat(parser.getForceSameDeviceBit()).isEqualTo(EmailLinkParserTest.FORCE_SAME_DEVICE);
    }

    @Test
    public void testGetters_decodedLink() {
        EmailLinkParser parser = new EmailLinkParser(EmailLinkParserTest.DECODED_EMAIL_LINK);
        assertThat(parser.getOobCode()).isEqualTo(EmailLinkParserTest.OOB_CODE);
        assertThat(parser.getSessionId()).isEqualTo(EmailLinkParserTest.SESSION_ID);
        assertThat(parser.getAnonymousUserId()).isEqualTo(EmailLinkParserTest.ANONYMOUS_USER_ID);
        assertThat(parser.getProviderId()).isEqualTo(EmailLinkParserTest.PROVIDER_ID);
        assertThat(parser.getForceSameDeviceBit()).isEqualTo(EmailLinkParserTest.FORCE_SAME_DEVICE);
    }

    @Test
    public void testGetters_noContinueUrlParams() {
        String encodedLink = EmailLinkParserTest.ENCODED_EMAIL_LINK.substring(0, ((EmailLinkParserTest.ENCODED_EMAIL_LINK.length()) - (EmailLinkParserTest.CONTINUE_URL.length())));
        EmailLinkParser parser = new EmailLinkParser(encodedLink);
        assertThat(parser.getOobCode()).isEqualTo(EmailLinkParserTest.OOB_CODE);
        assertThat(parser.getSessionId()).isNull();
        assertThat(parser.getAnonymousUserId()).isNull();
        assertThat(parser.getProviderId()).isNull();
        assertThat(parser.getForceSameDeviceBit()).isFalse();
    }
}

