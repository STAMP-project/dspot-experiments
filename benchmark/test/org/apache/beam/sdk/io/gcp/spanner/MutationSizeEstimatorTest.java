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
package org.apache.beam.sdk.io.gcp.spanner;


import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Mutation;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * A set of unit tests for {@link MutationSizeEstimator}.
 */
public class MutationSizeEstimatorTest {
    @Test
    public void primitives() throws Exception {
        Mutation int64 = Mutation.newInsertOrUpdateBuilder("test").set("one").to(1).build();
        Mutation float64 = Mutation.newInsertOrUpdateBuilder("test").set("one").to(2.9).build();
        Mutation bool = Mutation.newInsertOrUpdateBuilder("test").set("one").to(false).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(int64), Matchers.is(8L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(float64), Matchers.is(8L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(bool), Matchers.is(1L));
    }

    @Test
    public void primitiveArrays() throws Exception {
        Mutation int64 = Mutation.newInsertOrUpdateBuilder("test").set("one").toInt64Array(new long[]{ 1L, 2L, 3L }).build();
        Mutation float64 = Mutation.newInsertOrUpdateBuilder("test").set("one").toFloat64Array(new double[]{ 1.0, 2.0 }).build();
        Mutation bool = Mutation.newInsertOrUpdateBuilder("test").set("one").toBoolArray(new boolean[]{ true, true, false, true }).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(int64), Matchers.is(24L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(float64), Matchers.is(16L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(bool), Matchers.is(4L));
    }

    @Test
    public void nullPrimitiveArrays() throws Exception {
        Mutation int64 = Mutation.newInsertOrUpdateBuilder("test").set("one").toInt64Array(((long[]) (null))).build();
        Mutation float64 = Mutation.newInsertOrUpdateBuilder("test").set("one").toFloat64Array(((double[]) (null))).build();
        Mutation bool = Mutation.newInsertOrUpdateBuilder("test").set("one").toBoolArray(((boolean[]) (null))).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(int64), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(float64), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(bool), Matchers.is(0L));
    }

    @Test
    public void strings() throws Exception {
        Mutation emptyString = Mutation.newInsertOrUpdateBuilder("test").set("one").to("").build();
        Mutation nullString = Mutation.newInsertOrUpdateBuilder("test").set("one").to(((String) (null))).build();
        Mutation sampleString = Mutation.newInsertOrUpdateBuilder("test").set("one").to("abc").build();
        Mutation sampleArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toStringArray(Arrays.asList("one", "two", null)).build();
        Mutation nullArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toStringArray(null).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(emptyString), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullString), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(sampleString), Matchers.is(3L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(sampleArray), Matchers.is(6L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullArray), Matchers.is(0L));
    }

    @Test
    public void bytes() throws Exception {
        Mutation empty = Mutation.newInsertOrUpdateBuilder("test").set("one").to(ByteArray.fromBase64("")).build();
        Mutation nullValue = Mutation.newInsertOrUpdateBuilder("test").set("one").to(((ByteArray) (null))).build();
        Mutation sample = Mutation.newInsertOrUpdateBuilder("test").set("one").to(ByteArray.fromBase64("abcdabcd")).build();
        Mutation nullArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toBytesArray(null).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(empty), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullValue), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(sample), Matchers.is(6L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullArray), Matchers.is(0L));
    }

    @Test
    public void dates() throws Exception {
        Mutation timestamp = Mutation.newInsertOrUpdateBuilder("test").set("one").to(Timestamp.now()).build();
        Mutation nullTimestamp = Mutation.newInsertOrUpdateBuilder("test").set("one").to(((Timestamp) (null))).build();
        Mutation date = Mutation.newInsertOrUpdateBuilder("test").set("one").to(Date.fromYearMonthDay(2017, 10, 10)).build();
        Mutation nullDate = Mutation.newInsertOrUpdateBuilder("test").set("one").to(((Date) (null))).build();
        Mutation timestampArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toTimestampArray(Arrays.asList(Timestamp.now(), null)).build();
        Mutation dateArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toDateArray(Arrays.asList(null, Date.fromYearMonthDay(2017, 1, 1), null, Date.fromYearMonthDay(2017, 1, 2))).build();
        Mutation nullTimestampArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toTimestampArray(null).build();
        Mutation nullDateArray = Mutation.newInsertOrUpdateBuilder("test").set("one").toDateArray(null).build();
        Assert.assertThat(MutationSizeEstimator.sizeOf(timestamp), Matchers.is(12L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(date), Matchers.is(12L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullTimestamp), Matchers.is(12L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullDate), Matchers.is(12L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(timestampArray), Matchers.is(24L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(dateArray), Matchers.is(48L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullTimestampArray), Matchers.is(0L));
        Assert.assertThat(MutationSizeEstimator.sizeOf(nullDateArray), Matchers.is(0L));
    }

    @Test
    public void group() throws Exception {
        Mutation int64 = Mutation.newInsertOrUpdateBuilder("test").set("one").to(1).build();
        Mutation float64 = Mutation.newInsertOrUpdateBuilder("test").set("one").to(2.9).build();
        Mutation bool = Mutation.newInsertOrUpdateBuilder("test").set("one").to(false).build();
        MutationGroup group = MutationGroup.create(int64, float64, bool);
        Assert.assertThat(MutationSizeEstimator.sizeOf(group), Matchers.is(17L));
    }
}

