/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.context.annotation;


import RootBeanDefinition.SCOPE_PROTOTYPE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public class LazyAutowiredAnnotationBeanPostProcessorTests {
    @Test
    public void testLazyResourceInjectionWithField() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean.class);
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        RootBeanDefinition abd = new RootBeanDefinition(LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean.class);
        abd.setScope(SCOPE_PROTOTYPE);
        ac.registerBeanDefinition("annotatedBean", abd);
        RootBeanDefinition tbd = new RootBeanDefinition(TestBean.class);
        tbd.setLazyInit(true);
        ac.registerBeanDefinition("testBean", tbd);
        ac.refresh();
        LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean bean = ac.getBean("annotatedBean", LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean.class);
        Assert.assertFalse(ac.getBeanFactory().containsSingleton("testBean"));
        Assert.assertFalse(bean.getTestBeans().isEmpty());
        Assert.assertNull(getName());
        Assert.assertTrue(ac.getBeanFactory().containsSingleton("testBean"));
        TestBean tb = ((TestBean) (ac.getBean("testBean")));
        setName("tb");
        Assert.assertSame("tb", getName());
    }

    @Test
    public void testLazyResourceInjectionWithFieldAndCustomAnnotation() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBeanWithCompositeAnnotation.class);
    }

    @Test
    public void testLazyResourceInjectionWithMethod() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.MethodResourceInjectionBean.class);
    }

    @Test
    public void testLazyResourceInjectionWithMethodLevelLazy() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.MethodResourceInjectionBeanWithMethodLevelLazy.class);
    }

    @Test
    public void testLazyResourceInjectionWithMethodAndCustomAnnotation() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.MethodResourceInjectionBeanWithCompositeAnnotation.class);
    }

    @Test
    public void testLazyResourceInjectionWithConstructor() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBean.class);
    }

    @Test
    public void testLazyResourceInjectionWithConstructorLevelLazy() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBeanWithConstructorLevelLazy.class);
    }

    @Test
    public void testLazyResourceInjectionWithConstructorAndCustomAnnotation() {
        doTestLazyResourceInjection(LazyAutowiredAnnotationBeanPostProcessorTests.ConstructorResourceInjectionBeanWithCompositeAnnotation.class);
    }

    @Test
    public void testLazyResourceInjectionWithNonExistingTarget() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean bean = ((LazyAutowiredAnnotationBeanPostProcessorTests.FieldResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotNull(bean.getTestBean());
        try {
            getName();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    @Test
    public void testLazyOptionalResourceInjectionWithNonExistingTarget() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(bf);
        bf.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(LazyAutowiredAnnotationBeanPostProcessorTests.OptionalFieldResourceInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("annotatedBean", bd);
        LazyAutowiredAnnotationBeanPostProcessorTests.OptionalFieldResourceInjectionBean bean = ((LazyAutowiredAnnotationBeanPostProcessorTests.OptionalFieldResourceInjectionBean) (bf.getBean("annotatedBean")));
        Assert.assertNotNull(bean.getTestBean());
        Assert.assertNotNull(bean.getTestBeans());
        Assert.assertTrue(bean.getTestBeans().isEmpty());
        try {
            getName();
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
    }

    public interface TestBeanHolder {
        TestBean getTestBean();
    }

    public static class FieldResourceInjectionBean implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        @Autowired
        @Lazy
        private TestBean testBean;

        @Autowired
        @Lazy
        private List<TestBean> testBeans;

        public TestBean getTestBean() {
            return this.testBean;
        }

        public List<TestBean> getTestBeans() {
            return testBeans;
        }
    }

    public static class OptionalFieldResourceInjectionBean implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        @Autowired(required = false)
        @Lazy
        private TestBean testBean;

        @Autowired(required = false)
        @Lazy
        private List<TestBean> testBeans;

        public TestBean getTestBean() {
            return this.testBean;
        }

        public List<TestBean> getTestBeans() {
            return this.testBeans;
        }
    }

    public static class FieldResourceInjectionBeanWithCompositeAnnotation implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        @LazyAutowiredAnnotationBeanPostProcessorTests.LazyInject
        private TestBean testBean;

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class MethodResourceInjectionBean implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private TestBean testBean;

        @Autowired
        public void setTestBean(@Lazy
        TestBean testBean) {
            if ((this.testBean) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class MethodResourceInjectionBeanWithMethodLevelLazy implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private TestBean testBean;

        @Autowired
        @Lazy
        public void setTestBean(TestBean testBean) {
            if ((this.testBean) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class MethodResourceInjectionBeanWithCompositeAnnotation implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private TestBean testBean;

        @LazyAutowiredAnnotationBeanPostProcessorTests.LazyInject
        public void setTestBean(TestBean testBean) {
            if ((this.testBean) != null) {
                throw new IllegalStateException("Already called");
            }
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class ConstructorResourceInjectionBean implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private final TestBean testBean;

        @Autowired
        public ConstructorResourceInjectionBean(@Lazy
        TestBean testBean) {
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class ConstructorResourceInjectionBeanWithConstructorLevelLazy implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private final TestBean testBean;

        @Autowired
        @Lazy
        public ConstructorResourceInjectionBeanWithConstructorLevelLazy(TestBean testBean) {
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    public static class ConstructorResourceInjectionBeanWithCompositeAnnotation implements LazyAutowiredAnnotationBeanPostProcessorTests.TestBeanHolder {
        private final TestBean testBean;

        @LazyAutowiredAnnotationBeanPostProcessorTests.LazyInject
        public ConstructorResourceInjectionBeanWithCompositeAnnotation(TestBean testBean) {
            this.testBean = testBean;
        }

        public TestBean getTestBean() {
            return this.testBean;
        }
    }

    @Autowired
    @Lazy
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LazyInject {}
}

