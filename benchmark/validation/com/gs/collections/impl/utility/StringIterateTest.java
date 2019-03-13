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
package com.gs.collections.impl.utility;


import AddFunction.STRING;
import CharSets.immutable;
import CodePointFunction.PASS_THRU;
import CodePointFunction.TO_LOWERCASE;
import CodePointFunction.TO_UPPERCASE;
import CodePointPredicate.IS_BMP;
import CodePointPredicate.IS_DIGIT;
import CodePointPredicate.IS_UNDEFINED;
import CodePointPredicate.IS_UPPERCASE;
import Lists.mutable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.procedure.primitive.CharProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.CharList;
import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.ImmutableCharSet;
import com.gs.collections.api.set.primitive.ImmutableIntSet;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.factory.primitive.CharPredicates;
import com.gs.collections.impl.block.factory.primitive.CharToCharFunctions;
import com.gs.collections.impl.block.procedure.primitive.CodePointProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.string.immutable.CharAdapter;
import com.gs.collections.impl.string.immutable.CodePointAdapter;
import com.gs.collections.impl.string.immutable.CodePointList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link StringIterate}.
 */
public class StringIterateTest {
    public static final String THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG = "The quick brown fox jumps over the lazy dog.";

    public static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

    public static final Twin<String> HALF_ABET = StringIterate.splitAtIndex(StringIterateTest.ALPHABET_LOWERCASE, 13);

    public static final String TQBFJOTLD_MINUS_HALF_ABET_1 = "t qu rown ox ups ovr t zy o.";

    public static final String TQBFJOTLD_MINUS_HALF_ABET_2 = "he ick b f jm e he la dg.";

    @Test
    public void asCharAdapter() {
        CharAdapter answer = StringIterate.asCharAdapter("HelloHellow").collectChar(Character::toUpperCase).select(( c) -> c != 'W').distinct().toReversed().reject(CharAdapter.adapt("LE")::contains).newWith('!');
        Assert.assertEquals("OH!", answer.toString());
        Assert.assertEquals("OH!", answer.toStringBuilder().toString());
        Assert.assertEquals("OH!", answer.makeString(""));
        CharList charList = StringIterate.asCharAdapter("HelloHellow").asLazy().collectChar(Character::toUpperCase).select(( c) -> c != 'W').toList().distinct().toReversed().reject(CharAdapter.adapt("LE")::contains).with('!');
        Assert.assertEquals("OH!", CharAdapter.from(charList).toString());
        Assert.assertEquals("OH!", CharAdapter.from(CharAdapter.from(charList)).toString());
        String helloUppercase2 = StringIterate.asCharAdapter("Hello").asLazy().collectChar(Character::toUpperCase).makeString("");
        Assert.assertEquals("HELLO", helloUppercase2);
        CharArrayList arraylist = new CharArrayList();
        StringIterate.asCharAdapter("Hello".toUpperCase()).chars().sorted().forEach(( e) -> arraylist.add(((char) (e))));
        Assert.assertEquals(StringIterate.asCharAdapter("EHLLO"), arraylist);
        ImmutableCharList arrayList2 = StringIterate.asCharAdapter("Hello".toUpperCase()).toSortedList().toImmutable();
        Assert.assertEquals(StringIterate.asCharAdapter("EHLLO"), arrayList2);
        Assert.assertEquals(StringIterate.asCharAdapter("HELLO"), CharAdapter.adapt("hello").collectChar(Character::toUpperCase));
    }

    @Test
    public void asCharAdapterExtra() {
        Assert.assertEquals(9, StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(( c) -> !(Character.isLetter(c))));
        Assert.assertTrue(StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));
        Assert.assertEquals(8, StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(Character::isWhitespace));
        Verify.assertSize(26, StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).asLazy().select(Character::isLetter).collectChar(Character::toLowerCase).toSet());
        ImmutableCharSet alphaCharAdapter = StringIterate.asCharAdapter(StringIterateTest.ALPHABET_LOWERCASE).toSet().toImmutable();
        Assert.assertTrue(StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaCharAdapter));
        Assert.assertEquals(immutable.empty(), alphaCharAdapter.newWithoutAll(StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_1, StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.asCharAdapter(StringIterateTest.HALF_ABET.getOne())).toString());
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_2, StringIterate.asCharAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.asCharAdapter(StringIterateTest.HALF_ABET.getTwo())).toString());
    }

    @Test
    public void buildTheAlphabetFromEmpty() {
        String alphabet = StringIterate.asCharAdapter("").newWith('a').newWithAll(StringIterate.asCharAdapter(StringIterateTest.HALF_ABET.getOne())).newWithAll(StringIterate.asCharAdapter(StringIterateTest.HALF_ABET.getTwo())).newWithout('a').toString();
        Assert.assertEquals(StringIterateTest.ALPHABET_LOWERCASE, alphabet);
    }

    @Test
    public void asCodePointAdapter() {
        CodePointAdapter answer = StringIterate.asCodePointAdapter("HelloHellow").collectInt(Character::toUpperCase).select(( i) -> i != 'W').distinct().toReversed().reject(CodePointAdapter.adapt("LE")::contains).newWith('!');
        Assert.assertEquals("OH!", answer.toString());
        Assert.assertEquals("OH!", answer.toStringBuilder().toString());
        Assert.assertEquals("OH!", answer.makeString(""));
        IntList intList = StringIterate.asCodePointAdapter("HelloHellow").asLazy().collectInt(Character::toUpperCase).select(( i) -> i != 'W').toList().distinct().toReversed().reject(CodePointAdapter.adapt("LE")::contains).with('!');
        Assert.assertEquals("OH!", CodePointAdapter.from(intList).toString());
        Assert.assertEquals("OH!", CodePointAdapter.from(CodePointAdapter.from(intList)).toString());
    }

    @Test
    public void asCodePointAdapterExtra() {
        Assert.assertEquals(9, StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(( i) -> !(Character.isLetter(i))));
        Assert.assertTrue(StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));
        Assert.assertEquals(8, StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(Character::isWhitespace));
        Verify.assertSize(26, StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).asLazy().select(Character::isLetter).collectInt(Character::toLowerCase).toSet());
        ImmutableIntSet alphaints = StringIterate.asCodePointAdapter(StringIterateTest.ALPHABET_LOWERCASE).toSet().toImmutable();
        Assert.assertTrue(StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        Assert.assertEquals(IntSets.immutable.empty(), alphaints.newWithoutAll(StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_1, StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.asCodePointAdapter(StringIterateTest.HALF_ABET.getOne())).toString());
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_2, StringIterate.asCodePointAdapter(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.asCodePointAdapter(StringIterateTest.HALF_ABET.getTwo())).toString());
    }

    @Test
    public void toCodePointList() {
        CodePointList answer = StringIterate.toCodePointList("Hello").collectInt(Character::toUpperCase).select(( i) -> i != 'W').distinct().toReversed().reject(CodePointList.from("LE")::contains).newWith('!');
        Assert.assertEquals("OH!", answer.toString());
        Assert.assertEquals("OH!", answer.toStringBuilder().toString());
        Assert.assertEquals("OH!", answer.makeString(""));
        IntList intList = StringIterate.toCodePointList("HelloHellow").asLazy().collectInt(Character::toUpperCase).select(( i) -> i != 'W').toList().distinct().toReversed().reject(CodePointList.from("LE")::contains).with('!');
        Assert.assertEquals("OH!", CodePointList.from(intList).toString());
        Assert.assertEquals("OH!", CodePointList.from(CodePointList.from(intList)).toString());
    }

    @Test
    public void toCodePointListExtra() {
        Assert.assertEquals(9, StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(( i) -> !(Character.isLetter(i))));
        Assert.assertTrue(StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));
        Assert.assertEquals(8, StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).count(Character::isWhitespace));
        Verify.assertSize(26, StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).asLazy().select(Character::isLetter).collectInt(Character::toLowerCase).toSet());
        ImmutableIntSet alphaints = StringIterate.toCodePointList(StringIterateTest.ALPHABET_LOWERCASE).toSet().toImmutable();
        Assert.assertTrue(StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        Assert.assertEquals(IntSets.immutable.empty(), alphaints.newWithoutAll(StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assert.assertTrue(StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(StringIterate.toCodePointList(StringIterateTest.HALF_ABET.getOne())));
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_1, StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.toCodePointList(StringIterateTest.HALF_ABET.getOne())).toString());
        Assert.assertEquals(StringIterateTest.TQBFJOTLD_MINUS_HALF_ABET_2, StringIterate.toCodePointList(StringIterateTest.THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase()).newWithoutAll(StringIterate.toCodePointList(StringIterateTest.HALF_ABET.getTwo())).toString());
    }

    @Test
    public void englishToUpperLowerCase() {
        Assert.assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        Assert.assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
    }

    @Test
    public void collect() {
        Assert.assertEquals("ABC", StringIterate.collect("abc", CharToCharFunctions.toUpperCase()));
        Assert.assertEquals("abc", StringIterate.collect("abc", CharToCharFunctions.toLowerCase()));
    }

    @Test
    public void collectCodePoint() {
        Assert.assertEquals("ABC", StringIterate.collect("abc", TO_UPPERCASE));
        Assert.assertEquals("abc", StringIterate.collect("abc", TO_LOWERCASE));
    }

    @Test
    public void collectCodePointUnicode() {
        Assert.assertEquals("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", StringIterate.collect("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", PASS_THRU));
        Assert.assertEquals("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", StringIterate.collect("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", PASS_THRU));
    }

    @Test
    public void englishToUpperCase() {
        Assert.assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        Assert.assertEquals("A,B,C", StringIterate.englishToUpperCase("a,b,c"));
        Assert.assertSame("A,B,C", StringIterate.englishToUpperCase("A,B,C"));
    }

    @Test
    public void englishToLowerCase() {
        Assert.assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
        Assert.assertEquals("a,b,c", StringIterate.englishToLowerCase("A,B,C"));
        Assert.assertSame("a,b,c", StringIterate.englishToLowerCase("a,b,c"));
    }

    @Test
    public void englishIsUpperLowerCase() {
        String allValues = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~`!@#$%^&*()_-+=[]{};<>,.?/|";
        String jdkUpper = allValues.toUpperCase();
        String upper = StringIterate.englishToUpperCase(allValues);
        Assert.assertEquals(jdkUpper.length(), upper.length());
        Assert.assertEquals(jdkUpper, upper);
        String jdkLower = allValues.toLowerCase();
        String lower = StringIterate.englishToLowerCase(allValues);
        Assert.assertEquals(jdkLower.length(), lower.length());
        Assert.assertEquals(jdkLower, lower);
    }

    @Test
    public void select() {
        String string = StringIterate.select("1a2a3", CharPredicates.isDigit());
        Assert.assertEquals("123", string);
    }

    @Test
    public void selectCodePoint() {
        String string = StringIterate.select("1a2a3", IS_DIGIT);
        Assert.assertEquals("123", string);
    }

    @Test
    public void selectCodePointUnicode() {
        String string = StringIterate.select("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", IS_BMP);
        Assert.assertEquals("\u3042\u3044\u3046", string);
    }

    @Test
    public void detect() {
        char character = StringIterate.detect("1a2a3", CharPredicates.isLetter());
        Assert.assertEquals('a', character);
    }

    @Test
    public void detectIfNone() {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b".charAt(0));
        Assert.assertEquals('b', character);
    }

    @Test
    public void detectIfNoneWithString() {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b");
        Assert.assertEquals('b', character);
    }

    @Test
    public void allSatisfy() {
        Assert.assertTrue(StringIterate.allSatisfy("MARY", CharPredicates.isUpperCase()));
        Assert.assertFalse(StringIterate.allSatisfy("Mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void allSatisfyCodePoint() {
        Assert.assertTrue(StringIterate.allSatisfy("MARY", IS_UPPERCASE));
        Assert.assertFalse(StringIterate.allSatisfy("Mary", IS_UPPERCASE));
    }

    @Test
    public void allSatisfyCodePointUnicode() {
        Assert.assertTrue(StringIterate.allSatisfy("\u3042\u3044\u3046", IS_BMP));
        Assert.assertFalse(StringIterate.allSatisfy("\ud840\udc00\ud840\udc03\ud83d\ude09", IS_BMP));
    }

    @Test
    public void anySatisfy() {
        Assert.assertTrue(StringIterate.anySatisfy("MARY", CharPredicates.isUpperCase()));
        Assert.assertFalse(StringIterate.anySatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void anySatisfyCodePoint() {
        Assert.assertTrue(StringIterate.anySatisfy("MARY", IS_UPPERCASE));
        Assert.assertFalse(StringIterate.anySatisfy("mary", IS_UPPERCASE));
    }

    @Test
    public void anySatisfyCodePointUnicode() {
        Assert.assertTrue(StringIterate.anySatisfy("\u3042\u3044\u3046", IS_BMP));
        Assert.assertFalse(StringIterate.anySatisfy("\ud840\udc00\ud840\udc03\ud83d\ude09", IS_BMP));
    }

    @Test
    public void noneSatisfy() {
        Assert.assertFalse(StringIterate.noneSatisfy("MaRy", CharPredicates.isUpperCase()));
        Assert.assertTrue(StringIterate.noneSatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void noneSatisfyCodePoint() {
        Assert.assertFalse(StringIterate.noneSatisfy("MaRy", IS_UPPERCASE));
        Assert.assertTrue(StringIterate.noneSatisfy("mary", IS_UPPERCASE));
    }

    @Test
    public void noneSatisfyCodePointUnicode() {
        Assert.assertFalse(StringIterate.noneSatisfy("\u3042\u3044\u3046", IS_BMP));
        Assert.assertTrue(StringIterate.noneSatisfy("\ud840\udc00\ud840\udc03\ud83d\ude09", IS_BMP));
    }

    @Test
    public void isNumber() {
        Assert.assertTrue(StringIterate.isNumber("123"));
        Assert.assertFalse(StringIterate.isNumber("abc"));
        Assert.assertFalse(StringIterate.isNumber(""));
    }

    @Test
    public void isAlphaNumeric() {
        Assert.assertTrue(StringIterate.isAlphaNumeric("123"));
        Assert.assertTrue(StringIterate.isAlphaNumeric("abc"));
        Assert.assertTrue(StringIterate.isAlphaNumeric("123abc"));
        Assert.assertFalse(StringIterate.isAlphaNumeric("!@#"));
        Assert.assertFalse(StringIterate.isAlphaNumeric(""));
    }

    @Test
    public void csvTokensToList() {
        String tokens = "Ted,Mary  ";
        MutableList<String> results = StringIterate.csvTokensToList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Ted", "Mary  ");
    }

    @Test
    public void csvTokensToSortedList() {
        String tokens = " Ted, Mary ";
        MutableList<String> results = StringIterate.csvTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, " Mary ", " Ted");
    }

    @Test
    public void csvTrimmedTokensToSortedList() {
        String tokens = " Ted,Mary ";
        MutableList<String> results = StringIterate.csvTrimmedTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToSet() {
        String tokens = "Ted,Mary";
        MutableSet<String> results = StringIterate.csvTokensToSet(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsAll(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToReverseSortedList() {
        String tokens = "Ted,Mary";
        MutableList<String> results = StringIterate.csvTokensToReverseSortedList(tokens);
        Verify.assertSize(2, results);
    }

    @Test
    public void tokensToMap() {
        String tokens = "1:Ted|2:Mary";
        MutableMap<String, String> results = StringIterate.tokensToMap(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue("1", "Ted", results);
        Verify.assertContainsKeyValue("2", "Mary", results);
    }

    @Test
    public void tokensToMapWithFunctions() {
        String tokens = "1:Ted|2:Mary";
        Function<String, String> stringPassThruFunction = Functions.getPassThru();
        MutableMap<Integer, String> results = StringIterate.tokensToMap(tokens, "|", ":", Integer::valueOf, stringPassThruFunction);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue(1, "Ted", results);
        Verify.assertContainsKeyValue(2, "Mary", results);
    }

    @Test
    public void reject() {
        String string = StringIterate.reject("1a2b3c", CharPredicates.isDigit());
        Assert.assertEquals("abc", string);
    }

    @Test
    public void rejectCodePoint() {
        String string = StringIterate.reject("1a2b3c", IS_DIGIT);
        Assert.assertEquals("abc", string);
    }

    @Test
    public void count() {
        int count = StringIterate.count("1a2a3", CharPredicates.isDigit());
        Assert.assertEquals(3, count);
    }

    @Test
    public void countCodePoint() {
        int count = StringIterate.count("1a2a3", IS_DIGIT);
        Assert.assertEquals(3, count);
    }

    @Test
    public void occurrencesOf() {
        int count = StringIterate.occurrencesOf("1a2a3", 'a');
        Assert.assertEquals(2, count);
    }

    @Test(expected = IllegalArgumentException.class)
    public void occurrencesOf_multiple_character_string_throws() {
        StringIterate.occurrencesOf("1a2a3", "abc");
    }

    @Test
    public void occurrencesOfCodePoint() {
        int count = StringIterate.occurrencesOf("1a2a3", "a".codePointAt(0));
        Assert.assertEquals(2, count);
    }

    @Test
    public void occurrencesOfString() {
        int count = StringIterate.occurrencesOf("1a2a3", "a");
        Assert.assertEquals(2, count);
    }

    @Test
    public void count2() {
        int count = StringIterate.count("1a2a3", CharPredicates.isUndefined());
        Assert.assertEquals(0, count);
    }

    @Test
    public void count2CodePoint() {
        int count = StringIterate.count("1a2a3", IS_UNDEFINED);
        Assert.assertEquals(0, count);
    }

    @Test
    public void forEach() {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", ((CharProcedure) (builder::append)));
        Assert.assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePoint() {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", ((CodePointProcedure) (builder::appendCodePoint)));
        Assert.assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePointUnicode() {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", ((CodePointProcedure) (builder::appendCodePoint)));
        Assert.assertEquals("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", builder.toString());
    }

    @Test
    public void reverseForEach() {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", ((CharProcedure) (builder::append)));
        Assert.assertEquals("c3b2a1", builder.toString());
        StringIterate.reverseForEach("", (char character) -> Assert.fail());
    }

    @Test
    public void reverseForEachCodePoint() {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", ((CodePointProcedure) (builder::appendCodePoint)));
        Assert.assertEquals("c3b2a1", builder.toString());
        StringIterate.reverseForEach("", (int codePoint) -> Assert.fail());
    }

    @Test
    public void reverseForEachCodePointUnicode() {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\ud840\udc00\u3044\ud840\udc03\u3046\ud83d\ude09", ((CodePointProcedure) (builder::appendCodePoint)));
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringIterate.reverseForEach("", (int codePoint) -> Assert.fail());
    }

    @Test
    public void reverseForEachCodePointInvalidUnicode() {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\udc00\ud840\u3044\udc03\ud840\u3046\ude09\ud83d", ((CodePointProcedure) (builder::appendCodePoint)));
        Assert.assertEquals("\ud83d\ude09\u3046\ud840\udc03\u3044\ud840\udc00\u3042", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\ud840\u3044\ud840\u3046\ud840", ((CodePointProcedure) (builder2::appendCodePoint)));
        Assert.assertEquals("\ud840\u3046\ud840\u3044\ud840\u3042", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\udc00\u3044\udc03\u3046\udc06", ((CodePointProcedure) (builder3::appendCodePoint)));
        Assert.assertEquals("\udc06\u3046\udc03\u3044\udc00\u3042", builder3.toString());
        StringIterate.reverseForEach("", (int codePoint) -> Assert.fail());
    }

    @Test
    public void forEachToken() {
        String tokens = "1,2";
        MutableList<Integer> list = mutable.of();
        StringIterate.forEachToken(tokens, ",", Procedures.throwing(( string) -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void forEachTrimmedToken() {
        String tokens = " 1,2 ";
        MutableList<Integer> list = mutable.of();
        StringIterate.forEachTrimmedToken(tokens, ",", Procedures.throwing(( string) -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void csvTrimmedTokenToList() {
        String tokens = " 1,2 ";
        Assert.assertEquals(FastList.newListWith("1", "2"), StringIterate.csvTrimmedTokensToList(tokens));
    }

    @Test
    public void injectIntoTokens() {
        Assert.assertEquals("123", StringIterate.injectIntoTokens("1,2,3", ",", null, STRING));
    }

    @Test
    public void getLastToken() {
        Assert.assertEquals("charlie", StringIterate.getLastToken("alpha~|~beta~|~charlie", "~|~"));
        Assert.assertEquals("123", StringIterate.getLastToken("123", "~|~"));
        Assert.assertEquals("", StringIterate.getLastToken("", "~|~"));
        Assert.assertNull(StringIterate.getLastToken(null, "~|~"));
        Assert.assertEquals("", StringIterate.getLastToken("123~|~", "~|~"));
        Assert.assertEquals("123", StringIterate.getLastToken("~|~123", "~|~"));
    }

    @Test
    public void getFirstToken() {
        Assert.assertEquals("alpha", StringIterate.getFirstToken("alpha~|~beta~|~charlie", "~|~"));
        Assert.assertEquals("123", StringIterate.getFirstToken("123", "~|~"));
        Assert.assertEquals("", StringIterate.getFirstToken("", "~|~"));
        Assert.assertNull(StringIterate.getFirstToken(null, "~|~"));
        Assert.assertEquals("123", StringIterate.getFirstToken("123~|~", "~|~"));
        Assert.assertEquals("", StringIterate.getFirstToken("~|~123,", "~|~"));
    }

    @Test
    public void isEmptyOrWhitespace() {
        Assert.assertTrue(StringIterate.isEmptyOrWhitespace("   "));
        Assert.assertFalse(StringIterate.isEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void notEmptyOrWhitespace() {
        Assert.assertFalse(StringIterate.notEmptyOrWhitespace("   "));
        Assert.assertTrue(StringIterate.notEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(StringIterate.isEmpty(""));
        Assert.assertFalse(StringIterate.isEmpty("   "));
        Assert.assertFalse(StringIterate.isEmpty("1"));
    }

    @Test
    public void notEmpty() {
        Assert.assertFalse(StringIterate.notEmpty(""));
        Assert.assertTrue(StringIterate.notEmpty("   "));
        Assert.assertTrue(StringIterate.notEmpty("1"));
    }

    @Test
    public void repeat() {
        Assert.assertEquals("", StringIterate.repeat("", 42));
        Assert.assertEquals("    ", StringIterate.repeat(' ', 4));
        Assert.assertEquals("        ", StringIterate.repeat(" ", 8));
        Assert.assertEquals("CubedCubedCubed", StringIterate.repeat("Cubed", 3));
    }

    @Test
    public void padOrTrim() {
        Assert.assertEquals("abcdefghijkl", StringIterate.padOrTrim("abcdefghijkl", 12));
        Assert.assertEquals("this n", StringIterate.padOrTrim("this needs to be trimmed", 6));
        Assert.assertEquals("pad this      ", StringIterate.padOrTrim("pad this", 14));
    }

    @Test
    public void string() {
        Assert.assertEquals("Token2", StringIterate.getLastToken("Token1DelimiterToken2", "Delimiter"));
    }

    @Test
    public void toList() {
        Assert.assertEquals(FastList.newListWith('a', 'a', 'b', 'c', 'd', 'e'), StringIterate.toList("aabcde"));
    }

    @Test
    public void toLowercaseList() {
        MutableList<Character> set = StringIterate.toLowercaseList("America");
        Assert.assertEquals(FastList.newListWith('a', 'm', 'e', 'r', 'i', 'c', 'a'), set);
    }

    @Test
    public void toUppercaseList() {
        MutableList<Character> set = StringIterate.toUppercaseList("America");
        Assert.assertEquals(FastList.newListWith('A', 'M', 'E', 'R', 'I', 'C', 'A'), set);
    }

    @Test
    public void toSet() {
        Verify.assertSetsEqual(UnifiedSet.newSetWith('a', 'b', 'c', 'd', 'e'), StringIterate.toSet("aabcde"));
    }

    @Test
    public void chunk() {
        Assert.assertEquals(Lists.immutable.with("ab", "cd", "ef"), StringIterate.chunk("abcdef", 2));
        Assert.assertEquals(Lists.immutable.with("abc", "def"), StringIterate.chunk("abcdef", 3));
        Assert.assertEquals(Lists.immutable.with("abc", "def", "g"), StringIterate.chunk("abcdefg", 3));
        Assert.assertEquals(Lists.immutable.with("abcdef"), StringIterate.chunk("abcdef", 6));
        Assert.assertEquals(Lists.immutable.with("abcdef"), StringIterate.chunk("abcdef", 7));
        Assert.assertEquals(Lists.immutable.with(), StringIterate.chunk("", 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunkWithZeroSize() {
        StringIterate.chunk("abcdef", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunkWithNegativeSize() {
        StringIterate.chunk("abcdef", (-42));
    }

    @Test
    public void toLowercaseSet() {
        MutableSet<Character> set = StringIterate.toLowercaseSet("America");
        Assert.assertEquals(UnifiedSet.newSetWith('a', 'm', 'e', 'r', 'i', 'c'), set);
        Assert.assertEquals(StringIterate.asLowercaseSet("America"), set);
    }

    @Test
    public void toUppercaseSet() {
        MutableSet<Character> set = StringIterate.toUppercaseSet("America");
        Assert.assertEquals(UnifiedSet.newSetWith('A', 'M', 'E', 'R', 'I', 'C'), set);
        Assert.assertEquals(StringIterate.asUppercaseSet("America"), set);
    }

    @Test
    public void splitAtIndex() {
        String oompaLoompa = "oompaloompa";
        Assert.assertEquals(Tuples.twin("oompa", "loompa"), StringIterate.splitAtIndex(oompaLoompa, 5));
        Assert.assertEquals(Tuples.twin("", oompaLoompa), StringIterate.splitAtIndex(oompaLoompa, 0));
        Assert.assertEquals(Tuples.twin(oompaLoompa, ""), StringIterate.splitAtIndex(oompaLoompa, oompaLoompa.length()));
        Assert.assertEquals(Tuples.twin("", ""), StringIterate.splitAtIndex("", 0));
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, 17));
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, (-8)));
    }

    @Test
    public void toLowercaseBag() {
        MutableBag<Character> lowercaseBag = StringIterate.toLowercaseBag("America");
        Assert.assertEquals(2, lowercaseBag.occurrencesOf(Character.valueOf('a')));
        Assert.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('m')));
        Assert.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('e')));
        Assert.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('r')));
        Assert.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('i')));
        Assert.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('c')));
    }

    @Test
    public void toUppercaseBag() {
        MutableBag<Character> uppercaseBag = StringIterate.toUppercaseBag("America");
        Assert.assertEquals(2, uppercaseBag.occurrencesOf(Character.valueOf('A')));
        Assert.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('M')));
        Assert.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('E')));
        Assert.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('R')));
        Assert.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('I')));
        Assert.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('C')));
    }

    @Test
    public void toBag() {
        MutableBag<Character> bag = StringIterate.toBag("America");
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('A')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('m')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('e')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('r')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('i')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('c')));
        Assert.assertEquals(1, bag.occurrencesOf(Character.valueOf('a')));
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(StringIterate.class);
    }
}

