/**
 * Copyright 2015-2018 The OpenZipkin Authors
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
package zipkin2.elasticsearch.internal.client;


import java.util.Arrays;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


public class SearchCallFactoryTest {
    @Rule
    public MockWebServer es = new MockWebServer();

    SearchCallFactory client = new SearchCallFactory(new HttpCall.Factory(new OkHttpClient(), es.url("")));

    /**
     * Declaring queries alphabetically helps simplify amazon signature logic
     */
    @Test
    public void lenientSearchOrdersQueryAlphabetically() {
        es.enqueue(new MockResponse());
        assertThat(client.lenientSearch(Arrays.asList("zipkin:span-2016-10-01"), null).queryParameterNames()).containsExactly("allow_no_indices", "expand_wildcards", "ignore_unavailable");
    }
}

