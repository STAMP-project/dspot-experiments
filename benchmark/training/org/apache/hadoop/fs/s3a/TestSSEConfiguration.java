/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a;


import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;


/**
 * Test SSE setup operations and errors raised.
 * Tests related to secret providers and AWS credentials are also
 * included, as they share some common setup operations.
 */
public class TestSSEConfiguration extends Assert {
    /**
     * Bucket to use for per-bucket options.
     */
    public static final String BUCKET = "dataset-1";

    @Rule
    public Timeout testTimeout = new Timeout(S3ATestConstants.S3A_TEST_TIMEOUT);

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testSSECNoKey() throws Throwable {
        assertGetAlgorithmFails(SSE_C_NO_KEY_ERROR, SSE_C.getMethod(), null);
    }

    @Test
    public void testSSECBlankKey() throws Throwable {
        assertGetAlgorithmFails(SSE_C_NO_KEY_ERROR, SSE_C.getMethod(), "");
    }

    @Test
    public void testSSECGoodKey() throws Throwable {
        Assert.assertEquals(SSE_C, getAlgorithm(SSE_C, "sseckey"));
    }

    @Test
    public void testKMSGoodKey() throws Throwable {
        Assert.assertEquals(SSE_KMS, getAlgorithm(SSE_KMS, "kmskey"));
    }

    @Test
    public void testAESKeySet() throws Throwable {
        assertGetAlgorithmFails(SSE_S3_WITH_KEY_ERROR, SSE_S3.getMethod(), "setkey");
    }

    @Test
    public void testSSEEmptyKey() {
        // test the internal logic of the test setup code
        Configuration c = buildConf(SSE_C.getMethod(), "");
        Assert.assertEquals("", getServerSideEncryptionKey(TestSSEConfiguration.BUCKET, c));
    }

    @Test
    public void testSSEKeyNull() throws Throwable {
        // test the internal logic of the test setup code
        final Configuration c = buildConf(SSE_C.getMethod(), null);
        Assert.assertEquals("", getServerSideEncryptionKey(TestSSEConfiguration.BUCKET, c));
        intercept(IOException.class, SSE_C_NO_KEY_ERROR, () -> getEncryptionAlgorithm(BUCKET, c));
    }

    @Test
    public void testSSEKeyFromCredentialProvider() throws Exception {
        // set up conf to have a cred provider
        final Configuration conf = confWithProvider();
        String key = "provisioned";
        setProviderOption(conf, SERVER_SIDE_ENCRYPTION_KEY, key);
        // let's set the password in config and ensure that it uses the credential
        // provider provisioned value instead.
        conf.set(SERVER_SIDE_ENCRYPTION_KEY, "keyInConfObject");
        String sseKey = getServerSideEncryptionKey(TestSSEConfiguration.BUCKET, conf);
        Assert.assertNotNull("Proxy password should not retrun null.", sseKey);
        Assert.assertEquals("Proxy password override did NOT work.", key, sseKey);
    }

    private static final String SECRET = "*secret*";

    private static final String BUCKET_PATTERN = (FS_S3A_BUCKET_PREFIX) + "%s.%s";

    @Test
    public void testGetPasswordFromConf() throws Throwable {
        final Configuration conf = emptyConf();
        conf.set(SECRET_KEY, TestSSEConfiguration.SECRET);
        Assert.assertEquals(TestSSEConfiguration.SECRET, lookupPassword(conf, SECRET_KEY, ""));
        Assert.assertEquals(TestSSEConfiguration.SECRET, lookupPassword(conf, SECRET_KEY, "defVal"));
    }

    @Test
    public void testGetPasswordFromProvider() throws Throwable {
        final Configuration conf = confWithProvider();
        setProviderOption(conf, SECRET_KEY, TestSSEConfiguration.SECRET);
        Assert.assertEquals(TestSSEConfiguration.SECRET, lookupPassword(conf, SECRET_KEY, ""));
        assertSecretKeyEquals(conf, null, TestSSEConfiguration.SECRET, "");
        assertSecretKeyEquals(conf, null, "overidden", "overidden");
    }

    @Test
    public void testGetBucketPasswordFromProvider() throws Throwable {
        final Configuration conf = confWithProvider();
        URI bucketURI = new URI((("s3a://" + (TestSSEConfiguration.BUCKET)) + "/"));
        setProviderOption(conf, SECRET_KEY, "unbucketed");
        String bucketedKey = String.format(TestSSEConfiguration.BUCKET_PATTERN, TestSSEConfiguration.BUCKET, SECRET_KEY);
        setProviderOption(conf, bucketedKey, TestSSEConfiguration.SECRET);
        String overrideVal;
        overrideVal = "";
        assertSecretKeyEquals(conf, TestSSEConfiguration.BUCKET, TestSSEConfiguration.SECRET, overrideVal);
        assertSecretKeyEquals(conf, bucketURI.getHost(), TestSSEConfiguration.SECRET, "");
        assertSecretKeyEquals(conf, bucketURI.getHost(), "overidden", "overidden");
    }

    @Test
    public void testGetBucketPasswordFromProviderShort() throws Throwable {
        final Configuration conf = confWithProvider();
        URI bucketURI = new URI((("s3a://" + (TestSSEConfiguration.BUCKET)) + "/"));
        setProviderOption(conf, SECRET_KEY, "unbucketed");
        String bucketedKey = String.format(TestSSEConfiguration.BUCKET_PATTERN, TestSSEConfiguration.BUCKET, "secret.key");
        setProviderOption(conf, bucketedKey, TestSSEConfiguration.SECRET);
        assertSecretKeyEquals(conf, TestSSEConfiguration.BUCKET, TestSSEConfiguration.SECRET, "");
        assertSecretKeyEquals(conf, bucketURI.getHost(), TestSSEConfiguration.SECRET, "");
        assertSecretKeyEquals(conf, bucketURI.getHost(), "overidden", "overidden");
    }

    @Test
    public void testUnknownEncryptionMethod() throws Throwable {
        intercept(IOException.class, UNKNOWN_ALGORITHM, () -> S3AEncryptionMethods.S3AEncryptionMethods.getMethod("SSE-ROT13"));
    }

    @Test
    public void testClientEncryptionMethod() throws Throwable {
        S3AEncryptionMethods.S3AEncryptionMethods method = getMethod("CSE-KMS");
        Assert.assertEquals(CSE_KMS, method);
        Assert.assertFalse(("shouldn't be server side " + method), method.isServerSide());
    }

    @Test
    public void testCSEKMSEncryptionMethod() throws Throwable {
        S3AEncryptionMethods.S3AEncryptionMethods method = getMethod("CSE-CUSTOM");
        Assert.assertEquals(CSE_CUSTOM, method);
        Assert.assertFalse(("shouldn't be server side " + method), method.isServerSide());
    }

    @Test
    public void testNoEncryptionMethod() throws Throwable {
        Assert.assertEquals(NONE, getMethod(" "));
    }
}

