/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class StubbingReturnsSelfTest {
    @Test
    public void should_stub_builder_method() {
        StubbingReturnsSelfTest.Builder builder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnSelf()).isEqualTo(builder);
    }

    @Test
    public void should_return_default_return_when_not_a_builder() {
        StubbingReturnsSelfTest.Builder builder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnString()).isEqualTo(null);
    }

    @Test
    public void should_return_self_when_call_on_method_in_superclass() {
        StubbingReturnsSelfTest.BuilderSubClass builder = Mockito.mock(StubbingReturnsSelfTest.BuilderSubClass.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnSelf()).isEqualTo(builder);
    }

    @Test
    public void should_return_self_when_call_on_method_in_subclass() {
        StubbingReturnsSelfTest.BuilderSubClass builder = Mockito.mock(StubbingReturnsSelfTest.BuilderSubClass.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnsSubClass()).isEqualTo(builder);
    }

    @Test
    public void should_return_self_when_call_on_method_in_subclass_returns_superclass() {
        StubbingReturnsSelfTest.BuilderSubClass builder = Mockito.mock(StubbingReturnsSelfTest.BuilderSubClass.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnSuperClass()).isEqualTo(builder);
    }

    @Test
    public void should_return_stubbed_answer_when_call_on_method_returns_self() {
        StubbingReturnsSelfTest.Builder builder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        StubbingReturnsSelfTest.Builder anotherBuilder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        Mockito.when(builder.returnSelf()).thenReturn(anotherBuilder);
        assertThat(builder.returnSelf().returnSelf()).isEqualTo(anotherBuilder);
    }

    @Test
    public void should_not_fail_when_calling_void_returning_method() {
        StubbingReturnsSelfTest.Builder builder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        builder.returnNothing();
    }

    @Test
    public void should_not_fail_when_calling_primitive_returning_method() {
        StubbingReturnsSelfTest.Builder builder = Mockito.mock(StubbingReturnsSelfTest.Builder.class, Mockito.RETURNS_SELF);
        assertThat(builder.returnInt()).isEqualTo(0);
    }

    @Test
    public void use_full_builder_with_terminating_method() {
        StubbingReturnsSelfTest.HttpBuilder builder = Mockito.mock(StubbingReturnsSelfTest.HttpBuilder.class, Mockito.RETURNS_SELF);
        StubbingReturnsSelfTest.HttpRequesterWithHeaders requester = new StubbingReturnsSelfTest.HttpRequesterWithHeaders(builder);
        String response = "StatusCode: 200";
        Mockito.when(builder.request()).thenReturn(response);
        assertThat(requester.request("URI")).isEqualTo(response);
    }

    private static class Builder {
        public StubbingReturnsSelfTest.Builder returnSelf() {
            return this;
        }

        public String returnString() {
            return "Self";
        }

        public void returnNothing() {
        }

        public int returnInt() {
            return 1;
        }
    }

    private static class BuilderSubClass extends StubbingReturnsSelfTest.Builder {
        public StubbingReturnsSelfTest.BuilderSubClass returnsSubClass() {
            return this;
        }

        public StubbingReturnsSelfTest.Builder returnSuperClass() {
            return this;
        }
    }

    private static class HttpRequesterWithHeaders {
        private StubbingReturnsSelfTest.HttpBuilder builder;

        public HttpRequesterWithHeaders(StubbingReturnsSelfTest.HttpBuilder builder) {
            this.builder = builder;
        }

        public String request(String uri) {
            return builder.withUrl(uri).withHeader("Content-type: application/json").withHeader("Authorization: Bearer").request();
        }
    }

    private static class HttpBuilder {
        private String uri;

        private List<String> headers;

        public HttpBuilder() {
            this.headers = new ArrayList<String>();
        }

        public StubbingReturnsSelfTest.HttpBuilder withUrl(String uri) {
            this.uri = uri;
            return this;
        }

        public StubbingReturnsSelfTest.HttpBuilder withHeader(String header) {
            this.headers.add(header);
            return this;
        }

        public String request() {
            return (uri) + (headers.toString());
        }
    }
}

