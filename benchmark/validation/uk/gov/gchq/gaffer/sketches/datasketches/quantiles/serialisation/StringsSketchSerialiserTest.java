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


import com.yahoo.sketches.quantiles.ItemsSketch;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class StringsSketchSerialiserTest {
    private static final double DELTA = 0.01;

    private static final StringsSketchSerialiser SERIALISER = new StringsSketchSerialiser();

    @Test
    public void testSerialiseAndDeserialise() {
        final ItemsSketch<String> sketch = ItemsSketch.getInstance(Comparator.naturalOrder());
        sketch.update("A");
        sketch.update("B");
        sketch.update("C");
        testSerialiser(sketch);
        final ItemsSketch<String> emptySketch = ItemsSketch.getInstance(Comparator.naturalOrder());
        testSerialiser(emptySketch);
    }

    @Test
    public void testCanHandleDoublesUnion() {
        Assert.assertTrue(StringsSketchSerialiserTest.SERIALISER.canHandle(ItemsSketch.class));
        Assert.assertFalse(StringsSketchSerialiserTest.SERIALISER.canHandle(String.class));
    }
}

