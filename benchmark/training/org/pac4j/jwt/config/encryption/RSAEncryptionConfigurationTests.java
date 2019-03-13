package org.pac4j.jwt.config.encryption;


import EncryptionMethod.A128GCM;
import EncryptionMethod.A192CBC_HS384;
import JWEAlgorithm.RSA1_5;
import JWEAlgorithm.RSA_OAEP;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.AbstractKeyEncryptionConfigurationTests;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;


/**
 * Tests {@link RSAEncryptionConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class RSAEncryptionConfigurationTests extends AbstractKeyEncryptionConfigurationTests {
    @Test
    public void testMissingAlgorithm() {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair(), null, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testMissingMethod() {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair(), JWEAlgorithm.RSA1_5, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testUnsupportedAlgorithm() {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair(), JWEAlgorithm.ECDH_ES, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "Only RSA algorithms are supported with the appropriate encryption method");
    }

    @Test
    public void testEncryptDecryptSignedJWT() throws JOSEException, ParseException {
        final SecretSignatureConfiguration macConfig = new SecretSignatureConfiguration(MAC_SECRET);
        final SignedJWT signedJWT = macConfig.sign(buildClaims());
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(RSA1_5);
        config.setMethod(A192CBC_HS384);
        final String token = config.encrypt(signedJWT);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final SignedJWT signedJWT2 = encryptedJwt.getPayload().toSignedJWT();
        Assert.assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWT() throws JOSEException, ParseException {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(RSA_OAEP);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        Assert.assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptMissingKey() {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration();
        config.setAlgorithm(RSA_OAEP);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        TestsHelper.expectException(() -> config.encrypt(jwt), TechnicalException.class, "publicKey cannot be null");
    }

    @Test
    public void testDecryptMissingKey() throws ParseException {
        final RSAEncryptionConfiguration config = new RSAEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(RSA_OAEP);
        config.setMethod(A128GCM);
        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = ((EncryptedJWT) (JWTParser.parse(token)));
        final RSAEncryptionConfiguration config2 = new RSAEncryptionConfiguration();
        config2.setAlgorithm(RSA_OAEP);
        config2.setMethod(A128GCM);
        TestsHelper.expectException(() -> config2.decrypt(encryptedJwt), TechnicalException.class, "privateKey cannot be null");
    }
}

