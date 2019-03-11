/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.sketches.clearspring.cardinality.serialisation;


import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;


public class HyperLogLogPlusSerialiserTest extends ViaCalculatedValueSerialiserTest<HyperLogLogPlus, Long> {
    @Test
    public void testSerialiseAndDeserialiseWhenEmpty() {
        HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus(5, 5);
        long preSerialisationCardinality = hyperLogLogPlus.cardinality();
        byte[] hyperLogLogPlusSerialised;
        try {
            hyperLogLogPlusSerialised = serialiser.serialise(hyperLogLogPlus);
        } catch (final SerialisationException exception) {
            Assert.fail("A Serialisation Exception Occurred");
            return;
        }
        HyperLogLogPlus hyperLogLogPlusDeserialised;
        try {
            hyperLogLogPlusDeserialised = serialiser.deserialise(hyperLogLogPlusSerialised);
        } catch (final SerialisationException exception) {
            Assert.fail("A Serialisation Exception Occurred");
            return;
        }
        Assert.assertEquals(preSerialisationCardinality, hyperLogLogPlusDeserialised.cardinality());
    }

    @Test
    public void testSerialiseNullReturnsEmptyBytes() {
        // Given
        final byte[] hyperLogLogPlusSerialised = serialiser.serialiseNull();
        // Then
        Assert.assertArrayEquals(new byte[0], hyperLogLogPlusSerialised);
    }

    @Test
    public void testDeserialiseEmptyBytesReturnsNull() throws SerialisationException {
        // Given
        final HyperLogLogPlus hllp = serialiser.deserialiseEmpty();
        // Then
        Assert.assertNull(hllp);
    }

    @Test
    public void testCanHandleHyperLogLogPlus() {
        Assert.assertTrue(serialiser.canHandle(HyperLogLogPlus.class));
    }
}

