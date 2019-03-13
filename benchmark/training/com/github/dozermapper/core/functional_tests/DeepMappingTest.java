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
import com.github.dozermapper.core.vo.Car;
import com.github.dozermapper.core.vo.InsideTestObject;
import com.github.dozermapper.core.vo.InsideTestObjectPrime;
import com.github.dozermapper.core.vo.MetalThingyIF;
import com.github.dozermapper.core.vo.deep.DestDeepObj;
import com.github.dozermapper.core.vo.deep.HomeDescription;
import com.github.dozermapper.core.vo.deep.House;
import com.github.dozermapper.core.vo.deep.Person;
import com.github.dozermapper.core.vo.deep.SrcDeepObj;
import com.github.dozermapper.core.vo.deep2.Dest;
import com.github.dozermapper.core.vo.deep2.Src;
import org.junit.Assert;
import org.junit.Test;


public class DeepMappingTest extends AbstractFunctionalTest {
    @Test
    public void testDeepMapping() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        SrcDeepObj src = testDataFactory.getSrcDeepObj();
        DestDeepObj dest = mapper.map(src, DestDeepObj.class);
        SrcDeepObj src2 = mapper.map(dest, SrcDeepObj.class);
        DestDeepObj dest2 = mapper.map(src2, DestDeepObj.class);
        Assert.assertEquals(src, src2);
        Assert.assertEquals(dest, dest2);
    }

    @Test
    public void testDeepPropertyOneWay() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        House house = newInstance(House.class);
        Person owner = newInstance(Person.class);
        owner.setYourName("myName");
        house.setOwner(owner);
        HomeDescription desc = mapper.map(house, HomeDescription.class);
        Assert.assertEquals(desc.getDescription().getMyName(), "myName");
        // make sure we don't map back
        House house2 = mapper.map(desc, House.class);
        Assert.assertNull(house2.getOwner().getYourName());
    }

    @Test
    public void testDeepInterfaceWithHint() {
        Mapper mapper = getMapper("mappings/fieldAttributeMapping.xml");
        InsideTestObject ito = newInstance(InsideTestObject.class);
        House house = newInstance(House.class);
        MetalThingyIF thingy = newInstance(Car.class);
        thingy.setName("name");
        house.setThingy(thingy);
        ito.setHouse(house);
        InsideTestObjectPrime itop = mapper.map(ito, InsideTestObjectPrime.class);
        Assert.assertEquals("name", itop.getDeepInterfaceString());
        mapper.map(itop, InsideTestObject.class);
        Assert.assertEquals("name", ito.getHouse().getThingy().getName());
    }

    /* Related to feature request #1456486. Deep mapping with custom getter/setter does not work
    Also related to #2459076
     */
    @Test
    public void testDeepMapping_UsingCustomGetSetMethods() {
        mapper = super.getMapper("mappings/deepMappingUsingCustomGetSet.xml");
        Src src = newInstance(Src.class);
        src.setSrcField("srcFieldValue");
        Dest dest = mapper.map(src, Dest.class);
        Assert.assertNotNull(dest.getDestField().getNestedDestField().getNestedNestedDestField());
        Assert.assertEquals(src.getSrcField(), dest.getDestField().getNestedDestField().getNestedNestedDestField());
        Assert.assertTrue("should have been set by customer setter method", dest.getDestField().getNestedDestField().isSetWithCustomMethod());
        Src dest2 = mapper.map(dest, Src.class);
        Assert.assertNotNull(dest2.getSrcField());
        Assert.assertEquals(dest.getDestField().getNestedDestField().getNestedNestedDestField(), dest2.getSrcField());
    }
}

