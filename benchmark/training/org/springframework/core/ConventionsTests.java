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
package org.springframework.core;


import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.tests.sample.objects.TestObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link Conventions}.
 *
 * @author Rob Harrop
 * @author Sam Brannen
 */
public class ConventionsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void simpleObject() {
        Assert.assertEquals("Incorrect singular variable name", "testObject", Conventions.getVariableName(new TestObject()));
        Assert.assertEquals("Incorrect singular variable name", "testObject", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(TestObject.class)));
        Assert.assertEquals("Incorrect singular variable name", "testObject", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(TestObject.class)));
    }

    @Test
    public void array() {
        Assert.assertEquals("Incorrect plural array form", "testObjectList", Conventions.getVariableName(new TestObject[0]));
    }

    @Test
    public void list() {
        Assert.assertEquals("Incorrect plural List form", "testObjectList", Conventions.getVariableName(Collections.singletonList(new TestObject())));
        Assert.assertEquals("Incorrect plural List form", "testObjectList", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(List.class)));
        Assert.assertEquals("Incorrect plural List form", "testObjectList", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(List.class)));
    }

    @Test
    public void emptyList() {
        this.exception.expect(IllegalArgumentException.class);
        Conventions.getVariableName(new ArrayList());
    }

    @Test
    public void set() {
        Assert.assertEquals("Incorrect plural Set form", "testObjectList", Conventions.getVariableName(Collections.singleton(new TestObject())));
        Assert.assertEquals("Incorrect plural Set form", "testObjectList", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(Set.class)));
        Assert.assertEquals("Incorrect plural Set form", "testObjectList", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(Set.class)));
    }

    @Test
    public void reactiveParameters() {
        Assert.assertEquals("testObjectMono", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(Mono.class)));
        Assert.assertEquals("testObjectFlux", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(Flux.class)));
        Assert.assertEquals("testObjectSingle", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(Single.class)));
        Assert.assertEquals("testObjectObservable", Conventions.getVariableNameForParameter(ConventionsTests.getMethodParameter(Observable.class)));
    }

    @Test
    public void reactiveReturnTypes() {
        Assert.assertEquals("testObjectMono", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(Mono.class)));
        Assert.assertEquals("testObjectFlux", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(Flux.class)));
        Assert.assertEquals("testObjectSingle", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(Single.class)));
        Assert.assertEquals("testObjectObservable", Conventions.getVariableNameForReturnType(ConventionsTests.getMethodForReturnType(Observable.class)));
    }

    @Test
    public void attributeNameToPropertyName() {
        Assert.assertEquals("transactionManager", Conventions.attributeNameToPropertyName("transaction-manager"));
        Assert.assertEquals("pointcutRef", Conventions.attributeNameToPropertyName("pointcut-ref"));
        Assert.assertEquals("lookupOnStartup", Conventions.attributeNameToPropertyName("lookup-on-startup"));
    }

    @Test
    public void getQualifiedAttributeName() {
        String baseName = "foo";
        Class<String> cls = String.class;
        String desiredResult = "java.lang.String.foo";
        Assert.assertEquals(desiredResult, Conventions.getQualifiedAttributeName(cls, baseName));
    }

    @SuppressWarnings("unused")
    private static class TestBean {
        public void handle(TestObject to, List<TestObject> toList, Set<TestObject> toSet, Mono<TestObject> toMono, Flux<TestObject> toFlux, Single<TestObject> toSingle, Observable<TestObject> toObservable) {
        }

        public TestObject handleTo() {
            return null;
        }

        public List<TestObject> handleToList() {
            return null;
        }

        public Set<TestObject> handleToSet() {
            return null;
        }

        public Mono<TestObject> handleToMono() {
            return null;
        }

        public Flux<TestObject> handleToFlux() {
            return null;
        }

        public Single<TestObject> handleToSingle() {
            return null;
        }

        public Observable<TestObject> handleToObservable() {
            return null;
        }
    }
}

