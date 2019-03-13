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
package uk.gov.gchq.gaffer.serialisation.implementation;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.ParameterisedTestObject;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.SimpleTestObject;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class JavaSerialiserTest extends ToBytesSerialisationTest<Object> {
    @Test
    public void testPrimitiveSerialisation() throws SerialisationException {
        final byte[] b = serialiser.serialise(2);
        final Object o = serialiser.deserialise(b);
        Assert.assertEquals(Integer.class, o.getClass());
        Assert.assertEquals(2, o);
    }

    @Test
    public void canHandleUnParameterisedDAO() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(SimpleTestObject.class));
    }

    @Test
    public void testDAOSerialisation() throws SerialisationException {
        final SimpleTestObject test = new SimpleTestObject();
        test.setX("Test");
        final byte[] b = serialiser.serialise(test);
        final Object o = serialiser.deserialise(b);
        Assert.assertEquals(SimpleTestObject.class, o.getClass());
        Assert.assertEquals("Test", ((SimpleTestObject) (o)).getX());
    }

    @Test
    public void canHandleParameterisedDAO() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(ParameterisedTestObject.class));
    }

    @Test
    public void testParameterisedDAOSerialisation() throws SerialisationException {
        final ParameterisedTestObject<Integer> test = new ParameterisedTestObject<>();
        test.setX("Test");
        test.setK(2);
        final byte[] b = serialiser.serialise(test);
        final Object o = serialiser.deserialise(b);
        Assert.assertEquals(ParameterisedTestObject.class, o.getClass());
        Assert.assertEquals("Test", ((ParameterisedTestObject) (o)).getX());
        Assert.assertEquals(Integer.class, ((ParameterisedTestObject) (o)).getK().getClass());
        Assert.assertEquals(2, ((ParameterisedTestObject) (o)).getK());
    }
}

