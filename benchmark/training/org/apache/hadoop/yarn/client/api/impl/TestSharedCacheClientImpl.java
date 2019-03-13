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
package org.apache.hadoop.yarn.client.api.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ClientSCMProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.ReleaseSharedCacheResourceRequest;
import org.apache.hadoop.yarn.api.protocolrecords.UseSharedCacheResourceRequest;
import org.apache.hadoop.yarn.api.protocolrecords.UseSharedCacheResourceResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.UseSharedCacheResourceResponsePBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSharedCacheClientImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestSharedCacheClientImpl.class);

    public static SharedCacheClientImpl client;

    public static ClientSCMProtocol cProtocol;

    private static Path TEST_ROOT_DIR;

    private static FileSystem localFs;

    private static String input = "This is a test file.";

    private static String inputChecksumSHA256 = "f29bc64a9d3732b4b9035125fdb3285f5b6455778edca72414671e0ca3b2e0de";

    @Test
    public void testUseCacheMiss() throws Exception {
        UseSharedCacheResourceResponse response = new UseSharedCacheResourceResponsePBImpl();
        response.setPath(null);
        Mockito.when(TestSharedCacheClientImpl.cProtocol.use(ArgumentMatchers.isA(UseSharedCacheResourceRequest.class))).thenReturn(response);
        URL newURL = TestSharedCacheClientImpl.client.use(Mockito.mock(ApplicationId.class), "key");
        Assert.assertNull("The path is not null!", newURL);
    }

    @Test
    public void testUseCacheHit() throws Exception {
        Path file = new Path("viewfs://test/path");
        URL useUrl = URL.fromPath(new Path("viewfs://test/path"));
        UseSharedCacheResourceResponse response = new UseSharedCacheResourceResponsePBImpl();
        response.setPath(file.toString());
        Mockito.when(TestSharedCacheClientImpl.cProtocol.use(ArgumentMatchers.isA(UseSharedCacheResourceRequest.class))).thenReturn(response);
        URL newURL = TestSharedCacheClientImpl.client.use(Mockito.mock(ApplicationId.class), "key");
        Assert.assertEquals("The paths are not equal!", useUrl, newURL);
    }

    @Test(expected = YarnException.class)
    public void testUseError() throws Exception {
        String message = "Mock IOExcepiton!";
        Mockito.when(TestSharedCacheClientImpl.cProtocol.use(ArgumentMatchers.isA(UseSharedCacheResourceRequest.class))).thenThrow(new IOException(message));
        TestSharedCacheClientImpl.client.use(Mockito.mock(ApplicationId.class), "key");
    }

    @Test
    public void testRelease() throws Exception {
        // Release does not care about the return value because it is empty
        Mockito.when(TestSharedCacheClientImpl.cProtocol.release(ArgumentMatchers.isA(ReleaseSharedCacheResourceRequest.class))).thenReturn(null);
        TestSharedCacheClientImpl.client.release(Mockito.mock(ApplicationId.class), "key");
    }

    @Test(expected = YarnException.class)
    public void testReleaseError() throws Exception {
        String message = "Mock IOExcepiton!";
        Mockito.when(TestSharedCacheClientImpl.cProtocol.release(ArgumentMatchers.isA(ReleaseSharedCacheResourceRequest.class))).thenThrow(new IOException(message));
        TestSharedCacheClientImpl.client.release(Mockito.mock(ApplicationId.class), "key");
    }

    @Test
    public void testChecksum() throws Exception {
        String filename = "test1.txt";
        Path file = makeFile(filename);
        Assert.assertEquals(TestSharedCacheClientImpl.inputChecksumSHA256, TestSharedCacheClientImpl.client.getFileChecksum(file));
    }

    @Test(expected = FileNotFoundException.class)
    public void testNonexistantFileChecksum() throws Exception {
        Path file = new Path(TestSharedCacheClientImpl.TEST_ROOT_DIR, "non-existant-file");
        TestSharedCacheClientImpl.client.getFileChecksum(file);
    }
}

