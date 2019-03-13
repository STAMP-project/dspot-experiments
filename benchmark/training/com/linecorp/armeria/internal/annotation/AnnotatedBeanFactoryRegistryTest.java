/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.internal.annotation.AnnotatedBeanFactoryRegistry.BeanFactoryId;
import com.linecorp.armeria.internal.annotation.AnnotatedValueResolver.RequestObjectResolver;
import com.linecorp.armeria.server.annotation.Header;
import com.linecorp.armeria.server.annotation.Param;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.junit.Test;


public class AnnotatedBeanFactoryRegistryTest {
    private static final Set<String> vars = ImmutableSet.of();

    private static final List<RequestObjectResolver> resolvers = ImmutableList.of();

    @Test
    public void shouldFailToRegister() {
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("too many annotated constructors");
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("too many annotated constructors");
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("too many annotated constructors");
        // error: annotation used in constructor param
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Both a method and parameter are annotated");
        // error: annotation used in method param
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Both a method and parameter are annotated");
        // error: more than one params for annotated constructor
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Only one parameter is allowed to an annotated method");
        // error: more than one params for annotated method
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Only one parameter is allowed to an annotated method");
        // error: some constructor params not annotated
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unsupported parameter exists");
        // error: some method params not annotated
        assertThatThrownBy(() -> register(.class, vars, resolvers)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unsupported parameter exists");
    }

    @Test
    public void shouldBeRegisteredAsUnsupported() {
        BeanFactoryId id;
        id = AnnotatedBeanFactoryRegistry.register(AnnotatedBeanFactoryRegistryTest.InnerClass.class, AnnotatedBeanFactoryRegistryTest.vars, AnnotatedBeanFactoryRegistryTest.resolvers);
        assertThat(AnnotatedBeanFactoryRegistry.find(id).isPresent()).isFalse();
        id = AnnotatedBeanFactoryRegistry.register(AnnotatedBeanFactoryRegistryTest.NotARequestBeanBecauseOfInnerClass.class, AnnotatedBeanFactoryRegistryTest.vars, AnnotatedBeanFactoryRegistryTest.resolvers);
        assertThat(AnnotatedBeanFactoryRegistry.find(id).isPresent()).isFalse();
    }

    // error test case: more than 1 annotated constructors
    static class BadRequestBeanMoreThanOnConstructor01 {
        private String param1;

        private int header2;

        // constructor1: with annotation
        @Param("param1")
        BadRequestBeanMoreThanOnConstructor01(String param1) {
            this.param1 = param1;
        }

        // constructor2: with annotation
        @Header("header2")
        BadRequestBeanMoreThanOnConstructor01(int header2) {
            this.header2 = header2;
        }
    }

    // error test case: more than 1 annotated constructors
    static class BadRequestBeanMoreThanOnConstructor02 {
        private String param1;

        private int header2;

        // constructor1: param with annotation
        BadRequestBeanMoreThanOnConstructor02(@Param("param1")
        String param1) {
            this.param1 = param1;
        }

        // constructor2: param with annotation
        BadRequestBeanMoreThanOnConstructor02(@Header("header2")
        int header2) {
            this.header2 = header2;
        }
    }

    // error test case: more than 1 annotated constructors
    static class BadRequestBeanMoreThanOnConstructor03 {
        private String param1;

        private int header2;

        // constructor1: with annotation
        @Param("param1")
        BadRequestBeanMoreThanOnConstructor03(String param1) {
            this.param1 = param1;
        }

        // constructor2: param with annotation
        BadRequestBeanMoreThanOnConstructor03(@Header("header2")
        int header2) {
            this.header2 = header2;
        }
    }

    // error test case: annotated used both on constructor and parameter
    static class BadRequestBeanAnnotationInConstructorParam {
        private final int header2;

        @Header("header2")
        BadRequestBeanAnnotationInConstructorParam(@Param("header2")
        int header2) {
            this.header2 = header2;
        }
    }

    // error test case: annotated used both on method and parameter
    static class BadRequestBeanAnnotationInMethodParam {
        private int header2;

        @Header("header2")
        void setHeader2(@Param("header2")
        int header2) {
            this.header2 = header2;
        }
    }

    // error test case: more than 1 parameters for annotated constructor
    static class BadRequestBeanMoreThanOneConstructorParam {
        private final String param1;

        private final int header2;

        @Header("header2")
        BadRequestBeanMoreThanOneConstructorParam(String param1, int header2) {
            this.param1 = param1;
            this.header2 = header2;
        }
    }

    // error test case: more than 1 parameters for annotated method
    static class BadRequestBeanMoreThanOneMethodParam {
        @Nullable
        private String param1;

        private int header2;

        @Header("header2")
        void initParams(String param1, int header2) {
            this.param1 = param1;
            this.header2 = header2;
        }
    }

    // error test case: some constructor parameters are not annotated
    static class BadRequestBeanSomeConstructorParamWithoutAnnotation {
        private final String param1;

        private final String param2;

        private final int header1;

        private final int header2;

        BadRequestBeanSomeConstructorParamWithoutAnnotation(@Param("param1")
        String param1, String param2, @Header("header1")
        int header1, int header2) {
            this.param1 = param1;
            this.param2 = param2;
            this.header1 = header1;
            this.header2 = header2;
        }
    }

    // error test case: some method parameters are not annotated
    static class BadRequestBeanSomeMethodParamWithoutAnnotation {
        @Nullable
        private String param1;

        @Nullable
        private String param2;

        private int header1;

        private int header2;

        void initParams(@Param("param1")
        String param1, String param2, @Header("header1")
        int header1, int header2) {
            this.param1 = param1;
            this.param2 = param2;
            this.header1 = header1;
            this.header2 = header2;
        }
    }

    static class NotARequestBeanBecauseOfInnerClass {
        private AnnotatedBeanFactoryRegistryTest.InnerClass innerClass;

        NotARequestBeanBecauseOfInnerClass(AnnotatedBeanFactoryRegistryTest.InnerClass innerClass) {
            this.innerClass = innerClass;
        }
    }

    static class InnerClass {
        private HttpRequest httpRequest;

        private int someValue;

        // We don't know what a user intends for. A bean? or not?
        InnerClass(HttpRequest httpRequest, int someValue) {
            this.httpRequest = httpRequest;
            this.someValue = someValue;
        }
    }
}

