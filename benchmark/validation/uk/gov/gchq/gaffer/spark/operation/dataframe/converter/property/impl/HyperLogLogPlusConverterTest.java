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
package uk.gov.gchq.gaffer.spark.operation.dataframe.converter.property.impl;


import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.spark.operation.dataframe.converter.exception.ConversionException;


public class HyperLogLogPlusConverterTest {
    private static final HyperLogLogPlusConverter HYPER_LOG_LOG_PLUS_CONVERTER = new HyperLogLogPlusConverter();

    @Test
    public void testConverter() throws ConversionException {
        final HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus(5, 5);
        hyperLogLogPlus.offer("A");
        hyperLogLogPlus.offer("B");
        Assert.assertEquals(hyperLogLogPlus.cardinality(), ((long) (HyperLogLogPlusConverterTest.HYPER_LOG_LOG_PLUS_CONVERTER.convert(hyperLogLogPlus))));
        final HyperLogLogPlus emptyHyperLogLogPlus = new HyperLogLogPlus(5, 5);
        Assert.assertEquals(emptyHyperLogLogPlus.cardinality(), ((long) (HyperLogLogPlusConverterTest.HYPER_LOG_LOG_PLUS_CONVERTER.convert(emptyHyperLogLogPlus))));
    }

    @Test
    public void testCanHandleHyperLogLogPlus() {
        Assert.assertTrue(HyperLogLogPlusConverterTest.HYPER_LOG_LOG_PLUS_CONVERTER.canHandle(HyperLogLogPlus.class));
        Assert.assertFalse(HyperLogLogPlusConverterTest.HYPER_LOG_LOG_PLUS_CONVERTER.canHandle(String.class));
    }
}

