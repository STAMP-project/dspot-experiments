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
package uk.gov.gchq.gaffer.sketches.datasketches.theta.serialisation;


import com.yahoo.sketches.theta.Sketch;
import com.yahoo.sketches.theta.UpdateSketch;
import org.junit.Assert;
import org.junit.Test;


public class SketchSerialiserTest {
    private static final double DELTA = 0.01;

    private static final SketchSerialiser SERIALISER = new SketchSerialiser();

    @Test
    public void testSerialiseAndDeserialise() {
        final UpdateSketch sketch = UpdateSketch.builder().build();
        sketch.update(1.0);
        sketch.update(2.0);
        sketch.update(3.0);
        testSerialiser(sketch);
        final UpdateSketch emptySketch = UpdateSketch.builder().build();
        testSerialiser(emptySketch);
    }

    @Test
    public void testCanHandleUnion() {
        Assert.assertTrue(SketchSerialiserTest.SERIALISER.canHandle(Sketch.class));
        Assert.assertFalse(SketchSerialiserTest.SERIALISER.canHandle(String.class));
    }
}

