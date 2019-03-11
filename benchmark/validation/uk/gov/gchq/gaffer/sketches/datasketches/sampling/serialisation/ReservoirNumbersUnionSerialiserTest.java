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


import com.yahoo.sketches.sampling.ReservoirItemsUnion;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.sketches.clearspring.cardinality.serialisation.ViaCalculatedArrayValueSerialiserTest;


public class ReservoirNumbersUnionSerialiserTest extends ViaCalculatedArrayValueSerialiserTest<ReservoirItemsUnion<Number>, Number> {
    @Test
    public void testCanHandleReservoirItemsUnion() {
        Assert.assertTrue(serialiser.canHandle(ReservoirItemsUnion.class));
        Assert.assertFalse(serialiser.canHandle(String.class));
    }
}

