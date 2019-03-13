/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security.integration.webserver;


import io.helidon.security.Security;
import io.helidon.webserver.Routing;
import io.helidon.webserver.testsupport.TestClient;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link WebSecurity}.
 */
public class WebSecurityTest {
    @Test
    public void testExecutorService() throws InterruptedException, TimeoutException {
        Security security = Security.builder().build();
        AtomicReference<Class> execClassHolder = new AtomicReference<>();
        Routing routing = Routing.builder().register(WebSecurity.create(security)).get("/unit_test", ( req, res) -> {
            req.context().get(.class).ifPresent(( context) -> execClassHolder.set(context.executorService().getClass()));
            req.next();
        }).build();
        TestClient.create(routing).path("/unit_test").get();
        Class execClass = execClassHolder.get();
        MatcherAssert.assertThat(execClass, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(execClass, CoreMatchers.is(CoreMatchers.not(ForkJoinPool.class)));
    }
}

