/**
 * Copyright 2017 the original author or authors.
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
package org.awaitility.proxy;


import java.util.concurrent.Callable;
import org.awaitility.Awaitility;
import org.junit.Test;


public class DemoTest {
    @Test
    public void testUsingCallable() throws Exception {
        final CounterService service = new CounterServiceImpl();
        service.run();
        Awaitility.await().until(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return (service.getCount()) == 1;
            }
        });
    }

    @Test
    public void testUsingCallTo() throws Exception {
        final CounterService service = new CounterServiceImpl();
        service.run();
        Awaitility.await().untilCall(AwaitilityClassProxy.to(service).getCount(), is(equalTo(1)));
    }

    @Test
    public void testUsingGreaterThan() throws Exception {
        final CounterService service = new CounterServiceImpl();
        service.run();
        Awaitility.await().untilCall(AwaitilityClassProxy.to(service).getCount(), greaterThan(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrash() throws Exception {
        final CounterService service = new CounterServiceImpl(new IllegalArgumentException());
        service.run();
        Awaitility.await().untilCall(AwaitilityClassProxy.to(service).getCount(), is(equalTo(1)));
    }
}

