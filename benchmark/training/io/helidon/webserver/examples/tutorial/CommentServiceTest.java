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
package io.helidon.webserver.examples.tutorial;


import Http.Status.OK_200;
import MediaType.TEXT_PLAIN;
import io.helidon.webserver.Routing;
import io.helidon.webserver.testsupport.MediaPublisher;
import io.helidon.webserver.testsupport.TestClient;
import io.helidon.webserver.testsupport.TestResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link CommentService}.
 */
public class CommentServiceTest {
    @Test
    public void addAndGetComments() throws Exception {
        CommentService service = new CommentService();
        Assertions.assertEquals(0, service.getComments("one").size());
        Assertions.assertEquals(0, service.getComments("two").size());
        service.addComment("one", null, "aaa");
        Assertions.assertEquals(1, service.getComments("one").size());
        Assertions.assertEquals(0, service.getComments("two").size());
        service.addComment("one", null, "bbb");
        Assertions.assertEquals(2, service.getComments("one").size());
        Assertions.assertEquals(0, service.getComments("two").size());
        service.addComment("two", null, "bbb");
        Assertions.assertEquals(2, service.getComments("one").size());
        Assertions.assertEquals(1, service.getComments("two").size());
    }

    @Test
    public void testRouting() throws Exception {
        Routing routing = Routing.builder().register(new CommentService()).build();
        TestResponse response = TestClient.create(routing).path("one").get();
        Assertions.assertEquals(OK_200, response.status());
        // Add first comment
        response = TestClient.create(routing).path("one").post(MediaPublisher.create(TEXT_PLAIN, "aaa"));
        Assertions.assertEquals(OK_200, response.status());
        response = TestClient.create(routing).path("one").get();
        Assertions.assertEquals(OK_200, response.status());
        byte[] data = response.asBytes().toCompletableFuture().get();
        Assertions.assertEquals("anonymous: aaa\n", new String(data, StandardCharsets.UTF_8));
        // Add second comment
        response = TestClient.create(routing).path("one").post(MediaPublisher.create(TEXT_PLAIN, "bbb"));
        Assertions.assertEquals(OK_200, response.status());
        response = TestClient.create(routing).path("one").get();
        Assertions.assertEquals(OK_200, response.status());
        data = response.asBytes().toCompletableFuture().get();
        Assertions.assertEquals("anonymous: aaa\nanonymous: bbb\n", new String(data, StandardCharsets.UTF_8));
    }
}

