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
package zipkin2.storage;


import java.io.IOException;
import java.time.Instant;
import org.junit.Test;
import zipkin2.TestObjects;


/**
 * Base test for when {@link StorageComponent.Builder#autocompleteKeys(List)} has values.
 *
 * <p>Subtypes should create a connection to a real backend, even if that backend is in-process.
 */
public abstract class ITAutocompleteTags {
    protected StorageComponent storage;

    @Test
    public void Should_not_store_when_key_not_in_autocompleteTags() throws IOException {
        accept(TestObjects.LOTS_OF_SPANS[0].toBuilder().timestamp(Instant.now().toEpochMilli()).putTag("http.method", "GET").build());
        assertThat(storage.autocompleteTags().getKeys().execute()).doesNotContain("http.method");
        assertThat(storage.autocompleteTags().getValues("http.method").execute()).isEmpty();
    }

    @Test
    public void getTagsAndValues() throws IOException {
        for (int i = 0; i < 2; i++) {
            accept(TestObjects.LOTS_OF_SPANS[i].toBuilder().putTag("http.method", "GET").putTag("http.host", "host1").build());
        }
        assertThat(storage.autocompleteTags().getKeys().execute()).containsOnlyOnce("http.host");
        assertThat(storage.autocompleteTags().getValues("http.host").execute()).containsOnlyOnce("host1");
    }
}

