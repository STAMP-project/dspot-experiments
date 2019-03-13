/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.beans.factory.config;


import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Unit tests for {@link FieldRetrievingFactoryBean}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 31.07.2004
 */
public class FieldRetrievingFactoryBeanTests {
    @Test
    public void testStaticField() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setStaticField("java.sql.Connection.TRANSACTION_SERIALIZABLE");
        fr.afterPropertiesSet();
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
    }

    @Test
    public void testStaticFieldWithWhitespace() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setStaticField("  java.sql.Connection.TRANSACTION_SERIALIZABLE  ");
        fr.afterPropertiesSet();
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
    }

    @Test
    public void testStaticFieldViaClassAndFieldName() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setTargetClass(Connection.class);
        fr.setTargetField("TRANSACTION_SERIALIZABLE");
        fr.afterPropertiesSet();
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
    }

    @Test
    public void testNonStaticField() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        FieldRetrievingFactoryBeanTests.PublicFieldHolder target = new FieldRetrievingFactoryBeanTests.PublicFieldHolder();
        fr.setTargetObject(target);
        fr.setTargetField("publicField");
        fr.afterPropertiesSet();
        Assert.assertEquals(target.publicField, fr.getObject());
    }

    @Test
    public void testNothingButBeanName() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setBeanName("java.sql.Connection.TRANSACTION_SERIALIZABLE");
        fr.afterPropertiesSet();
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
    }

    @Test
    public void testJustTargetField() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setTargetField("TRANSACTION_SERIALIZABLE");
        try {
            fr.afterPropertiesSet();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testJustTargetClass() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setTargetClass(Connection.class);
        try {
            fr.afterPropertiesSet();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testJustTargetObject() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setTargetObject(new FieldRetrievingFactoryBeanTests.PublicFieldHolder());
        try {
            fr.afterPropertiesSet();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWithConstantOnClassWithPackageLevelVisibility() throws Exception {
        FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
        fr.setBeanName("org.springframework.tests.sample.beans.PackageLevelVisibleBean.CONSTANT");
        fr.afterPropertiesSet();
        Assert.assertEquals("Wuby", fr.getObject());
    }

    @Test
    public void testBeanNameSyntaxWithBeanFactory() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(qualifiedResource(FieldRetrievingFactoryBeanTests.class, "context.xml"));
        TestBean testBean = ((TestBean) (bf.getBean("testBean")));
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), testBean.getSomeIntegerArray()[0]);
        Assert.assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), testBean.getSomeIntegerArray()[1]);
    }

    private static class PublicFieldHolder {
        public String publicField = "test";
    }
}

