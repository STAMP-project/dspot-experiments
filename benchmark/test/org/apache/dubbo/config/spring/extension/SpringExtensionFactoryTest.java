/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config.spring.extension;


import org.apache.dubbo.config.spring.api.DemoService;
import org.apache.dubbo.config.spring.api.HelloService;
import org.apache.dubbo.rpc.Protocol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringExtensionFactoryTest {
    private SpringExtensionFactory springExtensionFactory = new SpringExtensionFactory();

    private AnnotationConfigApplicationContext context1;

    private AnnotationConfigApplicationContext context2;

    @Test
    public void testGetExtensionBySPI() {
        Protocol protocol = springExtensionFactory.getExtension(Protocol.class, "protocol");
        Assertions.assertNull(protocol);
    }

    @Test
    public void testGetExtensionByName() {
        DemoService bean = springExtensionFactory.getExtension(DemoService.class, "bean1");
        Assertions.assertNotNull(bean);
    }

    @Test
    public void testGetExtensionByTypeMultiple() {
        try {
            springExtensionFactory.getExtension(DemoService.class, "beanname-not-exist");
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.assertTrue((e instanceof NoUniqueBeanDefinitionException));
        }
    }

    @Test
    public void testGetExtensionByType() {
        HelloService bean = springExtensionFactory.getExtension(HelloService.class, "beanname-not-exist");
        Assertions.assertNotNull(bean);
    }
}

