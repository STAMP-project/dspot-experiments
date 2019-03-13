/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.execjar;


import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class NonAsciiServiceNameTest {
    OkHttpClient client = new OkHttpClient();

    @Rule
    public ExecJarRule zipkin = new ExecJarRule();

    @Test
    public void spanNameQueryWorksWithNonAsciiServiceName() throws IOException {
        Response response = client.newCall(new Request.Builder().url((("http://localhost:" + (zipkin.port())) + "/api/v2/spans?serviceName=??????")).build()).execute();
        Assert.assertEquals(200, response.code());
    }
}

