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
package org.springframework.beans.factory.xml;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class AbstractListableBeanFactoryTests extends AbstractBeanFactoryTests {
    /**
     * Subclasses can override this.
     */
    @Test
    public void count() {
        assertCount(13);
    }

    @Test
    public void getDefinitionsForNoSuchClass() {
        String[] defnames = getListableBeanFactory().getBeanNamesForType(String.class);
        Assert.assertTrue("No string definitions", ((defnames.length) == 0));
    }

    /**
     * Check that count refers to factory class, not bean class. (We don't know
     * what type factories may return, and it may even change over time.)
     */
    @Test
    public void getCountForFactoryClass() {
        Assert.assertTrue(("Should have 2 factories, not " + (getListableBeanFactory().getBeanNamesForType(FactoryBean.class).length)), ((getListableBeanFactory().getBeanNamesForType(FactoryBean.class).length) == 2));
        Assert.assertTrue(("Should have 2 factories, not " + (getListableBeanFactory().getBeanNamesForType(FactoryBean.class).length)), ((getListableBeanFactory().getBeanNamesForType(FactoryBean.class).length) == 2));
    }

    @Test
    public void containsBeanDefinition() {
        Assert.assertTrue(getListableBeanFactory().containsBeanDefinition("rod"));
        Assert.assertTrue(getListableBeanFactory().containsBeanDefinition("roderick"));
    }
}

