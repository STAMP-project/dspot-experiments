package org.pac4j.jwt.config.signature;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.ECPrivateKey;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.AbstractKeyEncryptionConfigurationTests;
import org.pac4j.jwt.util.JWKHelper;


/**
 * Tests {@link ECSignatureConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class ECSignatureConfigurationTests extends AbstractKeyEncryptionConfigurationTests {
    @Test
    public void testMissingPrivateKey() {
        final ECSignatureConfiguration config = new ECSignatureConfiguration();
        TestsHelper.expectException(() -> config.sign(buildClaims()), TechnicalException.class, "privateKey cannot be null");
    }

    @Test
    public void testMissingPublicKey() {
        final ECSignatureConfiguration config = new ECSignatureConfiguration();
        config.setPrivateKey(((ECPrivateKey) (buildKeyPair().getPrivate())));
        final SignedJWT signedJWT = config.sign(buildClaims());
        TestsHelper.expectException(() -> config.verify(signedJWT), TechnicalException.class, "publicKey cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        final ECSignatureConfiguration config = new ECSignatureConfiguration(buildKeyPair(), null);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testBadAlgorithm() {
        final ECSignatureConfiguration config = new ECSignatureConfiguration(buildKeyPair(), JWSAlgorithm.HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "Only the ES256, ES384 and ES512 algorithms are supported for elliptic curve signature");
    }

    @Test
    public void buildFromJwk() {
        final String json = build().toJSONObject().toJSONString();
        JWKHelper.buildECKeyPairFromJwk(json);
    }

    @Test
    public void testSignVerify() throws JOSEException {
        final ECSignatureConfiguration config = new ECSignatureConfiguration(buildKeyPair());
        final JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(VALUE).build();
        final SignedJWT signedJwt = config.sign(claims);
        Assert.assertTrue(config.verify(signedJwt));
    }
}

