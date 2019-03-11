/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.dimension;


import ExtractionFn.ExtractionType.ONE_TO_ONE;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import org.apache.druid.query.extraction.MapLookupExtractor;
import org.apache.druid.query.lookup.LookupExtractor;
import org.apache.druid.query.lookup.LookupReferencesManager;
import org.apache.druid.query.lookup.MapLookupExtractorFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class LookupDimensionSpecTest {
    private static final Map<String, String> STRING_MAP = ImmutableMap.of("key", "value", "key2", "value2");

    private static LookupExtractor MAP_LOOKUP_EXTRACTOR = new MapLookupExtractor(LookupDimensionSpecTest.STRING_MAP, true);

    private static final LookupReferencesManager LOOKUP_REF_MANAGER = EasyMock.createMock(LookupReferencesManager.class);

    static {
        EasyMock.expect(LookupDimensionSpecTest.LOOKUP_REF_MANAGER.get(EasyMock.eq("lookupName"))).andReturn(new org.apache.druid.query.lookup.LookupExtractorFactoryContainer("v0", new MapLookupExtractorFactory(LookupDimensionSpecTest.STRING_MAP, false))).anyTimes();
        EasyMock.replay(LookupDimensionSpecTest.LOOKUP_REF_MANAGER);
    }

    private final DimensionSpec lookupDimSpec = new LookupDimensionSpec("dimName", "outputName", LookupDimensionSpecTest.MAP_LOOKUP_EXTRACTOR, false, null, null, null, true);

    @Test(expected = Exception.class)
    public void testExceptionWhenNameAndLookupNotNull() {
        new LookupDimensionSpec("dimName", "outputName", LookupDimensionSpecTest.MAP_LOOKUP_EXTRACTOR, false, "replace", "name", null, true);
    }

    @Test(expected = Exception.class)
    public void testExceptionWhenNameAndLookupNull() {
        new LookupDimensionSpec("dimName", "outputName", null, false, "replace", "", null, true);
    }

    @Test
    public void testGetDimension() {
        Assert.assertEquals("dimName", lookupDimSpec.getDimension());
    }

    @Test
    public void testGetOutputName() {
        Assert.assertEquals("outputName", lookupDimSpec.getOutputName());
    }

    @Test
    public void testPreservesOrdering() {
        Assert.assertFalse(lookupDimSpec.preservesOrdering());
    }

    @Test
    public void testIsOneToOne() {
        Assert.assertEquals(lookupDimSpec.getExtractionFn().getExtractionType(), ONE_TO_ONE);
    }
}

