/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import com.google.cloud.bigquery.TimePartitioning.Type;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TimePartitioningTest {
    private static final Type TYPE = Type.DAY;

    private static final long EXPIRATION_MS = 42;

    private static final boolean REQUIRE_PARTITION_FILTER = false;

    private static final String FIELD = "field";

    private static final TimePartitioning TIME_PARTITIONING = TimePartitioning.newBuilder(TimePartitioningTest.TYPE).setExpirationMs(TimePartitioningTest.EXPIRATION_MS).setRequirePartitionFilter(TimePartitioningTest.REQUIRE_PARTITION_FILTER).setField(TimePartitioningTest.FIELD).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        Assert.assertEquals(TimePartitioningTest.TYPE, TimePartitioningTest.TIME_PARTITIONING.getType());
        Assert.assertEquals(TimePartitioningTest.EXPIRATION_MS, TimePartitioningTest.TIME_PARTITIONING.getExpirationMs().longValue());
        Assert.assertEquals(TimePartitioningTest.REQUIRE_PARTITION_FILTER, TimePartitioningTest.TIME_PARTITIONING.getRequirePartitionFilter());
        Assert.assertEquals(TimePartitioningTest.FIELD, TimePartitioningTest.TIME_PARTITIONING.getField());
        TimePartitioning partitioning = TimePartitioning.of(TimePartitioningTest.TYPE);
        Assert.assertEquals(TimePartitioningTest.TYPE, partitioning.getType());
        Assert.assertNull(partitioning.getExpirationMs());
    }

    @Test
    public void testBuilder() {
        TimePartitioning partitioning = TimePartitioning.newBuilder(TimePartitioningTest.TYPE).build();
        Assert.assertEquals(TimePartitioningTest.TYPE, partitioning.getType());
        Assert.assertNull(partitioning.getExpirationMs());
        Assert.assertNull(partitioning.getRequirePartitionFilter());
        Assert.assertNull(partitioning.getField());
        partitioning = TimePartitioning.newBuilder(TimePartitioningTest.TYPE).setExpirationMs(100L).build();
        Assert.assertEquals(TimePartitioningTest.TYPE, partitioning.getType());
        Assert.assertEquals(100, ((long) (partitioning.getExpirationMs())));
        Assert.assertNull(partitioning.getRequirePartitionFilter());
        Assert.assertNull(partitioning.getField());
    }

    @Test
    public void testTypeOf_Npe() {
        thrown.expect(NullPointerException.class);
        TimePartitioning.of(null);
    }

    @Test
    public void testTypeAndExpirationOf_Npe() {
        thrown.expect(NullPointerException.class);
        TimePartitioning.of(null, TimePartitioningTest.EXPIRATION_MS);
    }

    @Test
    public void testToAndFromPb() {
        compareTimePartitioning(TimePartitioningTest.TIME_PARTITIONING, TimePartitioning.fromPb(TimePartitioningTest.TIME_PARTITIONING.toPb()));
        TimePartitioning partitioning = TimePartitioning.of(TimePartitioningTest.TYPE);
        compareTimePartitioning(partitioning, TimePartitioning.fromPb(partitioning.toPb()));
    }
}

