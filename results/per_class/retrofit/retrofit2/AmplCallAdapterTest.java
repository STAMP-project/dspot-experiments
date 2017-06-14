/**
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package retrofit2;


public final class AmplCallAdapterTest {
    @org.junit.Test
    public void parameterizedTypeInvalidIndex() {
        java.lang.reflect.ParameterizedType listOfString = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.List<java.lang.String>>() {        }.getType()));
        try {
            retrofit2.CallAdapter.Factory.getParameterUpperBound((-1), listOfString);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Index -1 not in range [0,1) for java.util.List<java.lang.String>");
        }
        try {
            retrofit2.CallAdapter.Factory.getParameterUpperBound(1, listOfString);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("Index 1 not in range [0,1) for java.util.List<java.lang.String>");
        }
    }

    @org.junit.Test
    public void parameterizedTypes() {
        java.lang.reflect.ParameterizedType one = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.List<java.lang.String>>() {        }.getType()));
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getParameterUpperBound(0, one)).isSameAs(java.lang.String.class);
        java.lang.reflect.ParameterizedType two = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.Map<java.lang.String, java.lang.String>>() {        }.getType()));
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getParameterUpperBound(0, two)).isSameAs(java.lang.String.class);
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getParameterUpperBound(1, two)).isSameAs(java.lang.String.class);
        java.lang.reflect.ParameterizedType wild = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.List<? extends java.lang.CharSequence>>() {        }.getType()));
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getParameterUpperBound(0, wild)).isSameAs(java.lang.CharSequence.class);
    }

    @org.junit.Test
    public void rawTypeThrowsOnNull() {
        try {
            retrofit2.CallAdapter.Factory.getRawType(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("type == null");
        }
    }

    @org.junit.Test
    public void rawTypes() throws java.lang.NoSuchMethodException {
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(java.lang.String.class)).isSameAs(java.lang.String.class);
        java.lang.reflect.Type listOfString = new com.google.common.reflect.TypeToken<java.util.List<java.lang.String>>() {        }.getType();
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(listOfString)).isSameAs(java.util.List.class);
        java.lang.reflect.Type stringArray = new com.google.common.reflect.TypeToken<java.lang.String[]>() {        }.getType();
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(stringArray)).isSameAs(java.lang.String[].class);
        java.lang.reflect.Type wild = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.List<? extends java.lang.CharSequence>>() {        }.getType())).getActualTypeArguments()[0];
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(wild)).isSameAs(java.lang.CharSequence.class);
        java.lang.reflect.Type wildParam = ((java.lang.reflect.ParameterizedType) (new com.google.common.reflect.TypeToken<java.util.List<? extends java.util.List<java.lang.String>>>() {        }.getType())).getActualTypeArguments()[0];
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(wildParam)).isSameAs(java.util.List.class);
        java.lang.reflect.Type typeVar = retrofit2.AmplCallAdapterTest.A.class.getDeclaredMethod("method").getGenericReturnType();
        org.assertj.core.api.Assertions.assertThat(retrofit2.CallAdapter.Factory.getRawType(typeVar)).isSameAs(java.lang.Object.class);
    }

    // Used reflectively.
    @java.lang.SuppressWarnings(value = "unused")
    static class A<T> {
        T method() {
            return null;
        }
    }
}

