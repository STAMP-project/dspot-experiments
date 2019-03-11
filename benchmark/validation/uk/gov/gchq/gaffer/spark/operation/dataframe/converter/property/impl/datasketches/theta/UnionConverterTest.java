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
package uk.gov.gchq.gaffer.spark.operation.dataframe.converter.property.impl.datasketches.theta;


import com.yahoo.sketches.theta.SetOperation;
import com.yahoo.sketches.theta.Union;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.spark.operation.dataframe.converter.exception.ConversionException;


public class UnionConverterTest {
    private static final double DELTA = 1.0E-10;

    private static final UnionConverter UNION_CONVERTER = new UnionConverter();

    @Test
    public void testConverter() throws ConversionException {
        final Union union = SetOperation.builder().buildUnion();
        union.update(1.0);
        union.update(2.0);
        union.update(3.0);
        Assert.assertEquals(union.getResult().getEstimate(), UnionConverterTest.UNION_CONVERTER.convert(union), UnionConverterTest.DELTA);
        final Union emptyUnion = SetOperation.builder().buildUnion();
        Assert.assertEquals(emptyUnion.getResult().getEstimate(), UnionConverterTest.UNION_CONVERTER.convert(emptyUnion), UnionConverterTest.DELTA);
    }

    @Test
    public void testCanHandleUnion() {
        Assert.assertTrue(UnionConverterTest.UNION_CONVERTER.canHandle(Union.class));
        Assert.assertFalse(UnionConverterTest.UNION_CONVERTER.canHandle(String.class));
    }
}

