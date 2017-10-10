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


package com.squareup.moshi;


public final class AmplJsonQualifiersTest {
    @org.junit.Test
    public void builtInTypes() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.BuiltInTypesJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobar\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobar\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    static class BuiltInTypesJsonAdapter {
        @com.squareup.moshi.ToJson
        java.lang.String fooPrefixStringToString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) {
            return "foo" + s;
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String fooPrefixStringFromString(java.lang.String s) throws java.lang.Exception {
            if (!(s.startsWith("foo")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(3);
        }
    }

    @org.junit.Test
    public void readerWriterJsonAdapter() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.ReaderWriterJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobar\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobar\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    static class ReaderWriterJsonAdapter {
        @com.squareup.moshi.ToJson
        void fooPrefixStringToString(com.squareup.moshi.JsonWriter jsonWriter, @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) throws java.io.IOException {
            jsonWriter.value(("foo" + s));
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String fooPrefixStringFromString(com.squareup.moshi.JsonReader reader) throws java.lang.Exception {
            java.lang.String s = reader.nextString();
            if (!(s.startsWith("foo")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(3);
        }
    }

    /**
     * * Fields with this annotation get "foo" as a prefix in the JSON.
     */
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    public @interface FooPrefix {    }

    /**
     * * Fields with this annotation get "baz" as a suffix in the JSON.
     */
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    public @interface BazSuffix {    }

    static class StringAndFooString {
        java.lang.String a;

        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String b;
    }

    static class StringAndFooBazString {
        java.lang.String a;

        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String b;
    }

    @org.junit.Test
    public void builtInTypesWithMultipleAnnotations() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.BuiltInTypesWithMultipleAnnotationsJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    static class BuiltInTypesWithMultipleAnnotationsJsonAdapter {
        @com.squareup.moshi.ToJson
        java.lang.String fooPrefixAndBazSuffixStringToString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String s) {
            return ("foo" + s) + "baz";
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String fooPrefixAndBazSuffixStringFromString(java.lang.String s) throws java.lang.Exception {
            if (!(s.startsWith("foo")))
                throw new com.squareup.moshi.JsonDataException();
            
            if (!(s.endsWith("baz")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(3, ((s.length()) - 3));
        }
    }

    @org.junit.Test
    public void readerWriterWithMultipleAnnotations() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.ReaderWriterWithMultipleAnnotationsJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    static class ReaderWriterWithMultipleAnnotationsJsonAdapter {
        @com.squareup.moshi.ToJson
        void fooPrefixAndBazSuffixStringToString(com.squareup.moshi.JsonWriter jsonWriter, @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String s) throws java.io.IOException {
            jsonWriter.value((("foo" + s) + "baz"));
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String fooPrefixAndBazSuffixStringFromString(com.squareup.moshi.JsonReader reader) throws java.lang.Exception {
            java.lang.String s = reader.nextString();
            if (!(s.startsWith("foo")))
                throw new com.squareup.moshi.JsonDataException();
            
            if (!(s.endsWith("baz")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(3, ((s.length()) - 3));
        }
    }

    @org.junit.Test
    public void basicTypesAnnotationDelegating() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.BuiltInTypesDelegatingJsonAdapter()).add(new com.squareup.moshi.AmplJsonQualifiersTest.BuiltInTypesJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    static class BuiltInTypesDelegatingJsonAdapter {
        @com.squareup.moshi.ToJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String fooPrefixAndBazSuffixStringToString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String s) {
            return s + "baz";
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        @com.squareup.moshi.AmplJsonQualifiersTest.BazSuffix
        java.lang.String fooPrefixAndBazSuffixStringFromString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) throws java.lang.Exception {
            if (!(s.endsWith("baz")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(0, ((s.length()) - 3));
        }
    }

    @org.junit.Test
    public void readerWriterAnnotationDelegating() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.BuiltInTypesDelegatingJsonAdapter()).add(new com.squareup.moshi.AmplJsonQualifiersTest.ReaderWriterJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooBazString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobarbaz\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    @org.junit.Test
    public void manualJsonAdapter() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.lang.String> fooPrefixAdapter = new com.squareup.moshi.JsonAdapter<java.lang.String>() {
            @java.lang.Override
            public java.lang.String fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                java.lang.String s = reader.nextString();
                if (!(s.startsWith("foo")))
                    throw new com.squareup.moshi.JsonDataException();
                
                return s.substring(3);
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.String value) throws java.io.IOException {
                writer.value(("foo" + value));
            }
        };
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(java.lang.String.class, com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix.class, fooPrefixAdapter).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString();
        v1.a = "aa";
        v1.b = "bar";
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobar\"}");
        com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobar\"}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
    }

    @org.junit.Test
    public void noJsonAdapterForAnnotatedType() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().build();
        try {
            moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
        }
    }

    @org.junit.Test
    public void annotationWithoutJsonQualifierIsIgnoredByAdapterMethods() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.MissingJsonQualifierJsonAdapter()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.DateAndMillisDate> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.DateAndMillisDate.class);
        com.squareup.moshi.AmplJsonQualifiersTest.DateAndMillisDate v1 = new com.squareup.moshi.AmplJsonQualifiersTest.DateAndMillisDate();
        v1.a = new java.util.Date(5);
        v1.b = new java.util.Date(7);
        org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":5,\"b\":7}");
        com.squareup.moshi.AmplJsonQualifiersTest.DateAndMillisDate v2 = adapter.fromJson("{\"a\":5,\"b\":7}");
        org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo(new java.util.Date(5));
        org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo(new java.util.Date(7));
    }

    /**
     * * Despite the fact that these methods are annotated, they match all dates.
     */
    static class MissingJsonQualifierJsonAdapter {
        @com.squareup.moshi.ToJson
        long dateToJson(@com.squareup.moshi.AmplJsonQualifiersTest.Millis
        java.util.Date d) {
            return d.getTime();
        }

        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.Millis
        java.util.Date jsonToDate(long value) throws java.lang.Exception {
            return new java.util.Date(value);
        }
    }

    /**
     * * This annotation does nothing.
     */
    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Millis {    }

    static class DateAndMillisDate {
        java.util.Date a;

        @com.squareup.moshi.AmplJsonQualifiersTest.Millis
        java.util.Date b;
    }

    @org.junit.Test
    public void annotationWithoutJsonQualifierIsRejectedOnRegistration() throws java.lang.Exception {
        com.squareup.moshi.JsonAdapter<java.util.Date> jsonAdapter = new com.squareup.moshi.JsonAdapter<java.util.Date>() {
            @java.lang.Override
            public java.util.Date fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                throw new java.lang.AssertionError();
            }

            @java.lang.Override
            public void toJson(com.squareup.moshi.JsonWriter writer, java.util.Date value) throws java.io.IOException {
                throw new java.lang.AssertionError();
            }
        };
        try {
            new com.squareup.moshi.Moshi.Builder().add(java.util.Date.class, com.squareup.moshi.AmplJsonQualifiersTest.Millis.class, jsonAdapter);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("interface com.squareup.moshi.JsonQualifiersTest$Millis " + "does not have @JsonQualifier"));
        }
    }

    @org.junit.Test
    public void annotationsConflict() throws java.lang.Exception {
        try {
            new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.AnnotationsConflictJsonAdapter());
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessageContaining("Conflicting @ToJson methods");
        }
    }

    static class AnnotationsConflictJsonAdapter {
        @com.squareup.moshi.ToJson
        java.lang.String fooPrefixStringToString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) {
            return "foo" + s;
        }

        @com.squareup.moshi.ToJson
        java.lang.String fooPrefixStringToString2(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) {
            return "foo" + s;
        }
    }

    @org.junit.Test
    public void toButNoFromJson() throws java.lang.Exception {
        // Building it is okay.
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.ToButNoFromJsonAdapter()).build();
        try {
            moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("No @FromJson adapter for class java.lang.String " + "annotated [@com.squareup.moshi.JsonQualifiersTest$FooPrefix()]"));
        }
    }

    static class ToButNoFromJsonAdapter {
        @com.squareup.moshi.ToJson
        java.lang.String fooPrefixStringToString(@com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String s) {
            return "foo" + s;
        }
    }

    @org.junit.Test
    public void fromButNoToJson() throws java.lang.Exception {
        // Building it is okay.
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplJsonQualifiersTest.FromButNoToJsonAdapter()).build();
        try {
            moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
            org.junit.Assert.fail();
        } catch (java.lang.IllegalArgumentException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("No @ToJson adapter for class java.lang.String " + "annotated [@com.squareup.moshi.JsonQualifiersTest$FooPrefix()]"));
        }
    }

    static class FromButNoToJsonAdapter {
        @com.squareup.moshi.FromJson
        @com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix
        java.lang.String fooPrefixStringFromString(java.lang.String s) throws java.lang.Exception {
            if (!(s.startsWith("foo")))
                throw new com.squareup.moshi.JsonDataException();
            
            return s.substring(3);
        }
    }

    /* amplification of com.squareup.moshi.JsonQualifiersTest#manualJsonAdapter */
    @org.junit.Test(timeout = 10000)
    public void manualJsonAdapter_add10679_failAssert0_literalMutation10702() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonAdapter<java.lang.String> fooPrefixAdapter = new com.squareup.moshi.JsonAdapter<java.lang.String>() {
                @java.lang.Override
                public java.lang.String fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                    java.lang.String s = reader.nextString();
                    if (!(s.startsWith("foo")))
                        throw new com.squareup.moshi.JsonDataException();
                    
                    return s.substring(3);
                }

                @java.lang.Override
                public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.String value) throws java.io.IOException {
                    // AssertGenerator replace invocation
                    com.squareup.moshi.JsonWriter o_manualJsonAdapter_add10679_failAssert0_literalMutation10702__20 = // MethodCallAdder
writer.value(("%oo" + value));
                    // AssertGenerator add assertion
                    org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10702__20).isLenient());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10702__20).getSerializeNulls());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10702__20).getIndent(), "");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10702__20).getPath(), "$.b");
                    writer.value(("foo" + value));
                }
            };
            com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(java.lang.String.class, com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix.class, fooPrefixAdapter).build();
            com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
            com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString();
            v1.a = "aa";
            v1.b = "bar";
            org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobar\"}");
            com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobar\"}");
            org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
            org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
            org.junit.Assert.fail("manualJsonAdapter_add10679 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonQualifiersTest#manualJsonAdapter */
    @org.junit.Test(timeout = 10000)
    public void manualJsonAdapter_add10679_failAssert0_literalMutation10706_literalMutation10917() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonAdapter<java.lang.String> fooPrefixAdapter = new com.squareup.moshi.JsonAdapter<java.lang.String>() {
                @java.lang.Override
                public java.lang.String fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                    java.lang.String s = reader.nextString();
                    if (!(s.startsWith("foo")))
                        throw new com.squareup.moshi.JsonDataException();
                    
                    return s.substring(3);
                }

                @java.lang.Override
                public void toJson(com.squareup.moshi.JsonWriter writer, java.lang.String value) throws java.io.IOException {
                    // AssertGenerator replace invocation
                    com.squareup.moshi.JsonWriter o_manualJsonAdapter_add10679_failAssert0_literalMutation10706__20 = // MethodCallAdder
writer.value(("foo" + value));
                    // AssertGenerator add assertion
                    org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10706__20).isLenient());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10706__20).getSerializeNulls());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10706__20).getPath(), "$.b");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Writer)o_manualJsonAdapter_add10679_failAssert0_literalMutation10706__20).getIndent(), "");
                    writer.value(("}!:" + value));
                }
            };
            com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(java.lang.String.class, com.squareup.moshi.AmplJsonQualifiersTest.FooPrefix.class, fooPrefixAdapter).build();
            com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString> adapter = moshi.adapter(com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString.class);
            com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v1 = new com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString();
            v1.a = "aa";
            v1.b = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(v1.b, "");
            org.assertj.core.api.Assertions.assertThat(adapter.toJson(v1)).isEqualTo("{\"a\":\"aa\",\"b\":\"foobar\"}");
            com.squareup.moshi.AmplJsonQualifiersTest.StringAndFooString v2 = adapter.fromJson("{\"a\":\"aa\",\"b\":\"foobar\"}");
            org.assertj.core.api.Assertions.assertThat(v2.a).isEqualTo("aa");
            org.assertj.core.api.Assertions.assertThat(v2.b).isEqualTo("bar");
            org.junit.Assert.fail("manualJsonAdapter_add10679 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

