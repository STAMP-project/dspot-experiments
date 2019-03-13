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
import uk.gov.gchq.gaffer.types.IntegerFreqMap;


public class IntegerFreqMapSerialiserTest extends ToBytesSerialisationTest<IntegerFreqMap> {
    @Test
    public void canSerialiseEmptyFreqMap() throws SerialisationException {
        byte[] b = serialiser.serialise(new IntegerFreqMap());
        Object o = serialiser.deserialise(b);
        Assert.assertEquals(IntegerFreqMap.class, o.getClass());
        Assert.assertEquals(0, size());
    }

    @Test
    public void canSerialiseDeSerialiseFreqMapWithValues() throws SerialisationException {
        IntegerFreqMap freqMap = new IntegerFreqMap();
        freqMap.put("x", 10);
        freqMap.put("y", 5);
        freqMap.put("z", 20);
        byte[] b = serialiser.serialise(freqMap);
        IntegerFreqMap o = ((IntegerFreqMap) (serialiser.deserialise(b)));
        Assert.assertEquals(IntegerFreqMap.class, o.getClass());
        Assert.assertEquals(((Integer) (10)), o.get("x"));
        Assert.assertEquals(((Integer) (5)), o.get("y"));
        Assert.assertEquals(((Integer) (20)), o.get("z"));
    }

    @Test
    public void testSerialiserWillSkipEntryWithNullValue() throws SerialisationException {
        IntegerFreqMap freqMap = new IntegerFreqMap();
        freqMap.put("x", null);
        freqMap.put("y", 5);
        freqMap.put("z", 20);
        byte[] b = serialiser.serialise(freqMap);
        IntegerFreqMap o = ((IntegerFreqMap) (serialiser.deserialise(b)));
        Assert.assertEquals(IntegerFreqMap.class, o.getClass());
        Assert.assertNull(o.get("x"));
        Assert.assertEquals(((Integer) (5)), o.get("y"));
        Assert.assertEquals(((Integer) (20)), o.get("z"));
    }

    @Test
    public void cantSerialiseStringClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(String.class));
    }

    @Test
    public void canSerialiseFreqMap() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(IntegerFreqMap.class));
    }
}

