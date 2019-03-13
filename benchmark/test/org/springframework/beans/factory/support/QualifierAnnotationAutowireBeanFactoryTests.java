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
package org.springframework.beans.factory.support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 */
public class QualifierAnnotationAutowireBeanFactoryTests {
    private static final String JUERGEN = "juergen";

    private static final String MARK = "mark";

    @Test
    public void testAutowireCandidateDefaultWithIrrelevantDescriptor() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN);
        RootBeanDefinition rbd = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs, null);
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, rbd);
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, null));
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.Person.class.getDeclaredField("name"), false)));
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.Person.class.getDeclaredField("name"), true)));
    }

    @Test
    public void testAutowireCandidateExplicitlyFalseWithIrrelevantDescriptor() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN);
        RootBeanDefinition rbd = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs, null);
        rbd.setAutowireCandidate(false);
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, rbd);
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, null));
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.Person.class.getDeclaredField("name"), false)));
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.Person.class.getDeclaredField("name"), true)));
    }

    @Test
    public void testAutowireCandidateExplicitlyFalseWithFieldDescriptor() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs, null);
        person.setAutowireCandidate(false);
        person.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireBeanFactoryTests.TestQualifier.class));
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, person);
        DependencyDescriptor qualifiedDescriptor = new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.QualifiedTestBean.class.getDeclaredField("qualified"), false);
        DependencyDescriptor nonqualifiedDescriptor = new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.QualifiedTestBean.class.getDeclaredField("nonqualified"), false);
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, null));
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, nonqualifiedDescriptor));
        Assert.assertFalse(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, qualifiedDescriptor));
    }

    @Test
    public void testAutowireCandidateWithShortClassName() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        ConstructorArgumentValues cavs = new ConstructorArgumentValues();
        cavs.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN);
        RootBeanDefinition person = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs, null);
        person.addQualifier(new AutowireCandidateQualifier(ClassUtils.getShortName(QualifierAnnotationAutowireBeanFactoryTests.TestQualifier.class)));
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, person);
        DependencyDescriptor qualifiedDescriptor = new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.QualifiedTestBean.class.getDeclaredField("qualified"), false);
        DependencyDescriptor nonqualifiedDescriptor = new DependencyDescriptor(QualifierAnnotationAutowireBeanFactoryTests.QualifiedTestBean.class.getDeclaredField("nonqualified"), false);
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, null));
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, nonqualifiedDescriptor));
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, qualifiedDescriptor));
    }

    @Test
    public void testAutowireCandidateWithMultipleCandidatesDescriptor() throws Exception {
        DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
        ConstructorArgumentValues cavs1 = new ConstructorArgumentValues();
        cavs1.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN);
        RootBeanDefinition person1 = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs1, null);
        person1.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireBeanFactoryTests.TestQualifier.class));
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, person1);
        ConstructorArgumentValues cavs2 = new ConstructorArgumentValues();
        cavs2.addGenericArgumentValue(QualifierAnnotationAutowireBeanFactoryTests.MARK);
        RootBeanDefinition person2 = new RootBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.Person.class, cavs2, null);
        person2.addQualifier(new AutowireCandidateQualifier(QualifierAnnotationAutowireBeanFactoryTests.TestQualifier.class));
        lbf.registerBeanDefinition(QualifierAnnotationAutowireBeanFactoryTests.MARK, person2);
        DependencyDescriptor qualifiedDescriptor = new DependencyDescriptor(new MethodParameter(QualifierAnnotationAutowireBeanFactoryTests.QualifiedTestBean.class.getDeclaredConstructor(QualifierAnnotationAutowireBeanFactoryTests.Person.class), 0), false);
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.JUERGEN, qualifiedDescriptor));
        Assert.assertTrue(lbf.isAutowireCandidate(QualifierAnnotationAutowireBeanFactoryTests.MARK, qualifiedDescriptor));
    }

    @SuppressWarnings("unused")
    private static class QualifiedTestBean {
        @QualifierAnnotationAutowireBeanFactoryTests.TestQualifier
        private QualifierAnnotationAutowireBeanFactoryTests.Person qualified;

        private QualifierAnnotationAutowireBeanFactoryTests.Person nonqualified;

        public QualifiedTestBean(@QualifierAnnotationAutowireBeanFactoryTests.TestQualifier
        QualifierAnnotationAutowireBeanFactoryTests.Person tpb) {
        }

        public void autowireQualified(@QualifierAnnotationAutowireBeanFactoryTests.TestQualifier
        QualifierAnnotationAutowireBeanFactoryTests.Person tpb) {
        }

        public void autowireNonqualified(QualifierAnnotationAutowireBeanFactoryTests.Person tpb) {
        }
    }

    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    private static @interface TestQualifier {}
}

