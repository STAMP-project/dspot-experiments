/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.security.util.crypto;


import EncryptionMethod.PGP;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.codec.binary.Hex;
import org.apache.nifi.processor.io.StreamCallback;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpenPGPPasswordBasedEncryptorTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenPGPPasswordBasedEncryptorTest.class);

    private final File plainFile = new File("src/test/resources/TestEncryptContent/text.txt");

    private final File encryptedFile = new File("src/test/resources/TestEncryptContent/text.txt.asc");

    private static final String PASSWORD = "thisIsABadPassword";

    private static final String LEGACY_PASSWORD = "Hello, World!";

    @Test
    public void testShouldEncryptAndDecrypt() throws Exception {
        // Arrange
        final String PLAINTEXT = "This is a plaintext message.";
        OpenPGPPasswordBasedEncryptorTest.logger.info("Plaintext: {}", PLAINTEXT);
        InputStream plainStream = new ByteArrayInputStream(PLAINTEXT.getBytes("UTF-8"));
        OutputStream cipherStream = new ByteArrayOutputStream();
        OutputStream recoveredStream = new ByteArrayOutputStream();
        // No file, just streams
        String filename = "tempFile.txt";
        OpenPGPPasswordBasedEncryptor encryptor = new OpenPGPPasswordBasedEncryptor(PGP.getAlgorithm(), PGP.getProvider(), OpenPGPPasswordBasedEncryptorTest.PASSWORD.toCharArray(), filename);
        StreamCallback encryptionCallback = encryptor.getEncryptionCallback();
        StreamCallback decryptionCallback = encryptor.getDecryptionCallback();
        // Act
        encryptionCallback.process(plainStream, cipherStream);
        final byte[] cipherBytes = ((ByteArrayOutputStream) (cipherStream)).toByteArray();
        OpenPGPPasswordBasedEncryptorTest.logger.info("Encrypted: {}", Hex.encodeHexString(cipherBytes));
        InputStream cipherInputStream = new ByteArrayInputStream(cipherBytes);
        decryptionCallback.process(cipherInputStream, recoveredStream);
        // Assert
        byte[] recoveredBytes = ((ByteArrayOutputStream) (recoveredStream)).toByteArray();
        String recovered = new String(recoveredBytes, "UTF-8");
        OpenPGPPasswordBasedEncryptorTest.logger.info("Recovered: {}", recovered);
        assert PLAINTEXT.equals(recovered);
    }

    @Test
    public void testShouldDecryptExternalFile() throws Exception {
        // Arrange
        byte[] plainBytes = Files.readAllBytes(Paths.get(plainFile.getPath()));
        final String PLAINTEXT = new String(plainBytes, "UTF-8");
        InputStream cipherStream = new FileInputStream(encryptedFile);
        OutputStream recoveredStream = new ByteArrayOutputStream();
        // No file, just streams
        String filename = encryptedFile.getName();
        OpenPGPPasswordBasedEncryptor encryptor = new OpenPGPPasswordBasedEncryptor(PGP.getAlgorithm(), PGP.getProvider(), OpenPGPPasswordBasedEncryptorTest.LEGACY_PASSWORD.toCharArray(), filename);
        StreamCallback decryptionCallback = encryptor.getDecryptionCallback();
        // Act
        decryptionCallback.process(cipherStream, recoveredStream);
        // Assert
        byte[] recoveredBytes = ((ByteArrayOutputStream) (recoveredStream)).toByteArray();
        String recovered = new String(recoveredBytes, "UTF-8");
        OpenPGPPasswordBasedEncryptorTest.logger.info("Recovered: {}", recovered);
        Assert.assertEquals("Recovered text", PLAINTEXT, recovered);
    }
}

