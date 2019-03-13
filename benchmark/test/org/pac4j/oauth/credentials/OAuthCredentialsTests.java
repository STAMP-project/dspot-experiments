package org.pac4j.oauth.credentials;


import com.github.scribejava.core.model.OAuth1RequestToken;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;


/**
 * This class tests the {@link OAuthCredentials} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthCredentialsTests implements TestsConstants {
    private static final OAuth1RequestToken REQUEST_TOKEN = new OAuth1RequestToken(TOKEN, SECRET);

    @Test
    public void testOAuth10Credentials() {
        final OAuth10Credentials credentials = new OAuth10Credentials(OAuthCredentialsTests.REQUEST_TOKEN, TOKEN, VERIFIER);
        Assert.assertEquals(TOKEN, credentials.getToken());
        Assert.assertEquals(VERIFIER, credentials.getVerifier());
        final OAuth1RequestToken requestToken = credentials.getRequestToken();
        Assert.assertEquals(TOKEN, requestToken.getToken());
        Assert.assertEquals(SECRET, requestToken.getTokenSecret());
        // test serialization
        final JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();
        final byte[] bytes = javaSerializationHelper.serializeToBytes(credentials);
        final OAuth10Credentials credentials2 = ((OAuth10Credentials) (javaSerializationHelper.deserializeFromBytes(bytes)));
        Assert.assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        Assert.assertEquals(credentials.getToken(), credentials2.getToken());
        Assert.assertEquals(credentials.getVerifier(), credentials2.getVerifier());
    }
}

