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
package org.apache.druid.indexing.kafka;


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;


public class KafkaDataSourceMetadataTest {
    private static final KafkaDataSourceMetadata KM0 = KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of());

    private static final KafkaDataSourceMetadata KM1 = KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 3L));

    private static final KafkaDataSourceMetadata KM2 = KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 4L, 2, 5L));

    private static final KafkaDataSourceMetadata KM3 = KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 2, 5L));

    @Test
    public void testMatches() {
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM0.matches(KafkaDataSourceMetadataTest.KM0));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM0.matches(KafkaDataSourceMetadataTest.KM1));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM0.matches(KafkaDataSourceMetadataTest.KM2));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM0.matches(KafkaDataSourceMetadataTest.KM3));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM1.matches(KafkaDataSourceMetadataTest.KM0));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM1.matches(KafkaDataSourceMetadataTest.KM1));
        Assert.assertFalse(KafkaDataSourceMetadataTest.KM1.matches(KafkaDataSourceMetadataTest.KM2));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM1.matches(KafkaDataSourceMetadataTest.KM3));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM2.matches(KafkaDataSourceMetadataTest.KM0));
        Assert.assertFalse(KafkaDataSourceMetadataTest.KM2.matches(KafkaDataSourceMetadataTest.KM1));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM2.matches(KafkaDataSourceMetadataTest.KM2));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM2.matches(KafkaDataSourceMetadataTest.KM3));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM3.matches(KafkaDataSourceMetadataTest.KM0));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM3.matches(KafkaDataSourceMetadataTest.KM1));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM3.matches(KafkaDataSourceMetadataTest.KM2));
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM3.matches(KafkaDataSourceMetadataTest.KM3));
    }

    @Test
    public void testIsValidStart() {
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM0.isValidStart());
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM1.isValidStart());
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM2.isValidStart());
        Assert.assertTrue(KafkaDataSourceMetadataTest.KM3.isValidStart());
    }

    @Test
    public void testPlus() {
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 3L, 2, 5L)), KafkaDataSourceMetadataTest.KM1.plus(KafkaDataSourceMetadataTest.KM3));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 4L, 2, 5L)), KafkaDataSourceMetadataTest.KM0.plus(KafkaDataSourceMetadataTest.KM2));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 4L, 2, 5L)), KafkaDataSourceMetadataTest.KM1.plus(KafkaDataSourceMetadataTest.KM2));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 3L, 2, 5L)), KafkaDataSourceMetadataTest.KM2.plus(KafkaDataSourceMetadataTest.KM1));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(0, 2L, 1, 4L, 2, 5L)), KafkaDataSourceMetadataTest.KM2.plus(KafkaDataSourceMetadataTest.KM2));
    }

    @Test
    public void testMinus() {
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(1, 3L)), KafkaDataSourceMetadataTest.KM1.minus(KafkaDataSourceMetadataTest.KM3));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of()), KafkaDataSourceMetadataTest.KM0.minus(KafkaDataSourceMetadataTest.KM2));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of()), KafkaDataSourceMetadataTest.KM1.minus(KafkaDataSourceMetadataTest.KM2));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of(2, 5L)), KafkaDataSourceMetadataTest.KM2.minus(KafkaDataSourceMetadataTest.KM1));
        Assert.assertEquals(KafkaDataSourceMetadataTest.km("foo", ImmutableMap.of()), KafkaDataSourceMetadataTest.KM2.minus(KafkaDataSourceMetadataTest.KM2));
    }
}

