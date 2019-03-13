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
package org.springframework.aop.framework;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Integration test for Objenesis proxy creation.
 *
 * @author Oliver Gierke
 */
public class ObjenesisProxyTests {
    @Test
    public void appliesAspectToClassWithComplexConstructor() {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("ObjenesisProxyTests-context.xml", getClass());
        ClassWithComplexConstructor bean = context.getBean(ClassWithComplexConstructor.class);
        bean.method();
        DebugInterceptor interceptor = context.getBean(DebugInterceptor.class);
        Assert.assertThat(interceptor.getCount(), CoreMatchers.is(1L));
        Assert.assertThat(bean.getDependency().getValue(), CoreMatchers.is(1));
    }
}

