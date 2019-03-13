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
package zipkin2.elasticsearch;


import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


public class ElasticsearchAutocompleteTagsTest {
    @Rule
    public MockWebServer es = new MockWebServer();

    ElasticsearchStorage storage = ElasticsearchStorage.newBuilder().hosts(Arrays.asList(es.url("").toString())).autocompleteKeys(Arrays.asList("http#host", "http-url", "http.method")).build();

    ElasticsearchAutocompleteTags tagStore = new ElasticsearchAutocompleteTags(storage);

    @Test
    public void get_list_of_autocomplete_keys() throws Exception {
        // note: we don't enqueue a request!
        assertThat(tagStore.getKeys().execute()).contains("http#host", "http-url", "http.method");
    }

    @Test
    public void getValues_requestIncludesKeyName() throws Exception {
        es.enqueue(new MockResponse().setBody(TestResponses.AUTOCOMPLETE_VALUES));
        tagStore.getValues("http.method").execute();
        assertThat(es.takeRequest().getBody().readUtf8()).contains("\"tagKey\":\"http.method\"");
    }

    @Test
    public void getValues() throws Exception {
        es.enqueue(new MockResponse().setBody(TestResponses.AUTOCOMPLETE_VALUES));
        assertThat(tagStore.getValues("http.method").execute()).containsOnly("get", "post");
    }
}

