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


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.IntegerSerialiser;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class SetSerialiserTest extends ToBytesSerialisationTest<Set<? extends Object>> {
    @Test
    public void shouldSerialiseAndDeSerialiseSet() throws SerialisationException {
        Set<String> set = getExampleValue();
        byte[] b = serialiser.serialise(set);
        Set o = serialiser.deserialise(b);
        Assert.assertEquals(HashSet.class, o.getClass());
        Assert.assertEquals(6, o.size());
        Assert.assertEquals(set, o);
        Assert.assertTrue(o.contains("one"));
        Assert.assertTrue(o.contains("two"));
        Assert.assertTrue(o.contains("three"));
        Assert.assertTrue(o.contains("four"));
        Assert.assertTrue(o.contains("five"));
        Assert.assertTrue(o.contains("six"));
    }

    @Test
    public void setSerialiserWithOverlappingValuesTest() throws SerialisationException {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(3);
        set.add(2);
        set.add(7);
        set.add(3);
        set.add(11);
        ((SetSerialiser) (serialiser)).setObjectSerialiser(new IntegerSerialiser());
        setSetClass(LinkedHashSet.class);
        byte[] b = serialiser.serialise(set);
        Set o = serialiser.deserialise(b);
        Assert.assertEquals(LinkedHashSet.class, o.getClass());
        Assert.assertEquals(5, o.size());
        Assert.assertTrue(o.contains(1));
        Assert.assertTrue(o.contains(3));
        Assert.assertTrue(o.contains(2));
        Assert.assertTrue(o.contains(7));
        Assert.assertTrue(o.contains(11));
    }

    @Test
    @Override
    public void shouldDeserialiseEmpty() throws SerialisationException {
        Assert.assertEquals(new HashSet(), serialiser.deserialiseEmpty());
    }
}

