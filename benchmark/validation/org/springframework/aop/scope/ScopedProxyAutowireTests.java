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
package org.springframework.aop.scope;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ScopedProxyAutowireTests {
    @Test
    public void testScopedProxyInheritsAutowireCandidateFalse() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(qualifiedResource(ScopedProxyAutowireTests.class, "scopedAutowireFalse.xml"));
        Assert.assertTrue(Arrays.asList(bf.getBeanNamesForType(ScopedProxyAutowireTests.TestBean.class, false, false)).contains("scoped"));
        Assert.assertTrue(Arrays.asList(bf.getBeanNamesForType(ScopedProxyAutowireTests.TestBean.class, true, false)).contains("scoped"));
        Assert.assertFalse(bf.containsSingleton("scoped"));
        ScopedProxyAutowireTests.TestBean autowired = ((ScopedProxyAutowireTests.TestBean) (bf.getBean("autowired")));
        ScopedProxyAutowireTests.TestBean unscoped = ((ScopedProxyAutowireTests.TestBean) (bf.getBean("unscoped")));
        Assert.assertSame(unscoped, autowired.getChild());
    }

    @Test
    public void testScopedProxyReplacesAutowireCandidateTrue() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(qualifiedResource(ScopedProxyAutowireTests.class, "scopedAutowireTrue.xml"));
        Assert.assertTrue(Arrays.asList(bf.getBeanNamesForType(ScopedProxyAutowireTests.TestBean.class, true, false)).contains("scoped"));
        Assert.assertTrue(Arrays.asList(bf.getBeanNamesForType(ScopedProxyAutowireTests.TestBean.class, false, false)).contains("scoped"));
        Assert.assertFalse(bf.containsSingleton("scoped"));
        ScopedProxyAutowireTests.TestBean autowired = ((ScopedProxyAutowireTests.TestBean) (bf.getBean("autowired")));
        ScopedProxyAutowireTests.TestBean scoped = ((ScopedProxyAutowireTests.TestBean) (bf.getBean("scoped")));
        Assert.assertSame(scoped, autowired.getChild());
    }

    static class TestBean {
        private ScopedProxyAutowireTests.TestBean child;

        public void setChild(ScopedProxyAutowireTests.TestBean child) {
            this.child = child;
        }

        public ScopedProxyAutowireTests.TestBean getChild() {
            return this.child;
        }
    }
}

