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
import com.github.dozermapper.core.vo.mapid.AContainer;
import com.github.dozermapper.core.vo.mapid.AListContainer;
import com.github.dozermapper.core.vo.mapid.BContainer;
import com.github.dozermapper.core.vo.mapid.BContainer2;
import org.junit.Assert;
import org.junit.Test;


public class MapIdTest extends AbstractFunctionalTest {
    @Test
    public void testMapIdWithSubClasses() {
        mapper = getMapper("mappings/inheritanceMapping4.xml");
        AContainer src = new AContainer();
        src.setAProperty(getA());
        BContainer dest = mapper.map(src, BContainer.class);
        Assert.assertNotNull("B property of dest should be instantiated if mapping by map-id works", dest.getBProperty());
        // Remap to each other to test bi-directional mapping
        AContainer mappedSrc = mapper.map(dest, AContainer.class);
        BContainer mappedDest = mapper.map(mappedSrc, BContainer.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testMapIdWithHint() {
        mapper = getMapper("mappings/mapIdWithHint.xml");
        AListContainer aListContainer = new AListContainer();
        aListContainer.getAList().add(getA());
        BContainer2 bContainer = new BContainer2();
        mapper.map(aListContainer, bContainer);
        Assert.assertNotNull(bContainer);
        Assert.assertNotNull(bContainer.getBField());
        Assert.assertEquals(((A) (aListContainer.getAList().get(0))).getSuperAField(), bContainer.getBField().getSuperBField());
        AListContainer newAListContainer = mapper.map(bContainer, AListContainer.class);
        Assert.assertEquals("failed reverse map", aListContainer, newAListContainer);
    }
}

