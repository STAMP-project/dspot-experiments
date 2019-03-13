package com.auth0.jwt.algorithms;


import com.auth0.jwt.interfaces.ECDSAKeyProvider;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AlgorithmTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowHMAC256InstanceWithNullSecretBytes() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        byte[] secret = null;
        Algorithm.HMAC256(secret);
    }

    @Test
    public void shouldThrowHMAC384InstanceWithNullSecretBytes() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        byte[] secret = null;
        Algorithm.HMAC384(secret);
    }

    @Test
    public void shouldThrowHMAC512InstanceWithNullSecretBytes() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        byte[] secret = null;
        Algorithm.HMAC512(secret);
    }

    @Test
    public void shouldThrowHMAC256InstanceWithNullSecret() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        String secret = null;
        Algorithm.HMAC256(secret);
    }

    @Test
    public void shouldThrowHMAC384InstanceWithNullSecret() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        String secret = null;
        Algorithm.HMAC384(secret);
    }

    @Test
    public void shouldThrowHMAC512InstanceWithNullSecret() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Secret cannot be null");
        String secret = null;
        Algorithm.HMAC512(secret);
    }

    @Test
    public void shouldThrowRSA256InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        RSAKey key = null;
        Algorithm.RSA256(key);
    }

    @Test
    public void shouldThrowRSA256InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.RSA256(null, null);
    }

    @Test
    public void shouldThrowRSA256InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        RSAKeyProvider provider = null;
        Algorithm.RSA256(provider);
    }

    @Test
    public void shouldThrowRSA384InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        RSAKey key = null;
        Algorithm.RSA384(key);
    }

    @Test
    public void shouldThrowRSA384InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.RSA384(null, null);
    }

    @Test
    public void shouldThrowRSA384InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        RSAKeyProvider provider = null;
        Algorithm.RSA384(provider);
    }

    @Test
    public void shouldThrowRSA512InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        RSAKey key = null;
        Algorithm.RSA512(key);
    }

    @Test
    public void shouldThrowRSA512InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.RSA512(null, null);
    }

    @Test
    public void shouldThrowRSA512InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        RSAKeyProvider provider = null;
        Algorithm.RSA512(provider);
    }

    @Test
    public void shouldThrowECDSA256InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        ECKey key = null;
        Algorithm.ECDSA256(key);
    }

    @Test
    public void shouldThrowECDSA256InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.ECDSA256(null, null);
    }

    @Test
    public void shouldThrowECDSA256InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        ECDSAKeyProvider provider = null;
        Algorithm.ECDSA256(provider);
    }

    @Test
    public void shouldThrowECDSA384InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        ECKey key = null;
        Algorithm.ECDSA384(key);
    }

    @Test
    public void shouldThrowECDSA384InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.ECDSA384(null, null);
    }

    @Test
    public void shouldThrowECDSA384InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        ECDSAKeyProvider provider = null;
        Algorithm.ECDSA384(provider);
    }

    @Test
    public void shouldThrowECDSA512InstanceWithNullKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        ECKey key = null;
        Algorithm.ECDSA512(key);
    }

    @Test
    public void shouldThrowECDSA512InstanceWithNullKeys() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Both provided Keys cannot be null.");
        Algorithm.ECDSA512(null, null);
    }

    @Test
    public void shouldThrowECDSA512InstanceWithNullKeyProvider() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Key Provider cannot be null.");
        ECDSAKeyProvider provider = null;
        Algorithm.ECDSA512(provider);
    }

    @Test
    public void shouldCreateHMAC256AlgorithmWithBytes() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA256"));
        Assert.assertThat(algorithm.getName(), is("HS256"));
    }

    @Test
    public void shouldCreateHMAC384AlgorithmWithBytes() throws Exception {
        Algorithm algorithm = Algorithm.HMAC384("secret".getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA384"));
        Assert.assertThat(algorithm.getName(), is("HS384"));
    }

    @Test
    public void shouldCreateHMAC512AlgorithmWithBytes() throws Exception {
        Algorithm algorithm = Algorithm.HMAC512("secret".getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA512"));
        Assert.assertThat(algorithm.getName(), is("HS512"));
    }

    @Test
    public void shouldCreateHMAC256AlgorithmWithString() throws Exception {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA256"));
        Assert.assertThat(algorithm.getName(), is("HS256"));
    }

    @Test
    public void shouldCreateHMAC384AlgorithmWithString() throws Exception {
        Algorithm algorithm = Algorithm.HMAC384("secret");
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA384"));
        Assert.assertThat(algorithm.getName(), is("HS384"));
    }

    @Test
    public void shouldCreateHMAC512AlgorithmWithString() throws Exception {
        Algorithm algorithm = Algorithm.HMAC512("secret");
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(HMACAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("HmacSHA512"));
        Assert.assertThat(algorithm.getName(), is("HS512"));
    }

    @Test
    public void shouldCreateRSA256AlgorithmWithPublicKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPublicKey.class));
        Algorithm algorithm = Algorithm.RSA256(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS256"));
    }

    @Test
    public void shouldCreateRSA256AlgorithmWithPrivateKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPrivateKey.class));
        Algorithm algorithm = Algorithm.RSA256(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS256"));
    }

    @Test
    public void shouldCreateRSA256AlgorithmWithBothKeys() throws Exception {
        RSAPublicKey publicKey = Mockito.mock(RSAPublicKey.class);
        RSAPrivateKey privateKey = Mockito.mock(RSAPrivateKey.class);
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS256"));
    }

    @Test
    public void shouldCreateRSA256AlgorithmWithProvider() throws Exception {
        RSAKeyProvider provider = Mockito.mock(RSAKeyProvider.class);
        Algorithm algorithm = Algorithm.RSA256(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS256"));
    }

    @Test
    public void shouldCreateRSA384AlgorithmWithPublicKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPublicKey.class));
        Algorithm algorithm = Algorithm.RSA384(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS384"));
    }

    @Test
    public void shouldCreateRSA384AlgorithmWithPrivateKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPrivateKey.class));
        Algorithm algorithm = Algorithm.RSA384(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS384"));
    }

    @Test
    public void shouldCreateRSA384AlgorithmWithBothKeys() throws Exception {
        RSAPublicKey publicKey = Mockito.mock(RSAPublicKey.class);
        RSAPrivateKey privateKey = Mockito.mock(RSAPrivateKey.class);
        Algorithm algorithm = Algorithm.RSA384(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS384"));
    }

    @Test
    public void shouldCreateRSA384AlgorithmWithProvider() throws Exception {
        RSAKeyProvider provider = Mockito.mock(RSAKeyProvider.class);
        Algorithm algorithm = Algorithm.RSA384(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS384"));
    }

    @Test
    public void shouldCreateRSA512AlgorithmWithPublicKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPublicKey.class));
        Algorithm algorithm = Algorithm.RSA512(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS512"));
    }

    @Test
    public void shouldCreateRSA512AlgorithmWithPrivateKey() throws Exception {
        RSAKey key = Mockito.mock(RSAKey.class, Mockito.withSettings().extraInterfaces(RSAPrivateKey.class));
        Algorithm algorithm = Algorithm.RSA512(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS512"));
    }

    @Test
    public void shouldCreateRSA512AlgorithmWithBothKeys() throws Exception {
        RSAPublicKey publicKey = Mockito.mock(RSAPublicKey.class);
        RSAPrivateKey privateKey = Mockito.mock(RSAPrivateKey.class);
        Algorithm algorithm = Algorithm.RSA512(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS512"));
    }

    @Test
    public void shouldCreateRSA512AlgorithmWithProvider() throws Exception {
        RSAKeyProvider provider = Mockito.mock(RSAKeyProvider.class);
        Algorithm algorithm = Algorithm.RSA512(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(RSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withRSA"));
        Assert.assertThat(algorithm.getName(), is("RS512"));
    }

    @Test
    public void shouldCreateECDSA256AlgorithmWithPublicKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPublicKey.class));
        Algorithm algorithm = Algorithm.ECDSA256(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES256"));
    }

    @Test
    public void shouldCreateECDSA256AlgorithmWithPrivateKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPrivateKey.class));
        Algorithm algorithm = Algorithm.ECDSA256(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES256"));
    }

    @Test
    public void shouldCreateECDSA256AlgorithmWithBothKeys() throws Exception {
        ECPublicKey publicKey = Mockito.mock(ECPublicKey.class);
        ECPrivateKey privateKey = Mockito.mock(ECPrivateKey.class);
        Algorithm algorithm = Algorithm.ECDSA256(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES256"));
    }

    @Test
    public void shouldCreateECDSA256AlgorithmWithProvider() throws Exception {
        ECDSAKeyProvider provider = Mockito.mock(ECDSAKeyProvider.class);
        Algorithm algorithm = Algorithm.ECDSA256(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA256withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES256"));
    }

    @Test
    public void shouldCreateECDSA384AlgorithmWithPublicKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPublicKey.class));
        Algorithm algorithm = Algorithm.ECDSA384(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES384"));
    }

    @Test
    public void shouldCreateECDSA384AlgorithmWithPrivateKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPrivateKey.class));
        Algorithm algorithm = Algorithm.ECDSA384(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES384"));
    }

    @Test
    public void shouldCreateECDSA384AlgorithmWithBothKeys() throws Exception {
        ECPublicKey publicKey = Mockito.mock(ECPublicKey.class);
        ECPrivateKey privateKey = Mockito.mock(ECPrivateKey.class);
        Algorithm algorithm = Algorithm.ECDSA384(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES384"));
    }

    @Test
    public void shouldCreateECDSA384AlgorithmWithProvider() throws Exception {
        ECDSAKeyProvider provider = Mockito.mock(ECDSAKeyProvider.class);
        Algorithm algorithm = Algorithm.ECDSA384(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA384withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES384"));
    }

    @Test
    public void shouldCreateECDSA512AlgorithmWithPublicKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPublicKey.class));
        Algorithm algorithm = Algorithm.ECDSA512(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES512"));
    }

    @Test
    public void shouldCreateECDSA512AlgorithmWithPrivateKey() throws Exception {
        ECKey key = Mockito.mock(ECKey.class, Mockito.withSettings().extraInterfaces(ECPrivateKey.class));
        Algorithm algorithm = Algorithm.ECDSA512(key);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES512"));
    }

    @Test
    public void shouldCreateECDSA512AlgorithmWithBothKeys() throws Exception {
        ECPublicKey publicKey = Mockito.mock(ECPublicKey.class);
        ECPrivateKey privateKey = Mockito.mock(ECPrivateKey.class);
        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES512"));
    }

    @Test
    public void shouldCreateECDSA512AlgorithmWithProvider() throws Exception {
        ECDSAKeyProvider provider = Mockito.mock(ECDSAKeyProvider.class);
        Algorithm algorithm = Algorithm.ECDSA512(provider);
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(ECDSAAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("SHA512withECDSA"));
        Assert.assertThat(algorithm.getName(), is("ES512"));
    }

    @Test
    public void shouldCreateNoneAlgorithm() throws Exception {
        Algorithm algorithm = Algorithm.none();
        Assert.assertThat(algorithm, is(notNullValue()));
        Assert.assertThat(algorithm, is(instanceOf(NoneAlgorithm.class)));
        Assert.assertThat(algorithm.getDescription(), is("none"));
        Assert.assertThat(algorithm.getName(), is("none"));
    }

    @Test
    public void shouldForwardHeaderPayloadSignatureToSiblingSignMethodForBackwardsCompatibility() throws Exception {
        Algorithm algorithm = Mockito.mock(Algorithm.class);
        ArgumentCaptor<byte[]> contentCaptor = ArgumentCaptor.forClass(byte[].class);
        byte[] header = new byte[]{ 0, 1, 2 };
        byte[] payload = new byte[]{ 4, 5, 6 };
        byte[] signature = new byte[]{ 16, 17, 18 };
        Mockito.when(algorithm.sign(ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class))).thenCallRealMethod();
        Mockito.when(algorithm.sign(contentCaptor.capture())).thenReturn(signature);
        byte[] sign = algorithm.sign(header, payload);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(header);
        bout.write('.');
        bout.write(payload);
        Assert.assertThat(sign, is(signature));
        Assert.assertThat(contentCaptor.getValue(), is(bout.toByteArray()));
    }
}

