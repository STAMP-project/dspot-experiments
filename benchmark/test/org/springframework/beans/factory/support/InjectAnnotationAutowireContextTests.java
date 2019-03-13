/**
 * Copyright 2002-2015 the original author or authors.
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
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;


/**
 * Integration tests for handling JSR-303 {@link javax.inject.Qualifier} annotations.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class InjectAnnotationAutowireContextTests {
    private static final String JUERGEN = "juergen";

    private static final String MARK = "mark";

    @Test
    public void testAutowiredFieldWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
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
    public void testAutowiredMethodParameterWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
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
    public void testAutowiredConstructorArgumentWithSingleNonQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
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
    public void testAutowiredFieldWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredMethodParameterWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredMethodParameterWithStaticallyQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedPerson.class, cavs, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, ScopedProxyUtils.createScopedProxy(new org.springframework.beans.factory.config.BeanDefinitionHolder(person, InjectAnnotationAutowireContextTests.JUERGEN), context, true).getBeanDefinition());
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredMethodParameterWithStaticallyQualifiedCandidateAmongOthers() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedPerson.class, cavs, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredConstructorArgumentWithSingleQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
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
    public void testAutowiredMethodParameterWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
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
    public void testAutowiredConstructorArgumentWithMultipleNonQualifiedCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
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
    public void testAutowiredFieldResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredMethodParameterResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedMethodParameterTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredConstructorArgumentResolvesQualifiedCandidate() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifier.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldResolvesQualifiedCandidateWithDefaultValueAndNoValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, but includes no value
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldDoesNotResolveCandidateWithDefaultValueAndConflictingValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, and non-default value specified
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class, "not the default"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
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
    public void testAutowiredFieldResolvesWithDefaultValueAndExplicitDefaultValueOnBeanDefinition() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        // qualifier added, and value matches the default
        person1.addQualifier(new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithDefaultValue.class, "default"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldWithDefaultValueTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.JUERGEN, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldResolvesWithMultipleQualifierValues() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.MARK, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndConflictingDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "not the default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
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
    public void testAutowiredFieldResolvesWithMultipleQualifierValuesAndExplicitDefaultValue() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 456);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean bean = ((InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean) (context.getBean("autowired")));
        Assert.assertEquals(InjectAnnotationAutowireContextTests.MARK, bean.getPerson().getName());
    }

    @Test
    public void testAutowiredFieldDoesNotResolveWithMultipleQualifierValuesAndMultipleMatchingCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(InjectAnnotationAutowireContextTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier.setAttribute("number", 123);
        person1.addQualifier(qualifier);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(InjectAnnotationAutowireContextTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        AutowireCandidateQualifier qualifier2 = new AutowireCandidateQualifier(InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes.class);
        qualifier2.setAttribute("number", 123);
        qualifier2.setAttribute("value", "default");
        person2.addQualifier(qualifier2);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.JUERGEN, person1);
        context.registerBeanDefinition(InjectAnnotationAutowireContextTests.MARK, person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedFieldWithMultipleAttributesTestBean.class));
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
    public void testAutowiredFieldDoesNotResolveWithBaseQualifierAndNonDefaultValueAndMultipleMatchingCandidates() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue("the real juergen");
        RootBeanDefinition person1 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "juergen"));
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue("juergen imposter");
        RootBeanDefinition person2 = new RootBeanDefinition(InjectAnnotationAutowireContextTests.Person.class, cavs2, null);
        person2.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "juergen"));
        context.registerBeanDefinition("juergen1", person1);
        context.registerBeanDefinition("juergen2", person2);
        context.registerBeanDefinition("autowired", new RootBeanDefinition(InjectAnnotationAutowireContextTests.QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean.class));
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
        @Inject
        @InjectAnnotationAutowireContextTests.TestQualifier
        private InjectAnnotationAutowireContextTests.Person person;

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedMethodParameterTestBean {
        private InjectAnnotationAutowireContextTests.Person person;

        @Inject
        public void setPerson(@InjectAnnotationAutowireContextTests.TestQualifier
        InjectAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    private static class QualifiedConstructorArgumentTestBean {
        private InjectAnnotationAutowireContextTests.Person person;

        @Inject
        public QualifiedConstructorArgumentTestBean(@InjectAnnotationAutowireContextTests.TestQualifier
        InjectAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    public static class QualifiedFieldWithDefaultValueTestBean {
        @Inject
        @InjectAnnotationAutowireContextTests.TestQualifierWithDefaultValue
        private InjectAnnotationAutowireContextTests.Person person;

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    public static class QualifiedFieldWithMultipleAttributesTestBean {
        @Inject
        @InjectAnnotationAutowireContextTests.TestQualifierWithMultipleAttributes(number = 123)
        private InjectAnnotationAutowireContextTests.Person person;

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    @SuppressWarnings("unused")
    private static class QualifiedFieldWithBaseQualifierDefaultValueTestBean {
        @Inject
        private InjectAnnotationAutowireContextTests.Person person;

        public InjectAnnotationAutowireContextTests.Person getPerson() {
            return this.person;
        }
    }

    public static class QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean {
        private InjectAnnotationAutowireContextTests.Person person;

        @Inject
        public QualifiedConstructorArgumentWithBaseQualifierNonDefaultValueTestBean(@Named("juergen")
        InjectAnnotationAutowireContextTests.Person person) {
            this.person = person;
        }

        public InjectAnnotationAutowireContextTests.Person getPerson() {
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

    @InjectAnnotationAutowireContextTests.TestQualifier
    private static class QualifiedPerson extends InjectAnnotationAutowireContextTests.Person {
        public QualifiedPerson() {
            super(null);
        }

        public QualifiedPerson(String name) {
            super(name);
        }
    }

    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface TestQualifier {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface TestQualifierWithDefaultValue {
        String value() default "default";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface TestQualifierWithMultipleAttributes {
        String value() default "default";

        int number();
    }
}

