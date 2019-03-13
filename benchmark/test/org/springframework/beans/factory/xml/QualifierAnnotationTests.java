/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.beans.factory.xml;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class QualifierAnnotationTests {
    private static final String CLASSNAME = QualifierAnnotationTests.class.getName();

    private static final String CONFIG_LOCATION = String.format("classpath:%s-context.xml", convertClassNameToResourcePath(QualifierAnnotationTests.CLASSNAME));

    @Test
    public void testNonQualifiedFieldFails() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.NonQualifiedTestBean.class);
        try {
            context.refresh();
            Assert.fail("Should have thrown a BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(e.getMessage().contains("found 6"));
        }
    }

    @Test
    public void testQualifiedByValue() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByValueTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByValueTestBean testBean = ((QualifierAnnotationTests.QualifiedByValueTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("Larry", person.getName());
    }

    @Test
    public void testQualifiedByParentValue() {
        StaticApplicationContext parent = new StaticApplicationContext();
        GenericBeanDefinition parentLarry = new GenericBeanDefinition();
        parentLarry.setBeanClass(QualifierAnnotationTests.Person.class);
        parentLarry.getPropertyValues().add("name", "ParentLarry");
        parentLarry.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "parentLarry"));
        parent.registerBeanDefinition("someLarry", parentLarry);
        GenericBeanDefinition otherLarry = new GenericBeanDefinition();
        otherLarry.setBeanClass(QualifierAnnotationTests.Person.class);
        otherLarry.getPropertyValues().add("name", "OtherLarry");
        otherLarry.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "otherLarry"));
        parent.registerBeanDefinition("someOtherLarry", otherLarry);
        parent.refresh();
        StaticApplicationContext context = new StaticApplicationContext(parent);
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByParentValueTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByParentValueTestBean testBean = ((QualifierAnnotationTests.QualifiedByParentValueTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("ParentLarry", person.getName());
    }

    @Test
    public void testQualifiedByBeanName() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByBeanNameTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByBeanNameTestBean testBean = ((QualifierAnnotationTests.QualifiedByBeanNameTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("LarryBean", person.getName());
        Assert.assertTrue((((testBean.myProps) != null) && (testBean.myProps.isEmpty())));
    }

    @Test
    public void testQualifiedByFieldName() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByFieldNameTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByFieldNameTestBean testBean = ((QualifierAnnotationTests.QualifiedByFieldNameTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("LarryBean", person.getName());
    }

    @Test
    public void testQualifiedByParameterName() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByParameterNameTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByParameterNameTestBean testBean = ((QualifierAnnotationTests.QualifiedByParameterNameTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("LarryBean", person.getName());
    }

    @Test
    public void testQualifiedByAlias() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByAliasTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByAliasTestBean testBean = ((QualifierAnnotationTests.QualifiedByAliasTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getStooge();
        Assert.assertEquals("LarryBean", person.getName());
    }

    @Test
    public void testQualifiedByAnnotation() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByAnnotationTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByAnnotationTestBean testBean = ((QualifierAnnotationTests.QualifiedByAnnotationTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("LarrySpecial", person.getName());
    }

    @Test
    public void testQualifiedByCustomValue() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByCustomValueTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByCustomValueTestBean testBean = ((QualifierAnnotationTests.QualifiedByCustomValueTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getCurly();
        Assert.assertEquals("Curly", person.getName());
    }

    @Test
    public void testQualifiedByAnnotationValue() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByAnnotationValueTestBean.class);
        context.refresh();
        QualifierAnnotationTests.QualifiedByAnnotationValueTestBean testBean = ((QualifierAnnotationTests.QualifiedByAnnotationValueTestBean) (context.getBean("testBean")));
        QualifierAnnotationTests.Person person = testBean.getLarry();
        Assert.assertEquals("LarrySpecial", person.getName());
    }

    @Test
    public void testQualifiedByAttributesFailsWithoutCustomQualifierRegistered() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        context.registerSingleton("testBean", QualifierAnnotationTests.QualifiedByAttributesTestBean.class);
        try {
            context.refresh();
            Assert.fail("should have thrown a BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(e.getMessage().contains("found 6"));
        }
    }

    @Test
    public void testQualifiedByAttributesWithCustomQualifierRegistered() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
        QualifierAnnotationAutowireCandidateResolver resolver = ((QualifierAnnotationAutowireCandidateResolver) (context.getDefaultListableBeanFactory().getAutowireCandidateResolver()));
        resolver.addQualifierType(QualifierAnnotationTests.MultipleAttributeQualifier.class);
        context.registerSingleton("testBean", QualifierAnnotationTests.MultiQualifierClient.class);
        context.refresh();
        QualifierAnnotationTests.MultiQualifierClient testBean = ((QualifierAnnotationTests.MultiQualifierClient) (context.getBean("testBean")));
        Assert.assertNotNull(testBean.factoryTheta);
        Assert.assertNotNull(testBean.implTheta);
    }

    @Test
    public void testInterfaceWithOneQualifiedFactoryAndOneQualifiedBean() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(QualifierAnnotationTests.CONFIG_LOCATION);
    }

    @SuppressWarnings("unused")
    private static class NonQualifiedTestBean {
        @Autowired
        private QualifierAnnotationTests.Person anonymous;

        public QualifierAnnotationTests.Person getAnonymous() {
            return anonymous;
        }
    }

    private static class QualifiedByValueTestBean {
        @Autowired
        @Qualifier("larry")
        private QualifierAnnotationTests.Person larry;

        public QualifierAnnotationTests.Person getLarry() {
            return larry;
        }
    }

    private static class QualifiedByParentValueTestBean {
        @Autowired
        @Qualifier("parentLarry")
        private QualifierAnnotationTests.Person larry;

        public QualifierAnnotationTests.Person getLarry() {
            return larry;
        }
    }

    private static class QualifiedByBeanNameTestBean {
        @Autowired
        @Qualifier("larryBean")
        private QualifierAnnotationTests.Person larry;

        @Autowired
        @Qualifier("testProperties")
        public Properties myProps;

        public QualifierAnnotationTests.Person getLarry() {
            return larry;
        }
    }

    private static class QualifiedByFieldNameTestBean {
        @Autowired
        private QualifierAnnotationTests.Person larryBean;

        public QualifierAnnotationTests.Person getLarry() {
            return larryBean;
        }
    }

    private static class QualifiedByParameterNameTestBean {
        private QualifierAnnotationTests.Person larryBean;

        @Autowired
        public void setLarryBean(QualifierAnnotationTests.Person larryBean) {
            this.larryBean = larryBean;
        }

        public QualifierAnnotationTests.Person getLarry() {
            return larryBean;
        }
    }

    private static class QualifiedByAliasTestBean {
        @Autowired
        @Qualifier("stooge")
        private QualifierAnnotationTests.Person stooge;

        public QualifierAnnotationTests.Person getStooge() {
            return stooge;
        }
    }

    private static class QualifiedByAnnotationTestBean {
        @Autowired
        @Qualifier("special")
        private QualifierAnnotationTests.Person larry;

        public QualifierAnnotationTests.Person getLarry() {
            return larry;
        }
    }

    private static class QualifiedByCustomValueTestBean {
        @Autowired
        @QualifierAnnotationTests.SimpleValueQualifier("curly")
        private QualifierAnnotationTests.Person curly;

        public QualifierAnnotationTests.Person getCurly() {
            return curly;
        }
    }

    private static class QualifiedByAnnotationValueTestBean {
        @Autowired
        @QualifierAnnotationTests.SimpleValueQualifier("special")
        private QualifierAnnotationTests.Person larry;

        public QualifierAnnotationTests.Person getLarry() {
            return larry;
        }
    }

    @SuppressWarnings("unused")
    private static class QualifiedByAttributesTestBean {
        @Autowired
        @QualifierAnnotationTests.MultipleAttributeQualifier(name = "moe", age = 42)
        private QualifierAnnotationTests.Person moeSenior;

        @Autowired
        @QualifierAnnotationTests.MultipleAttributeQualifier(name = "moe", age = 15)
        private QualifierAnnotationTests.Person moeJunior;

        public QualifierAnnotationTests.Person getMoeSenior() {
            return moeSenior;
        }

        public QualifierAnnotationTests.Person getMoeJunior() {
            return moeJunior;
        }
    }

    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Qualifier("special")
    @QualifierAnnotationTests.SimpleValueQualifier("special")
    private static class SpecialPerson extends QualifierAnnotationTests.Person {}

    @Target({ ElementType.FIELD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface SimpleValueQualifier {
        String value() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MultipleAttributeQualifier {
        String name();

        int age();
    }

    private static final String FACTORY_QUALIFIER = "FACTORY";

    private static final String IMPL_QUALIFIER = "IMPL";

    public static class MultiQualifierClient {
        @Autowired
        @Qualifier(QualifierAnnotationTests.FACTORY_QUALIFIER)
        public QualifierAnnotationTests.Theta factoryTheta;

        @Autowired
        @Qualifier(QualifierAnnotationTests.IMPL_QUALIFIER)
        public QualifierAnnotationTests.Theta implTheta;
    }

    public interface Theta {}

    @Qualifier(QualifierAnnotationTests.IMPL_QUALIFIER)
    public static class ThetaImpl implements QualifierAnnotationTests.Theta {}

    @Qualifier(QualifierAnnotationTests.FACTORY_QUALIFIER)
    public static class QualifiedFactoryBean implements FactoryBean<QualifierAnnotationTests.Theta> {
        @Override
        public QualifierAnnotationTests.Theta getObject() {
            return new QualifierAnnotationTests.Theta() {};
        }

        @Override
        public Class<QualifierAnnotationTests.Theta> getObjectType() {
            return QualifierAnnotationTests.Theta.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}

