/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util.pool;


import DigestObjectPools.CreateDigest;
import DigestObjectPools.DigestOperation;
import DigestObjectPools.SHA_256;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DigestObjectPoolsTest {
    private DigestObjectPools pools;

    @Test
    public void shouldCallPerformActionWithADigest() throws IOException {
        DigestObjectPools.DigestOperation operation = Mockito.mock(DigestOperation.class);
        pools.computeDigest(SHA_256, operation);
        Mockito.verify(operation).perform(ArgumentMatchers.any(MessageDigest.class));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTheAlgorithmIsUnknown() throws IOException {
        DigestObjectPools.DigestOperation operation = Mockito.mock(DigestOperation.class);
        try {
            pools.computeDigest("upside_down_fred_rubble_bubble_cake", operation);
            Assert.fail("Expected to get an exception as the algorithm is Flintstones proprietary!");
        } catch (IllegalArgumentException expected) {
            Assert.assertThat(expected.getMessage(), Matchers.is("Algorithm not supported"));
        }
    }

    @Test
    public void shouldResetDigestForFutureUsage() {
        DigestObjectPools.DigestOperation operation = new DigestObjectPools.DigestOperation() {
            public String perform(MessageDigest digest) throws IOException {
                digest.update(StringUtils.getBytesUtf8("foo"));
                return Hex.encodeHexString(digest.digest());
            }
        };
        String shaFirst = pools.computeDigest(SHA_256, operation);
        String shaSecond = pools.computeDigest(SHA_256, operation);
        Assert.assertThat(shaFirst, Matchers.is(shaSecond));
    }

    @Test
    public void shouldCreateDigestOnlyIfItIsNotAlreadyInitializedOnThisThreadsThreadLocal() throws NoSuchAlgorithmException {
        DigestObjectPools.CreateDigest creator = Mockito.mock(CreateDigest.class);
        Mockito.when(creator.create(SHA_256)).thenReturn(MessageDigest.getInstance(SHA_256));
        DigestObjectPools pools = new DigestObjectPools(creator);
        try {
            DigestObjectPools.DigestOperation operation = Mockito.mock(DigestOperation.class);
            pools.computeDigest(SHA_256, operation);
            pools.computeDigest(SHA_256, operation);
            Mockito.verify(creator).create(SHA_256);
            Mockito.verifyNoMoreInteractions(creator);
        } finally {
            pools.clearThreadLocals();
        }
    }

    @Test
    public void shouldCreateDigestOnlyIfItIsNotAlreadyInitializedOnThreads() throws InterruptedException, NoSuchAlgorithmException {
        DigestObjectPools.CreateDigest creator = Mockito.mock(CreateDigest.class);
        Mockito.when(creator.create(SHA_256)).thenReturn(MessageDigest.getInstance(SHA_256));
        final DigestObjectPools pools = new DigestObjectPools(creator);
        try {
            final DigestObjectPools.DigestOperation operation = Mockito.mock(DigestOperation.class);
            pools.computeDigest(SHA_256, operation);
            pools.computeDigest(SHA_256, operation);
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    pools.computeDigest(SHA_256, operation);
                    pools.computeDigest(SHA_256, operation);
                }
            });
            thread.start();
            thread.join();
            Mockito.verify(creator, Mockito.times(2)).create(SHA_256);
            Mockito.verifyNoMoreInteractions(creator);
        } finally {
            pools.clearThreadLocals();
        }
    }
}

