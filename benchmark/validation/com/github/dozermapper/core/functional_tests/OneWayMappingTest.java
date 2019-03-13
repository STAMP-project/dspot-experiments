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


import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.oneway.DestClass;
import com.github.dozermapper.core.vo.oneway.Holder;
import com.github.dozermapper.core.vo.oneway.SourceClass;
import org.junit.Assert;
import org.junit.Test;


public class OneWayMappingTest extends AbstractFunctionalTest {
    @Test
    public void testOneWay() {
        Mapper mapper = getMapper("mappings/oneWayMapping.xml");
        SourceClass source = newInstance(SourceClass.class, new Object[]{ "A" });
        Holder holder = mapper.map(source, Holder.class);
        DestClass dest = holder.getDest();
        Assert.assertNotNull(dest);
        Assert.assertEquals("A", dest.anonymousAccessor());
    }
}

