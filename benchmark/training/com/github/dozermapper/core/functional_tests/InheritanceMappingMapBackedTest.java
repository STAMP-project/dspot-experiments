/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.inheritance.A;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class InheritanceMappingMapBackedTest extends AbstractFunctionalTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testInheritedToMappedBacked() {
        /* Test two things.
         1) When wildcards are turned off and the destination is a map then the
           sub class fields where not being mapped because of a bug in mappedParentFields key generation
         2) If one side of a mapping is defined via an interface then the super class mapping was not being processed
           because the super classes and the interface classes were searched separately.
         */
        mapper = getMapper("mappings/inheritanceMappingMapBacked.xml");
        A src = createA();
        Map dest = mapper.map(src, Map.class);
        Assert.assertEquals(src.getSuperAField(), dest.get("superAField"));
        Assert.assertEquals(src.getSuperField1(), dest.get("superField1"));
        Assert.assertEquals(src.getField1(), dest.get("field1"));
        Assert.assertEquals(src.getFieldA(), dest.get("fieldA"));
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        Map mappedDest = mapper.map(mappedSrc, Map.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }
}

