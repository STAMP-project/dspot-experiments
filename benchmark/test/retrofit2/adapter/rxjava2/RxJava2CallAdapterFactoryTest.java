/**
 * Copyright (C) 2015 Square, Inc.
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
package retrofit2.adapter.rxjava2;


import CallAdapter.Factory;
import com.google.common.reflect.TypeToken;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RxJava2CallAdapterFactoryTest {
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final Factory factory = RxJava2CallAdapterFactory.create();

    private Retrofit retrofit;

    @Test
    public void nullSchedulerThrows() {
        try {
            RxJava2CallAdapterFactory.createWithScheduler(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("scheduler == null");
        }
    }

    @Test
    public void nonRxJavaTypeReturnsNull() {
        CallAdapter<?, ?> adapter = factory.get(String.class, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
        assertThat(adapter).isNull();
    }

    @Test
    public void responseTypes() {
        Type oBodyClass = new TypeToken<Observable<String>>() {}.getType();
        assertThat(factory.get(oBodyClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sBodyClass = new TypeToken<Single<String>>() {}.getType();
        assertThat(factory.get(sBodyClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mBodyClass = new TypeToken<Maybe<String>>() {}.getType();
        assertThat(factory.get(mBodyClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fBodyClass = new TypeToken<Flowable<String>>() {}.getType();
        assertThat(factory.get(fBodyClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type oBodyWildcard = new TypeToken<Observable<? extends String>>() {}.getType();
        assertThat(factory.get(oBodyWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sBodyWildcard = new TypeToken<Single<? extends String>>() {}.getType();
        assertThat(factory.get(sBodyWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mBodyWildcard = new TypeToken<Maybe<? extends String>>() {}.getType();
        assertThat(factory.get(mBodyWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fBodyWildcard = new TypeToken<Flowable<? extends String>>() {}.getType();
        assertThat(factory.get(fBodyWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type oBodyGeneric = new TypeToken<Observable<List<String>>>() {}.getType();
        assertThat(factory.get(oBodyGeneric, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
        Type sBodyGeneric = new TypeToken<Single<List<String>>>() {}.getType();
        assertThat(factory.get(sBodyGeneric, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
        Type mBodyGeneric = new TypeToken<Maybe<List<String>>>() {}.getType();
        assertThat(factory.get(mBodyGeneric, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
        Type fBodyGeneric = new TypeToken<Flowable<List<String>>>() {}.getType();
        assertThat(factory.get(fBodyGeneric, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
        Type oResponseClass = new TypeToken<Observable<Response<String>>>() {}.getType();
        assertThat(factory.get(oResponseClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sResponseClass = new TypeToken<Single<Response<String>>>() {}.getType();
        assertThat(factory.get(sResponseClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mResponseClass = new TypeToken<Maybe<Response<String>>>() {}.getType();
        assertThat(factory.get(mResponseClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fResponseClass = new TypeToken<Flowable<Response<String>>>() {}.getType();
        assertThat(factory.get(fResponseClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type oResponseWildcard = new TypeToken<Observable<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(oResponseWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sResponseWildcard = new TypeToken<Single<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(sResponseWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mResponseWildcard = new TypeToken<Maybe<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(mResponseWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fResponseWildcard = new TypeToken<Flowable<Response<? extends String>>>() {}.getType();
        assertThat(factory.get(fResponseWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type oResultClass = new TypeToken<Observable<Result<String>>>() {}.getType();
        assertThat(factory.get(oResultClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sResultClass = new TypeToken<Single<Result<String>>>() {}.getType();
        assertThat(factory.get(sResultClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mResultClass = new TypeToken<Maybe<Result<String>>>() {}.getType();
        assertThat(factory.get(mResultClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fResultClass = new TypeToken<Flowable<Result<String>>>() {}.getType();
        assertThat(factory.get(fResultClass, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type oResultWildcard = new TypeToken<Observable<Result<? extends String>>>() {}.getType();
        assertThat(factory.get(oResultWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type sResultWildcard = new TypeToken<Single<Result<? extends String>>>() {}.getType();
        assertThat(factory.get(sResultWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type mResultWildcard = new TypeToken<Maybe<Result<? extends String>>>() {}.getType();
        assertThat(factory.get(mResultWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
        Type fResultWildcard = new TypeToken<Flowable<Result<? extends String>>>() {}.getType();
        assertThat(factory.get(fResultWildcard, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit).responseType()).isEqualTo(String.class);
    }

    @Test
    public void rawBodyTypeThrows() {
        Type observableType = new TypeToken<Observable>() {}.getType();
        try {
            factory.get(observableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Observable return type must be parameterized as Observable<Foo> or Observable<? extends Foo>");
        }
        Type singleType = new TypeToken<Single>() {}.getType();
        try {
            factory.get(singleType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Single return type must be parameterized as Single<Foo> or Single<? extends Foo>");
        }
        Type maybeType = new TypeToken<Maybe>() {}.getType();
        try {
            factory.get(maybeType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Maybe return type must be parameterized as Maybe<Foo> or Maybe<? extends Foo>");
        }
        Type flowableType = new TypeToken<Flowable>() {}.getType();
        try {
            factory.get(flowableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Flowable return type must be parameterized as Flowable<Foo> or Flowable<? extends Foo>");
        }
    }

    @Test
    public void rawResponseTypeThrows() {
        Type observableType = new TypeToken<Observable<Response>>() {}.getType();
        try {
            factory.get(observableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type singleType = new TypeToken<Single<Response>>() {}.getType();
        try {
            factory.get(singleType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type maybeType = new TypeToken<Maybe<Response>>() {}.getType();
        try {
            factory.get(maybeType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
        Type flowableType = new TypeToken<Flowable<Response>>() {}.getType();
        try {
            factory.get(flowableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
        }
    }

    @Test
    public void rawResultTypeThrows() {
        Type observableType = new TypeToken<Observable<Result>>() {}.getType();
        try {
            factory.get(observableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
        }
        Type singleType = new TypeToken<Single<Result>>() {}.getType();
        try {
            factory.get(singleType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
        }
        Type maybeType = new TypeToken<Maybe<Result>>() {}.getType();
        try {
            factory.get(maybeType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
        }
        Type flowableType = new TypeToken<Flowable<Result>>() {}.getType();
        try {
            factory.get(flowableType, RxJava2CallAdapterFactoryTest.NO_ANNOTATIONS, retrofit);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
        }
    }
}

