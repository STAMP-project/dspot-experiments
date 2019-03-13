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
package uk.gov.gchq.gaffer.time.serialisation;


import CommonTimeUtil.TimeBucket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;
import uk.gov.gchq.gaffer.time.RBMBackedTimestampSet;


public class RBMBackedTimestampSetSerialiserTest extends ToBytesSerialisationTest<RBMBackedTimestampSet> {
    @Test
    public void testSerialiser() throws SerialisationException {
        // Given
        final RBMBackedTimestampSet rbmBackedTimestampSet = getExampleValue();
        // When
        final byte[] serialised = serialiser.serialise(rbmBackedTimestampSet);
        final RBMBackedTimestampSet deserialised = serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(rbmBackedTimestampSet, deserialised);
    }

    @Test
    public void testCanHandle() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(RBMBackedTimestampSet.class));
        Assert.assertFalse(serialiser.canHandle(String.class));
    }

    @Test
    public void testSerialisationSizesQuotedInJavadoc() throws SerialisationException {
        // Given
        final Random random = new Random(123456789L);
        final Instant instant = ZonedDateTime.of(2017, 1, 1, 1, 1, 1, 0, ZoneId.of("UTC")).toInstant();
        // Set of 100 minutes in a day and the time bucket is a minute
        final RBMBackedTimestampSet rbmBackedTimestampSet1 = new RBMBackedTimestampSet(TimeBucket.MINUTE);
        IntStream.range(0, 100).forEach(( i) -> rbmBackedTimestampSet1.add(instant.plusSeconds(((random.nextInt((24 * 60))) * 60))));
        // Set of every minute in a year and the time bucket is a minute
        final RBMBackedTimestampSet rbmBackedTimestampSet2 = new RBMBackedTimestampSet(TimeBucket.MINUTE);
        IntStream.range(0, ((365 * 24) * 60)).forEach(( i) -> rbmBackedTimestampSet2.add(instant.plusSeconds((i * 60))));
        // Set of every second in a year is set and the time bucket is a second
        final RBMBackedTimestampSet rbmBackedTimestampSet3 = new RBMBackedTimestampSet(TimeBucket.SECOND);
        IntStream.range(0, (((365 * 24) * 60) * 60)).forEach(( i) -> rbmBackedTimestampSet3.add(instant.plusSeconds(i)));
        // When
        final int lengthSet1 = serialiser.serialise(rbmBackedTimestampSet1).length;
        final int lengthSet2 = serialiser.serialise(rbmBackedTimestampSet2).length;
        final int lengthSet3 = serialiser.serialise(rbmBackedTimestampSet3).length;
        // Then
        Assert.assertTrue(((200 < lengthSet1) && (lengthSet1 < 220)));
        Assert.assertTrue(((72000 < lengthSet2) && (lengthSet2 < 74000)));
        Assert.assertTrue(((3900000 < lengthSet3) && (lengthSet3 < 4100000)));
    }
}

