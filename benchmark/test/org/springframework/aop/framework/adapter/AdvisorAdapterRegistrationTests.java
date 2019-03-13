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
package org.springframework.aop.framework.adapter;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;


/**
 * TestCase for AdvisorAdapterRegistrationManager mechanism.
 *
 * @author Dmitriy Kopylenko
 * @author Chris Beams
 */
public class AdvisorAdapterRegistrationTests {
    @Test
    public void testAdvisorAdapterRegistrationManagerNotPresentInContext() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((getClass().getSimpleName()) + "-without-bpp.xml"), getClass());
        ITestBean tb = ((ITestBean) (ctx.getBean("testBean")));
        // just invoke any method to see if advice fired
        try {
            tb.getName();
            Assert.fail("Should throw UnknownAdviceTypeException");
        } catch (UnknownAdviceTypeException ex) {
            // expected
            Assert.assertEquals(0, getAdviceImpl(tb).getInvocationCounter());
        }
    }

    @Test
    public void testAdvisorAdapterRegistrationManagerPresentInContext() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((getClass().getSimpleName()) + "-with-bpp.xml"), getClass());
        ITestBean tb = ((ITestBean) (ctx.getBean("testBean")));
        // just invoke any method to see if advice fired
        try {
            tb.getName();
            Assert.assertEquals(1, getAdviceImpl(tb).getInvocationCounter());
        } catch (UnknownAdviceTypeException ex) {
            Assert.fail("Should not throw UnknownAdviceTypeException");
        }
    }
}

