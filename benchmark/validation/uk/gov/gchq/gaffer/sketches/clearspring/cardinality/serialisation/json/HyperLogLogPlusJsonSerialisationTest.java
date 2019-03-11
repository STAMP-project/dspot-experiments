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
package uk.gov.gchq.gaffer.sketches.clearspring.cardinality.serialisation.json;


import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class HyperLogLogPlusJsonSerialisationTest {
    private ObjectMapper mapper;

    @Test
    public void testValidHyperLogLogPlusSketchSerialisedCorrectly() throws IOException {
        // Given
        final String testString = "TestString";
        final HyperLogLogPlus sketch = new HyperLogLogPlus(5, 5);
        sketch.offer(testString);
        // When / Then
        runTestWithSketch(sketch);
    }

    @Test
    public void testEmptyHyperLogLogPlusSketchIsSerialised() throws IOException {
        // Given
        final HyperLogLogPlus sketch = new HyperLogLogPlus(5, 5);
        // When / Then
        runTestWithSketch(sketch);
    }

    @Test
    public void testNullHyperLogLogPlusSketchIsSerialisedAsNullString() throws JsonProcessingException {
        // Given
        final HyperLogLogPlus sketch = null;
        // When
        final String sketchAsString = mapper.writeValueAsString(sketch);
        // Then - Serialisation framework will serialise nulls as 'null' string.
        Assert.assertEquals("null", sketchAsString);
    }
}

