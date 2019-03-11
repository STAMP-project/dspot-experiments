/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.integration.awskms;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.subtle.Random;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for AwsKmsAead.
 */
@RunWith(MockitoJUnitRunner.class)
public class AwsKmsAeadTest {
    private static final String KEY_ID = "123";

    @Mock
    private AWSKMS mockKms;

    @Test
    public void testEncryptDecrypt() throws Exception {
        DecryptResult mockDecryptResult = Mockito.mock(DecryptResult.class);
        EncryptResult mockEncryptResult = Mockito.mock(EncryptResult.class);
        Mockito.when(mockKms.decrypt(ArgumentMatchers.isA(DecryptRequest.class))).thenReturn(mockDecryptResult);
        Mockito.when(mockKms.encrypt(ArgumentMatchers.isA(EncryptRequest.class))).thenReturn(mockEncryptResult);
        Aead aead = new AwsKmsAead(mockKms, AwsKmsAeadTest.KEY_ID);
        byte[] aad = Random.randBytes(20);
        for (int messageSize = 0; messageSize < 75; messageSize++) {
            byte[] message = Random.randBytes(messageSize);
            Mockito.when(mockDecryptResult.getKeyId()).thenReturn(AwsKmsAeadTest.KEY_ID);
            Mockito.when(mockDecryptResult.getPlaintext()).thenReturn(ByteBuffer.wrap(message));
            Mockito.when(mockEncryptResult.getCiphertextBlob()).thenReturn(ByteBuffer.wrap(message));
            byte[] ciphertext = aead.encrypt(message, aad);
            byte[] decrypted = aead.decrypt(ciphertext, aad);
            Assert.assertArrayEquals(message, decrypted);
        }
    }

    @Test
    public void testEncryptShouldThrowExceptionIfRequestFailed() throws Exception {
        AmazonServiceException exception = Mockito.mock(AmazonServiceException.class);
        Mockito.when(mockKms.encrypt(ArgumentMatchers.isA(EncryptRequest.class))).thenThrow(exception);
        Aead aead = new AwsKmsAead(mockKms, AwsKmsAeadTest.KEY_ID);
        byte[] aad = Random.randBytes(20);
        byte[] message = Random.randBytes(20);
        try {
            aead.encrypt(message, aad);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            // expected.
        }
    }

    @Test
    public void testDecryptShouldThrowExceptionIfRequestFailed() throws Exception {
        EncryptResult mockEncryptResult = Mockito.mock(EncryptResult.class);
        Mockito.when(mockKms.encrypt(ArgumentMatchers.isA(EncryptRequest.class))).thenReturn(mockEncryptResult);
        AmazonServiceException exception = Mockito.mock(AmazonServiceException.class);
        Mockito.when(mockKms.decrypt(ArgumentMatchers.isA(DecryptRequest.class))).thenThrow(exception);
        Aead aead = new AwsKmsAead(mockKms, AwsKmsAeadTest.KEY_ID);
        byte[] aad = Random.randBytes(20);
        byte[] message = Random.randBytes(20);
        Mockito.when(mockEncryptResult.getCiphertextBlob()).thenReturn(ByteBuffer.wrap(message));
        byte[] ciphertext = aead.encrypt(message, aad);
        try {
            aead.decrypt(ciphertext, aad);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            // expected.
        }
    }

    @Test
    public void testDecryptShouldThrowExceptionIfKeyIdIsDifferent() throws Exception {
        DecryptResult mockDecryptResult = Mockito.mock(DecryptResult.class);
        EncryptResult mockEncryptResult = Mockito.mock(EncryptResult.class);
        Mockito.when(mockKms.decrypt(ArgumentMatchers.isA(DecryptRequest.class))).thenReturn(mockDecryptResult);
        Mockito.when(mockKms.encrypt(ArgumentMatchers.isA(EncryptRequest.class))).thenReturn(mockEncryptResult);
        Aead aead = new AwsKmsAead(mockKms, AwsKmsAeadTest.KEY_ID);
        byte[] aad = Random.randBytes(20);
        byte[] message = Random.randBytes(20);
        Mockito.when(mockEncryptResult.getCiphertextBlob()).thenReturn(ByteBuffer.wrap(message));
        Mockito.when(mockDecryptResult.getKeyId()).thenReturn(((AwsKmsAeadTest.KEY_ID) + "1"));
        byte[] ciphertext = aead.encrypt(message, aad);
        try {
            aead.decrypt(ciphertext, aad);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            // expected.
        }
    }
}

