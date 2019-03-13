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
package org.springframework.web.context.support;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class SpringBeanAutowiringSupportTests {
    @Test
    public void testProcessInjectionBasedOnServletContext() {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", "tb");
        wac.registerSingleton("testBean", TestBean.class, pvs);
        MockServletContext sc = new MockServletContext();
        wac.setServletContext(sc);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        SpringBeanAutowiringSupportTests.InjectionTarget target = new SpringBeanAutowiringSupportTests.InjectionTarget();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(target, sc);
        Assert.assertTrue(((target.testBean) instanceof TestBean));
        Assert.assertEquals("tb", target.name);
    }

    public static class InjectionTarget {
        @Autowired
        public ITestBean testBean;

        @Value("#{testBean.name}")
        public String name;
    }
}

