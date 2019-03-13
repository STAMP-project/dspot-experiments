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


import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CommonTimeUtil.TimeBucket;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;
import uk.gov.gchq.gaffer.time.LongTimeSeries;


public class DeltaLongTimeSeriesSerialiserTest extends ToBytesSerialisationTest<LongTimeSeries> {
    private static final DeltaLongTimeSeriesSerialiser serialiser = new DeltaLongTimeSeriesSerialiser();

    @Test
    public void testSerialiser() throws SerialisationException {
        testSerialiser(getExampleValueMillisecond());
        testSerialiser(getExampleValueSecond());
        testSerialiser(getExampleValueMinute());
        testSerialiser(getExampleValueHour());
        testSerialiser(getExampleValueDay());
        testSerialiser(getExampleValueMonth());
        testSerialiser(getExampleValueYear());
    }

    @Test
    public void testSerialiserForEmptyTimeSeries() throws SerialisationException {
        // Given
        final LongTimeSeries timeSeries = new LongTimeSeries(TimeBucket.SECOND);
        // When
        final byte[] serialised = DeltaLongTimeSeriesSerialiserTest.serialiser.serialise(timeSeries);
        final LongTimeSeries deserialised = DeltaLongTimeSeriesSerialiserTest.serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(timeSeries, deserialised);
    }

    @Test
    public void testValueCloseToLongMax() throws SerialisationException {
        // Given
        final LongTimeSeries timeSeries = new LongTimeSeries(TimeBucket.SECOND);
        timeSeries.upsert(Instant.ofEpochMilli(1000L), Long.MAX_VALUE);
        // When
        final byte[] serialised = DeltaLongTimeSeriesSerialiserTest.serialiser.serialise(timeSeries);
        final LongTimeSeries deserialised = DeltaLongTimeSeriesSerialiserTest.serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(timeSeries, deserialised);
        Assert.assertEquals(Long.MAX_VALUE, ((long) (deserialised.get(Instant.ofEpochMilli(1000L)))));
    }

    @Test
    public void testCanHandle() throws SerialisationException {
        Assert.assertTrue(DeltaLongTimeSeriesSerialiserTest.serialiser.canHandle(LongTimeSeries.class));
        Assert.assertFalse(DeltaLongTimeSeriesSerialiserTest.serialiser.canHandle(String.class));
    }

    @Test
    public void testConsistent() throws SerialisationException {
        // Given
        final LongTimeSeries timeSeries1 = new LongTimeSeries(TimeBucket.SECOND);
        timeSeries1.upsert(Instant.ofEpochMilli(1000L), 10L);
        final LongTimeSeries timeSeries2 = new LongTimeSeries(TimeBucket.SECOND);
        timeSeries2.upsert(Instant.ofEpochMilli(1000L), 5L);
        timeSeries2.upsert(Instant.ofEpochMilli(1000L), 5L);
        // When
        final byte[] serialised1 = DeltaLongTimeSeriesSerialiserTest.serialiser.serialise(timeSeries1);
        final byte[] serialised2 = DeltaLongTimeSeriesSerialiserTest.serialiser.serialise(timeSeries2);
        // Then
        Assert.assertArrayEquals(serialised1, serialised2);
    }
}

