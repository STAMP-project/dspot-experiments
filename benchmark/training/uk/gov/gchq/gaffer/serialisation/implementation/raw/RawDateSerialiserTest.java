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
package uk.gov.gchq.gaffer.serialisation.implementation.raw;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.serialisation.SerialisationTest;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialisationTest;


public class RawDateSerialiserTest extends ToBytesSerialisationTest<Date> {
    @Test
    public void testCanSerialiseASampleRange() throws SerialisationException {
        for (long i = 1000000L; i < 1001000L; i++) {
            final byte[] b = serialiser.serialise(new Date(i));
            final Object o = serialiser.deserialise(b);
            Assert.assertEquals(Date.class, o.getClass());
            Assert.assertEquals(new Date(i), o);
        }
    }

    @Test
    public void canSerialiseEpoch() throws SerialisationException {
        final byte[] b = serialiser.serialise(new Date(0));
        final Object o = serialiser.deserialise(b);
        Assert.assertEquals(Date.class, o.getClass());
        Assert.assertEquals(new Date(0), o);
    }

    @Test
    public void cantSerialiseStringClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(String.class));
    }

    @Test
    public void canSerialiseDateClass() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(Date.class));
    }

    @Test
    public void checkOrderPreserved() throws SerialisationException {
        Date now = new Date(1L);
        Date aDayLater = new Date(((now.getTime()) + (((24 * 60) * 60) * 1000L)));
        Assert.assertTrue(((RawDateSerialiserTest.compare(serialiser.serialise(now), serialiser.serialise(aDayLater))) < 0));
    }
}

