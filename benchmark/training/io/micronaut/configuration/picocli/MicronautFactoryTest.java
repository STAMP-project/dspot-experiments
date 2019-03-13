/**
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.configuration.picocli;


import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.util.CollectionUtils;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;


public class MicronautFactoryTest {
    @Test
    public void createDelegatesToApplicationContext() throws Exception {
        System.setProperty("a.name", "testValue");
        ApplicationContext applicationContext = ApplicationContext.run(PropertySource.of("test", CollectionUtils.mapOf("a.name", "testValue")));
        MicronautFactory factory = new MicronautFactory(applicationContext);
        MicronautFactoryTest.A a = factory.create(MicronautFactoryTest.A.class);
        MicronautFactoryTest.A another = applicationContext.getBean(MicronautFactoryTest.A.class);
        Assert.assertSame("can get singleton A from factory and context", another, a);
        Assert.assertEquals("injected value is available", "testValue", a.injectedValue);
        applicationContext.close();
    }

    @Test
    public void createInstantiatesIfNotFound() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.run(PropertySource.of("test", CollectionUtils.mapOf("a.name", "testValue")));
        MicronautFactory factory = new MicronautFactory(applicationContext);
        long before1 = MicronautFactoryTest.count.incrementAndGet();
        MicronautFactoryTest.B b1 = factory.create(MicronautFactoryTest.B.class);
        long before2 = MicronautFactoryTest.count.incrementAndGet();
        MicronautFactoryTest.B b2 = factory.create(MicronautFactoryTest.B.class);
        Assert.assertTrue(((b1.seq) > before1));
        Assert.assertTrue(((b2.seq) > before2));
        Assert.assertTrue(((b2.seq) > (b1.seq)));
        applicationContext.close();
    }

    static AtomicInteger count = new AtomicInteger();

    @Singleton
    static class A {
        @Value("${a.name:hello}")
        String injectedValue;
    }

    static class B {
        final int seq = MicronautFactoryTest.count.incrementAndGet();
    }
}

