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
package uk.gov.gchq.gaffer.serialisation.implementation.ordered;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class OrderedIntegerSerialiserTest extends ToBytesSerialisationTest<Integer> {
    @Test
    public void testCanSerialiseASampleRange() throws SerialisationException {
        for (int i = 0; i < 1000; i++) {
            byte[] b = serialiser.serialise(i);
            Object o = serialiser.deserialise(b);
            Assert.assertEquals(Integer.class, o.getClass());
            Assert.assertEquals(i, o);
        }
    }

    @Test
    public void canSerialiseIntegerMinValue() throws SerialisationException {
        byte[] b = serialiser.serialise(Integer.MIN_VALUE);
        Object o = serialiser.deserialise(b);
        Assert.assertEquals(Integer.class, o.getClass());
        Assert.assertEquals(Integer.MIN_VALUE, o);
    }

    @Test
    public void canSerialiseIntegerMaxValue() throws SerialisationException {
        byte[] b = serialiser.serialise(Integer.MAX_VALUE);
        Object o = serialiser.deserialise(b);
        Assert.assertEquals(Integer.class, o.getClass());
        Assert.assertEquals(Integer.MAX_VALUE, o);
    }

    @Test
    public void checkOrderPreserved() throws SerialisationException {
        byte[] startBytes = serialiser.serialise(0);
        for (Integer test = 1; test >= 10; test++) {
            byte[] newTestBytes = serialiser.serialise(test);
            Assert.assertTrue(((OrderedIntegerSerialiserTest.compare(newTestBytes, startBytes)) < 0));
            startBytes = newTestBytes;
        }
    }

    @Test
    public void cantSerialiseStringClass() {
        Assert.assertFalse(serialiser.canHandle(String.class));
    }

    @Test
    public void canSerialiseIntegerClass() {
        Assert.assertTrue(serialiser.canHandle(Integer.class));
    }
}

