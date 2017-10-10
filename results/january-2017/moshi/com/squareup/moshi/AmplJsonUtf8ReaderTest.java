/**
 * Copyright (C) 2010 Google Inc.
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


public final class AmplJsonUtf8ReaderTest {
    @org.junit.Test
    public void readingDoesNotBuffer() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer().writeUtf8("{}{}");
        com.squareup.moshi.JsonReader reader1 = com.squareup.moshi.JsonReader.of(buffer);
        reader1.beginObject();
        reader1.endObject();
        org.assertj.core.api.Assertions.assertThat(buffer.size()).isEqualTo(2);
        com.squareup.moshi.JsonReader reader2 = com.squareup.moshi.JsonReader.of(buffer);
        reader2.beginObject();
        reader2.endObject();
        org.assertj.core.api.Assertions.assertThat(buffer.size()).isEqualTo(0);
    }

    @org.junit.Test
    public void readObjectBuffer() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer().writeUtf8("{\"a\": \"android\", \"b\": \"banana\"}");
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.JsonReader.of(buffer);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("android");
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("b");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("banana");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void readObjectSource() throws java.io.IOException {
        okio.Buffer buffer = new okio.Buffer().writeUtf8("{\"a\": \"android\", \"b\": \"banana\"}");
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.JsonReader.of(okio.Okio.buffer(new okio.ForwardingSource(buffer) {        }));
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("android");
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("b");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("banana");
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void nullSource() {
        try {
            com.squareup.moshi.JsonReader.of(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException expected) {
        }
    }

    @org.junit.Test
    public void unescapingInvalidCharacters() throws java.io.IOException {
        java.lang.String json = "[\"\\u000g\"]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void unescapingTruncatedCharacters() throws java.io.IOException {
        java.lang.String json = "[\"\\u000";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (java.io.EOFException expected) {
        }
    }

    @org.junit.Test
    public void unescapingTruncatedSequence() throws java.io.IOException {
        java.lang.String json = "[\"\\";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictNonFiniteDoublesWithSkipValue() throws java.io.IOException {
        java.lang.String json = "[NaN]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    @org.junit.Ignore
    public void numberWithOctalPrefix() throws java.io.IOException {
        java.lang.String json = "[01]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        try {
            reader.nextInt();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        try {
            reader.nextDouble();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("01");
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void peekingUnquotedStringsPrefixedWithBooleans() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[truey]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("truey");
        reader.endArray();
    }

    @org.junit.Test
    public void malformedNumbers() throws java.io.IOException {
        assertNotANumber("-");
        assertNotANumber(".");
        // exponent lacks digit
        assertNotANumber("e");
        assertNotANumber("0e");
        assertNotANumber(".e");
        assertNotANumber("0.e");
        assertNotANumber("-.0e");
        // no integer
        assertNotANumber("e1");
        assertNotANumber(".e1");
        assertNotANumber("-e1");
        // trailing characters
        assertNotANumber("1x");
        assertNotANumber("1.1x");
        assertNotANumber("1e1x");
        assertNotANumber("1ex");
        assertNotANumber("1.1ex");
        assertNotANumber("1.1e1x");
        // fraction has no digit
        assertNotANumber("0.");
        assertNotANumber("-0.");
        assertNotANumber("0.e1");
        assertNotANumber("-0.e1");
        // no leading digit
        assertNotANumber(".0");
        assertNotANumber("-.0");
        assertNotANumber(".0e1");
        assertNotANumber("-.0e1");
    }

    private void assertNotANumber(java.lang.String s) throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader((("[" + s) + "]"));
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(s);
        reader.endArray();
    }

    @org.junit.Test
    public void peekingUnquotedStringsPrefixedWithIntegers() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[12.34e5x]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        try {
            reader.nextInt();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("12.34e5x");
    }

    @org.junit.Test
    public void peekLongMinValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[-9223372036854775808]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        org.assertj.core.api.Assertions.assertThat(reader.nextLong()).isEqualTo(-9223372036854775808L);
    }

    @org.junit.Test
    public void peekLongMaxValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[9223372036854775807]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        org.assertj.core.api.Assertions.assertThat(reader.nextLong()).isEqualTo(9223372036854775807L);
    }

    @org.junit.Test
    public void longLargerThanMaxLongThatWrapsAround() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[22233720368547758070]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
    }

    @org.junit.Test
    public void longLargerThanMinLongThatWrapsAround() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[-22233720368547758070]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
    }

    @org.junit.Test
    public void peekLargerThanLongMaxValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[9223372036854775808]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
    }

    @org.junit.Test
    public void precisionNotDiscarded() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[9223372036854775806.5]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
    }

    @org.junit.Test
    public void peekLargerThanLongMinValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[-9223372036854775809]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextDouble()).isEqualTo((-9.223372036854776E18));
    }

    @org.junit.Test
    public void highPrecisionLong() throws java.io.IOException {
        java.lang.String json = "[9223372036854775806.000]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextLong()).isEqualTo(9223372036854775806L);
        reader.endArray();
    }

    @org.junit.Test
    public void peekMuchLargerThanLongMinValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[-92233720368547758080]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.NUMBER);
        try {
            reader.nextLong();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
        }
        org.assertj.core.api.Assertions.assertThat(reader.nextDouble()).isEqualTo((-9.223372036854776E19));
    }

    @org.junit.Test
    public void quotedNumberWithEscape() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\"1234\"]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        org.assertj.core.api.Assertions.assertThat(reader.nextInt()).isEqualTo(1234);
    }

    @org.junit.Test
    public void mixedCaseLiterals() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[True,TruE,False,FALSE,NULL,nulL]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isFalse();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isFalse();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void missingValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void prematureEndOfInput() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true,");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextName();
            org.junit.Assert.fail();
        } catch (java.io.EOFException expected) {
        }
    }

    @org.junit.Test
    public void prematurelyClosed() throws java.io.IOException {
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":[]}");
            reader.beginObject();
            reader.close();
            reader.nextName();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":[]}");
            reader.close();
            reader.beginObject();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true}");
            reader.beginObject();
            reader.nextName();
            reader.peek();
            reader.close();
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (java.lang.IllegalStateException expected) {
        }
    }

    @org.junit.Test
    public void strictNameValueSeparator() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=>true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientNameValueSeparator() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=true}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=>true}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
    }

    @org.junit.Test
    public void strictNameValueSeparatorWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("{\"a\"=>true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void commentsInStringValue() throws java.lang.Exception {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\"// comment\"]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("// comment");
        reader.endArray();
        reader = com.squareup.moshi.TestUtil.newReader("{\"a\":\"#someComment\"}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("#someComment");
        reader.endObject();
        reader = com.squareup.moshi.TestUtil.newReader("{\"#//a\":\"#some //Comment\"}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("#//a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("#some //Comment");
        reader.endObject();
    }

    @org.junit.Test
    public void strictComments() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[// comment \n true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[# comment \n true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[/* comment */ true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientComments() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[// comment \n true]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader = com.squareup.moshi.TestUtil.newReader("[# comment \n true]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader = com.squareup.moshi.TestUtil.newReader("[/* comment */ true]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader = com.squareup.moshi.TestUtil.newReader("a//");
        reader.setLenient(true);
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void strictCommentsWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[// comment \n true]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[# comment \n true]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[/* comment */ true]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictUnquotedNames() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{a:true}");
        reader.beginObject();
        try {
            reader.nextName();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientUnquotedNames() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{a:true}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
    }

    @org.junit.Test
    public void jsonIsSingleUnquotedString() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("abc");
        reader.setLenient(true);
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("abc");
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void strictUnquotedNamesWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{a:true}");
        reader.beginObject();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictSingleQuotedNames() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{'a':true}");
        reader.beginObject();
        try {
            reader.nextName();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientSingleQuotedNames() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{'a':true}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
    }

    @org.junit.Test
    public void strictSingleQuotedNamesWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{'a':true}");
        reader.beginObject();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictUnquotedStrings() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[a]");
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictUnquotedStringsWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[a]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientUnquotedStrings() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[a]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
    }

    @org.junit.Test
    public void lenientUnquotedStringsDelimitedByComment() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[a#comment\n]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @org.junit.Test
    public void strictSingleQuotedStrings() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("['a']");
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientSingleQuotedStrings() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("['a']");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
    }

    @org.junit.Test
    public void strictSingleQuotedStringsWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("['a']");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictSemicolonDelimitedArray() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true;true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            reader.nextBoolean();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientSemicolonDelimitedArray() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true;true]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
    }

    @org.junit.Test
    public void strictSemicolonDelimitedArrayWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true;true]");
        reader.beginArray();
        try {
            reader.skipValue();
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictSemicolonDelimitedNameValuePair() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            reader.nextName();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientSemicolonDelimitedNameValuePair() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("b");
    }

    @org.junit.Test
    public void strictSemicolonDelimitedNameValuePairWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictUnnecessaryArraySeparators() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true,,true]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextNull();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[,true]");
        reader.beginArray();
        try {
            reader.nextNull();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[true,]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextNull();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[,]");
        reader.beginArray();
        try {
            reader.nextNull();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientUnnecessaryArraySeparators() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true,,true]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader.nextNull();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader.endArray();
        reader = com.squareup.moshi.TestUtil.newReader("[,true]");
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader.endArray();
        reader = com.squareup.moshi.TestUtil.newReader("[true,]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader.nextNull();
        reader.endArray();
        reader = com.squareup.moshi.TestUtil.newReader("[,]");
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
    }

    @org.junit.Test
    public void strictUnnecessaryArraySeparatorsWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true,,true]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[,true]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[true,]");
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
        reader = com.squareup.moshi.TestUtil.newReader("[,]");
        reader.beginArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void strictMultipleTopLevelValues() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[] []");
        reader.beginArray();
        reader.endArray();
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientMultipleTopLevelValues() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[] true {}");
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
        reader.beginObject();
        reader.endObject();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void strictMultipleTopLevelValuesWithSkipValue() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[] []");
        reader.beginArray();
        reader.endArray();
        try {
            reader.skipValue();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    @org.junit.Ignore
    public void bomIgnoredAsFirstCharacterOfDocument() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("\ufeff[]");
        reader.beginArray();
        reader.endArray();
    }

    @org.junit.Test
    public void bomForbiddenAsOtherCharacterInDocument() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\ufeff]");
        reader.beginArray();
        try {
            reader.endArray();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void failWithPosition() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\n\n\n\n\"a\",}]");
    }

    @org.junit.Test
    public void failWithPositionGreaterThanBufferSize() throws java.io.IOException {
        java.lang.String spaces = com.squareup.moshi.TestUtil.repeat(' ', 8192);
        testFailWithPosition("Expected value at path $[1]", (("[\n\n" + spaces) + "\n\n\n\"a\",}]"));
    }

    @org.junit.Test
    public void failWithPositionOverSlashSlashEndOfLineComment() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "\n// foo\n\n//bar\r\n[\"a\",}");
    }

    @org.junit.Test
    public void failWithPositionOverHashEndOfLineComment() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "\n# foo\n\n#bar\r\n[\"a\",}");
    }

    @org.junit.Test
    public void failWithPositionOverCStyleComment() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "\n\n/* foo\n*\n*\r\nbar */[\"a\",}");
    }

    @org.junit.Test
    public void failWithPositionOverQuotedString() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "[\"foo\nbar\r\nbaz\n\",\n  }");
    }

    @org.junit.Test
    public void failWithPositionOverUnquotedString() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\nabcd\n\n,}");
    }

    @org.junit.Test
    public void failWithEscapedNewlineCharacter() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\n\"\\\n\n\",}");
    }

    @org.junit.Test
    @org.junit.Ignore
    public void failWithPositionIsOffsetByBom() throws java.io.IOException {
        testFailWithPosition("Expected value at path $[1]", "\ufeff[\"a\",}]");
    }

    private void testFailWithPosition(java.lang.String message, java.lang.String json) throws java.io.IOException {
        // Validate that it works reading the string normally.
        com.squareup.moshi.JsonReader reader1 = com.squareup.moshi.TestUtil.newReader(json);
        reader1.setLenient(true);
        reader1.beginArray();
        reader1.nextString();
        try {
            reader1.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(message);
        }
        // Also validate that it works when skipping.
        com.squareup.moshi.JsonReader reader2 = com.squareup.moshi.TestUtil.newReader(json);
        reader2.setLenient(true);
        reader2.beginArray();
        reader2.skipValue();
        try {
            reader2.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(message);
        }
    }

    @org.junit.Test
    public void failWithPositionDeepPath() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[1,{\"a\":[2,3,}");
        reader.beginArray();
        reader.nextInt();
        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        reader.nextInt();
        reader.nextInt();
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Expected value at path $[1].a[2]");
        }
    }

    @org.junit.Test
    @org.junit.Ignore
    public void strictVeryLongNumber() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader((("[0." + (com.squareup.moshi.TestUtil.repeat('9', 8192))) + "]"));
        reader.beginArray();
        try {
            org.assertj.core.api.Assertions.assertThat(reader.nextDouble()).isEqualTo(1.0);
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    @org.junit.Ignore
    public void lenientVeryLongNumber() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader((("[0." + (com.squareup.moshi.TestUtil.repeat('9', 8192))) + "]"));
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        org.assertj.core.api.Assertions.assertThat(reader.nextDouble()).isEqualTo(1.0);
        reader.endArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
    }

    @org.junit.Test
    public void veryLongUnquotedLiteral() throws java.io.IOException {
        java.lang.String literal = ("a" + (com.squareup.moshi.TestUtil.repeat('b', 8192))) + "c";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader((("[" + literal) + "]"));
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(literal);
        reader.endArray();
    }

    @org.junit.Test
    public void tooDeeplyNestedArrays() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
        for (int i = 0; i < 31; i++) {
            reader.beginArray();
        }
        try {
            reader.beginArray();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage(("Nesting too deep at $[0][0][0][0][0][0][0][0][0][0][0][0][0]" + "[0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0]"));
        }
    }

    @org.junit.Test
    public void tooDeeplyNestedObjects() throws java.io.IOException {
        // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 31 levels deep.
        java.lang.String array = "{\"a\":%s}";
        java.lang.String json = "true";
        for (int i = 0; i < 32; i++) {
            json = java.lang.String.format(array, json);
        }
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        for (int i = 0; i < 31; i++) {
            reader.beginObject();
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        }
        try {
            reader.beginObject();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonDataException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Nesting too deep at $.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a");
        }
    }

    // http://code.google.com/p/google-gson/issues/detail?id=409
    @org.junit.Test
    public void stringEndingInSlash() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("/");
        reader.setLenient(true);
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void documentWithCommentEndingInSlash() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("/* foo *//");
        reader.setLenient(true);
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void stringWithLeadingSlash() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("/x");
        reader.setLenient(true);
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void unterminatedObject() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":\"android\"x");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("android");
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void veryLongQuotedString() throws java.io.IOException {
        char[] stringChars = new char[1024 * 16];
        java.util.Arrays.fill(stringChars, 'x');
        java.lang.String string = new java.lang.String(stringChars);
        java.lang.String json = ("[\"" + string) + "\"]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
        reader.endArray();
    }

    @org.junit.Test
    public void veryLongUnquotedString() throws java.io.IOException {
        char[] stringChars = new char[1024 * 16];
        java.util.Arrays.fill(stringChars, 'x');
        java.lang.String string = new java.lang.String(stringChars);
        java.lang.String json = ("[" + string) + "]";
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
        reader.endArray();
    }

    @org.junit.Test
    public void veryLongUnterminatedString() throws java.io.IOException {
        char[] stringChars = new char[1024 * 16];
        java.util.Arrays.fill(stringChars, 'x');
        java.lang.String string = new java.lang.String(stringChars);
        java.lang.String json = "[" + string;
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (java.io.EOFException expected) {
        }
    }

    @org.junit.Test
    public void strictExtraCommasInMaps() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":\"b\",}");
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("b");
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void lenientExtraCommasInMaps() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":\"b\",}");
        reader.setLenient(true);
        reader.beginObject();
        org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("b");
        try {
            reader.peek();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void malformedDocuments() throws java.io.IOException {
        assertDocument("{]", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{,", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{{", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{[", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{:", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\",", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\":}", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"::", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\":,", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=}", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=>}", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\":", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\"=", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\"=>", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\",", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("{\"name\"=>\"string\",\"name\"", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, com.squareup.moshi.JsonReader.Token.NAME);
        assertDocument("[}", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("[,]", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.NULL, com.squareup.moshi.JsonReader.Token.NULL, com.squareup.moshi.JsonReader.Token.END_ARRAY);
        assertDocument("{", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, java.io.EOFException.class);
        assertDocument("{\"name\"", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, java.io.EOFException.class);
        assertDocument("{\"name\",", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{'name'", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, java.io.EOFException.class);
        assertDocument("{'name',", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonEncodingException.class);
        assertDocument("{name", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, java.io.EOFException.class);
        assertDocument("[", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, java.io.EOFException.class);
        assertDocument("[string", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("[\"string\"", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("['string'", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("[123", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.NUMBER, java.io.EOFException.class);
        assertDocument("[123,", com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY, com.squareup.moshi.JsonReader.Token.NUMBER, java.io.EOFException.class);
        assertDocument("{\"name\":123", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.NUMBER, java.io.EOFException.class);
        assertDocument("{\"name\":123,", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.NUMBER, java.io.EOFException.class);
        assertDocument("{\"name\":\"string\"", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("{\"name\":\"string\",", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("{\"name\":\'string\'", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("{\"name\":\'string\',", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.STRING, java.io.EOFException.class);
        assertDocument("{\"name\":false", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.BOOLEAN, java.io.EOFException.class);
        assertDocument("{\"name\":false,,", com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT, com.squareup.moshi.JsonReader.Token.NAME, com.squareup.moshi.JsonReader.Token.BOOLEAN, com.squareup.moshi.JsonEncodingException.class);
    }

    /**
     * This test behave slightly differently in Gson 2.2 and earlier. It fails
     * during peek rather than during nextString().
     */
    @org.junit.Test
    public void unterminatedStringFailure() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\"string");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.STRING);
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
        }
    }

    @org.junit.Test
    public void invalidEscape() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\"str\\ing\"]");
        reader.beginArray();
        try {
            reader.nextString();
            org.junit.Assert.fail();
        } catch (com.squareup.moshi.JsonEncodingException expected) {
            org.assertj.core.api.Assertions.assertThat(expected).hasMessage("Invalid escape sequence: \\i at path $[0]");
        }
    }

    @org.junit.Test
    public void lenientInvalidEscape() throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[\"str\\ing\"]");
        reader.setLenient(true);
        reader.beginArray();
        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("string");
    }

    private void assertDocument(java.lang.String document, java.lang.Object... expectations) throws java.io.IOException {
        com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(document);
        reader.setLenient(true);
        for (java.lang.Object expectation : expectations) {
            if (expectation == (com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT)) {
                reader.beginObject();
            }else
                if (expectation == (com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY)) {
                    reader.beginArray();
                }else
                    if (expectation == (com.squareup.moshi.JsonReader.Token.END_OBJECT)) {
                        reader.endObject();
                    }else
                        if (expectation == (com.squareup.moshi.JsonReader.Token.END_ARRAY)) {
                            reader.endArray();
                        }else
                            if (expectation == (com.squareup.moshi.JsonReader.Token.NAME)) {
                                org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("name");
                            }else
                                if (expectation == (com.squareup.moshi.JsonReader.Token.BOOLEAN)) {
                                    org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isFalse();
                                }else
                                    if (expectation == (com.squareup.moshi.JsonReader.Token.STRING)) {
                                        org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("string");
                                    }else
                                        if (expectation == (com.squareup.moshi.JsonReader.Token.NUMBER)) {
                                            org.assertj.core.api.Assertions.assertThat(reader.nextInt()).isEqualTo(123);
                                        }else
                                            if (expectation == (com.squareup.moshi.JsonReader.Token.NULL)) {
                                                reader.nextNull();
                                            }else
                                                if ((expectation instanceof java.lang.Class) && (java.lang.Exception.class.isAssignableFrom(((java.lang.Class<?>) (expectation))))) {
                                                    try {
                                                        reader.peek();
                                                        org.junit.Assert.fail();
                                                    } catch (java.lang.Exception expected) {
                                                        org.junit.Assert.assertEquals(expected.toString(), expectation, expected.getClass());
                                                    }
                                                }else {
                                                    throw new java.lang.AssertionError();
                                                }
                                            
                                        
                                    
                                
                            
                        
                    
                
            
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#failWithPositionGreaterThanBufferSize */
    @org.junit.Test
    public void failWithPositionGreaterThanBufferSize_literalMutation848_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String spaces = com.squareup.moshi.TestUtil.repeat(' ', 8192);
            testFailWithPosition("Expected value at path $[1]", (("\\Q\\" + spaces) + "\n\n\n\"a\",}]"));
            org.junit.Assert.fail("failWithPositionGreaterThanBufferSize_literalMutation848 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#failWithPositionGreaterThanBufferSize */
    @org.junit.Test
    public void failWithPositionGreaterThanBufferSize_literalMutation854_failAssert5_literalMutation898_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String spaces = com.squareup.moshi.TestUtil.repeat(' ', 8192);
                testFailWithPosition("Expected value at path $[1]", (("\\\n\n" + spaces) + ")JWOM_4gd"));
                org.junit.Assert.fail("failWithPositionGreaterThanBufferSize_literalMutation854 should have thrown EOFException");
            } catch (java.io.EOFException eee) {
            }
            org.junit.Assert.fail("failWithPositionGreaterThanBufferSize_literalMutation854_failAssert5_literalMutation898 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#jsonIsSingleUnquotedString */
    @org.junit.Test(timeout = 10000)
    public void jsonIsSingleUnquotedString_add1475_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("abc");
            reader.setLenient(true);
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("abc");
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("abc");
            org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
            org.junit.Assert.fail("jsonIsSingleUnquotedString_add1475 should have thrown JsonDataException");
        } catch (com.squareup.moshi.JsonDataException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#lenientComments */
    @org.junit.Test(timeout = 10000)
    public void lenientComments_add1491_failAssert6() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[// comment \n true]");
            reader.setLenient(true);
            reader.beginArray();
            org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
            reader = com.squareup.moshi.TestUtil.newReader("[# comment \n true]");
            reader.setLenient(true);
            reader.beginArray();
            org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
            reader = com.squareup.moshi.TestUtil.newReader("[/* comment */ true]");
            reader.setLenient(true);
            reader.beginArray();
            org.assertj.core.api.Assertions.assertThat(reader.nextBoolean()).isTrue();
            reader = com.squareup.moshi.TestUtil.newReader("a//");
            reader.setLenient(true);
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo("a");
            org.assertj.core.api.Assertions.assertThat(reader.peek()).isEqualTo(com.squareup.moshi.JsonReader.Token.END_DOCUMENT);
            org.junit.Assert.fail("lenientComments_add1491 should have thrown JsonDataException");
        } catch (com.squareup.moshi.JsonDataException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#lenientSingleQuotedNames */
    @org.junit.Test(timeout = 10000)
    public void lenientSingleQuotedNames_add1802_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{'a':true}");
            reader.setLenient(true);
            // MethodCallAdder
            reader.beginObject();
            reader.beginObject();
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            org.junit.Assert.fail("lenientSingleQuotedNames_add1802 should have thrown JsonDataException");
        } catch (com.squareup.moshi.JsonDataException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#lenientUnquotedNames */
    @org.junit.Test(timeout = 10000)
    public void lenientUnquotedNames_add3208_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{a:true}");
            reader.setLenient(true);
            // MethodCallAdder
            reader.beginObject();
            reader.beginObject();
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            org.junit.Assert.fail("lenientUnquotedNames_add3208 should have thrown JsonDataException");
        } catch (com.squareup.moshi.JsonDataException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#strictSemicolonDelimitedArray */
    @org.junit.Test(timeout = 10000)
    public void strictSemicolonDelimitedArray_add5496_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true;true]");
            // MethodCallAdder
            reader.beginArray();
            reader.beginArray();
            try {
                reader.nextBoolean();
                reader.nextBoolean();
                org.junit.Assert.fail();
            } catch (com.squareup.moshi.JsonEncodingException expected) {
            }
            org.junit.Assert.fail("strictSemicolonDelimitedArray_add5496 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#strictSemicolonDelimitedArrayWithSkipValue */
    @org.junit.Test(timeout = 10000)
    public void strictSemicolonDelimitedArrayWithSkipValue_add5521_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("[true;true]");
            // MethodCallAdder
            reader.beginArray();
            reader.beginArray();
            try {
                reader.skipValue();
                reader.skipValue();
                org.junit.Assert.fail();
            } catch (com.squareup.moshi.JsonEncodingException expected) {
            }
            org.junit.Assert.fail("strictSemicolonDelimitedArrayWithSkipValue_add5521 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#strictSemicolonDelimitedNameValuePair */
    @org.junit.Test(timeout = 10000)
    public void strictSemicolonDelimitedNameValuePair_add5547_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true;\"b\":true}");
            reader.beginObject();
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            try {
                reader.nextBoolean();
                reader.nextName();
                org.junit.Assert.fail();
            } catch (com.squareup.moshi.JsonEncodingException expected) {
            }
            org.junit.Assert.fail("strictSemicolonDelimitedNameValuePair_add5547 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#strictSemicolonDelimitedNameValuePairWithSkipValue */
    @org.junit.Test(timeout = 10000)
    public void strictSemicolonDelimitedNameValuePairWithSkipValue_add5575_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader("{\"a\":true;\"b\":true}");
            reader.beginObject();
            // MethodCallAdder
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            org.assertj.core.api.Assertions.assertThat(reader.nextName()).isEqualTo("a");
            try {
                reader.skipValue();
                reader.skipValue();
                org.junit.Assert.fail();
            } catch (com.squareup.moshi.JsonEncodingException expected) {
            }
            org.junit.Assert.fail("strictSemicolonDelimitedNameValuePairWithSkipValue_add5575 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#unescapingInvalidCharacters */
    @org.junit.Test(timeout = 10000)
    public void unescapingInvalidCharacters_add11654_failAssert0_literalMutation11666_failAssert0_literalMutation11692_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    java.lang.String json = "F";
                    com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
                    // MethodCallAdder
                    reader.beginArray();
                    reader.beginArray();
                    try {
                        reader.nextString();
                        org.junit.Assert.fail();
                    } catch (com.squareup.moshi.JsonEncodingException expected) {
                    }
                    org.junit.Assert.fail("unescapingInvalidCharacters_add11654 should have thrown JsonDataException");
                } catch (com.squareup.moshi.JsonDataException eee) {
                }
                org.junit.Assert.fail("unescapingInvalidCharacters_add11654_failAssert0_literalMutation11666 should have thrown EOFException");
            } catch (java.io.EOFException eee) {
            }
            org.junit.Assert.fail("unescapingInvalidCharacters_add11654_failAssert0_literalMutation11666_failAssert0_literalMutation11692 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#veryLongUnquotedLiteral */
    @org.junit.Test
    public void veryLongUnquotedLiteral_literalMutation13472_failAssert6() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String literal = ("a" + (com.squareup.moshi.TestUtil.repeat('b', 8192))) + "c";
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader((("[" + literal) + "{"));
            reader.setLenient(true);
            reader.beginArray();
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(literal);
            reader.endArray();
            org.junit.Assert.fail("veryLongUnquotedLiteral_literalMutation13472 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#veryLongUnquotedLiteral */
    @org.junit.Test
    public void veryLongUnquotedLiteral_literalMutation13470_failAssert4_literalMutation13581() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String literal = ("a" + (com.squareup.moshi.TestUtil.repeat('b', 8192))) + "c";
            com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(((";" + literal) + "]"));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.squareup.moshi.JsonUtf8Reader)reader).getPath(), "$");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Reader)reader).failOnUnknown());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.squareup.moshi.JsonUtf8Reader)reader).isLenient());
            reader.setLenient(true);
            reader.beginArray();
            org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(literal);
            reader.endArray();
            org.junit.Assert.fail("veryLongUnquotedLiteral_literalMutation13470 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#veryLongUnquotedString */
    @org.junit.Test
    public void veryLongUnquotedString_literalMutation14579_failAssert7_literalMutation14733_failAssert22() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                char[] stringChars = new char[1024 * 16];
                java.util.Arrays.fill(stringChars, 'x');
                java.lang.String string = new java.lang.String(stringChars);
                java.lang.String json = ("[" + string) + "{";
                com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
                reader.setLenient(true);
                reader.beginArray();
                org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
                reader.endArray();
                org.junit.Assert.fail("veryLongUnquotedString_literalMutation14579 should have thrown EOFException");
            } catch (java.io.EOFException eee) {
            }
            org.junit.Assert.fail("veryLongUnquotedString_literalMutation14579_failAssert7_literalMutation14733 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#veryLongUnterminatedString */
    @org.junit.Test
    public void veryLongUnterminatedString_literalMutation16659_failAssert3_literalMutation16736_literalMutation17448_failAssert40() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                char[] stringChars = new char[1024 * 0];
                // AssertGenerator add assertion
                char[] array_2055561857 = new char[]{};
	char[] array_434073956 = (char[])stringChars;
	for(int ii = 0; ii <array_2055561857.length; ii++) {
		org.junit.Assert.assertEquals(array_2055561857[ii], array_434073956[ii]);
	};
                java.util.Arrays.fill(stringChars, 'x');
                java.lang.String string = new java.lang.String(stringChars);
                java.lang.String json = "\\" + string;
                com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
                reader.setLenient(true);
                reader.beginArray();
                org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
                try {
                    reader.peek();
                    org.junit.Assert.fail();
                } catch (java.io.EOFException expected) {
                }
                org.junit.Assert.fail("veryLongUnterminatedString_literalMutation16659 should have thrown EOFException");
            } catch (java.io.EOFException eee) {
            }
            org.junit.Assert.fail("veryLongUnterminatedString_literalMutation16659_failAssert3_literalMutation16736_literalMutation17448 should have thrown JsonEncodingException");
        } catch (com.squareup.moshi.JsonEncodingException eee) {
        }
    }

    /* amplification of com.squareup.moshi.JsonUtf8ReaderTest#veryLongUnterminatedString */
    @org.junit.Test
    public void veryLongUnterminatedString_literalMutation16659_failAssert3_literalMutation16739_failAssert12_literalMutation17484() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                char[] stringChars = new char[1024 * 0];
                // AssertGenerator add assertion
                char[] array_928730607 = new char[]{};
	char[] array_87979051 = (char[])stringChars;
	for(int ii = 0; ii <array_928730607.length; ii++) {
		org.junit.Assert.assertEquals(array_928730607[ii], array_87979051[ii]);
	};
                java.util.Arrays.fill(stringChars, 'x');
                java.lang.String string = new java.lang.String(stringChars);
                java.lang.String json = "f" + string;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(json, "f");
                com.squareup.moshi.JsonReader reader = com.squareup.moshi.TestUtil.newReader(json);
                reader.setLenient(true);
                reader.beginArray();
                org.assertj.core.api.Assertions.assertThat(reader.nextString()).isEqualTo(string);
                try {
                    reader.peek();
                    org.junit.Assert.fail();
                } catch (java.io.EOFException expected) {
                }
                org.junit.Assert.fail("veryLongUnterminatedString_literalMutation16659 should have thrown EOFException");
            } catch (java.io.EOFException eee) {
            }
            org.junit.Assert.fail("veryLongUnterminatedString_literalMutation16659_failAssert3_literalMutation16739 should have thrown JsonDataException");
        } catch (com.squareup.moshi.JsonDataException eee) {
        }
    }
}

