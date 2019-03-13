/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.string.immutable;


import com.gs.collections.api.IntIterable;
import com.gs.collections.api.list.primitive.ImmutableIntList;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.list.immutable.primitive.AbstractImmutableIntListTestCase;
import com.gs.collections.impl.test.Verify;
import java.io.IOException;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class CodePointAdapterTest extends AbstractImmutableIntListTestCase {
    private static final String UNICODE_STRING = "\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09";

    @SuppressWarnings("StringBufferReplaceableByString")
    @Test
    public void stringBuilder() {
        CodePointAdapter adapt = CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING);
        Assert.assertEquals(CodePointAdapterTest.UNICODE_STRING, new StringBuilder(adapt).toString());
    }

    @Test
    public void subSequence() {
        CodePointAdapter adapt = CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING);
        CharSequence sequence = adapt.subSequence(1, 3);
        Assert.assertEquals(CodePointAdapterTest.UNICODE_STRING.subSequence(1, 3), sequence);
    }

    @Override
    @Test
    public void testEquals() {
        super.testEquals();
        ImmutableIntList list1 = this.newWith(1, 2, 3, 4);
        ImmutableIntList list2 = this.newWith(4, 3, 2, 1);
        Assert.assertNotEquals(list1, list2);
        Assert.assertEquals(CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING), CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING));
        Assert.assertNotEquals(CodePointAdapter.adapt("\u3042\ud840\udc00\u3044\ud840\udc03\u3046"), CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING));
        Assert.assertEquals(CodePointAdapter.adapt("ABC"), CodePointAdapter.adapt("ABC"));
        Assert.assertNotEquals(CodePointAdapter.adapt("123"), CodePointAdapter.adapt("ABC"));
        Verify.assertEqualsAndHashCode(CodePointAdapter.adapt("ABC"), CodePointList.from("ABC"));
        Verify.assertEqualsAndHashCode(CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING), CodePointList.from(CodePointAdapterTest.UNICODE_STRING));
        Assert.assertNotEquals(CodePointList.from("123"), CodePointAdapter.adapt("ABC"));
        Assert.assertNotEquals(CodePointAdapter.adapt("ABC"), CodePointList.from("123"));
        Assert.assertNotEquals(CodePointList.from("ABCD"), CodePointAdapter.adapt("ABC"));
        Assert.assertNotEquals(CodePointAdapter.adapt("ABC"), CodePointList.from("ABCD"));
        Assert.assertNotEquals(CodePointList.from("ABC"), CodePointAdapter.adapt("ABCD"));
        Assert.assertNotEquals(CodePointAdapter.adapt("ABCD"), CodePointList.from("ABC"));
    }

    @Override
    @Test
    public void max() {
        Assert.assertEquals(9L, this.newWith(1, 2, 9).max());
        Assert.assertEquals(32L, this.newWith(1, 0, 9, 30, 31, 32).max());
        Assert.assertEquals(32L, this.newWith(0, 9, 30, 31, 32).max());
        Assert.assertEquals(31L, this.newWith(31, 0, 30).max());
        Assert.assertEquals(39L, this.newWith(32, 39, 35).max());
        Assert.assertEquals(this.classUnderTest().size(), this.classUnderTest().max());
    }

    @Override
    @Test
    public void min() {
        Assert.assertEquals(1L, this.newWith(1, 2, 9).min());
        Assert.assertEquals(0L, this.newWith(1, 0, 9, 30, 31, 32).min());
        Assert.assertEquals(31L, this.newWith(31, 32, 33).min());
        Assert.assertEquals(32L, this.newWith(32, 39, 35).min());
        Assert.assertEquals(1L, this.classUnderTest().min());
    }

    @Override
    @Test
    public void allSatisfy() {
        Assert.assertFalse(this.newWith(1, 0, 2).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertTrue(this.newWith(1, 2, 3).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(this.newWith(1, 0, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(this.newWith(1, 0, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertTrue(this.newWith(1, 2, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(this.newWith(32).allSatisfy(IntPredicates.equal(33)));
        IntIterable iterable = this.newWith(0, 1, 2);
        Assert.assertFalse(iterable.allSatisfy(( value) -> 3 < value));
        Assert.assertTrue(iterable.allSatisfy(IntPredicates.lessThan(3)));
        IntIterable iterable1 = this.classUnderTest();
        int size = iterable1.size();
        Assert.assertEquals((size == 0), iterable1.allSatisfy(IntPredicates.greaterThan(3)));
        Assert.assertEquals((size < 3), iterable1.allSatisfy(IntPredicates.lessThan(3)));
    }

    @Override
    @Test
    public void anySatisfy() {
        Assert.assertTrue(this.newWith(1, 2).anySatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(this.newWith(1, 2).anySatisfy(IntPredicates.equal(0)));
        Assert.assertTrue(this.newWith(31, 32).anySatisfy(IntPredicates.greaterThan(0)));
        Assert.assertTrue(this.newWith(2, 31, 32).anySatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(this.newWith(1, 31, 32).anySatisfy(IntPredicates.equal(0)));
        Assert.assertTrue(this.newWith(32).anySatisfy(IntPredicates.greaterThan(0)));
        IntIterable iterable = this.newWith(0, 1, 2);
        Assert.assertTrue(iterable.anySatisfy(( value) -> value < 3));
        Assert.assertFalse(iterable.anySatisfy(IntPredicates.greaterThan(3)));
        IntIterable iterable1 = this.classUnderTest();
        int size = iterable1.size();
        Assert.assertEquals((size > 3), iterable1.anySatisfy(IntPredicates.greaterThan(3)));
        Assert.assertEquals((size != 0), iterable1.anySatisfy(IntPredicates.lessThan(3)));
    }

    @Override
    @Test
    public void testToString() {
        StringBuilder expectedString = new StringBuilder();
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++) {
            expectedString.appendCodePoint((each + 1));
        }
        Assert.assertEquals(expectedString.toString(), this.classUnderTest().toString());
    }

    @Override
    @Test
    public void makeString() {
        ImmutableIntList list = this.classUnderTest();
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = list.size();
        for (int each = 0; each < size; each++) {
            expectedString.appendCodePoint((each + 1));
            expectedString1.appendCodePoint((each + 1));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        Assert.assertEquals(expectedString.toString(), list.makeString());
        Assert.assertEquals(expectedString1.toString(), list.makeString("/"));
    }

    @Override
    @Test
    public void appendString() {
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++) {
            expectedString.appendCodePoint((each + 1));
            expectedString1.appendCodePoint((each + 1));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        ImmutableIntList list = this.classUnderTest();
        StringBuilder appendable2 = new StringBuilder();
        list.appendString(appendable2);
        Assert.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        list.appendString(appendable3, "/");
        Assert.assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @SuppressWarnings("StringBufferMayBeStringBuilder")
    @Test
    public void appendStringStringBuffer() {
        StringBuffer expectedString = new StringBuffer("");
        StringBuffer expectedString1 = new StringBuffer("");
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++) {
            expectedString.appendCodePoint((each + 1));
            expectedString1.appendCodePoint((each + 1));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        ImmutableIntList list = this.classUnderTest();
        StringBuffer appendable2 = new StringBuffer();
        list.appendString(appendable2);
        Assert.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuffer appendable3 = new StringBuffer();
        list.appendString(appendable3, "/");
        Assert.assertEquals(expectedString1.toString(), appendable3.toString());
        StringBuffer appendable4 = new StringBuffer();
        this.classUnderTest().appendString(appendable4, "", "", "");
        Assert.assertEquals(this.classUnderTest().toString(), appendable4.toString());
    }

    @Test
    public void appendStringAppendable() {
        StringBuilder expectedString = new StringBuilder();
        StringBuilder expectedString1 = new StringBuilder();
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++) {
            expectedString.appendCodePoint((each + 1));
            expectedString1.appendCodePoint((each + 1));
            expectedString.append((each == (size - 1) ? "" : ", "));
            expectedString1.append((each == (size - 1) ? "" : "/"));
        }
        ImmutableIntList list = this.classUnderTest();
        CodePointAdapterTest.SBAppendable appendable2 = new CodePointAdapterTest.SBAppendable();
        list.appendString(appendable2);
        Assert.assertEquals(expectedString.toString(), appendable2.toString());
        CodePointAdapterTest.SBAppendable appendable3 = new CodePointAdapterTest.SBAppendable();
        list.appendString(appendable3, "/");
        Assert.assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @Test
    public void collectCodePointUnicode() {
        Assert.assertEquals(CodePointAdapterTest.UNICODE_STRING.codePoints().boxed().collect(Collectors.toList()), CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).collect(( i) -> i));
        Assert.assertEquals(CodePointAdapterTest.UNICODE_STRING.codePoints().boxed().collect(Collectors.toList()), CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).collect(( i) -> i));
    }

    @Test
    public void selectCodePointUnicode() {
        String string = CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).select(Character::isBmpCodePoint).toString();
        Assert.assertEquals("\u3042\u3044\u3046", string);
    }

    @Test
    public void allSatisfyUnicode() {
        Assert.assertTrue(CodePointAdapter.adapt("\u3042\u3044\u3046").allSatisfy(Character::isBmpCodePoint));
        Assert.assertFalse(CodePointAdapter.adapt("\ud840\udc00\ud840\udc03\ud83d\ude09").allSatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void anySatisfyUnicode() {
        Assert.assertTrue(CodePointAdapter.adapt("\u3042\u3044\u3046").anySatisfy(Character::isBmpCodePoint));
        Assert.assertFalse(CodePointAdapter.adapt("\ud840\udc00\ud840\udc03\ud83d\ude09").anySatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void noneSatisfyUnicode() {
        Assert.assertFalse(CodePointAdapter.adapt("\u3042\u3044\u3046").noneSatisfy(Character::isBmpCodePoint));
        Assert.assertTrue(CodePointAdapter.adapt("\ud840\udc00\ud840\udc03\ud83d\ude09").noneSatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void forEachUnicode() {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).forEach(builder::appendCodePoint);
        Assert.assertEquals(CodePointAdapterTest.UNICODE_STRING, builder.toString());
    }

    @Test
    public void asReversedForEachUnicode() {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).asReversed().forEach(builder::appendCodePoint);
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\ud840\udc00\u3042\ud840\udc03\u3044\ud83d\ude09\u3046").asReversed().forEach(builder2::appendCodePoint);
        Assert.assertEquals("\u3046\ud83d\ude09\u3044\ud840\udc03\u3042\ud840\udc00", builder2.toString());
        CodePointAdapter.adapt("").asReversed().forEach((int codePoint) -> Assert.fail());
    }

    @Test
    public void asReversedForEachInvalidUnicode() {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt("\u3042\udc00\ud840\u3044\udc03\ud840\u3046\ude09\ud83d").asReversed().forEach(builder::appendCodePoint);
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\ud840\u3044\ud840\u3046\ud840").asReversed().forEach(builder2::appendCodePoint);
        Assert.assertEquals("\ud840\u3046\ud840\u3044\ud840\u3042", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\udc00\u3044\udc03\u3046\udc06").asReversed().forEach(builder3::appendCodePoint);
        Assert.assertEquals("\udc06\u3046\udc03\u3044\udc00\u3042", builder3.toString());
        CodePointAdapter.adapt("").asReversed().forEach((int codePoint) -> Assert.fail());
    }

    @Test
    public void toReversedForEachUnicode() {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(CodePointAdapterTest.UNICODE_STRING).toReversed().forEach(builder::appendCodePoint);
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\ud840\udc00\u3042\ud840\udc03\u3044\ud83d\ude09\u3046").toReversed().forEach(builder2::appendCodePoint);
        Assert.assertEquals("\u3046\ud83d\ude09\u3044\ud840\udc03\u3042\ud840\udc00", builder2.toString());
        CodePointAdapter.adapt("").toReversed().forEach((int codePoint) -> Assert.fail());
    }

    @Test
    public void toReversedForEachInvalidUnicode() {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt("\u3042\udc00\ud840\u3044\udc03\ud840\u3046\ude09\ud83d").toReversed().forEach(builder::appendCodePoint);
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\ud840\u3044\ud840\u3046\ud840").toReversed().forEach(builder2::appendCodePoint);
        Assert.assertEquals("\ud840\u3046\ud840\u3044\ud840\u3042", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\udc00\u3044\udc03\u3046\udc06").toReversed().forEach(builder3::appendCodePoint);
        Assert.assertEquals("\udc06\u3046\udc03\u3044\udc00\u3042", builder3.toString());
        CodePointAdapter.adapt("").toReversed().forEach((int codePoint) -> Assert.fail());
    }

    @Test
    public void distinctUnicode() {
        Assert.assertEquals("\ud840\udc00\ud840\udc03\ud83d\ude09", CodePointAdapter.adapt("\ud840\udc00\ud840\udc03\ud83d\ude09\ud840\udc00\ud840\udc03\ud83d\ude09").distinct().toString());
    }

    @Test
    public void newWithUnicode() {
        CodePointAdapter codePointAdapter = CodePointAdapter.adapt("");
        CodePointAdapter collection = codePointAdapter.newWith(12354);
        CodePointAdapter collection0 = codePointAdapter.newWith(12354).newWith(131072);
        CodePointAdapter collection1 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356);
        CodePointAdapter collection2 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356).newWith(131075);
        CodePointAdapter collection3 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356).newWith(131075).newWith(12358);
        this.assertSizeAndContains(codePointAdapter);
        this.assertSizeAndContains(collection, 12354);
        this.assertSizeAndContains(collection0, 12354, 131072);
        this.assertSizeAndContains(collection1, 12354, 131072, 12356);
        this.assertSizeAndContains(collection2, 12354, 131072, 12356, 131075);
        this.assertSizeAndContains(collection3, 12354, 131072, 12356, 131075, 12358);
    }

    @Test
    public void newWithoutUnicode() {
        CodePointAdapter collection0 = CodePointAdapter.adapt("\u3042\ud840\udc00\u3044\ud840\udc03\u3046");
        CodePointAdapter collection1 = collection0.newWithout(12358);
        CodePointAdapter collection2 = collection1.newWithout(131075);
        CodePointAdapter collection3 = collection2.newWithout(12356);
        CodePointAdapter collection4 = collection3.newWithout(131072);
        CodePointAdapter collection5 = collection4.newWithout(12354);
        CodePointAdapter collection6 = collection5.newWithout(131078);
        this.assertSizeAndContains(collection6);
        this.assertSizeAndContains(collection5);
        this.assertSizeAndContains(collection4, 12354);
        this.assertSizeAndContains(collection3, 12354, 131072);
        this.assertSizeAndContains(collection2, 12354, 131072, 12356);
        this.assertSizeAndContains(collection1, 12354, 131072, 12356, 131075);
    }

    private static class SBAppendable implements Appendable {
        private final StringBuilder builder = new StringBuilder();

        @Override
        public Appendable append(char c) throws IOException {
            return this.builder.append(c);
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return this.builder.append(csq);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return this.builder.append(csq, start, end);
        }

        @Override
        public String toString() {
            return this.builder.toString();
        }
    }
}

