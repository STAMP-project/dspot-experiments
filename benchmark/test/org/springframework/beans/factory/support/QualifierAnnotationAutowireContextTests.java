/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.beans.factory.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Integration tests for handling {@link Qualifier} annotations.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 */
public class QualifierAnnotationAutowireContextTests {
    private static final String JUERGEN = "juergen";

    private static final String MARK = "mark";

    @Test
    public void autowiredFieldWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredMethodParameterWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredConstructorArgumentWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue((e instanceof UnsatisfiedDependencyException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredFieldWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredMethodParameterWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredMethodParameterWithStaticallyQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedPerson.class, cavs, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, ScopedProxyUtils.createScopedProxy(new org.springframework.beans.factory.config.BeanDefinitionHolder(person, QualifierAnnotationAutowireContextTests.JUERGEN), context, true).getBeanDefinition());
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredMethodParameterWithStaticallyQualifiedCandidateAmongOthers() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedPerson.class, cavs, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.DefaultValueQualifiedPerson.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredConstructorArgumentWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredMethodParameterWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredConstructorArgumentWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue((e instanceof UnsatisfiedDependencyException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredFieldResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldResolvesMetaQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.MetaQualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.MetaQualifiedFieldTestBean bean = ((QualifierAnnotationAutowireContextTests.MetaQualifiedFieldTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredMethodParameterResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredConstructorArgumentResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldResolvesQualifiedCandidateWithDefaultValueAndNoValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, but includes no value
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldDoesNotResolveCandidateWithDefaultValueAndConflictingValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, and non-default value specified
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class, "not the default"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredFieldResolvesWithDefaultValueAndExplicitDefaultValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, and value matches the default
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class, "default"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldResolvesWithMultipleQualifierValues() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.MARK, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldDoesNotResolveWithMultipleQualifierValuesAndConflictingDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "not the default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredFieldResolvesWithMultipleQualifierValuesAndExplicitDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.MARK, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldDoesNotResolveWithMultipleQualifierValuesAndMultipleMatchingCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 123);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getRootCause()) instanceof NoSuchBeanDefinitionException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    @Test
    public void autowiredFieldResolvesWithBaseQualifierAndDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        person2.addQualifier(new AutowireCandidateQualifier(Qualifier.class));
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(QualifierAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedFieldWithBaseQualifierDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedFieldWithBaseQualifierDefaultValueTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedFieldWithBaseQualifierDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals(QualifierAnnotationAutowireContextTests.MARK, bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldResolvesWithBaseQualifierAndNonDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue("the real juergen");
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "juergen"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue("juergen imposter");
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        person2.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "not really juergen"));
        context.registerBeanDefinition("juergen1", person1);
        context.registerBeanDefinition("juergen2", person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean bean = ((QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals("the real juergen", bean.getPerson().getName());
    }

    @Test
    public void autowiredFieldDoesNotResolveWithBaseQualifierAndNonDefaultValueAndMultipleMatchingCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue("the real juergen");
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "juergen"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue("juergen imposter");
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireContextTests.Person.class, cavs2, null);
        person2.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "juergen"));
        context.registerBeanDefinition("juergen1", person1);
        context.registerBeanDefinition("juergen2", person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(QualifierAnnotationAutowireContextTests.QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        try {
            context.refresh();
            Assert.fail("expected BeanCreationException");
        } catch (BeanCreationException e) {
            Assert.assertTrue((e instanceof UnsatisfiedDependencyException));
            Assert.assertEquals("autowired", e.getBeanName());
        }
    }

    private static class QualifiedFieldTestBean {
        @Autowired
        @QualifierAnnotationAutowireContextTests.TestQualifier
        private QualifierAnnotationAutowireContextTests.Person person;

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class MetaQualifiedFieldTestBean {
        @QualifierAnnotationAutowireContextTests.MyAutowired
        private QualifierAnnotationAutowireContextTests.Person person;

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    @Autowired
    @QualifierAnnotationAutowireContextTests.TestQualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAutowired {}

    private static class QualifiedMethodParameterTestBean {
        private QualifierAnnotationAutowireContextTests.Person person;

        @Autowired
        public void setPerson(@QualifierAnnotationAutowireContextTests.TestQualifier
        QualifierAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedConstructorArgumentTestBean {
        private QualifierAnnotationAutowireContextTests.Person person;

        @Autowired
        public QualifiedConstructorArgumentTestBean(@QualifierAnnotationAutowireContextTests.TestQualifier
        QualifierAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedFieldWithDefaultValueTestBean {
        @Autowired
        @QualifierAnnotationAutowireContextTests.TestQualifierWithDefaultValue
        private QualifierAnnotationAutowireContextTests.Person person;

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedFieldWithMultipleAttributesTestBean {
        @Autowired
        @QualifierAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes(number = 123)
        private QualifierAnnotationAutowireContextTests.Person person;

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedFieldWithBaseQualifierDefaultValueTestBean {
        @Autowired
        @Qualifier
        private QualifierAnnotationAutowireContextTests.Person person;

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean {
        private QualifierAnnotationAutowireContextTests.Person person;

        @Autowired
        public QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean(@Qualifier("juergen")
        QualifierAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public QualifierAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    @QualifierAnnotationAutowireContextTests.TestQualifier
    private static class QualifiedPerson extends QualifierAnnotationAutowireContextTests.Person {
        public QualifiedPerson() {
            super(null);
        }

        public QualifiedPerson(String name) {
            super(name);
        }
    }

    @QualifierAnnotationAutowireContextTests.TestQualifierWithDefaultValue
    private static class DefaultValueQualifiedPerson extends QualifierAnnotationAutowireContextTests.Person {
        public DefaultValueQualifiedPerson() {
            super(null);
        }

        public DefaultValueQualifiedPerson(String name) {
            super(name);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface TestQualifier {}

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface TestQualifierWithDefaultValue {
        String value() default "default";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface TestQualifierWithMultipleAttributes {
        String value() default "default";

        int number();
    }
}

