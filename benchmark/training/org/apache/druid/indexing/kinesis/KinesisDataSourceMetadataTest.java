/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.kinesis;


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;


public class KinesisDataSourceMetadataTest {
    private static final KinesisDataSourceMetadata KM0 = KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of());

    private static final KinesisDataSourceMetadata KM1 = KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "3L"));

    private static final KinesisDataSourceMetadata KM2 = KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "4L", "2", "5L"));

    private static final KinesisDataSourceMetadata KM3 = KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "2", "5L"));

    @Test
    public void testMatches() {
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM0.matches(KinesisDataSourceMetadataTest.KM0));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM0.matches(KinesisDataSourceMetadataTest.KM1));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM0.matches(KinesisDataSourceMetadataTest.KM2));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM0.matches(KinesisDataSourceMetadataTest.KM3));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM1.matches(KinesisDataSourceMetadataTest.KM0));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM1.matches(KinesisDataSourceMetadataTest.KM1));
        Assert.assertFalse(KinesisDataSourceMetadataTest.KM1.matches(KinesisDataSourceMetadataTest.KM2));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM1.matches(KinesisDataSourceMetadataTest.KM3));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM2.matches(KinesisDataSourceMetadataTest.KM0));
        Assert.assertFalse(KinesisDataSourceMetadataTest.KM2.matches(KinesisDataSourceMetadataTest.KM1));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM2.matches(KinesisDataSourceMetadataTest.KM2));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM2.matches(KinesisDataSourceMetadataTest.KM3));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM3.matches(KinesisDataSourceMetadataTest.KM0));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM3.matches(KinesisDataSourceMetadataTest.KM1));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM3.matches(KinesisDataSourceMetadataTest.KM2));
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM3.matches(KinesisDataSourceMetadataTest.KM3));
    }

    @Test
    public void testIsValidStart() {
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM0.isValidStart());
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM1.isValidStart());
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM2.isValidStart());
        Assert.assertTrue(KinesisDataSourceMetadataTest.KM3.isValidStart());
    }

    @Test
    public void testPlus() {
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "3L", "2", "5L")), KinesisDataSourceMetadataTest.KM1.plus(KinesisDataSourceMetadataTest.KM3));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "4L", "2", "5L")), KinesisDataSourceMetadataTest.KM0.plus(KinesisDataSourceMetadataTest.KM2));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "4L", "2", "5L")), KinesisDataSourceMetadataTest.KM1.plus(KinesisDataSourceMetadataTest.KM2));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "3L", "2", "5L")), KinesisDataSourceMetadataTest.KM2.plus(KinesisDataSourceMetadataTest.KM1));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("0", "2L", "1", "4L", "2", "5L")), KinesisDataSourceMetadataTest.KM2.plus(KinesisDataSourceMetadataTest.KM2));
    }

    @Test
    public void testMinus() {
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("1", "3L")), KinesisDataSourceMetadataTest.KM1.minus(KinesisDataSourceMetadataTest.KM3));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of()), KinesisDataSourceMetadataTest.KM0.minus(KinesisDataSourceMetadataTest.KM2));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of()), KinesisDataSourceMetadataTest.KM1.minus(KinesisDataSourceMetadataTest.KM2));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of("2", "5L")), KinesisDataSourceMetadataTest.KM2.minus(KinesisDataSourceMetadataTest.KM1));
        Assert.assertEquals(KinesisDataSourceMetadataTest.km("foo", ImmutableMap.of()), KinesisDataSourceMetadataTest.KM2.minus(KinesisDataSourceMetadataTest.KM2));
    }
}

