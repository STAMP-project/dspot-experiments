/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.time;


import CommonTimeUtil.TimeBucket;
import TimeBucket.SECOND;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.commonutil.CommonTimeUtil;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;

import static TimeBucket.MINUTE;
import static TimeBucket.SECOND;


public class RBMBackedTimestampSetTest extends JSONSerialisationTest<RBMBackedTimestampSet> {
    private SortedSet<Instant> instants = new TreeSet<>();

    private Instant instant1;

    private Instant instant2;

    @Test
    public void shouldSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final RBMBackedTimestampSet boundedTimestampSet = new RBMBackedTimestampSet(TimeBucket.SECOND);
        IntStream.range(0, 20).forEach(( i) -> {
            boundedTimestampSet.add(Instant.ofEpochMilli((i * 1000L)));
        });
        // When
        final byte[] json = JSONSerialiser.serialise(boundedTimestampSet, true);
        final RBMBackedTimestampSet deserialisedObj = JSONSerialiser.deserialise(json, RBMBackedTimestampSet.class);
        // Then
        Assert.assertEquals(boundedTimestampSet, deserialisedObj);
    }

    @Test
    public void testGet() {
        testGet(instants);
        final SortedSet<Instant> randomDates = new TreeSet<>();
        IntStream.range(0, 100).forEach(( i) -> randomDates.add(Instant.ofEpochMilli(((instant1.toEpochMilli()) + (i * 12345678L)))));
        testGet(randomDates);
    }

    @Test
    public void testGetEarliestAndGetLatest() {
        // Given
        final RBMBackedTimestampSet timestampSet = new RBMBackedTimestampSet(SECOND);
        timestampSet.add(instant1);
        timestampSet.add(instant2);
        // When
        final Instant earliest = timestampSet.getEarliest();
        final Instant latest = timestampSet.getLatest();
        // Then
        Assert.assertEquals(Instant.ofEpochMilli(CommonTimeUtil.timeToBucket(instant1.toEpochMilli(), SECOND)), earliest);
        Assert.assertEquals(Instant.ofEpochMilli(CommonTimeUtil.timeToBucket(instant2.toEpochMilli(), SECOND)), latest);
    }

    @Test
    public void testGetNumberOfTimestamps() {
        // Given
        final RBMBackedTimestampSet timestampSet = new RBMBackedTimestampSet(SECOND);
        timestampSet.add(instant1);
        timestampSet.add(instant1.plus(Duration.ofDays(100L)));
        timestampSet.add(instant1.plus(Duration.ofDays(200L)));
        timestampSet.add(instant1.plus(Duration.ofDays(300L)));
        // Add another instant that should be truncated to the same as the previous one
        timestampSet.add(instant1.plus(Duration.ofDays(300L)).plusMillis(1L));
        // When
        final long numberOfTimestamps = timestampSet.getNumberOfTimestamps();
        // Then
        Assert.assertEquals(4, numberOfTimestamps);
    }

    @Test
    public void testEqualsAndHashcode() {
        // Given
        final RBMBackedTimestampSet timestampSet1 = new RBMBackedTimestampSet(SECOND);
        timestampSet1.add(instant1);
        timestampSet1.add(instant2);
        final RBMBackedTimestampSet timestampSet2 = new RBMBackedTimestampSet(SECOND);
        timestampSet2.add(instant1);
        timestampSet2.add(instant2);
        final RBMBackedTimestampSet timestampSet3 = new RBMBackedTimestampSet(SECOND);
        timestampSet3.add(instant1);
        final RBMBackedTimestampSet timestampSet4 = new RBMBackedTimestampSet(MINUTE);
        timestampSet4.add(instant1);
        // When
        final boolean equal1And2 = timestampSet1.equals(timestampSet2);
        final boolean equal1And3 = timestampSet1.equals(timestampSet3);
        final boolean equal1And4 = timestampSet1.equals(timestampSet4);
        final int hashCode1 = timestampSet1.hashCode();
        final int hashCode2 = timestampSet2.hashCode();
        final int hashCode3 = timestampSet3.hashCode();
        final int hashCode4 = timestampSet4.hashCode();
        // Then
        Assert.assertTrue(equal1And2);
        Assert.assertFalse(equal1And3);
        Assert.assertFalse(equal1And4);
        Assert.assertEquals(hashCode1, hashCode2);
        Assert.assertNotEquals(hashCode1, hashCode3);
        Assert.assertNotEquals(hashCode1, hashCode4);
    }
}

