package org.pac4j.oidc.credentials;


import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.Scope;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link OidcCredentials}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class OidcCredentialsTests implements TestsConstants {
    private static final JavaSerializationHelper serializer = new JavaSerializationHelper();

    private static final String ID_TOKEN = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbX" + ("BsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2" + "MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.");

    @Test
    public void testSerialization() throws ParseException {
        final OidcCredentials credentials = new OidcCredentials();
        credentials.setCode(new com.nimbusds.oauth2.sdk.AuthorizationCode(VALUE));
        credentials.setAccessToken(new com.nimbusds.oauth2.sdk.token.BearerAccessToken(VALUE, 0L, Scope.parse("oidc email")));
        credentials.setRefreshToken(new com.nimbusds.oauth2.sdk.token.RefreshToken(VALUE));
        credentials.setIdToken(JWTParser.parse(OidcCredentialsTests.ID_TOKEN));
        byte[] result = OidcCredentialsTests.serializer.serializeToBytes(credentials);
        final OidcCredentials credentials2 = ((OidcCredentials) (OidcCredentialsTests.serializer.deserializeFromBytes(result)));
        Assert.assertEquals(credentials.getAccessToken(), credentials2.getAccessToken());
        Assert.assertEquals(credentials.getRefreshToken(), credentials2.getRefreshToken());
        Assert.assertEquals(credentials.getIdToken().getParsedString(), credentials2.getIdToken().getParsedString());
    }
}

