/**
 * Copyright (C) 2016 Square, Inc.
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
package retrofit2.adapter.java8;


import CallAdapter.Factory;
import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;


public final class Java8CallAdapterFactoryTest {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    @Rule
    public final MockWebServer server = new MockWebServer();

    private final Factory factory = Java8CallAdapterFactory.create();

    private Retrofit retrofit;

    @Test
    public void responseType() {
        Type bodyClass = new TypeToken<CompletableFuture<String>>() {}.getType();
        assertThat(factory.get(bodyClass, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type bodyWildcard = new TypeToken<CompletableFuture<? extends String>>() {}.getType();
        assertThat(factory.get(bodyWildcard, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type bodyGeneric = new TypeToken<CompletableFuture<List<String>>>() {}.getType();
        assertThat(factory.get(bodyGeneric, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
        Type responseClass = new TypeToken<CompletableFuture<Response<String>>>() {}.getType();
        assertThat(factory.get(responseClass, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type responseWildcard = new TypeToken<CompletableFuture<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(responseWildcard, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type resultClass = new TypeToken<CompletableFuture<Response<String>>>() {}.getType();
        assertThat(factory.get(resultClass, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type resultWildcard = new TypeToken<CompletableFuture<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(resultWildcard, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
    }

    @Test
    public void nonListenableFutureReturnsNull() {
        CallAdapter<?, ?> adapter = factory.get(String.class, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
        assertThat(adapter).isNull();
    }

    @Test
    public void rawTypeThrows() {
        Type observableType = new TypeToken<CompletableFuture>() {}.getType();
        try {
            factory.get(observableType, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("CompletableFuture return type must be parameterized as CompletableFuture<Foo> or CompletableFuture<? extends Foo>");
        }
    }

    @Test
    public void rawResponseTypeThrows() {
        Type observableType = new TypeToken<CompletableFuture<Response>>() {}.getType();
        try {
            factory.get(observableType, Java8CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
    }
}

