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
package org.springframework.aop.aspectj.autoproxy;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Chris Beams
 */
public class AspectJAutoProxyCreatorAndLazyInitTargetSourceTests {
    @Test
    public void testAdrian() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((getClass().getSimpleName()) + "-context.xml"), getClass());
        ITestBean adrian = ((ITestBean) (ctx.getBean("adrian")));
        Assert.assertEquals(0, LazyTestBean.instantiations);
        Assert.assertNotNull(adrian);
        adrian.getAge();
        Assert.assertEquals(68, adrian.getAge());
        Assert.assertEquals(1, LazyTestBean.instantiations);
    }
}

