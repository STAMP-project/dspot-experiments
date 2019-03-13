/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.http;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.client.HttpClient;
import org.rapidoid.http.client.HttpClientCallback;
import org.rapidoid.lambda.ResultCounterCallback;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class HttpClientTest extends IsolatedIntegrationTest {
    private static final String GET_LOCALHOST = "GET / HTTP/1.1\nHost: localhost\n\n";

    private static final String GET_RAPIDOID_ORG = "GET / HTTP/1.1\nHost: www.rapidoid.org\n\n";

    private static final String SIMPLE_RESPONSE = "AbC";

    @Test
    public void testHttpClientOnLocalServer() {
        On.req(( req) -> SIMPLE_RESPONSE);
        for (int k = 0; k < 3; k++) {
            ResultCounterCallback<String> cb = new ResultCounterCallback();
            HttpClientCallback hcb = new org.rapidoid.http.client.HttpClientBodyCallback(cb);
            HttpClient client = new HttpClient();
            int count1 = 1000;
            for (int i = 0; i < count1; i++) {
                client.get("localhost", 8080, HttpClientTest.GET_LOCALHOST, hcb);
            }
            waiting();
            while ((cb.getResultCount()) < count1) {
                timeout(50000);
            } 
            eq(cb.getResults(), U.set(HttpClientTest.SIMPLE_RESPONSE));
            client.shutdown();
        }
    }
}

