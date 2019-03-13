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


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.functional_tests.runner.NoProxyDataObjectInstantiator;
import com.github.dozermapper.core.functional_tests.support.SampleCustomBeanFactory;
import com.github.dozermapper.core.functional_tests.support.SampleCustomBeanFactory2;
import com.github.dozermapper.core.functional_tests.support.SampleDefaultBeanFactory;
import com.github.dozermapper.core.functional_tests.support.TestDataFactory;
import com.github.dozermapper.core.vo.MetalThingyIF;
import com.github.dozermapper.core.vo.SuperSuperSuperClass;
import com.github.dozermapper.core.vo.SuperSuperSuperClassPrime;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.Van;
import org.junit.Assert;
import org.junit.Test;


/**
 * Very high level tests of the DozerBeanMapper. This test class is not intended to provide in-depth testing of all the
 * possible mapping use cases. The more in-depth unit tests of the DozerBeanMapper and MappingProcessor can be found in
 * other test classes within this same package. i.e) GranularDozerBeanMapperTest, MapperTest, IndexMappingTest, etc
 */
public class DozerBeanMapperTest extends AbstractDozerTest {
    private Mapper mapper;

    private TestDataFactory testDataFactory = new TestDataFactory(NoProxyDataObjectInstantiator.INSTANCE);

    @Test(expected = MappingException.class)
    public void testNoSourceObject() {
        mapper.map(null, TestObjectPrime.class);
        Assert.fail("should have thrown exception");
    }

    @Test(expected = MappingException.class)
    public void testNoDestinationClass() {
        mapper.map(new TestObjectPrime(), null);
        Assert.fail("should have thrown exception");
    }

    @Test(expected = MappingException.class)
    public void testNullDestObj() {
        mapper.map(new TestObject(), null);
        Assert.fail("should have thrown mapping exception");
    }

    @Test(expected = MappingException.class)
    public void testMapIdDoesNotExist() {
        mapper.map(new TestObject(), TestObjectPrime.class, "thisMapIdDoesNotExist");
        Assert.fail("should have thrown exception");
    }

    @Test
    public void testGeneralMapping() {
        assertCommon(mapper);
    }

    @Test
    public void testNoMappingFilesSpecified() {
        // Mapper can be used without specifying any mapping files. Fields that have the same name will be mapped
        // automatically.
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        assertCommon(mapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDetectDuplicateMapping() {
        Mapper myMapper = DozerBeanMapperBuilder.create().withMappingFiles("mappings/duplicateMapping.xml").build();
        myMapper.map(new SuperSuperSuperClass(), SuperSuperSuperClassPrime.class);
        Assert.fail("should have thrown exception");
    }

    @Test
    public void testCustomBeanFactory() {
        // -----------------------------------------------------------
        // Test that java beans get created with explicitly specified
        // custom bean factory
        // -----------------------------------------------------------
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("mappings/customfactorymapping.xml").build();
        TestObjectPrime prime = mapper.map(testDataFactory.getInputGeneralMappingTestObject(), TestObjectPrime.class);
        TestObject source = mapper.map(prime, TestObject.class);
        // The following asserts will verify that the ClassMap beanFactory attr gets applied to both classes
        Assert.assertNotNull("created by factory name should not be null", prime.getCreatedByFactoryName());
        Assert.assertNotNull("created by factory name should not be null", source.getCreatedByFactoryName());
        Assert.assertEquals(SampleCustomBeanFactory.class.getName(), prime.getCreatedByFactoryName());
        Assert.assertEquals(SampleCustomBeanFactory.class.getName(), source.getCreatedByFactoryName());
        // The following asserts will verify that default configuration is being applied
        Assert.assertNotNull("created by factory name should not be null", source.getThree().getCreatedByFactoryName());
        Assert.assertEquals(SampleDefaultBeanFactory.class.getName(), source.getThree().getCreatedByFactoryName());
        // The following asserts will verify that dest or src class level attr's override classMap and default config attr's
        Assert.assertNotNull("created by factory name should not be null", prime.getThreePrime().getCreatedByFactoryName());
        Assert.assertEquals(SampleCustomBeanFactory2.class.getName(), prime.getThreePrime().getCreatedByFactoryName());
        // test returning an Interface
        Van van = new Van();
        van.setName("testName");
        MetalThingyIF car = mapper.map(van, MetalThingyIF.class);
        Assert.assertEquals("testName", car.getName());
    }

    @Test
    public void testGlobalNullAndEmptyString() {
        Mapper mapperMapNull = DozerBeanMapperBuilder.buildDefault();
        Mapper mapperNotMapNull = DozerBeanMapperBuilder.create().withMappingFiles("mappings/customGlobalConfigWithNullAndEmptyStringTest.xml").build();
        Van src = new Van();
        Van dest = new Van();
        dest.setName("not null or empty");
        mapperNotMapNull.map(src, dest);
        Assert.assertEquals("not null or empty", dest.getName());
        mapperMapNull.map(src, dest);
        Assert.assertNull(dest.getName());
    }
}

