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
package org.apache.beam.sdk.io.aws.s3;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link S3WritableByteChannel}.
 */
@RunWith(JUnit4.class)
public class S3WritableByteChannelTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void write() throws IOException {
        writeFromOptions(S3TestUtils.s3Options());
        writeFromOptions(S3TestUtils.s3OptionsWithSSEAlgorithm());
        writeFromOptions(S3TestUtils.s3OptionsWithSSECustomerKey());
        writeFromOptions(S3TestUtils.s3OptionsWithSSEAwsKeyManagementParams());
        expected.expect(IllegalArgumentException.class);
        writeFromOptions(S3TestUtils.s3OptionsWithMultipleSSEOptions());
    }

    @Test
    public void testAtMostOne() {
        Assert.assertTrue(S3WritableByteChannel.atMostOne(true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false));
        Assert.assertFalse(S3WritableByteChannel.atMostOne(true, true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(true, false));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false, true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false, false));
        Assert.assertFalse(S3WritableByteChannel.atMostOne(true, true, true));
        Assert.assertFalse(S3WritableByteChannel.atMostOne(true, true, false));
        Assert.assertFalse(S3WritableByteChannel.atMostOne(true, false, true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(true, false, false));
        Assert.assertFalse(S3WritableByteChannel.atMostOne(false, true, true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false, true, false));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false, false, true));
        Assert.assertTrue(S3WritableByteChannel.atMostOne(false, false, false));
    }
}

