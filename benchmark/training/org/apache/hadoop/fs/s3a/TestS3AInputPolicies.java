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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test of the input policy logic, without making any S3 calls.
 */
@RunWith(Parameterized.class)
public class TestS3AInputPolicies {
    private S3AInputPolicy policy;

    private long targetPos;

    private long length;

    private long contentLength;

    private long readahead;

    private long expectedLimit;

    public static final long _64K = 64 * 1024;

    public static final long _128K = 128 * 1024;

    public static final long _256K = 256 * 1024;

    public static final long _1MB = 1024L * 1024;

    public static final long _10MB = (TestS3AInputPolicies._1MB) * 10;

    public TestS3AInputPolicies(S3AInputPolicy policy, long targetPos, long length, long contentLength, long readahead, long expectedLimit) {
        this.policy = policy;
        this.targetPos = targetPos;
        this.length = length;
        this.contentLength = contentLength;
        this.readahead = readahead;
        this.expectedLimit = expectedLimit;
    }

    @Test
    public void testInputPolicies() throws Throwable {
        Assert.assertEquals(String.format("calculateRequestLimit(%s, %d, %d, %d, %d)", policy, targetPos, length, contentLength, readahead), expectedLimit, S3AInputStream.calculateRequestLimit(policy, targetPos, length, contentLength, readahead));
    }
}

