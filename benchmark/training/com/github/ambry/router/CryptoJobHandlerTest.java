/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.router;


import DecryptJob.DecryptJobResult;
import EncryptJob.EncryptJobResult;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.commons.BlobId;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.UtilsTest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link CryptoJobHandler}
 */
public class CryptoJobHandlerTest {
    static final int DEFAULT_THREAD_COUNT = 2;

    static final int DEFAULT_KEY_SIZE = 64;

    private static final int MAX_DATA_SIZE_IN_BYTES = 10000;

    private static final int RANDOM_KEY_SIZE_IN_BITS = 256;

    private static final String ENCRYPT_JOB_TYPE = "encrypt";

    private static final String DECRYPT_JOB_TYPE = "decrypt";

    private static final String CLUSTER_NAME = UtilsTest.getRandomString(10);

    private static final MetricRegistry REGISTRY = new MetricRegistry();

    private final CryptoService<SecretKeySpec> cryptoService;

    private final KeyManagementService<SecretKeySpec> kms;

    private final ClusterMap referenceClusterMap;

    private final String defaultKey;

    private final VerifiableProperties verifiableProperties;

    private final NonBlockingRouterMetrics routerMetrics;

    private CryptoJobHandler cryptoJobHandler;

    public CryptoJobHandlerTest() throws IOException, GeneralSecurityException {
        defaultKey = TestUtils.getRandomKey(CryptoJobHandlerTest.DEFAULT_KEY_SIZE);
        Properties props = CryptoTestUtils.getKMSProperties(defaultKey, CryptoJobHandlerTest.RANDOM_KEY_SIZE_IN_BITS);
        verifiableProperties = new VerifiableProperties(props);
        kms = getKeyManagementService();
        cryptoService = getCryptoService();
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        referenceClusterMap = new MockClusterMap();
        routerMetrics = new NonBlockingRouterMetrics(referenceClusterMap);
    }

    /**
     * Tests {@link CryptoJobHandler} for happy path. i.e. Encrypt and Decrypt jobs are executed and callback invoked
     *
     * @throws GeneralSecurityException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCryptoJobExecutorService() throws InterruptedException, GeneralSecurityException {
        int totalDataCount = 10;
        CountDownLatch encryptCallBackCount = new CountDownLatch((totalDataCount * 3));
        CountDownLatch decryptCallBackCount = new CountDownLatch((totalDataCount * 3));
        SecretKeySpec perBlobKey = kms.getRandomKey();
        for (int i = 0; i < totalDataCount; i++) {
            testEncryptDecryptFlow(perBlobKey, encryptCallBackCount, decryptCallBackCount, CryptoJobHandlerTest.Mode.Data);
            testEncryptDecryptFlow(perBlobKey, encryptCallBackCount, decryptCallBackCount, CryptoJobHandlerTest.Mode.UserMetadata);
            testEncryptDecryptFlow(perBlobKey, encryptCallBackCount, decryptCallBackCount, CryptoJobHandlerTest.Mode.Both);
        }
        CryptoJobHandlerTest.awaitCountDownLatch(encryptCallBackCount, CryptoJobHandlerTest.ENCRYPT_JOB_TYPE);
        CryptoJobHandlerTest.awaitCountDownLatch(decryptCallBackCount, CryptoJobHandlerTest.DECRYPT_JOB_TYPE);
    }

    /**
     * Tests {@link CryptoJobHandler} for with different worker count
     *
     * @throws GeneralSecurityException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCryptoJobExecutorServiceDiffThreadCount() throws InterruptedException, GeneralSecurityException {
        int totalDataCount = 10;
        for (int j = 0; j < 5; j++) {
            cryptoJobHandler.close();
            cryptoJobHandler = new CryptoJobHandler((j + 1));
            CountDownLatch encryptCallBackCount = new CountDownLatch(totalDataCount);
            CountDownLatch decryptCallBackCount = new CountDownLatch(totalDataCount);
            SecretKeySpec perBlobKey = kms.getRandomKey();
            for (int i = 0; i < totalDataCount; i++) {
                testEncryptDecryptFlow(perBlobKey, encryptCallBackCount, decryptCallBackCount, CryptoJobHandlerTest.Mode.Both);
            }
            CryptoJobHandlerTest.awaitCountDownLatch(encryptCallBackCount, CryptoJobHandlerTest.ENCRYPT_JOB_TYPE);
            CryptoJobHandlerTest.awaitCountDownLatch(decryptCallBackCount, CryptoJobHandlerTest.DECRYPT_JOB_TYPE);
        }
    }

    /**
     * Tests {@link CryptoJobHandler} for failures during encryption
     *
     * @throws InterruptedException
     * 		
     * @throws GeneralSecurityException
     * 		
     */
    @Test
    public void testEncryptionFailure() throws InterruptedException, GeneralSecurityException {
        cryptoJobHandler.close();
        MockCryptoService mockCryptoService = new MockCryptoService(new com.github.ambry.config.CryptoServiceConfig(verifiableProperties));
        mockCryptoService.exceptionOnEncryption.set(new GeneralSecurityException("Exception to test", new IllegalStateException()));
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        SecretKeySpec perBlobSecretKey = kms.getRandomKey();
        testFailureOnEncryption(perBlobSecretKey, mockCryptoService, kms);
        mockCryptoService.clearStates();
        cryptoJobHandler.close();
        MockKeyManagementService mockKms = new MockKeyManagementService(new com.github.ambry.config.KMSConfig(verifiableProperties), defaultKey);
        mockKms.exceptionToThrow.set(new GeneralSecurityException("Exception to test", new IllegalStateException()));
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        testFailureOnEncryption(perBlobSecretKey, cryptoService, mockKms);
    }

    /**
     * Tests {@link CryptoJobHandler} for failures during decryption
     *
     * @throws InterruptedException
     * 		
     * @throws GeneralSecurityException
     * 		
     */
    @Test
    public void testDecryptionFailure() throws InterruptedException, GeneralSecurityException {
        cryptoJobHandler.close();
        MockCryptoService mockCryptoService = new MockCryptoService(new com.github.ambry.config.CryptoServiceConfig(verifiableProperties));
        mockCryptoService.exceptionOnDecryption.set(new GeneralSecurityException("Exception to test", new IllegalStateException()));
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        SecretKeySpec perBlobSecretKey = kms.getRandomKey();
        testFailureOnDecryption(perBlobSecretKey, null, false, mockCryptoService, kms);
        mockCryptoService.clearStates();
        cryptoJobHandler.close();
        MockKeyManagementService mockKms = new MockKeyManagementService(new com.github.ambry.config.KMSConfig(verifiableProperties), defaultKey);
        cryptoJobHandler = new CryptoJobHandler(CryptoJobHandlerTest.DEFAULT_THREAD_COUNT);
        testFailureOnDecryption(perBlobSecretKey, mockKms, true, cryptoService, mockKms);
    }

    /**
     * Tests {@link CryptoJobHandler} for pending encrypt jobs callback after closing the thread
     *
     * @throws InterruptedException
     * 		
     * @throws GeneralSecurityException
     * 		
     */
    @Test
    public void testPendingEncryptJobs() throws InterruptedException, GeneralSecurityException {
        int testDataCount = 10;
        int closeOnCount = 4;
        CountDownLatch encryptCallBackCount = new CountDownLatch(testDataCount);
        List<EncryptJob> encryptJobs = new ArrayList<>();
        SecretKeySpec perBlobKey = kms.getRandomKey();
        for (int i = 0; i < testDataCount; i++) {
            CryptoJobHandlerTest.TestBlobData testData = getRandomBlob(referenceClusterMap);
            if (i < closeOnCount) {
                cryptoJobHandler.submitJob(new EncryptJob(testData.blobId.getAccountId(), testData.blobId.getContainerId(), testData.blobContent, testData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult result,Exception exception) -> {
                    encryptCallBackCount.countDown();
                    assertNotNull("Encrypted content should not be null", result.getEncryptedBlobContent());
                    assertNotNull("Encrypted user-metadata should not be null", result.getEncryptedUserMetadata());
                    assertNotNull("Encrypted key should not be null", result.getEncryptedKey());
                }));
            } else {
                encryptJobs.add(new EncryptJob(testData.blobId.getAccountId(), testData.blobId.getContainerId(), testData.blobContent, testData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult result,Exception exception) -> {
                    encryptCallBackCount.countDown();
                    if (exception == null) {
                        assertNotNull("Encrypted content should not be null", result.getEncryptedBlobContent());
                        assertNotNull("Encrypted user-metadata should not be null", result.getEncryptedUserMetadata());
                        assertNotNull("Encrypted key should not be null", result.getEncryptedKey());
                    } else {
                        assertTrue("Exception cause should have been GeneralSecurityException", (exception instanceof GeneralSecurityException));
                        assertNull("Result should have been null", result);
                    }
                }));
            }
        }
        // add special job that will close the thread. Add all the encrypt jobs to the queue before closing the thread.
        CryptoJobHandlerTest.TestBlobData probeData = getRandomBlob(referenceClusterMap);
        cryptoJobHandler.submitJob(new EncryptJob(probeData.blobId.getAccountId(), probeData.blobId.getContainerId(), probeData.blobContent, probeData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult result,Exception exception) -> {
            for (EncryptJob encryptJob : encryptJobs) {
                cryptoJobHandler.submitJob(encryptJob);
            }
            new Thread(new com.github.ambry.router.CloseCryptoHandler(cryptoJobHandler)).start();
        }));
        CryptoJobHandlerTest.awaitCountDownLatch(encryptCallBackCount, CryptoJobHandlerTest.ENCRYPT_JOB_TYPE);
    }

    /**
     * Tests {@link CryptoJobHandler} for pending decrypt jobs callback after closing the thread
     *
     * @throws InterruptedException
     * 		
     * @throws GeneralSecurityException
     * 		
     */
    @Test
    public void testPendingDecryptJobs() throws InterruptedException, GeneralSecurityException {
        int testDataCount = 10;
        CountDownLatch encryptCallBackCount = new CountDownLatch(testDataCount);
        CountDownLatch decryptCallBackCount = new CountDownLatch(testDataCount);
        SecretKeySpec perBlobKey = kms.getRandomKey();
        List<DecryptJob> decryptJobs = new CopyOnWriteArrayList<>();
        for (int i = 0; i < testDataCount; i++) {
            CryptoJobHandlerTest.TestBlobData testData = getRandomBlob(referenceClusterMap);
            cryptoJobHandler.submitJob(new EncryptJob(testData.blobId.getAccountId(), testData.blobId.getContainerId(), testData.blobContent, testData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult encryptJobResult,Exception exception) -> {
                assertNotNull("Encrypted content should not be null", encryptJobResult.getEncryptedBlobContent());
                assertNotNull("Encrypted key should not be null", encryptJobResult.getEncryptedKey());
                decryptJobs.add(new DecryptJob(testData.blobId, encryptJobResult.getEncryptedKey(), encryptJobResult.getEncryptedBlobContent(), encryptJobResult.getEncryptedUserMetadata(), cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.decryptJobMetrics), (DecryptJob.DecryptJobResult decryptJobResult,Exception e) -> {
                    if (e == null) {
                        assertEquals("BlobId mismatch ", testData.blobId, decryptJobResult.getBlobId());
                        assertNull(("Exception shouldn't have been thrown to decrypt contents for " + testData.blobId), exception);
                        assertNotNull("Decrypted contents should not be null", decryptJobResult.getDecryptedBlobContent());
                        assertArrayEquals("Decrypted content and plain bytes should match", testData.blobContent.array(), decryptJobResult.getDecryptedBlobContent().array());
                        assertArrayEquals("Decrypted userMetadata and plain bytes should match", testData.userMetadata.array(), decryptJobResult.getDecryptedUserMetadata().array());
                    } else {
                        assertNotNull(("Exception should have been thrown to decrypt contents for " + testData.blobId), e);
                        assertTrue("Exception cause should have been GeneralSecurityException", (e instanceof GeneralSecurityException));
                        assertNull("Result should have been null", decryptJobResult);
                    }
                    decryptCallBackCount.countDown();
                }));
                encryptCallBackCount.countDown();
            }));
        }
        // wait for all encryption to complete
        CryptoJobHandlerTest.awaitCountDownLatch(encryptCallBackCount, CryptoJobHandlerTest.ENCRYPT_JOB_TYPE);
        // add special job that will close the thread. Add all the decrypt jobs to the queue before closing the thread.
        CryptoJobHandlerTest.TestBlobData probeData = getRandomBlob(referenceClusterMap);
        cryptoJobHandler.submitJob(new EncryptJob(probeData.blobId.getAccountId(), probeData.blobId.getContainerId(), probeData.blobContent, probeData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult result,Exception exception) -> {
            for (DecryptJob decryptJob : decryptJobs) {
                cryptoJobHandler.submitJob(decryptJob);
            }
            new Thread(new com.github.ambry.router.CloseCryptoHandler(cryptoJobHandler)).start();
        }));
        CryptoJobHandlerTest.awaitCountDownLatch(decryptCallBackCount, CryptoJobHandlerTest.DECRYPT_JOB_TYPE);
    }

    /**
     * Tests {@link CryptoJobHandler} for encrypt and decrypt calls after closing the thread
     *
     * @throws GeneralSecurityException
     * 		
     */
    @Test
    public void testCryptoJobHandlerClose() throws GeneralSecurityException {
        SecretKeySpec perBlobKey = kms.getRandomKey();
        CryptoJobHandlerTest.TestBlobData randomData = getRandomBlob(referenceClusterMap);
        cryptoJobHandler.close();
        cryptoJobHandler.submitJob(new EncryptJob(randomData.blobId.getAccountId(), randomData.blobId.getContainerId(), randomData.blobContent, randomData.userMetadata, perBlobKey, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.encryptJobMetrics), (EncryptJob.EncryptJobResult result,Exception exception) -> {
            fail("Callback should not have been called since CryptoWorker is closed");
        }));
        cryptoJobHandler.submitJob(new DecryptJob(randomData.blobId, randomData.blobContent, randomData.blobContent, randomData.userMetadata, cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.decryptJobMetrics), (DecryptJob.DecryptJobResult result,Exception exception) -> {
            fail("Callback should not have been called since CryptoWorker is closed");
        }));
    }

    /**
     * Close the {@link CryptoJobHandler} asynchronously
     */
    private class CloseCryptoHandler implements Runnable {
        private final CryptoJobHandler cryptoJobHandler;

        private CloseCryptoHandler(CryptoJobHandler cryptoJobHandler) {
            this.cryptoJobHandler = cryptoJobHandler;
        }

        @Override
        public void run() {
            cryptoJobHandler.close();
        }
    }

    /**
     * Encrypt callback verifier. Verifies non null for arguments and adds a decrypt job to the jobQueue on successful completion.
     * Else, verifies the exception is set correctly.
     */
    private class EncryptCallbackVerifier implements Callback<EncryptJob.EncryptJobResult> {
        private final BlobId blobId;

        private final boolean expectException;

        private final CountDownLatch countDownLatch;

        private final CryptoJobHandlerTest.DecryptCallbackVerifier decryptCallBackVerifier;

        private final CryptoJobHandlerTest.Mode mode;

        EncryptCallbackVerifier(BlobId blobId, boolean expectException, CountDownLatch encryptCountDownLatch, CryptoJobHandlerTest.DecryptCallbackVerifier decryptCallBackVerifier, CryptoJobHandlerTest.Mode mode) {
            this.blobId = blobId;
            this.expectException = expectException;
            this.countDownLatch = encryptCountDownLatch;
            this.decryptCallBackVerifier = decryptCallBackVerifier;
            this.mode = mode;
        }

        @Override
        public void onCompletion(EncryptJob.EncryptJobResult encryptJobResult, Exception exception) {
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
            }
            if (!(expectException)) {
                Assert.assertNull(("Exception shouldn't have been thrown to encrypt contents for " + (blobId)), exception);
                if ((mode) != (CryptoJobHandlerTest.Mode.UserMetadata)) {
                    Assert.assertNotNull("Encrypted content should not be null", encryptJobResult.getEncryptedBlobContent());
                }
                if ((mode) != (CryptoJobHandlerTest.Mode.Data)) {
                    Assert.assertNotNull("Encrypted userMetadata should not be null", encryptJobResult.getEncryptedUserMetadata());
                }
                Assert.assertNotNull("Encrypted key should not be null", encryptJobResult.getEncryptedKey());
                cryptoJobHandler.submitJob(new DecryptJob(blobId, encryptJobResult.getEncryptedKey(), encryptJobResult.getEncryptedBlobContent(), encryptJobResult.getEncryptedUserMetadata(), cryptoService, kms, new CryptoJobMetricsTracker(routerMetrics.decryptJobMetrics), decryptCallBackVerifier));
            } else {
                Assert.assertNotNull(("Exception should have been thrown to encrypt contents for " + (blobId)), exception);
                Assert.assertTrue("Exception cause should have been GeneralSecurityException", (exception instanceof GeneralSecurityException));
                Assert.assertNull("Result should have been null", encryptJobResult);
            }
        }
    }

    /**
     * Decrypt callback verifier. Verifies the decrypted content matches raw content on successful completion.
     * Else, verifies the exception is set correctly.
     */
    private class DecryptCallbackVerifier implements Callback<DecryptJob.DecryptJobResult> {
        private final BlobId blobId;

        private final boolean expectException;

        private final ByteBuffer unencryptedContent;

        private final ByteBuffer unencryptedUserMetadata;

        private final CountDownLatch countDownLatch;

        DecryptCallbackVerifier(BlobId blobId, ByteBuffer unencryptedContent, ByteBuffer unencryptedUserMetadata, boolean expectException, CountDownLatch countDownLatch) {
            this.blobId = blobId;
            this.unencryptedContent = unencryptedContent;
            this.unencryptedUserMetadata = unencryptedUserMetadata;
            this.expectException = expectException;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onCompletion(DecryptJob.DecryptJobResult result, Exception exception) {
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
            }
            if (!(expectException)) {
                Assert.assertEquals("BlobId mismatch ", blobId, result.getBlobId());
                Assert.assertNull(("Exception shouldn't have been thrown to decrypt contents for " + (blobId)), exception);
                if ((unencryptedContent) != null) {
                    Assert.assertNotNull("Decrypted contents should not be null", result.getDecryptedBlobContent());
                    Assert.assertArrayEquals("Decrypted content and plain bytes should match", unencryptedContent.array(), result.getDecryptedBlobContent().array());
                }
                if ((unencryptedUserMetadata) != null) {
                    Assert.assertNotNull("Decrypted userMetadata should not be null", result.getDecryptedUserMetadata());
                    Assert.assertArrayEquals("Decrypted userMetadata and plain bytes should match", unencryptedUserMetadata.array(), result.getDecryptedUserMetadata().array());
                }
            } else {
                Assert.assertNotNull(("Exception should have been thrown to decrypt contents for " + (blobId)), exception);
                Assert.assertTrue("Exception cause should have been GeneralSecurityException", (exception instanceof GeneralSecurityException));
                Assert.assertNull("Result should have been null", result);
            }
        }
    }

    /**
     * Mode to represent the entity that needs to be tested for encryption and decryption
     */
    enum Mode {

        Data,
        UserMetadata,
        Both;}

    /**
     * Class to hold all data for a test blob like BlobId, blob content and user-metadata
     */
    static class TestBlobData {
        BlobId blobId;

        ByteBuffer userMetadata;

        ByteBuffer blobContent;

        TestBlobData(BlobId blobId, ByteBuffer userMetadata, ByteBuffer blobContent) {
            this.blobId = blobId;
            this.userMetadata = userMetadata;
            this.blobContent = blobContent;
        }
    }
}

