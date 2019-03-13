/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.aop.aspectj.autoproxy.spr3064;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class SPR3064Tests {
    private Service service;

    @Test
    public void testServiceIsAdvised() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(((getClass().getSimpleName()) + ".xml"), getClass());
        service = ((Service) (ctx.getBean("service")));
        try {
            this.service.serveMe();
            Assert.fail("service operation has not been advised by transaction interceptor");
        } catch (RuntimeException ex) {
            Assert.assertEquals("advice invoked", ex.getMessage());
        }
    }
}

