/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver.examples.tutorial.user;


import User.ANONYMOUS;
import io.helidon.webserver.Routing;
import io.helidon.webserver.testsupport.TestClient;
import io.helidon.webserver.testsupport.TestResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link UserFilter}.
 */
public class UserFilterTest {
    @Test
    public void filter() throws Exception {
        AtomicReference<User> userReference = new AtomicReference<>();
        Routing routing = Routing.builder().any(new UserFilter()).any(( req, res) -> {
            userReference.set(req.context().get(.class).orElse(null));
            res.send();
        }).build();
        TestResponse response = TestClient.create(routing).path("/").get();
        Assertions.assertEquals(ANONYMOUS, userReference.get());
        response = TestClient.create(routing).path("/").header("Cookie", "Unauthenticated-User-Alias=Foo").get();
        Assertions.assertEquals("Foo", userReference.get().getAlias());
    }
}

