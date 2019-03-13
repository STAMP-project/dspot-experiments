/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.function.server;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class InvalidHttpMethodIntegrationTests extends AbstractRouterFunctionIntegrationTests {
    @Test
    public void invalidHttpMethod() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().method("BAZ", null).url((("http://localhost:" + (port)) + "/")).build();
        try (Response response = client.newCall(request).execute()) {
            Assert.assertEquals("BAR", response.body().string());
        }
    }
}

