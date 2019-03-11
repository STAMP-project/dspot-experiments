/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import MediaType.APPLICATION_OCTET_STREAM_TYPE;
import Response.Status.Family.SUCCESSFUL;
import io.helidon.common.InputStreamHelper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * The ResponseOrderingTest tests whether http chunks were sent in a correct order which was reported as MIC-6419.
 * Note that in order to better reproduce the original intermittent failures, {@code REQUESTS_COUNT}
 * environment variable needs to be set to {@code 1000} at least.
 */
public class ResponseOrderingTest {
    private static ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();

    private static ConcurrentLinkedQueue<Throwable> errorQueue = new ConcurrentLinkedQueue<>();

    private static WebServer webServer;

    @Test
    public void testOrdering() throws Exception {
        JerseyClient client = JerseyClientBuilder.createClient();
        JerseyWebTarget target = client.target(("http://0.0.0.0:" + (ResponseOrderingTest.webServer.port())));
        ArrayList<Long> returnedIds = new ArrayList<>();
        int i1 = Optional.ofNullable(System.getenv("REQUESTS_COUNT")).map(Integer::valueOf).orElse(10);
        for (int i = 0; i < i1; i++) {
            Response response = target.path("multi").request().get();
            MatcherAssert.assertThat(response.getStatusInfo().getFamily(), Is.is(SUCCESSFUL));
            String entity = response.readEntity(String.class);
            returnedIds.add(Long.valueOf(entity));
        }
        MatcherAssert.assertThat(returnedIds.toArray(), AllOf.allOf(arrayWithSize(i1), Is.is(ResponseOrderingTest.queue.toArray())));
        MatcherAssert.assertThat(("No exceptions expected: " + (exceptions())), ResponseOrderingTest.errorQueue, hasSize(0));
    }

    @Test
    public void testContentOrdering() throws Exception {
        JerseyClient client = JerseyClientBuilder.createClient();
        JerseyWebTarget target = client.target(("http://0.0.0.0:" + (ResponseOrderingTest.webServer.port()))).path("stream");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(i).append("\n");
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Response response = target.request().post(Entity.entity(inputStream, APPLICATION_OCTET_STREAM_TYPE));
        InputStream resultStream = response.readEntity(InputStream.class);
        String s = new String(InputStreamHelper.readAllBytes(resultStream));
        MatcherAssert.assertThat(s, Is.is(sb.toString()));
    }
}

