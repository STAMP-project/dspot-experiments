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
package uk.gov.gchq.gaffer.sketches.datasketches.sampling.serialisation;


import com.yahoo.sketches.sampling.ReservoirItemsSketch;
import org.junit.Assert;
import org.junit.Test;


public class ReservoirStringsSketchSerialiserTest {
    private static final ReservoirStringsSketchSerialiser SERIALISER = new ReservoirStringsSketchSerialiser();

    @Test
    public void testSerialiseAndDeserialise() {
        final ReservoirItemsSketch<String> sketch = ReservoirItemsSketch.newInstance(20);
        sketch.update("1");
        sketch.update("2");
        sketch.update("3");
        testSerialiser(sketch);
        final ReservoirItemsSketch<String> emptySketch = ReservoirItemsSketch.newInstance(20);
        testSerialiser(emptySketch);
    }

    @Test
    public void testCanHandleReservoirItemsSketch() {
        Assert.assertTrue(ReservoirStringsSketchSerialiserTest.SERIALISER.canHandle(ReservoirItemsSketch.class));
        Assert.assertFalse(ReservoirStringsSketchSerialiserTest.SERIALISER.canHandle(String.class));
    }
}

