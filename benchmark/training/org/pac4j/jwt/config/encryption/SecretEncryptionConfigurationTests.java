package org.pac4j.jwt.config.encryption;


import EncryptionMethod.A128GCM;
import JWEAlgorithm.A256GCMKW;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;


/**
 * Tests {@link SecretEncryptionConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class SecretEncryptionConfigurationTests implements TestsConstants {
    @Test
    public void testMissingSecret() {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration();
        TestsHelper.expectException(config::init, TechnicalException.class, "secret cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration(SECRET, null, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testMissingMethod() {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.DIR, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testUnsupportedAlgorithm() {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.ECDH_ES, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "Only the direct and AES algorithms are supported with the appropriate encryption method");
    }

    @Test
    public void testEncryptDecryptSignedJWT() throws JOSEException, ParseException {
        final SecretSignatureConfiguration macConfig = new SecretSignatureConfiguration(MAC_SECRET);
        final SignedJWT signedJWT = macConfig.sign(buildClaims());
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration(MAC_SECRET);
        final String token = config.encrypt(signedJWT);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final SignedJWT signedJWT2 = encryptedJwt.getPayload().toSignedJWT();
        Assert.assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWT() throws JOSEException, ParseException {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration(MAC_SECRET);
        config.setAlgorithm(A256GCMKW);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        Assert.assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWTBase64Secret() throws JOSEException, ParseException {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration();
        config.setSecretBase64(BASE64_256_BIT_ENC_SECRET);
        config.setAlgorithm(A256GCMKW);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        Assert.assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWTBytesSecret() throws JOSEException, ParseException {
        final SecretEncryptionConfiguration config = new SecretEncryptionConfiguration();
        config.setSecretBytes(decode());
        config.setAlgorithm(A256GCMKW);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        Assert.assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }
}

