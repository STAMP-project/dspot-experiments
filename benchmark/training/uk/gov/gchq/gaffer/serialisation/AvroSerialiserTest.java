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


public class AvroSerialiserTest extends ToBytesSerialisationTest<Object> {
    @Test
    public void testCanHandleObjectClass() {
        Assert.assertTrue(serialiser.canHandle(Object.class));
    }

    @Test
    public void testPrimitiveSerialisation() throws SerialisationException {
        byte[] b = serialiser.serialise(2);
        Object o = serialiser.deserialise(b);
        Assert.assertEquals(Integer.class, o.getClass());
        Assert.assertEquals(2, o);
    }
}

