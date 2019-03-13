/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.aop.aspectj;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.Advised;


/**
 *
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class SubtypeSensitiveMatchingTests {
    private NonSerializableFoo nonSerializableBean;

    private SerializableFoo serializableBean;

    private Bar bar;

    @Test
    public void testBeansAreProxiedOnStaticMatch() {
        Assert.assertTrue("bean with serializable type should be proxied", ((this.serializableBean) instanceof Advised));
    }

    @Test
    public void testBeansThatDoNotMatchBasedSolelyOnRuntimeTypeAreNotProxied() {
        Assert.assertFalse("bean with non-serializable type should not be proxied", ((this.nonSerializableBean) instanceof Advised));
    }

    @Test
    public void testBeansThatDoNotMatchBasedOnOtherTestAreProxied() {
        Assert.assertTrue("bean with args check should be proxied", ((this.bar) instanceof Advised));
    }
}

