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
package org.springframework.aop.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class ClassFiltersTests {
    private ClassFilter exceptionFilter = new RootClassFilter(Exception.class);

    private ClassFilter itbFilter = new RootClassFilter(ITestBean.class);

    private ClassFilter hasRootCauseFilter = new RootClassFilter(NestedRuntimeException.class);

    @Test
    public void testUnion() {
        Assert.assertTrue(exceptionFilter.matches(RuntimeException.class));
        Assert.assertFalse(exceptionFilter.matches(TestBean.class));
        Assert.assertFalse(itbFilter.matches(Exception.class));
        Assert.assertTrue(itbFilter.matches(TestBean.class));
        ClassFilter union = ClassFilters.union(exceptionFilter, itbFilter);
        Assert.assertTrue(union.matches(RuntimeException.class));
        Assert.assertTrue(union.matches(TestBean.class));
    }

    @Test
    public void testIntersection() {
        Assert.assertTrue(exceptionFilter.matches(RuntimeException.class));
        Assert.assertTrue(hasRootCauseFilter.matches(NestedRuntimeException.class));
        ClassFilter intersection = ClassFilters.intersection(exceptionFilter, hasRootCauseFilter);
        Assert.assertFalse(intersection.matches(RuntimeException.class));
        Assert.assertFalse(intersection.matches(TestBean.class));
        Assert.assertTrue(intersection.matches(NestedRuntimeException.class));
    }
}

