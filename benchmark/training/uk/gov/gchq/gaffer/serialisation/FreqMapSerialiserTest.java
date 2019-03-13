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
package uk.gov.gchq.gaffer.serialisation;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.types.FreqMap;


public class FreqMapSerialiserTest extends ToBytesSerialisationTest<FreqMap> {
    @Test
    public void canSerialiseEmptyFreqMap() throws SerialisationException {
        byte[] b = serialiser.serialise(new FreqMap());
        Object o = serialiser.deserialise(b);
        Assert.assertEquals(FreqMap.class, o.getClass());
        Assert.assertEquals(0, size());
    }

    @Test
    public void shouldSerialiseDeserialiseFreqMapWithValues() throws SerialisationException {
        // Given
        final FreqMap freqMap = new FreqMap();
        freqMap.put("x", 10L);
        freqMap.put("y", 5L);
        freqMap.put("z", 20L);
        // When
        final byte[] serialised = serialiser.serialise(freqMap);
        final FreqMap deserialised = serialiser.deserialise(serialised);
        // Then
        Assert.assertEquals(((Long) (10L)), deserialised.get("x"));
        Assert.assertEquals(((Long) (5L)), deserialised.get("y"));
        Assert.assertEquals(((Long) (20L)), deserialised.get("z"));
    }

    @Test
    public void shouldSerialiseDeserialiseFreqMapWithAnEmptyKey() throws SerialisationException {
        // Given
        final FreqMap freqMap = new FreqMap();
        freqMap.put("", 10L);
        freqMap.put("y", 5L);
        freqMap.put("z", 20L);
        // When
        final byte[] serialised = serialiser.serialise(freqMap);
        final FreqMap deserialised = serialiser.deserialise(serialised);
        Assert.assertEquals(((Long) (10L)), deserialised.get(""));
        Assert.assertEquals(((Long) (5L)), deserialised.get("y"));
        Assert.assertEquals(((Long) (20L)), deserialised.get("z"));
    }

    @Test
    public void shouldSkipEntryWithNullKey() throws SerialisationException {
        // Given
        final FreqMap freqMap = new FreqMap();
        freqMap.put(null, 10L);
        freqMap.put("y", 5L);
        freqMap.put("z", 20L);
        // When
        final byte[] serialised = serialiser.serialise(freqMap);
        final FreqMap deserialised = serialiser.deserialise(serialised);
        Assert.assertFalse(deserialised.containsKey("x"));
        Assert.assertEquals(((Long) (5L)), deserialised.get("y"));
        Assert.assertEquals(((Long) (20L)), deserialised.get("z"));
    }

    @Test
    public void shouldSkipEntryWithNullValues() throws SerialisationException {
        // Given
        final FreqMap freqMap = new FreqMap();
        freqMap.put("v", null);
        freqMap.put("w", 5L);
        freqMap.put("x", null);
        freqMap.put("y", 20L);
        freqMap.put("z", null);
        // When
        final byte[] serialised = serialiser.serialise(freqMap);
        final FreqMap deserialised = serialiser.deserialise(serialised);
        Assert.assertFalse(deserialised.containsKey("v"));
        Assert.assertEquals(((Long) (5L)), deserialised.get("w"));
        Assert.assertFalse(deserialised.containsKey("x"));
        Assert.assertEquals(((Long) (20L)), deserialised.get("y"));
        Assert.assertFalse(deserialised.containsKey("z"));
    }

    @Test
    public void cantSerialiseStringClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(String.class));
    }

    @Test
    public void canSerialiseFreqMap() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(FreqMap.class));
    }
}

