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
package uk.gov.gchq.gaffer.sketches.datasketches.quantiles.serialisation;


import com.yahoo.sketches.quantiles.DoublesSketch;
import com.yahoo.sketches.quantiles.UpdateDoublesSketch;
import org.junit.Assert;
import org.junit.Test;


public class DoublesSketchSerialiserTest {
    private static final double DELTA = 0.01;

    private static final DoublesSketchSerialiser SERIALISER = new DoublesSketchSerialiser();

    @Test
    public void testSerialiseAndDeserialise() {
        final UpdateDoublesSketch sketch = DoublesSketch.builder().build();
        sketch.update(1.0);
        sketch.update(2.0);
        sketch.update(3.0);
        testSerialiser(sketch);
        final DoublesSketch emptySketch = DoublesSketch.builder().build();
        testSerialiser(emptySketch);
    }

    @Test
    public void testCanHandleDoublesUnion() {
        Assert.assertTrue(DoublesSketchSerialiserTest.SERIALISER.canHandle(DoublesSketch.class));
        Assert.assertFalse(DoublesSketchSerialiserTest.SERIALISER.canHandle(String.class));
    }
}

