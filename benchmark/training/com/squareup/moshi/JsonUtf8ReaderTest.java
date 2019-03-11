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


import JsonReader.Token.END_DOCUMENT;
import JsonReader.Token.STRING;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Okio;
import org.junit.Assert;
import org.junit.Test;


public final class JsonUtf8ReaderTest {
    @Test
    public void readingDoesNotBuffer() throws IOException {
        Buffer buffer = new Buffer().writeUtf8("{}{}");
        JsonReader reader1 = JsonReader.of(buffer);
        reader1.beginObject();
        reader1.endObject();
        assertThat(buffer.size()).isEqualTo(2);
        JsonReader reader2 = JsonReader.of(buffer);
        reader2.beginObject();
        reader2.endObject();
        assertThat(buffer.size()).isEqualTo(0);
    }

    @Test
    public void readObjectBuffer() throws IOException {
        Buffer buffer = new Buffer().writeUtf8("{\"a\": \"android\", \"b\": \"banana\"}");
        JsonReader reader = JsonReader.of(buffer);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("android");
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.nextString()).isEqualTo("banana");
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void readObjectSource() throws IOException {
        Buffer buffer = new Buffer().writeUtf8("{\"a\": \"android\", \"b\": \"banana\"}");
        JsonReader reader = JsonReader.of(Okio.buffer(new ForwardingSource(buffer) {}));
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("android");
        assertThat(reader.nextName()).isEqualTo("b");
        assertThat(reader.nextString()).isEqualTo("banana");
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void nullSource() {
        try {
            JsonReader.of(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void unescapingInvalidCharacters() throws IOException {
        String json = "[\"\\u000g\"]";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void unescapingTruncatedCharacters() throws IOException {
        String json = "[\"\\u000";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @Test
    public void unescapingTruncatedSequence() throws IOException {
        String json = "[\"\\";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictNonFiniteDoublesWithSkipValue() throws IOException {
        String json = "[NaN]";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void peekingUnquotedStringsPrefixedWithBooleans() throws IOException {
        JsonReader reader = TestUtil.newReader("[truey]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextString()).isEqualTo("truey");
        reader.endArray();
    }

    @Test
    public void malformedNumbers() throws IOException {
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

    @Test
    public void peekingUnquotedStringsPrefixedWithIntegers() throws IOException {
        JsonReader reader = TestUtil.newReader("[12.34e5x]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
        try {
            reader.nextInt();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextString()).isEqualTo("12.34e5x");
    }

    @Test
    public void peekLongMinValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[-9223372036854775808]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        assertThat(reader.nextLong()).isEqualTo(-9223372036854775808L);
    }

    @Test
    public void peekLongMaxValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[9223372036854775807]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        assertThat(reader.nextLong()).isEqualTo(9223372036854775807L);
    }

    @Test
    public void longLargerThanMaxLongThatWrapsAround() throws IOException {
        JsonReader reader = TestUtil.newReader("[22233720368547758070]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void longLargerThanMinLongThatWrapsAround() throws IOException {
        JsonReader reader = TestUtil.newReader("[-22233720368547758070]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void peekLargerThanLongMaxValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[9223372036854775808]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void precisionNotDiscarded() throws IOException {
        JsonReader reader = TestUtil.newReader("[9223372036854775806.5]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
    }

    @Test
    public void peekLargerThanLongMinValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[-9223372036854775809]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextDouble()).isEqualTo((-9.223372036854776E18));
    }

    @Test
    public void highPrecisionLong() throws IOException {
        String json = "[9223372036854775806.000]";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        assertThat(reader.nextLong()).isEqualTo(9223372036854775806L);
        reader.endArray();
    }

    @Test
    public void peekMuchLargerThanLongMinValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[-92233720368547758080]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.NUMBER);
        try {
            reader.nextLong();
            Assert.fail();
        } catch (JsonDataException expected) {
        }
        assertThat(reader.nextDouble()).isEqualTo((-9.223372036854776E19));
    }

    @Test
    public void negativeZeroIsANumber() throws Exception {
        JsonReader reader = TestUtil.newReader("-0");
        Assert.assertEquals(Token.NUMBER, reader.peek());
        Assert.assertEquals("-0", reader.nextString());
    }

    @Test
    public void numberToStringCoersion() throws Exception {
        JsonReader reader = TestUtil.newReader("[0, 9223372036854775807, 2.5, 3.010, \"a\", \"5\"]");
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("0");
        assertThat(reader.nextString()).isEqualTo("9223372036854775807");
        assertThat(reader.nextString()).isEqualTo("2.5");
        assertThat(reader.nextString()).isEqualTo("3.010");
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("5");
        reader.endArray();
    }

    @Test
    public void quotedNumberWithEscape() throws IOException {
        JsonReader reader = TestUtil.newReader("[\"1234\"]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(Token.STRING);
        assertThat(reader.nextInt()).isEqualTo(1234);
    }

    @Test
    public void mixedCaseLiterals() throws IOException {
        JsonReader reader = TestUtil.newReader("[True,TruE,False,FALSE,NULL,nulL]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextBoolean()).isFalse();
        assertThat(reader.nextBoolean()).isFalse();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void missingValue() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void prematureEndOfInput() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":true,");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextName();
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void prematurelyClosed() throws IOException {
        try {
            JsonReader reader = TestUtil.newReader("{\"a\":[]}");
            reader.beginObject();
            reader.close();
            reader.nextName();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            JsonReader reader = TestUtil.newReader("{\"a\":[]}");
            reader.close();
            reader.beginObject();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            JsonReader reader = TestUtil.newReader("{\"a\":true}");
            reader.beginObject();
            reader.nextName();
            reader.peek();
            reader.close();
            reader.nextBoolean();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void strictNameValueSeparator() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\"=true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("{\"a\"=>true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientNameValueSeparator() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\"=true}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextBoolean()).isTrue();
        reader = TestUtil.newReader("{\"a\"=>true}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextBoolean()).isTrue();
    }

    @Test
    public void strictNameValueSeparatorWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\"=true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("{\"a\"=>true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void commentsInStringValue() throws Exception {
        JsonReader reader = TestUtil.newReader("[\"// comment\"]");
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("// comment");
        reader.endArray();
        reader = TestUtil.newReader("{\"a\":\"#someComment\"}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("#someComment");
        reader.endObject();
        reader = TestUtil.newReader("{\"#//a\":\"#some //Comment\"}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("#//a");
        assertThat(reader.nextString()).isEqualTo("#some //Comment");
        reader.endObject();
    }

    @Test
    public void strictComments() throws IOException {
        JsonReader reader = TestUtil.newReader("[// comment \n true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[# comment \n true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[/* comment */ true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientComments() throws IOException {
        JsonReader reader = TestUtil.newReader("[// comment \n true]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader = TestUtil.newReader("[# comment \n true]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader = TestUtil.newReader("[/* comment */ true]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader = TestUtil.newReader("a//");
        reader.setLenient(true);
        assertThat(reader.nextString()).isEqualTo("a");
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void strictCommentsWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[// comment \n true]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[# comment \n true]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[/* comment */ true]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictUnquotedNames() throws IOException {
        JsonReader reader = TestUtil.newReader("{a:true}");
        reader.beginObject();
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientUnquotedNames() throws IOException {
        JsonReader reader = TestUtil.newReader("{a:true}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
    }

    @Test
    public void jsonIsSingleUnquotedString() throws IOException {
        JsonReader reader = TestUtil.newReader("abc");
        reader.setLenient(true);
        assertThat(reader.nextString()).isEqualTo("abc");
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void strictUnquotedNamesWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("{a:true}");
        reader.beginObject();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictSingleQuotedNames() throws IOException {
        JsonReader reader = TestUtil.newReader("{'a':true}");
        reader.beginObject();
        try {
            reader.nextName();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientSingleQuotedNames() throws IOException {
        JsonReader reader = TestUtil.newReader("{'a':true}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
    }

    @Test
    public void strictSingleQuotedNamesWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("{'a':true}");
        reader.beginObject();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictUnquotedStrings() throws IOException {
        JsonReader reader = TestUtil.newReader("[a]");
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictUnquotedStringsWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[a]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientUnquotedStrings() throws IOException {
        JsonReader reader = TestUtil.newReader("[a]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("a");
    }

    @Test
    public void lenientUnquotedStringsDelimitedByComment() throws IOException {
        JsonReader reader = TestUtil.newReader("[a#comment\n]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("a");
        reader.endArray();
    }

    @Test
    public void strictSingleQuotedStrings() throws IOException {
        JsonReader reader = TestUtil.newReader("['a']");
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientSingleQuotedStrings() throws IOException {
        JsonReader reader = TestUtil.newReader("['a']");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("a");
    }

    @Test
    public void strictSingleQuotedStringsWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("['a']");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictSemicolonDelimitedArray() throws IOException {
        JsonReader reader = TestUtil.newReader("[true;true]");
        reader.beginArray();
        try {
            reader.nextBoolean();
            reader.nextBoolean();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientSemicolonDelimitedArray() throws IOException {
        JsonReader reader = TestUtil.newReader("[true;true]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextBoolean()).isTrue();
    }

    @Test
    public void strictSemicolonDelimitedArrayWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[true;true]");
        reader.beginArray();
        try {
            reader.skipValue();
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictSemicolonDelimitedNameValuePair() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.nextBoolean();
            reader.nextName();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientSemicolonDelimitedNameValuePair() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextBoolean()).isTrue();
        assertThat(reader.nextName()).isEqualTo("b");
    }

    @Test
    public void strictSemicolonDelimitedNameValuePairWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":true;\"b\":true}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        try {
            reader.skipValue();
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictUnnecessaryArraySeparators() throws IOException {
        JsonReader reader = TestUtil.newReader("[true,,true]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[,true]");
        reader.beginArray();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[true,]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[,]");
        reader.beginArray();
        try {
            reader.nextNull();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientUnnecessaryArraySeparators() throws IOException {
        JsonReader reader = TestUtil.newReader("[true,,true]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader.nextNull();
        assertThat(reader.nextBoolean()).isTrue();
        reader.endArray();
        reader = TestUtil.newReader("[,true]");
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        assertThat(reader.nextBoolean()).isTrue();
        reader.endArray();
        reader = TestUtil.newReader("[true,]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader.nextNull();
        reader.endArray();
        reader = TestUtil.newReader("[,]");
        reader.setLenient(true);
        reader.beginArray();
        reader.nextNull();
        reader.nextNull();
        reader.endArray();
    }

    @Test
    public void strictUnnecessaryArraySeparatorsWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[true,,true]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[,true]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[true,]");
        reader.beginArray();
        assertThat(reader.nextBoolean()).isTrue();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
        reader = TestUtil.newReader("[,]");
        reader.beginArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void strictMultipleTopLevelValues() throws IOException {
        JsonReader reader = TestUtil.newReader("[] []");
        reader.beginArray();
        reader.endArray();
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientMultipleTopLevelValues() throws IOException {
        JsonReader reader = TestUtil.newReader("[] true {}");
        reader.setLenient(true);
        reader.beginArray();
        reader.endArray();
        assertThat(reader.nextBoolean()).isTrue();
        reader.beginObject();
        reader.endObject();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void strictMultipleTopLevelValuesWithSkipValue() throws IOException {
        JsonReader reader = TestUtil.newReader("[] []");
        reader.beginArray();
        reader.endArray();
        try {
            reader.skipValue();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void bomForbiddenAsOtherCharacterInDocument() throws IOException {
        JsonReader reader = TestUtil.newReader("[\ufeff]");
        reader.beginArray();
        try {
            reader.endArray();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void failWithPosition() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\n\n\n\n\"a\",}]");
    }

    @Test
    public void failWithPositionGreaterThanBufferSize() throws IOException {
        String spaces = TestUtil.repeat(' ', 8192);
        testFailWithPosition("Expected value at path $[1]", (("[\n\n" + spaces) + "\n\n\n\"a\",}]"));
    }

    @Test
    public void failWithPositionOverSlashSlashEndOfLineComment() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "\n// foo\n\n//bar\r\n[\"a\",}");
    }

    @Test
    public void failWithPositionOverHashEndOfLineComment() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "\n# foo\n\n#bar\r\n[\"a\",}");
    }

    @Test
    public void failWithPositionOverCStyleComment() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "\n\n/* foo\n*\n*\r\nbar */[\"a\",}");
    }

    @Test
    public void failWithPositionOverQuotedString() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "[\"foo\nbar\r\nbaz\n\",\n  }");
    }

    @Test
    public void failWithPositionOverUnquotedString() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\nabcd\n\n,}");
    }

    @Test
    public void failWithEscapedNewlineCharacter() throws IOException {
        testFailWithPosition("Expected value at path $[1]", "[\n\n\"\\\n\n\",}");
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void failWithPositionDeepPath() throws IOException {
        JsonReader reader = TestUtil.newReader("[1,{\"a\":[2,3,}");
        reader.beginArray();
        reader.nextInt();
        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        reader.nextInt();
        reader.nextInt();
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
            assertThat(expected).hasMessage("Expected value at path $[1].a[2]");
        }
    }

    @Test
    public void failureMessagePathFromSkipName() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":[42,}");
        reader.beginObject();
        reader.skipName();
        reader.beginArray();
        reader.nextInt();
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
            assertThat(expected).hasMessage("Expected value at path $.null[1]");
        }
    }

    @Test
    public void veryLongUnquotedLiteral() throws IOException {
        String literal = ("a" + (TestUtil.repeat('b', 8192))) + "c";
        JsonReader reader = TestUtil.newReader((("[" + literal) + "]"));
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo(literal);
        reader.endArray();
    }

    @Test
    public void tooDeeplyNestedArrays() throws IOException {
        JsonReader reader = TestUtil.newReader(((TestUtil.repeat("[", ((TestUtil.MAX_DEPTH) + 1))) + (TestUtil.repeat("]", ((TestUtil.MAX_DEPTH) + 1)))));
        for (int i = 0; i < (TestUtil.MAX_DEPTH); i++) {
            reader.beginArray();
        }
        try {
            reader.beginArray();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage(("Nesting too deep at $" + (TestUtil.repeat("[0]", TestUtil.MAX_DEPTH))));
        }
    }

    @Test
    public void tooDeeplyNestedObjects() throws IOException {
        // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 255 levels deep.
        String array = "{\"a\":%s}";
        String json = "true";
        for (int i = 0; i < ((TestUtil.MAX_DEPTH) + 1); i++) {
            json = String.format(array, json);
        }
        JsonReader reader = TestUtil.newReader(json);
        for (int i = 0; i < (TestUtil.MAX_DEPTH); i++) {
            reader.beginObject();
            assertThat(reader.nextName()).isEqualTo("a");
        }
        try {
            reader.beginObject();
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage(("Nesting too deep at $" + (TestUtil.repeat(".a", TestUtil.MAX_DEPTH))));
        }
    }

    // http://code.google.com/p/google-gson/issues/detail?id=409
    @Test
    public void stringEndingInSlash() throws IOException {
        JsonReader reader = TestUtil.newReader("/");
        reader.setLenient(true);
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void documentWithCommentEndingInSlash() throws IOException {
        JsonReader reader = TestUtil.newReader("/* foo *//");
        reader.setLenient(true);
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void stringWithLeadingSlash() throws IOException {
        JsonReader reader = TestUtil.newReader("/x");
        reader.setLenient(true);
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void unterminatedObject() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":\"android\"x");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("android");
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void veryLongQuotedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = ("[\"" + string) + "\"]";
        JsonReader reader = TestUtil.newReader(json);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo(string);
        reader.endArray();
    }

    @Test
    public void veryLongUnquotedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = ("[" + string) + "]";
        JsonReader reader = TestUtil.newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo(string);
        reader.endArray();
    }

    @Test
    public void veryLongUnterminatedString() throws IOException {
        char[] stringChars = new char[1024 * 16];
        Arrays.fill(stringChars, 'x');
        String string = new String(stringChars);
        String json = "[" + string;
        JsonReader reader = TestUtil.newReader(json);
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo(string);
        try {
            reader.peek();
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @Test
    public void strictExtraCommasInMaps() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":\"b\",}");
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("b");
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void lenientExtraCommasInMaps() throws IOException {
        JsonReader reader = TestUtil.newReader("{\"a\":\"b\",}");
        reader.setLenient(true);
        reader.beginObject();
        assertThat(reader.nextName()).isEqualTo("a");
        assertThat(reader.nextString()).isEqualTo("b");
        try {
            reader.peek();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void malformedDocuments() throws IOException {
        assertDocument("{]", Token.BEGIN_OBJECT, JsonEncodingException.class);
        assertDocument("{,", Token.BEGIN_OBJECT, JsonEncodingException.class);
        assertDocument("{{", Token.BEGIN_OBJECT, JsonEncodingException.class);
        assertDocument("{[", Token.BEGIN_OBJECT, JsonEncodingException.class);
        assertDocument("{:", Token.BEGIN_OBJECT, JsonEncodingException.class);
        assertDocument("{\"name\",", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\":}", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\"::", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\":,", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\"=}", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\"=>}", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\":", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\"=", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\"=>", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, JsonEncodingException.class);
        assertDocument("{\"name\"=>\"string\",", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, EOFException.class);
        assertDocument("{\"name\"=>\"string\",\"name\"", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, Token.NAME);
        assertDocument("[}", Token.BEGIN_ARRAY, JsonEncodingException.class);
        assertDocument("[,]", Token.BEGIN_ARRAY, Token.NULL, Token.NULL, Token.END_ARRAY);
        assertDocument("{", Token.BEGIN_OBJECT, EOFException.class);
        assertDocument("{\"name\"", Token.BEGIN_OBJECT, Token.NAME, EOFException.class);
        assertDocument("{\"name\",", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{'name'", Token.BEGIN_OBJECT, Token.NAME, EOFException.class);
        assertDocument("{'name',", Token.BEGIN_OBJECT, Token.NAME, JsonEncodingException.class);
        assertDocument("{name", Token.BEGIN_OBJECT, Token.NAME, EOFException.class);
        assertDocument("[", Token.BEGIN_ARRAY, EOFException.class);
        assertDocument("[string", Token.BEGIN_ARRAY, Token.STRING, EOFException.class);
        assertDocument("[\"string\"", Token.BEGIN_ARRAY, Token.STRING, EOFException.class);
        assertDocument("['string'", Token.BEGIN_ARRAY, Token.STRING, EOFException.class);
        assertDocument("[123", Token.BEGIN_ARRAY, Token.NUMBER, EOFException.class);
        assertDocument("[123,", Token.BEGIN_ARRAY, Token.NUMBER, EOFException.class);
        assertDocument("{\"name\":123", Token.BEGIN_OBJECT, Token.NAME, Token.NUMBER, EOFException.class);
        assertDocument("{\"name\":123,", Token.BEGIN_OBJECT, Token.NAME, Token.NUMBER, EOFException.class);
        assertDocument("{\"name\":\"string\"", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, EOFException.class);
        assertDocument("{\"name\":\"string\",", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, EOFException.class);
        assertDocument("{\"name\":\'string\'", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, EOFException.class);
        assertDocument("{\"name\":\'string\',", Token.BEGIN_OBJECT, Token.NAME, Token.STRING, EOFException.class);
        assertDocument("{\"name\":false", Token.BEGIN_OBJECT, Token.NAME, Token.BOOLEAN, EOFException.class);
        assertDocument("{\"name\":false,,", Token.BEGIN_OBJECT, Token.NAME, Token.BOOLEAN, JsonEncodingException.class);
    }

    /**
     * This test behave slightly differently in Gson 2.2 and earlier. It fails
     * during peek rather than during nextString().
     */
    @Test
    public void unterminatedStringFailure() throws IOException {
        JsonReader reader = TestUtil.newReader("[\"string");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.peek()).isEqualTo(STRING);
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
        }
    }

    @Test
    public void invalidEscape() throws IOException {
        JsonReader reader = TestUtil.newReader("[\"str\\ing\"]");
        reader.beginArray();
        try {
            reader.nextString();
            Assert.fail();
        } catch (JsonEncodingException expected) {
            assertThat(expected).hasMessage("Invalid escape sequence: \\i at path $[0]");
        }
    }

    @Test
    public void lenientInvalidEscape() throws IOException {
        JsonReader reader = TestUtil.newReader("[\"str\\ing\"]");
        reader.setLenient(true);
        reader.beginArray();
        assertThat(reader.nextString()).isEqualTo("string");
    }
}

