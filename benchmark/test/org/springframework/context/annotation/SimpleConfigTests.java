/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.context.annotation;


import example.scannable.FooService;
import example.scannable.ServiceInvocationCounter;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 */
public class SimpleConfigTests {
    @Test
    public void testFooService() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(getConfigLocations(), getClass());
        FooService fooService = ctx.getBean("fooServiceImpl", FooService.class);
        ServiceInvocationCounter serviceInvocationCounter = ctx.getBean("serviceInvocationCounter", ServiceInvocationCounter.class);
        String value = fooService.foo(1);
        Assert.assertEquals("bar", value);
        Future<?> future = fooService.asyncFoo(1);
        Assert.assertTrue((future instanceof FutureTask));
        Assert.assertEquals("bar", future.get());
        Assert.assertEquals(2, serviceInvocationCounter.getCount());
        fooService.foo(1);
        Assert.assertEquals(3, serviceInvocationCounter.getCount());
    }
}

