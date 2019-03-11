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
package io.helidon.webserver.examples.comments;


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
 * Tests {@link CommentsService}.
 */
public class CommentsServiceTest {
    @Test
    public void addAndGetComments() throws Exception {
        CommentsService service = new CommentsService();
        Assertions.assertEquals("", service.listComments("one"));
        Assertions.assertEquals("", service.listComments("two"));
        service.addComment("aaa", null, "one");
        Assertions.assertEquals("anonymous: aaa", service.listComments("one"));
        Assertions.assertEquals("", service.listComments("two"));
        service.addComment("bbb", "Foo", "one");
        Assertions.assertEquals("anonymous: aaa\nFoo: bbb", service.listComments("one"));
        Assertions.assertEquals("", service.listComments("two"));
        service.addComment("bbb", "Bar", "two");
        Assertions.assertEquals("anonymous: aaa\nFoo: bbb", service.listComments("one"));
        Assertions.assertEquals("Bar: bbb", service.listComments("two"));
    }

    @Test
    public void testRouting() throws Exception {
        Routing routing = Routing.builder().register(new CommentsService()).build();
        TestResponse response = TestClient.create(routing).path("one").get();
        Assertions.assertEquals(OK_200, response.status());
        response = TestClient.create(routing).path("one").post(MediaPublisher.create(TEXT_PLAIN, "aaa"));
        Assertions.assertEquals(OK_200, response.status());
        response = TestClient.create(routing).path("one").get();
        Assertions.assertEquals(OK_200, response.status());
        byte[] data = response.asBytes().toCompletableFuture().join();
        Assertions.assertEquals("anonymous: aaa", new String(data, StandardCharsets.UTF_8));
    }
}

