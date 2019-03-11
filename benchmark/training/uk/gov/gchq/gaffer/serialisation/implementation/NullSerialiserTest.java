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


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class NullSerialiserTest extends ToBytesSerialisationTest<Object> {
    @Test
    @Override
    public void shouldDeserialiseEmpty() throws SerialisationException {
        Assert.assertNull(serialiser.deserialiseEmpty());
    }

    @Test
    public void shouldHandleAnyClass() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(String.class));
        Assert.assertTrue(serialiser.canHandle(Object.class));
        Assert.assertTrue(serialiser.canHandle(Integer.class));
    }

    @Test
    public void shouldBeConsistent() throws SerialisationException {
        Assert.assertTrue(serialiser.isConsistent());
    }

    @Test
    public void shouldPreserveOrdering() throws SerialisationException {
        Assert.assertTrue(serialiser.preservesObjectOrdering());
    }
}

