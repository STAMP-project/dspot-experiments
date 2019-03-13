package org.pac4j.oidc.profile;


import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.jwt.profile.JwtGenerator;


/**
 * General test cases for {@link OidcProfile}.
 *
 * @author Jacob Severson
 * @author Misagh Moayyed
 * @author Juan Jos? V?zquez
 * @since 1.8.0
 */
public final class OidcProfileTests implements TestsConstants {
    private static final JavaSerializationHelper serializer = new JavaSerializationHelper();

    public static final String ID_TOKEN = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbX" + ("BsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2" + "MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.");

    private static final String REFRESH_TOKEN = "13/FuJRLB-4xn_4rd9iJPAUL0-gApRRtpDYuXH5ub5uW5Ne0-" + "oSohI6jUTnlb1cYPMIHq0Ne63h8HdZjAidLFlgNg==";

    private BearerAccessToken populatedAccessToken;

    @Test
    public void testClearProfile() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(new BearerAccessToken());
        profile.setIdTokenString(ID);
        profile.setRefreshToken(new RefreshToken(OidcProfileTests.REFRESH_TOKEN));
        profile.clearSensitiveData();
        Assert.assertNull(profile.getAccessToken());
        Assert.assertNotNull(profile.getIdTokenString());
    }

    @Test
    public void testReadWriteObject() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(OidcProfileTests.ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(OidcProfileTests.REFRESH_TOKEN));
        byte[] result = OidcProfileTests.serializer.serializeToBytes(profile);
        profile = ((OidcProfile) (OidcProfileTests.serializer.deserializeFromBytes(result)));
        Assert.assertNotNull("accessToken", profile.getAccessToken());
        Assert.assertNotNull("value", profile.getAccessToken().getValue());
        Assert.assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        Assert.assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        Assert.assertEquals(profile.getIdTokenString(), OidcProfileTests.ID_TOKEN);
        Assert.assertEquals(profile.getRefreshToken().getValue(), OidcProfileTests.REFRESH_TOKEN);
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the BearerAccessToken is null.
     */
    @Test
    public void testReadWriteObjectNullAccessToken() {
        OidcProfile profile = new OidcProfile();
        profile.setIdTokenString(OidcProfileTests.ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(OidcProfileTests.REFRESH_TOKEN));
        byte[] result = OidcProfileTests.serializer.serializeToBytes(profile);
        profile = ((OidcProfile) (OidcProfileTests.serializer.deserializeFromBytes(result)));
        Assert.assertNull(profile.getAccessToken());
        Assert.assertEquals(profile.getIdTokenString(), OidcProfileTests.ID_TOKEN);
        Assert.assertEquals(profile.getRefreshToken().getValue(), OidcProfileTests.REFRESH_TOKEN);
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the Id token is null.
     */
    @Test
    public void testReadWriteObjectNullIdToken() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setRefreshToken(new RefreshToken(OidcProfileTests.REFRESH_TOKEN));
        byte[] result = OidcProfileTests.serializer.serializeToBytes(profile);
        profile = ((OidcProfile) (OidcProfileTests.serializer.deserializeFromBytes(result)));
        Assert.assertNotNull("accessToken", profile.getAccessToken());
        Assert.assertNotNull("value", profile.getAccessToken().getValue());
        Assert.assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        Assert.assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        Assert.assertEquals(profile.getRefreshToken().getValue(), OidcProfileTests.REFRESH_TOKEN);
        Assert.assertNull(profile.getIdTokenString());
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the Refresh token is null.
     */
    @Test
    public void testReadWriteObjectNullRefreshToken() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(OidcProfileTests.ID_TOKEN);
        byte[] result = OidcProfileTests.serializer.serializeToBytes(profile);
        profile = ((OidcProfile) (OidcProfileTests.serializer.deserializeFromBytes(result)));
        Assert.assertNotNull("accessToken", profile.getAccessToken());
        Assert.assertNotNull("value", profile.getAccessToken().getValue());
        Assert.assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        Assert.assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        Assert.assertEquals(profile.getIdTokenString(), OidcProfileTests.ID_TOKEN);
        Assert.assertNull(profile.getRefreshToken());
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when tokens are null, after a call
     * to clearSensitiveData().
     */
    @Test
    public void testReadWriteObjectNullTokens() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.clearSensitiveData();
        byte[] result = OidcProfileTests.serializer.serializeToBytes(profile);
        profile = ((OidcProfile) (OidcProfileTests.serializer.deserializeFromBytes(result)));
        Assert.assertNull(profile.getAccessToken());
        Assert.assertNull(profile.getIdTokenString());
        Assert.assertNull(profile.getRefreshToken());
    }

    /**
     * Default behavior. No expiration info.
     */
    @Test
    public void testNullTokenExpiration() {
        OidcProfile profile = new OidcProfile();
        Assert.assertFalse(profile.isExpired());
    }

    /**
     * If the token is not expired, then the session is not considered expired.
     */
    @Test
    public void testNoExpirationWithNoExpiredToken() {
        final JwtGenerator jwtGenerator = new JwtGenerator<OidcProfile>();
        final ZonedDateTime expAfter = LocalDateTime.from(LocalDateTime.now()).plusHours(1).atZone(ZoneId.systemDefault());
        jwtGenerator.setExpirationTime(Date.from(expAfter.toInstant()));
        final OidcProfile profile = new OidcProfile();
        final String idTokenString = jwtGenerator.generate(profile);
        profile.setIdTokenString(idTokenString);
        profile.setTokenExpirationAdvance(0);
        Assert.assertFalse(profile.isExpired());
    }

    /**
     * If the token is expired, then the session is considered expired.
     */
    @Test
    public void testExpirationWithExpiredToken() {
        final JwtGenerator jwtGenerator = new JwtGenerator<OidcProfile>();
        final ZonedDateTime expBefore = LocalDateTime.from(LocalDateTime.now()).plusHours((-1)).atZone(ZoneId.systemDefault());
        jwtGenerator.setExpirationTime(Date.from(expBefore.toInstant()));
        final OidcProfile profile = new OidcProfile();
        final String idTokenString = jwtGenerator.generate(profile);
        profile.setIdTokenString(idTokenString);
        profile.setTokenExpirationAdvance(0);
        Assert.assertTrue(profile.isExpired());
    }

    /**
     * The token is not expired but the session will be consider expired if
     * a long enough token expiration advance is established.
     */
    @Test
    public void testAdvancedExpirationWithNoExpiredToken() {
        final JwtGenerator jwtGenerator = new JwtGenerator<OidcProfile>();
        final ZonedDateTime expAfter = LocalDateTime.from(LocalDateTime.now()).plusHours(1).atZone(ZoneId.systemDefault());
        jwtGenerator.setExpirationTime(Date.from(expAfter.toInstant()));
        final OidcProfile profile = new OidcProfile();
        final String idTokenString = jwtGenerator.generate(profile);
        profile.setIdTokenString(idTokenString);
        profile.setTokenExpirationAdvance(3600);// 1 hour

        Assert.assertTrue(profile.isExpired());
    }
}

