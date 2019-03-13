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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Simple test to illustrate and verify scope usage.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class SimpleScopeTests {
    private DefaultListableBeanFactory beanFactory;

    @Test
    public void testCanGetScopedObject() {
        TestBean tb1 = ((TestBean) (beanFactory.getBean("usesScope")));
        TestBean tb2 = ((TestBean) (beanFactory.getBean("usesScope")));
        Assert.assertNotSame(tb1, tb2);
        TestBean tb3 = ((TestBean) (beanFactory.getBean("usesScope")));
        Assert.assertSame(tb3, tb1);
    }
}

