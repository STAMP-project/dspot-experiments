/**
 * Copyright 2018-2019 Crown Copyright
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


import com.yahoo.sketches.kll.KllFloatsSketch;
import org.junit.Assert;
import org.junit.Test;


public class KllFloatsSketchSerialiserTest {
    private static final double DELTA = 0.01;

    private static final KllFloatsSketchSerialiser SERIALISER = new KllFloatsSketchSerialiser();

    @Test
    public void testSerialiseAndDeserialise() {
        final KllFloatsSketch sketch = new KllFloatsSketch();
        sketch.update(1.0F);
        sketch.update(2.0F);
        sketch.update(3.0F);
        testSerialiser(sketch);
        final KllFloatsSketch emptySketch = new KllFloatsSketch();
        testSerialiser(emptySketch);
    }

    @Test
    public void testCanHandleKllFloatsSketch() {
        Assert.assertTrue(KllFloatsSketchSerialiserTest.SERIALISER.canHandle(KllFloatsSketch.class));
        Assert.assertFalse(KllFloatsSketchSerialiserTest.SERIALISER.canHandle(String.class));
    }
}

