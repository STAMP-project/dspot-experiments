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
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;
import uk.gov.gchq.gaffer.time.BoundedTimestampSet;


public class BoundedTimestampSetSerialiserTest extends ToBytesSerialisationTest<BoundedTimestampSet> {
    @Test
    public void testSerialiserWhenNotFull() throws SerialisationException {
        // Given
        final BoundedTimestampSet boundedTimestampSet = getExampleValue();
        // When
        final byte[] serialised = serialiser.serialise(boundedTimestampSet);
        final BoundedTimestampSet deserialised = serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(boundedTimestampSet.getState(), deserialised.getState());
        Assert.assertEquals(boundedTimestampSet.getTimeBucket(), deserialised.getTimeBucket());
        Assert.assertEquals(boundedTimestampSet.getMaxSize(), deserialised.getMaxSize());
        Assert.assertEquals(boundedTimestampSet.getNumberOfTimestamps(), deserialised.getNumberOfTimestamps());
        Assert.assertEquals(boundedTimestampSet.getTimestamps(), deserialised.getTimestamps());
    }

    @Test
    public void testSerialiserWhenSampling() throws SerialisationException {
        // Given
        final Set<Instant> instants = new HashSet<>();
        IntStream.range(0, 1000).forEach(( i) -> instants.add(Instant.ofEpochMilli((i * 1000L))));
        final BoundedTimestampSet boundedTimestampSet = new BoundedTimestampSet(TimeBucket.SECOND, 10);
        instants.forEach(boundedTimestampSet::add);
        // When
        final byte[] serialised = serialiser.serialise(boundedTimestampSet);
        final BoundedTimestampSet deserialised = serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(boundedTimestampSet.getState(), deserialised.getState());
        Assert.assertEquals(boundedTimestampSet.getTimeBucket(), deserialised.getTimeBucket());
        Assert.assertEquals(boundedTimestampSet.getMaxSize(), deserialised.getMaxSize());
        Assert.assertEquals(boundedTimestampSet.getNumberOfTimestamps(), deserialised.getNumberOfTimestamps());
        Assert.assertEquals(boundedTimestampSet.getTimestamps(), deserialised.getTimestamps());
    }

    @Test
    public void testCanHandle() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(BoundedTimestampSet.class));
        Assert.assertFalse(serialiser.canHandle(String.class));
    }
}

