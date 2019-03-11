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
package com.twitter.distributedlog.impl.metadata;


import com.twitter.distributedlog.DLMTestUtil;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class TestZKLogMetadata {
    @Test(timeout = 60000)
    public void testGetPaths() throws Exception {
        String rootPath = "/test-get-paths";
        URI uri = DLMTestUtil.createDLMURI(2181, rootPath);
        String logName = "test-log";
        String logIdentifier = "<default>";
        String logRootPath = ((((uri.getPath()) + "/") + logName) + "/") + logIdentifier;
        String logSegmentName = "test-segment";
        ZKLogMetadata.ZKLogMetadata logMetadata = new ZKLogMetadata.ZKLogMetadata(uri, logName, logIdentifier);
        Assert.assertEquals("wrong log name", logName, logMetadata.getLogName());
        Assert.assertEquals("wrong root path", logRootPath, logMetadata.getLogRootPath());
        Assert.assertEquals("wrong log segments path", (logRootPath + (LOGSEGMENTS_PATH)), logMetadata.getLogSegmentsPath());
        Assert.assertEquals("wrong log segment path", (((logRootPath + (LOGSEGMENTS_PATH)) + "/") + logSegmentName), logMetadata.getLogSegmentPath(logSegmentName));
        Assert.assertEquals("wrong lock path", (logRootPath + (LOCK_PATH)), logMetadata.getLockPath());
        Assert.assertEquals("wrong max tx id path", (logRootPath + (MAX_TXID_PATH)), logMetadata.getMaxTxIdPath());
        Assert.assertEquals("wrong allocation path", (logRootPath + (ALLOCATION_PATH)), logMetadata.getAllocationPath());
        Assert.assertEquals("wrong qualified name", ((logName + ":") + logIdentifier), logMetadata.getFullyQualifiedName());
    }
}

