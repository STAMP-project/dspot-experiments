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


import org.junit.Test;
import zipkin2.TestObjects;


/**
 * Base test for when {@link StorageComponent.Builder#searchEnabled(boolean) searchEnabled ==
 * false}.
 *
 * <p>Subtypes should create a connection to a real backend, even if that backend is in-process.
 */
public abstract class ITSearchEnabledFalse {
    @Test
    public void getTraces_indexDataReturnsNothing() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName(TestObjects.CLIENT_SPAN.localServiceName()).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().spanName(TestObjects.CLIENT_SPAN.name()).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().annotationQuery(TestObjects.CLIENT_SPAN.tags()).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().minDuration(TestObjects.CLIENT_SPAN.duration()).build()).execute()).isEmpty();
    }

    @Test
    public void getSpanNames_isEmpty() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getSpanNames(TestObjects.CLIENT_SPAN.name()).execute()).isEmpty();
    }

    @Test
    public void getServiceNames_isEmpty() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getServiceNames().execute()).isEmpty();
    }
}

