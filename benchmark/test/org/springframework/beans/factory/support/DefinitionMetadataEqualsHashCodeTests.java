/**
 * Copyright 2002-2014 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Unit tests for {@code equals()} and {@code hashCode()} in bean definitions.
 *
 * @author Rob Harrop
 * @author Sam Brannen
 */
@SuppressWarnings("serial")
public class DefinitionMetadataEqualsHashCodeTests {
    @Test
    public void rootBeanDefinition() {
        RootBeanDefinition master = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition equal = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition notEqual = new RootBeanDefinition(String.class);
        RootBeanDefinition subclass = new RootBeanDefinition(TestBean.class) {};
        setBaseProperties(master);
        setBaseProperties(equal);
        setBaseProperties(notEqual);
        setBaseProperties(subclass);
        assertEqualsAndHashCodeContracts(master, equal, notEqual, subclass);
    }

    /**
     *
     *
     * @since 3.2.8
     * @see <a href="https://jira.spring.io/browse/SPR-11420">SPR-11420</a>
     */
    @Test
    public void rootBeanDefinitionAndMethodOverridesWithDifferentOverloadedValues() {
        RootBeanDefinition master = new RootBeanDefinition(TestBean.class);
        RootBeanDefinition equal = new RootBeanDefinition(TestBean.class);
        setBaseProperties(master);
        setBaseProperties(equal);
        // Simulate AbstractBeanDefinition.validate() which delegates to
        // AbstractBeanDefinition.prepareMethodOverrides():
        master.getMethodOverrides().getOverrides().iterator().next().setOverloaded(false);
        // But do not simulate validation of the 'equal' bean. As a consequence, a method
        // override in 'equal' will be marked as overloaded, but the corresponding
        // override in 'master' will not. But... the bean definitions should still be
        // considered equal.
        Assert.assertEquals("Should be equal", master, equal);
        Assert.assertEquals("Hash code for equal instances must match", master.hashCode(), equal.hashCode());
    }

    @Test
    public void childBeanDefinition() {
        ChildBeanDefinition master = new ChildBeanDefinition("foo");
        ChildBeanDefinition equal = new ChildBeanDefinition("foo");
        ChildBeanDefinition notEqual = new ChildBeanDefinition("bar");
        ChildBeanDefinition subclass = new ChildBeanDefinition("foo") {};
        setBaseProperties(master);
        setBaseProperties(equal);
        setBaseProperties(notEqual);
        setBaseProperties(subclass);
        assertEqualsAndHashCodeContracts(master, equal, notEqual, subclass);
    }

    @Test
    public void runtimeBeanReference() {
        RuntimeBeanReference master = new RuntimeBeanReference("name");
        RuntimeBeanReference equal = new RuntimeBeanReference("name");
        RuntimeBeanReference notEqual = new RuntimeBeanReference("someOtherName");
        RuntimeBeanReference subclass = new RuntimeBeanReference("name") {};
        assertEqualsAndHashCodeContracts(master, equal, notEqual, subclass);
    }
}

