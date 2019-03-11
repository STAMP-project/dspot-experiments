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
package uk.gov.gchq.gaffer.serialisation.implementation;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.IntegerSerialiser;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class MapSerialiserTest extends ToBytesSerialisationTest<Map<? extends Object, ? extends Object>> {
    @Test
    public void shouldSerialiseAndDeSerialiseOverlappingMapValuesWithDifferentKeys() throws SerialisationException {
        Map<String, Long> map = getExampleValue();
        byte[] b = serialiser.serialise(map);
        Map o = serialiser.deserialise(b);
        Assert.assertEquals(HashMap.class, o.getClass());
        Assert.assertEquals(6, o.size());
        Assert.assertEquals(map, o);
        Assert.assertEquals(((Long) (123298333L)), o.get("one"));
        Assert.assertEquals(((Long) (342903339L)), o.get("two"));
        Assert.assertEquals(((Long) (123298333L)), o.get("three"));
        Assert.assertEquals(((Long) (345353439L)), o.get("four"));
        Assert.assertEquals(((Long) (123338333L)), o.get("five"));
        Assert.assertEquals(((Long) (345353439L)), o.get("six"));
    }

    @Test
    public void mapSerialiserTest() throws SerialisationException {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 3);
        map.put(2, 7);
        map.put(3, 11);
        ((MapSerialiser) (serialiser)).setKeySerialiser(new IntegerSerialiser());
        ((MapSerialiser) (serialiser)).setValueSerialiser(new IntegerSerialiser());
        setMapClass(LinkedHashMap.class);
        byte[] b = serialiser.serialise(map);
        Map o = serialiser.deserialise(b);
        Assert.assertEquals(LinkedHashMap.class, o.getClass());
        Assert.assertEquals(3, o.size());
        Assert.assertEquals(3, o.get(1));
        Assert.assertEquals(7, o.get(2));
        Assert.assertEquals(11, o.get(3));
    }
}

