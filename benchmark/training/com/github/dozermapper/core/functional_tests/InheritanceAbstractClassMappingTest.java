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


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.vo.abstractinheritance.A;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractA;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractACollectionContainer;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractAContainer;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractB;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractBCollectionContainer;
import com.github.dozermapper.core.vo.abstractinheritance.AbstractBContainer;
import com.github.dozermapper.core.vo.abstractinheritance.B;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for data objects that have Abstract Class(s) in their object hierarchy
 */
public class InheritanceAbstractClassMappingTest extends AbstractFunctionalTest {
    @Test
    public void testCustomMappingForAbstractClasses() {
        // Test that the explicit abstract custom mapping definition is used when mapping sub classes
        mapper = getMapper("mappings/abstractMapping.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
        Assert.assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        Assert.assertEquals("fieldB not mapped correctly", src.getFieldA(), dest.getFieldB());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForAbstractClasses() {
        // Test that wildcard fields in abstract classes are mapped when there is no explicit abstract custom mapping
        // definition
        mapper = DozerBeanMapperBuilder.buildDefault();
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertEquals("abstractField1 not mapped correctly", src.getAbstractField1(), dest.getAbstractField1());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        Assert.assertNull("abstractBField should not have been mapped", dest.getAbstractBField());
        Assert.assertNull("fieldB should not have been mapped", dest.getFieldB());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test(expected = MappingException.class)
    public void testAbstractDestClassThrowsException() {
        mapper.map(newInstance(A.class), AbstractB.class);
    }

    @Test
    public void testCustomMappingForAbstractDestClass() {
        mapper = getMapper("mappings/abstractMapping.xml");
        A src = getA();
        AbstractB dest = mapper.map(src, AbstractB.class);
        Assert.assertTrue((dest instanceof B));
        Assert.assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
        Assert.assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), ((B) (dest)).getField1());
        Assert.assertEquals("fieldB not mapped correctly", src.getFieldA(), ((B) (dest)).getFieldB());
        // Remap to each other to test bi-directional mapping
        AbstractA mappedSrc = mapper.map(dest, AbstractA.class);
        AbstractB mappedDest = mapper.map(mappedSrc, AbstractB.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testCustomMappingForAbstractDestClassLevelTwo() {
        mapper = getMapper("mappings/abstractMapping.xml");
        AbstractAContainer src = getAWrapper();
        AbstractBContainer dest = mapper.map(src, AbstractBContainer.class);
        Assert.assertTrue(((dest.getB()) instanceof B));
        Assert.assertNull("abstractField1 should have been excluded", dest.getB().getAbstractField1());
        Assert.assertEquals("abstractBField not mapped correctly", src.getA().getAbstractAField(), dest.getB().getAbstractBField());
        Assert.assertEquals("field1 not mapped correctly", ((A) (src.getA())).getField1(), ((B) (dest.getB())).getField1());
        Assert.assertEquals("fieldB not mapped correctly", ((A) (src.getA())).getFieldA(), ((B) (dest.getB())).getFieldB());
        // Remap to each other to test bi-directional mapping
        AbstractAContainer mappedSrc = mapper.map(dest, AbstractAContainer.class);
        AbstractBContainer mappedDest = mapper.map(mappedSrc, AbstractBContainer.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testCustomMappingForAsbstractDestClassCollection() {
        mapper = getMapper("mappings/abstractMapping.xml");
        AbstractACollectionContainer src = getAsContainer();
        AbstractBCollectionContainer dest = mapper.map(src, AbstractBCollectionContainer.class);
        Assert.assertTrue(((dest.getBs().get(0)) instanceof B));
        Assert.assertNull("abstractField1 should have been excluded", dest.getBs().get(0).getAbstractField1());
        Assert.assertEquals("abstractBField not mapped correctly", src.getAs().get(0).getAbstractAField(), dest.getBs().get(0).getAbstractBField());
        Assert.assertEquals("field1 not mapped correctly", ((A) (src.getAs().get(0))).getField1(), ((B) (dest.getBs().get(0))).getField1());
        Assert.assertEquals("fieldB not mapped correctly", ((A) (src.getAs().get(0))).getFieldA(), ((B) (dest.getBs().get(0))).getFieldB());
        // Remap to each other to test bi-directional mapping
        AbstractACollectionContainer mappedSrc = mapper.map(dest, AbstractACollectionContainer.class);
        AbstractBCollectionContainer mappedDest = mapper.map(mappedSrc, AbstractBCollectionContainer.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForAbstractClasses_SubclassAttrsAppliedToAbstractClasses() {
        // Test that when there isnt an explicit abstract custom mapping definition the subclass mapping def attrs are
        // applied to the abstract class mapping. In this use case, wildcard="false" for the A --> B mapping definition
        mapper = getMapper("mappings/abstractMapping2.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("fieldB should not have been mapped", dest.getAbstractField1());
        Assert.assertNull("abstractBField should have not been mapped", dest.getAbstractBField());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForSubclasses_CustomMappingForAbstractClasses() {
        // Tests that custom mappings for abstract classes are used when there are no custom mappings
        // for subclasses. Also tests that a default class map is properly created and used for the subclass
        // field mappings
        mapper = getMapper("mappings/abstractMapping3.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
        Assert.assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }
}

