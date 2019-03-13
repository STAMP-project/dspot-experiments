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
package com.github.dozermapper.core.factory;


import ConstructionStrategies.ByConstructor;
import ConstructionStrategies.ByCreateMethod;
import ConstructionStrategies.ByFactory;
import ConstructionStrategies.ByGetInstance;
import ConstructionStrategies.ByInterface;
import ConstructionStrategies.ByNoArgObjectConstructor;
import ConstructionStrategies.JAXBBeansBased;
import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ConstructionStrategiesTest extends AbstractDozerTest {
    private BeanCreationDirective directive;

    private ByCreateMethod byCreateMethod;

    private ByGetInstance byGetInstance;

    private ByFactory byFactory;

    private ByInterface byInterface;

    private ByConstructor byConstructor;

    private ByNoArgObjectConstructor byNoArgObjectConstructor;

    private JAXBBeansBased jaxbBeansBased;

    private JAXBBeanFactory jaxbBeanFactory;

    private BeanContainer beanContainer;

    @Test
    public void shouldAcceptOnlyWhenCreateMethodDefined() {
        directive.setCreateMethod("a");
        Assert.assertTrue(byCreateMethod.isApplicable(directive));
        directive.setCreateMethod("");
        Assert.assertFalse(byCreateMethod.isApplicable(directive));
        directive.setCreateMethod(null);
        Assert.assertFalse(byCreateMethod.isApplicable(directive));
    }

    @Test
    public void shouldUseStaticCreateMethod() {
        directive.setTargetClass(ConstructionStrategiesTest.SelfFactory.class);
        directive.setCreateMethod("create");
        ConstructionStrategiesTest.SelfFactory result = ((ConstructionStrategiesTest.SelfFactory) (byCreateMethod.create(directive)));
        Assert.assertNotNull(result);
        Assert.assertEquals("a", result.getName());
    }

    @Test
    public void shouldUseFullyQualifiedStaticCreateMethod() {
        directive.setTargetClass(String.class);
        directive.setCreateMethod("com.github.dozermapper.core.factory.ConstructionStrategiesTest$ExternalFactory.create");
        String result = ((String) (byCreateMethod.create(directive)));
        Assert.assertEquals("hello", result);
    }

    @Test(expected = MappingException.class)
    public void shouldFailWithNoSuchMethod() {
        directive.setTargetClass(ConstructionStrategiesTest.SelfFactory.class);
        directive.setCreateMethod("wrong");
        byCreateMethod.create(directive);
        Assert.fail();
    }

    @Test
    public void shouldAcceptCalendar() {
        directive.setTargetClass(Calendar.class);
        Assert.assertTrue(byGetInstance.isApplicable(directive));
        directive.setTargetClass(String.class);
        Assert.assertFalse(byGetInstance.isApplicable(directive));
    }

    @Test
    public void shouldCreateByGetInstance() {
        directive.setTargetClass(Calendar.class);
        Calendar result = ((Calendar) (byGetInstance.create(directive)));
        Assert.assertNotNull(result);
    }

    @Test
    public void shouldAcceptWhenFactoryDefined() {
        directive.setFactoryName(ConstructionStrategiesTest.MyBeanFactory.class.getName());
        Assert.assertTrue(byFactory.isApplicable(directive));
        directive.setFactoryName("");
        Assert.assertFalse(byFactory.isApplicable(directive));
    }

    @Test
    public void shouldTakeFactoryFromCache() {
        HashMap<String, BeanFactory> factories = new HashMap<>();
        factories.put(ConstructionStrategiesTest.MyBeanFactory.class.getName(), new ConstructionStrategiesTest.MyBeanFactory());
        byFactory.setStoredFactories(factories);
        directive.setFactoryName(ConstructionStrategiesTest.MyBeanFactory.class.getName());
        directive.setTargetClass(String.class);
        Assert.assertEquals("", byFactory.create(directive));
    }

    @Test
    public void shouldInstantiateFactory() {
        directive.setFactoryName(ConstructionStrategiesTest.MyBeanFactory.class.getName());
        directive.setTargetClass(String.class);
        Assert.assertEquals("", byFactory.create(directive));
    }

    @Test(expected = MappingException.class)
    public void shouldCheckType() {
        directive.setFactoryName(String.class.getName());
        directive.setTargetClass(String.class);
        byFactory.create(directive);
        Assert.fail();
    }

    @Test
    public void shouldAcceptInterfaces() {
        directive.setTargetClass(String.class);
        Assert.assertFalse(byInterface.isApplicable(directive));
        directive.setTargetClass(List.class);
        Assert.assertTrue(byInterface.isApplicable(directive));
        directive.setTargetClass(Map.class);
        Assert.assertTrue(byInterface.isApplicable(directive));
        directive.setTargetClass(Set.class);
        Assert.assertTrue(byInterface.isApplicable(directive));
    }

    @Test
    public void shouldCreateDefaultImpl() {
        directive.setTargetClass(List.class);
        Assert.assertTrue(((byInterface.create(directive)) instanceof ArrayList));
        directive.setTargetClass(Map.class);
        Assert.assertTrue(((byInterface.create(directive)) instanceof HashMap));
        directive.setTargetClass(Set.class);
        Assert.assertTrue(((byInterface.create(directive)) instanceof HashSet));
    }

    @Test
    public void shouldCreateByConstructor() {
        directive.setTargetClass(String.class);
        Assert.assertEquals("", byConstructor.create(directive));
    }

    @Test(expected = MappingException.class)
    public void shouldFailToFindConstructor() {
        directive.setTargetClass(ConstructionStrategiesTest.SelfFactory.class);
        byConstructor.create(directive);
    }

    @Test(expected = MappingException.class)
    public void shouldFailToReturnCorrectType() {
        directive.setFactoryName(ConstructionStrategiesTest.MyBeanFactory.class.getName());
        directive.setTargetClass(ConstructionStrategiesTest.SelfFactory.class);
        byFactory.create(directive);
    }

    @Test
    public void shouldInstantiateByNoArgObjectConstructor() {
        directive.setSkipConstructor(true);
        directive.setTargetClass(ConstructionStrategiesTest.SelfFactory.class);
        ConstructionStrategiesTest.SelfFactory result = ((ConstructionStrategiesTest.SelfFactory) (byNoArgObjectConstructor.create(directive)));
        Assert.assertEquals(null, result.getName());
    }

    public static final class SelfFactory {
        private String name;

        private SelfFactory(String name) {
            this.name = name;
        }

        public static ConstructionStrategiesTest.SelfFactory create() {
            return new ConstructionStrategiesTest.SelfFactory("a");
        }

        public String getName() {
            return name;
        }
    }

    public static class MyBeanFactory implements BeanFactory {
        public Object createBean(Object source, Class<?> sourceClass, String targetBeanId, BeanContainer beanContainer) {
            return "";
        }
    }

    public static class ExternalFactory {
        public static String create() {
            return "hello";
        }
    }
}

