/**
 * * The MIT License
 *  *
 *  * Copyright 2016 Shekhar Gulati <shekhargulati84@gmail.com>.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 */
package strman;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StrmanTests {
    @Test
    public void append_shouldAppendStringsToEndOfValue() throws Exception {
        Assert.assertThat(append("f", "o", "o", "b", "a", "r"), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(append("foobar"), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(append("", "foobar"), CoreMatchers.equalTo("foobar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void append_shouldThrowIllegalArgumentExceptionWhenValueIsNull() throws Exception {
        append(null);
    }

    @Test
    public void appendArray_shouldAppendStringArrayToEndOfValue() throws Exception {
        Assert.assertThat(appendArray("f", new String[]{ "o", "o", "b", "a", "r" }), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(appendArray("foobar", new String[]{  }), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(appendArray("", new String[]{ "foobar" }), CoreMatchers.equalTo("foobar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendArray_ShouldThrowIllegalArgumentExceptionWhenValueIsNull() throws Exception {
        appendArray(null, new String[]{  });
    }

    @Test
    public void at_shouldFindCharacterAtIndex() throws Exception {
        Assert.assertThat(at("foobar", 0), CoreMatchers.equalTo(Optional.of("f")));
        Assert.assertThat(at("foobar", 1), CoreMatchers.equalTo(Optional.of("o")));
        Assert.assertThat(at("foobar", (-1)), CoreMatchers.equalTo(Optional.of("r")));
        Assert.assertThat(at("foobar", (-2)), CoreMatchers.equalTo(Optional.of("a")));
        Assert.assertThat(at("foobar", 10), CoreMatchers.equalTo(Optional.empty()));
        Assert.assertThat(at("foobar", (-10)), CoreMatchers.equalTo(Optional.empty()));
    }

    @Test
    public void between_shouldReturnArrayWithStringsBetweenStartAndEnd() throws Exception {
        Assert.assertThat(between("[abc][def]", "[", "]"), arrayContaining("abc", "def"));
        Assert.assertThat(between("<span>foo</span>", "<span>", "</span>"), arrayContaining("foo"));
        Assert.assertThat(between("<span>foo</span><span>bar</span>", "<span>", "</span>"), arrayContaining("foo", "bar"));
    }

    @Test
    public void between_shouldReturnEmptyArrayWhenStartAndEndDoesNotExist() throws Exception {
        Assert.assertThat(between("[abc][def]", "{", "}").length, CoreMatchers.equalTo(0));
        Assert.assertThat(between("", "{", "}").length, CoreMatchers.equalTo(0));
    }

    @Test
    public void chars_shouldReturnAllCharactersInString() throws Exception {
        final String title = "title";
        Assert.assertThat(chars(title), CoreMatchers.equalTo(new String[]{ "t", "i", "t", "l", "e" }));
    }

    @Test
    public void collapseWhitespace_shouldReplaceConsecutiveWhitespaceWithSingleSpace() throws Exception {
        String[] fixture = new String[]{ "foo    bar", "     foo     bar    ", " foo     bar   ", "    foo     bar " };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(collapseWhitespace(el), CoreMatchers.equalTo("foo bar")));
    }

    @Test
    public void collapseWhitespace_shouldReplaceConsecutiveWhitespaceBetweenMultipleStrings() throws Exception {
        String input = " foo      bar      bazz     hello    world    ";
        Assert.assertThat(collapseWhitespace(input), CoreMatchers.equalTo("foo bar bazz hello world"));
    }

    @Test
    public void containsWithCaseSensitiveFalse_shouldReturnTrueWhenStringContainsNeedle() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar", "foo" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(contains(el, "FOO")));
    }

    @Test
    public void containsWithCaseSensitiveTrue_shouldReturnTrueWhenStringContainsNeedle() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar", "foo" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertFalse(contains(el, "FOO", true)));
    }

    @Test
    public void containsAll_shouldReturnTrueOnlyWhenAllNeedlesAreContainedInValue() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(containsAll(el, new String[]{ "foo", "bar" })));
    }

    @Test
    public void containsAll_shouldReturnFalseOnlyWhenAllNeedlesAreNotContainedInValue() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertFalse(containsAll(el, new String[]{ "FOO", "bar" }, true)));
    }

    @Test
    public void containsAny_shouldReturnTrueWhenAnyOfSearchNeedleExistInInputValue() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(containsAny(el, new String[]{ "foo", "bar", "test" })));
    }

    @Test
    public void containsAny_shouldReturnFalseWhenNoneOfSearchNeedleExistInInputValue() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertFalse(containsAny(el, new String[]{ "FOO", "BAR", "Test" }, true)));
    }

    @Test
    public void countSubstr_shouldCountSubStrCountCaseInsensitiveWithoutOverlapInValue() throws Exception {
        Assert.assertThat(countSubstr("aaaAAAaaa", "aaa", false, false), CoreMatchers.equalTo(3L));
    }

    @Test
    public void countSubstr_shouldCountSubStrCountCaseSensitiveWithoutOverlapInValue() throws Exception {
        Assert.assertThat(countSubstr("aaaAAAaaa", "aaa"), CoreMatchers.equalTo(2L));
    }

    @Test
    public void countSubstr_shouldCountSubStrCountCaseInsensitiveWithOverlapInValue() throws Exception {
        Assert.assertThat(countSubstr("aaaAAAaaa", "aaa", false, true), CoreMatchers.equalTo(7L));
    }

    @Test
    public void countSubstr_shouldCountSubStrCountCaseSensitiveWithOverlapInValue() throws Exception {
        Assert.assertThat(countSubstr("aaaAAAaaa", "AAA", true, true), CoreMatchers.equalTo(1L));
    }

    @Test
    public void countSubstrTestFixture_caseSensitiveTrueAndOverlappingFalse() throws Exception {
        String[] fixture = new String[]{ "aaaaaAaaAA", "faaaAAaaaaAA", "aaAAaaaaafA", "AAaaafaaaaAAAA" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(countSubstr(el, "a", true, false), CoreMatchers.equalTo(7L)));
    }

    @Test
    public void countSubstrTestFixture_caseSensitiveFalseAndOverlappingFalse() throws Exception {
        String[] fixture = new String[]{ "aaaaaaa", "faaaaaaa", "aaaaaaaf", "aaafaaaa" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(countSubstr(el, "A", false, false), CoreMatchers.equalTo(7L)));
    }

    @Test
    public void countSubstrTestFixture_caseSensitiveTrueAndOverlappingTrue() throws Exception {
        Assert.assertThat(countSubstr("aaa", "aa", true, true), CoreMatchers.equalTo(2L));
    }

    @Test
    public void endsWith_caseSensitive_ShouldBeTrueWhenStringEndsWithSearchStr() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(CoreMatchers.endsWith(el, "bar")));
    }

    @Test
    public void endsWith_notCaseSensitive_ShouldBeTrueWhenStringEndsWithSearchStr() throws Exception {
        String[] fixture = new String[]{ "foo bar", "bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(CoreMatchers.endsWith(el, "BAR", false)));
    }

    @Test
    public void endsWith_caseSensitiveAtPosition_ShouldBeTrueWhenStringEndsWithSearchStr() throws Exception {
        String[] fixture = new String[]{ "foo barr", "barr" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(CoreMatchers.endsWith(el, "bar", ((el.length()) - 1), true)));
    }

    @Test
    public void endsWith_notCaseSensitiveAtPosition_ShouldBeTrueWhenStringEndsWithSearchStr() throws Exception {
        String[] fixture = new String[]{ "foo barr", "barr" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertTrue(CoreMatchers.endsWith(el, "BAR", ((el.length()) - 1), false)));
    }

    @Test
    public void ensureLeft_shouldEnsureValueStartsWithFoo() throws Exception {
        String[] fixture = new String[]{ "foobar", "bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(ensureLeft(el, "foo"), CoreMatchers.equalTo("foobar")));
    }

    @Test
    public void ensureLeft_notCaseSensitive_shouldEnsureValueStartsWithFoo() throws Exception {
        Assert.assertThat(ensureLeft("foobar", "FOO", false), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(ensureLeft("bar", "FOO", false), CoreMatchers.equalTo("FOObar"));
    }

    @Test
    public void base64Decode_shouldDecodeABase64DecodedValueToString() throws Exception {
        Assert.assertThat(base64Decode("c3RybWFu"), CoreMatchers.equalTo("strman"));
        Assert.assertThat(base64Decode("Zm9v"), CoreMatchers.equalTo("foo"));
        Assert.assertThat(base64Decode("YmFy"), CoreMatchers.equalTo("bar"));
        Assert.assertThat(base64Decode("YsOhciE="), CoreMatchers.equalTo("b?r!"));
        Assert.assertThat(base64Decode("5ryi"), CoreMatchers.equalTo("?"));
    }

    @Test
    public void base64Encode_shouldEncodeAString() throws Exception {
        Assert.assertThat(base64Encode("strman"), CoreMatchers.equalTo("c3RybWFu"));
        Assert.assertThat(base64Encode("foo"), CoreMatchers.equalTo("Zm9v"));
        Assert.assertThat(base64Encode("bar"), CoreMatchers.equalTo("YmFy"));
        Assert.assertThat(base64Encode("b?r!"), CoreMatchers.equalTo("YsOhciE="));
        Assert.assertThat(base64Encode("?"), CoreMatchers.equalTo("5ryi"));
    }

    @Test
    public void binDecode_shouldDecodeABinaryStringToAValue() throws Exception {
        Assert.assertThat(binDecode("000000000111001100000000011101000000000001110010000000000110110100000000011000010000000001101110"), CoreMatchers.equalTo("strman"));
        Assert.assertThat(binDecode("0110111100100010"), CoreMatchers.equalTo("?"));
        Assert.assertThat(binDecode("0000000001000001"), CoreMatchers.equalTo("A"));
        Assert.assertThat(binDecode("0000000011000001"), CoreMatchers.equalTo("?"));
        Assert.assertThat(binDecode("00000000010000010000000001000001"), CoreMatchers.equalTo("AA"));
    }

    @Test
    public void binEncode_shouldEncodeAStringToBinaryFormat() throws Exception {
        Assert.assertThat(binEncode("?"), CoreMatchers.equalTo("0110111100100010"));
        Assert.assertThat(binEncode("A"), CoreMatchers.equalTo("0000000001000001"));
        Assert.assertThat(binEncode("?"), CoreMatchers.equalTo("0000000011000001"));
        Assert.assertThat(binEncode("AA"), CoreMatchers.equalTo("00000000010000010000000001000001"));
    }

    @Test
    public void decDecode_shouldDecodeDecimalStringToString() throws Exception {
        Assert.assertThat(decDecode("28450"), CoreMatchers.equalTo("?"));
        Assert.assertThat(decDecode("00065"), CoreMatchers.equalTo("A"));
        Assert.assertThat(decDecode("00193"), CoreMatchers.equalTo("?"));
        Assert.assertThat(decDecode("0006500065"), CoreMatchers.equalTo("AA"));
    }

    @Test
    public void decEncode_shouldEncodeStringToDecimal() throws Exception {
        Assert.assertThat(decEncode("?"), CoreMatchers.equalTo("28450"));
        Assert.assertThat(decEncode("A"), CoreMatchers.equalTo("00065"));
        Assert.assertThat(decEncode("?"), CoreMatchers.equalTo("00193"));
        Assert.assertThat(decEncode("AA"), CoreMatchers.equalTo("0006500065"));
    }

    @Test
    public void ensureRight_shouldEnsureStringEndsWithBar() throws Exception {
        final String[] fixture = new String[]{ "foo", "foobar", "fooBAR" };
        Assert.assertThat(Arrays.stream(fixture).map(( el) -> ensureRight(el, "bar", false)).collect(Collectors.toList()), CoreMatchers.hasItems("foobar", "foobar", "fooBAR"));
        Assert.assertThat(Arrays.stream(fixture).map(( el) -> ensureRight(el, "bar")).collect(Collectors.toList()), CoreMatchers.hasItems("foobar", "foobar", "fooBARbar"));
    }

    @Test
    public void first_shouldReturnFirstThreeCharsOfString() throws Exception {
        final String[] fixture = new String[]{ "foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(first(el, 3), CoreMatchers.equalTo(Optional.of("foo"))));
    }

    @Test
    public void head_shouldReturnFirstCharOfString() throws Exception {
        final String[] fixture = new String[]{ "foo", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(head(el), CoreMatchers.equalTo(Optional.of("f"))));
    }

    @Test
    public void format_shouldFormatStringsToFooBar() throws Exception {
        Assert.assertThat(Assert.format("{0} bar", "foo"), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(Assert.format("foo {0}", "bar"), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(Assert.format("foo {0}", "bar", "foo"), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(Assert.format("{0} {1}", "foo", "bar"), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(Assert.format("{1} {0}", "bar", "foo"), CoreMatchers.equalTo("foo bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_shouldThrowExceptionWhenValueDoesNotExist() throws Exception {
        Assert.assertThat(Assert.format("{1} {0}"), CoreMatchers.equalTo("{1} {0}"));
    }

    @Test
    public void hexDecode_shouldDecodeHexCodeToString() throws Exception {
        Assert.assertThat(hexDecode("6f22"), CoreMatchers.equalTo("?"));
        Assert.assertThat(hexDecode("0041"), CoreMatchers.equalTo("A"));
        Assert.assertThat(hexDecode("00c1"), CoreMatchers.equalTo("?"));
        Assert.assertThat(hexDecode("00410041"), CoreMatchers.equalTo("AA"));
    }

    @Test
    public void hexEncode_shouldEncodeStringToHexadecimalFormat() throws Exception {
        Assert.assertThat(hexEncode("?"), CoreMatchers.equalTo("6f22"));
        Assert.assertThat(hexEncode("A"), CoreMatchers.equalTo("0041"));
        Assert.assertThat(hexEncode("?"), CoreMatchers.equalTo("00c1"));
        Assert.assertThat(hexEncode("AA"), CoreMatchers.equalTo("00410041"));
    }

    @Test
    public void indexOf_shouldBeTrueWhenNeedleExists() throws Exception {
        final String value = "foobar";
        Assert.assertThat(indexOf(value, "f", 0, true), CoreMatchers.equalTo(0));
        Assert.assertThat(indexOf(value, "o", 0, true), CoreMatchers.equalTo(1));
        Assert.assertThat(indexOf(value, "b", 0, true), CoreMatchers.equalTo(3));
        Assert.assertThat(indexOf(value, "a", 0, true), CoreMatchers.equalTo(4));
        Assert.assertThat(indexOf(value, "r", 0, true), CoreMatchers.equalTo(5));
        Assert.assertThat(indexOf(value, "t", 0, true), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void indexOf_shouldBeTrueWhenNeedleExistCaseSensitive() throws Exception {
        final String value = "foobar";
        Assert.assertThat(indexOf(value, "F", 0, false), CoreMatchers.equalTo(0));
        Assert.assertThat(indexOf(value, "O", 0, false), CoreMatchers.equalTo(1));
        Assert.assertThat(indexOf(value, "B", 0, false), CoreMatchers.equalTo(3));
        Assert.assertThat(indexOf(value, "A", 0, false), CoreMatchers.equalTo(4));
        Assert.assertThat(indexOf(value, "R", 0, false), CoreMatchers.equalTo(5));
        Assert.assertThat(indexOf(value, "T", 0, false), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void inequal_shouldTestInequalityOfStrings() throws Exception {
        Assert.assertThat(unequal("a", "b"), CoreMatchers.equalTo(true));
        Assert.assertThat(unequal("a", "a"), CoreMatchers.equalTo(false));
        Assert.assertThat(unequal("0", "1"), CoreMatchers.equalTo(true));
    }

    @Test
    public void insert_shouldInsertStringAtIndex() throws Exception {
        Assert.assertThat(insert("fbar", "oo", 1), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(insert("foo", "bar", 3), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(insert("foobar", "x", 5), CoreMatchers.equalTo("foobaxr"));
        Assert.assertThat(insert("foobar", "x", 6), CoreMatchers.equalTo("foobarx"));
        Assert.assertThat(insert("foo bar", "asadasd", 100), CoreMatchers.equalTo("foo bar"));
    }

    @Test
    public void isLowerCase_shouldBeTrueWhenStringIsLowerCase() throws Exception {
        Assert.assertThat(isLowerCase(""), CoreMatchers.equalTo(true));
        Assert.assertThat(isLowerCase("foo"), CoreMatchers.equalTo(true));
        Assert.assertThat(isLowerCase("foobarfoo"), CoreMatchers.equalTo(true));
    }

    @Test
    public void isLowerCase_shouldBeFalseWhenStringIsNotLowerCase() throws Exception {
        Assert.assertThat(isLowerCase("Foo"), CoreMatchers.equalTo(false));
        Assert.assertThat(isLowerCase("foobarfooA"), CoreMatchers.equalTo(false));
    }

    @Test
    public void isUpperCase_shouldBeTrueWhenStringIsUpperCase() throws Exception {
        Assert.assertThat(isUpperCase(""), CoreMatchers.equalTo(true));
        Assert.assertThat(isUpperCase("FOO"), CoreMatchers.equalTo(true));
        Assert.assertThat(isUpperCase("FOOBARFOO"), CoreMatchers.equalTo(true));
    }

    @Test
    public void isUpperCase_shouldBeFalseWhenStringIsNotUpperCase() throws Exception {
        Assert.assertThat(isUpperCase("Foo"), CoreMatchers.equalTo(false));
        Assert.assertThat(isUpperCase("foobarfooA"), CoreMatchers.equalTo(false));
    }

    @Test
    public void last_shouldReturnLastNChars() throws Exception {
        Assert.assertThat(last("foo", 3), CoreMatchers.equalTo("foo"));
        Assert.assertThat(last("foobarfoo", 3), CoreMatchers.equalTo("foo"));
        Assert.assertThat(last("", 3), CoreMatchers.equalTo(""));
        Assert.assertThat(last("f", 3), CoreMatchers.equalTo("f"));
    }

    @Test
    public void leftPad_shouldAddPaddingOnTheLeft() throws Exception {
        Assert.assertThat(leftPad("1", "0", 5), CoreMatchers.equalTo("00001"));
        Assert.assertThat(leftPad("01", "0", 5), CoreMatchers.equalTo("00001"));
        Assert.assertThat(leftPad("001", "0", 5), CoreMatchers.equalTo("00001"));
        Assert.assertThat(leftPad("0001", "0", 5), CoreMatchers.equalTo("00001"));
        Assert.assertThat(leftPad("00001", "0", 5), CoreMatchers.equalTo("00001"));
    }

    @Test
    public void isString_shouldBeFalseWhenValueIsNotString() throws Exception {
        Assert.assertFalse(isString(1));
        Assert.assertFalse(isString(false));
        Assert.assertFalse(isString(1.2));
        Assert.assertFalse(isString(new String[]{  }));
    }

    @Test
    public void isString_shouldBeTrueWhenValueIsString() throws Exception {
        Assert.assertTrue(isString("string"));
        Assert.assertTrue(isString(""));
    }

    @Test
    public void lastIndexOf_shouldFindIndexOfNeedle() throws Exception {
        final String value = "foobarfoobar";
        Assert.assertThat(lastIndexOf(value, "f"), CoreMatchers.equalTo(6));
        Assert.assertThat(lastIndexOf(value, "o"), CoreMatchers.equalTo(8));
        Assert.assertThat(lastIndexOf(value, "b"), CoreMatchers.equalTo(9));
        Assert.assertThat(lastIndexOf(value, "a"), CoreMatchers.equalTo(10));
        Assert.assertThat(lastIndexOf(value, "r"), CoreMatchers.equalTo(11));
        Assert.assertThat(lastIndexOf(value, "t"), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void lastIndexOf_shouldFindIndexOfNeedleCaseInsensitive() throws Exception {
        final String value = "foobarfoobar";
        Assert.assertThat(lastIndexOf(value, "F", false), CoreMatchers.equalTo(6));
        Assert.assertThat(lastIndexOf(value, "O", false), CoreMatchers.equalTo(8));
        Assert.assertThat(lastIndexOf(value, "B", false), CoreMatchers.equalTo(9));
        Assert.assertThat(lastIndexOf(value, "A", false), CoreMatchers.equalTo(10));
        Assert.assertThat(lastIndexOf(value, "R", false), CoreMatchers.equalTo(11));
        Assert.assertThat(lastIndexOf(value, "T", false), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void leftTrim_shouldRemoveSpacesOnLeft() throws Exception {
        Assert.assertThat(leftTrim("     strman"), CoreMatchers.equalTo("strman"));
        Assert.assertThat(leftTrim("     strman  "), CoreMatchers.equalTo("strman  "));
    }

    @Test
    public void prepend_shouldPrependStrings() throws Exception {
        Assert.assertThat(prepend("r", "f", "o", "o", "b", "a"), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prepend("foobar"), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prepend("", "foobar"), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prepend("bar", "foo"), CoreMatchers.equalTo("foobar"));
    }

    @Test
    public void prependArray_shouldPrependStrings() throws Exception {
        Assert.assertThat(prependArray("r", new String[]{ "f", "o", "o", "b", "a" }), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prependArray("foobar", new String[0]), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prependArray("", new String[]{ "foobar" }), CoreMatchers.equalTo("foobar"));
        Assert.assertThat(prependArray("bar", new String[]{ "foo" }), CoreMatchers.equalTo("foobar"));
    }

    @Test
    public void removeEmptyStrings_shouldRemoveEmptyStrings() throws Exception {
        Assert.assertThat(removeEmptyStrings(new String[]{ "aa", "", "   ", "bb", "cc", null }), arrayContaining("aa", "bb", "cc"));
        Assert.assertThat(removeEmptyStrings(new String[0]), emptyArray());
    }

    @Test
    public void removeLeft_shouldRemoveStringFromLeft() throws Exception {
        final String[] fixture = new String[]{ "foobar", "bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeLeft(el, "foo"), CoreMatchers.equalTo("bar")));
        Assert.assertThat(removeLeft("barfoo", "foo"), CoreMatchers.equalTo("barfoo"));
        Assert.assertThat(removeLeft("foofoo", "foo"), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void removeLeft_shouldRemoveStringFromLeftCaseInSensitive() throws Exception {
        final String[] fixture = new String[]{ "foobar", "bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeLeft(el, "FOO", false), CoreMatchers.equalTo("bar")));
    }

    @Test
    public void removeNonWords_shouldRemoveAllNonWordsFromInputString() throws Exception {
        final String[] fixture = new String[]{ "foo bar", "foo&bar-", "foobar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeNonWords(el), CoreMatchers.equalTo("foobar")));
    }

    @Test
    public void removeRight_shouldRemoveStringFromRight() throws Exception {
        final String[] fixture = new String[]{ "foobar", "foo" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeRight(el, "bar"), CoreMatchers.equalTo("foo")));
        Assert.assertThat(removeRight("barfoo", "bar"), CoreMatchers.equalTo("barfoo"));
        Assert.assertThat(removeRight("barbar", "bar"), CoreMatchers.equalTo("bar"));
    }

    @Test
    public void removeRight_shouldRemoveStringFromRightCaseInSensitive() throws Exception {
        final String[] fixture = new String[]{ "foobar", "foo" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeRight(el, "BAR", false), CoreMatchers.equalTo("foo")));
    }

    @Test
    public void removeSpaces_shouldRemoveSpacesInTheString() throws Exception {
        final String[] fixture = new String[]{ "foo bar", "foo bar ", " foo bar", " foo bar " };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(removeSpaces(el), CoreMatchers.equalTo("foobar")));
    }

    @Test
    public void repeat_shouldRepeatAStringNTimes() throws Exception {
        Assert.assertThat(repeat("1", 1), CoreMatchers.equalTo("1"));
        Assert.assertThat(repeat("1", 2), CoreMatchers.equalTo("11"));
        Assert.assertThat(repeat("1", 3), CoreMatchers.equalTo("111"));
        Assert.assertThat(repeat("1", 4), CoreMatchers.equalTo("1111"));
        Assert.assertThat(repeat("1", 5), CoreMatchers.equalTo("11111"));
    }

    @Test
    public void replace_shouldReplaceAllOccurrencesOfString() throws Exception {
        Assert.assertThat(replace("foo bar", "foo", "bar", true), CoreMatchers.equalTo("bar bar"));
        Assert.assertThat(replace("foo bar foo", "foo", "bar", true), CoreMatchers.equalTo("bar bar bar"));
    }

    @Test
    public void replace_shouldReplaceAllOccurrencesOfStringCaseSensitive() throws Exception {
        Assert.assertThat(replace("FOO bar", "foo", "bar", false), CoreMatchers.equalTo("bar bar"));
        Assert.assertThat(replace("FOO bar foo", "foo", "bar", false), CoreMatchers.equalTo("bar bar bar"));
    }

    @Test
    public void reverse_shouldReverseInputString() throws Exception {
        Assert.assertThat(reverse(""), CoreMatchers.equalTo(""));
        Assert.assertThat(reverse("foo"), CoreMatchers.equalTo("oof"));
        Assert.assertThat(reverse("shekhar"), CoreMatchers.equalTo("rahkehs"));
        Assert.assertThat(reverse("bar"), CoreMatchers.equalTo("rab"));
        Assert.assertThat(reverse("foo_"), CoreMatchers.equalTo("_oof"));
        Assert.assertThat(reverse("f"), CoreMatchers.equalTo("f"));
    }

    @Test
    public void rightPad_shouldRightPadAString() throws Exception {
        Assert.assertThat(rightPad("1", "0", 5), CoreMatchers.equalTo("10000"));
        Assert.assertThat(rightPad("10", "0", 5), CoreMatchers.equalTo("10000"));
        Assert.assertThat(rightPad("100", "0", 5), CoreMatchers.equalTo("10000"));
        Assert.assertThat(rightPad("1000", "0", 5), CoreMatchers.equalTo("10000"));
        Assert.assertThat(rightPad("10000", "0", 5), CoreMatchers.equalTo("10000"));
        Assert.assertThat(rightPad("10000000", "0", 5), CoreMatchers.equalTo("10000000"));
    }

    @Test
    public void rightTrim_shouldRemoveSpacesFromTheRight() throws Exception {
        Assert.assertThat(rightTrim("strman   "), CoreMatchers.equalTo("strman"));
        Assert.assertThat(rightTrim("   strman"), CoreMatchers.equalTo("   strman"));
        Assert.assertThat(rightTrim("strman"), CoreMatchers.equalTo("strman"));
    }

    @Test
    public void safeTruncate_shouldSafelyTruncateStrings() throws Exception {
        Assert.assertThat(safeTruncate("foo bar", 0, "."), CoreMatchers.equalTo(""));
        Assert.assertThat(safeTruncate("foo bar", 4, "."), CoreMatchers.equalTo("foo."));
        Assert.assertThat(safeTruncate("foo bar", 3, "."), CoreMatchers.equalTo("."));
        Assert.assertThat(safeTruncate("foo bar", 2, "."), CoreMatchers.equalTo("."));
        Assert.assertThat(safeTruncate("foo bar", 7, "."), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(safeTruncate("foo bar", 8, "."), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(safeTruncate("A Javascript string manipulation library.", 16, "..."), CoreMatchers.equalTo("A Javascript..."));
        Assert.assertThat(safeTruncate("A Javascript string manipulation library.", 15, "..."), CoreMatchers.equalTo("A Javascript..."));
        Assert.assertThat(safeTruncate("A Javascript string manipulation library.", 14, "..."), CoreMatchers.equalTo("A..."));
        Assert.assertThat(safeTruncate("A Javascript string manipulation library.", 13, "..."), CoreMatchers.equalTo("A..."));
    }

    @Test
    public void truncate_shouldTruncateString() throws Exception {
        Assert.assertThat(truncate("foo bar", 0, "."), CoreMatchers.equalTo(""));
        Assert.assertThat(truncate("foo bar", 3, "."), CoreMatchers.equalTo("fo."));
        Assert.assertThat(truncate("foo bar", 2, "."), CoreMatchers.equalTo("f."));
        Assert.assertThat(truncate("foo bar", 4, "."), CoreMatchers.equalTo("foo."));
        Assert.assertThat(truncate("foo bar", 7, "."), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(truncate("foo bar", 8, "."), CoreMatchers.equalTo("foo bar"));
        Assert.assertThat(truncate("A Javascript string manipulation library.", 16, "..."), CoreMatchers.equalTo("A Javascript ..."));
        Assert.assertThat(truncate("A Javascript string manipulation library.", 15, "..."), CoreMatchers.equalTo("A Javascript..."));
        Assert.assertThat(truncate("A Javascript string manipulation library.", 14, "..."), CoreMatchers.equalTo("A Javascrip..."));
    }

    @Test
    public void htmlDecode_shouldDecodeToHtml() throws Exception {
        Assert.assertThat(htmlDecode("&aacute;"), CoreMatchers.equalTo("\u00e1"));
        Assert.assertThat(htmlDecode("&SHcy;"), CoreMatchers.equalTo("?"));
        Assert.assertThat(htmlDecode("&ZHcy;"), CoreMatchers.equalTo("?"));
        Assert.assertThat(htmlDecode("&boxdl;"), CoreMatchers.equalTo("?"));
    }

    @Test
    public void htmlEncode_shouldBeEncodedToHtmlEntities() throws Exception {
        Assert.assertThat(htmlEncode("?"), CoreMatchers.equalTo("&aacute;"));
        Assert.assertThat(htmlEncode("?????"), CoreMatchers.equalTo("&aacute;&eacute;&iacute;&oacute;&uacute;"));
        Assert.assertThat(htmlEncode("?"), CoreMatchers.equalTo("&SHcy;"));
        Assert.assertThat(htmlEncode("?"), CoreMatchers.equalTo("&ZHcy;"));
        Assert.assertThat(htmlEncode("?"), CoreMatchers.equalTo("&boxdl;"));
    }

    @Test
    public void shuffle_shouldShuffleAString() throws Exception {
        Assert.assertThat(shuffle("shekhar"), CoreMatchers.not(CoreMatchers.equalTo("shekhar")));
        Assert.assertThat(shuffle("strman"), CoreMatchers.not(CoreMatchers.equalTo("strman")));
        Assert.assertThat(shuffle(""), CoreMatchers.equalTo(""));
        Assert.assertThat(shuffle("s"), CoreMatchers.equalTo("s"));
    }

    @Test
    public void slugify_shouldBeFooBar() throws Exception {
        String[] fixture = new String[]{ "foo bar", "foo bar.", "foo bar ", " foo bar", " foo bar ", "foo------bar", "f?? b?r", "foo ! bar", "foo ~~ bar", "foo     bar", "FOO     bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("slugify(%s) should be foo-bar ", el), slugify(el), CoreMatchers.equalTo("foo-bar")));
    }

    @Test
    public void slugify_shouldBeFooAndBar() throws Exception {
        String[] fixture = new String[]{ "foo&bar", "foo&bar.", "foo&bar ", " foo&bar", " foo&bar ", "foo&bar", "f??-and---b?r", "foo  &    bar", "FOO  &   bar" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("slugify(%s) should be foo-and-bar ", el), slugify(el), CoreMatchers.equalTo("foo-and-bar")));
    }

    @Test
    public void transliterate_shouldTransliterateTheText() throws Exception {
        Assert.assertThat(transliterate("f?? b?r"), CoreMatchers.equalTo("foo bar"));
    }

    @Test
    public void surround_shouldSurroundStringWithPrefixAndSuffix() throws Exception {
        Assert.assertThat(surround("foo", "bar", null), CoreMatchers.equalTo("barfoobar"));
        Assert.assertThat(surround("shekhar", "***", null), CoreMatchers.equalTo("***shekhar***"));
        Assert.assertThat(surround("", ">", null), CoreMatchers.equalTo(">>"));
        Assert.assertThat(surround("bar", "", null), CoreMatchers.equalTo("bar"));
        Assert.assertThat(surround("f", null, null), CoreMatchers.equalTo("f"));
        Assert.assertThat(surround("div", "<", ">"), CoreMatchers.equalTo("<div>"));
    }

    @Test
    public void toCamelCase_shouldConvertStringToCamelCase() throws Exception {
        String[] fixture = new String[]{ "CamelCase", "camelCase", "Camel case", "Camel  case", "camel Case", "camel-case", "-camel--case", "camel_case", "     camel_case" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("toCameCase(%s) should be camelCase", el), toCamelCase(el), CoreMatchers.equalTo("camelCase")));
        Assert.assertThat(toCamelCase("c"), CoreMatchers.equalTo("c"));
    }

    @Test
    public void toDeCamelCase_shouldDeCamelCaseAString() throws Exception {
        String[] fixture = new String[]{ "deCamelize", "de-Camelize", "de camelize", "de  camelize", "de Camelize", "de-camelize", "-de--camelize", "de_camelize", "     de_camelize" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("toDecamelize(%s) should be de-camelize", el), toDecamelize(el, null), CoreMatchers.equalTo("de camelize")));
        Assert.assertThat(toDecamelize("camelCase", "_"), CoreMatchers.equalTo("camel_case"));
    }

    @Test
    public void toKebabCase_shouldKebabCaseAString() throws Exception {
        String[] fixture = new String[]{ "deCamelize", "de-Camelize", "de camelize", "de  camelize", "de Camelize", "de-camelize", "-de--camelize", "de_camelize", "     de_camelize" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("toKebabCase(%s) should be de-camelize", el), toKebabCase(el), CoreMatchers.equalTo("de-camelize")));
    }

    @Test
    public void toSnakeCase_shouldSnakeCaseAString() throws Exception {
        String[] fixture = new String[]{ "deCamelize", "de-Camelize", "de camelize", "de  camelize", "de Camelize", "de-camelize", "-de--camelize", "de_camelize", "     de_camelize" };
        Arrays.stream(fixture).forEach(( el) -> Assert.assertThat(String.format("toSnakeCase(%s) should be de_camelize", el), toSnakeCase(el), CoreMatchers.equalTo("de_camelize")));
    }

    @Test
    public void snakeCase_shouldConvertAStringToSnakecase() throws Exception {
        String[] input = new String[]{ "Foo Bar", "fooBar" };
        Arrays.stream(input).forEach(( el) -> Assert.assertThat(String.format("%s should be foo_bar", el), toSnakeCase(el), CoreMatchers.is(CoreMatchers.equalTo("foo_bar"))));
    }

    @Test
    public void unequal_shouldTestInequalityOfStrings() throws Exception {
        Assert.assertThat(unequal("a", "b"), CoreMatchers.equalTo(true));
        Assert.assertThat(unequal("a", "a"), CoreMatchers.equalTo(false));
        Assert.assertThat(unequal("0", "1"), CoreMatchers.equalTo(true));
    }

    @Test
    public void removeLeft_shouldNotLowercaseWhenCaseInsensitive() throws Exception {
        String result = removeLeft("This HAS A THIS IN FRONT", "THIS ", false);
        Assert.assertThat(result, CoreMatchers.is("HAS A THIS IN FRONT"));
    }

    @Test
    public void replace_shouldNotLowercaseWhenCaseInsensitive() throws Exception {
        String result = replace("One and two and THREE and Four", "and", "&", false);
        Assert.assertThat(result, CoreMatchers.is("One & two & THREE & Four"));
    }

    @Test
    public void removeRight_shouldNotLowercaseWhenCaseInsensitive() throws Exception {
        String result = removeRight("Remove the END at the end", " END", false);
        Assert.assertThat(result, CoreMatchers.is("Remove the END at the"));
    }

    @Test
    public void transliterate_shouldDeburrTheString() throws Exception {
        String result = transliterate("d?j? vu");
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("deja vu")));
    }

    @Test
    public void kebabCase_shouldConvertAStringToKebabCase() throws Exception {
        String[] input = new String[]{ "Foo Bar", "fooBar" };
        Arrays.stream(input).forEach(( el) -> Assert.assertThat(String.format("%s should be foo-bar", el), toKebabCase(el), CoreMatchers.is(CoreMatchers.equalTo("foo-bar"))));
    }

    @Test
    public void join_shouldJoinArrayOfStringIntoASingleString() throws Exception {
        String[] strings = new String[]{ "hello", "world", "123" };
        Assert.assertThat(join(strings, ";"), CoreMatchers.is(CoreMatchers.equalTo("hello;world;123")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void join_shouldThrowIllegalArgumentExceptionWhenSeparatorIsNull() throws Exception {
        String[] strings = new String[]{ "hello", "world", "123" };
        join(strings, null);
    }

    @Test
    public void join_shouldReturnEmptyStringWhenInputArrayIsEmpty() throws Exception {
        String[] emptyArray = new String[]{  };
        Assert.assertThat(join(emptyArray, ","), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void capitalize_shouldCapitalizeFirstCharacterOfString() throws Exception {
        String[] strings = new String[]{ "FRED", "fRED", "fred" };
        Arrays.stream(strings).forEach(( el) -> Assert.assertThat(String.format("%s should be Fred", el), capitalize(el), CoreMatchers.equalTo("Fred")));
    }

    @Test
    public void lowerFirst_shouldLowercasedFirstCharacterOfString() throws Exception {
        Assert.assertThat(lowerFirst("FRED"), CoreMatchers.is(CoreMatchers.equalTo("fRED")));
        Assert.assertThat(lowerFirst("fred"), CoreMatchers.is(CoreMatchers.equalTo("fred")));
        Assert.assertThat(lowerFirst("Fred"), CoreMatchers.is(CoreMatchers.equalTo("fred")));
    }

    @Test
    public void isEnclosedBetween_shouldChekcWhetherStringIsEnclosed() throws Exception {
        Assert.assertThat(isEnclosedBetween("{{shekhar}}", "{{", "}}"), CoreMatchers.is(true));
        Assert.assertThat(isEnclosedBetween("shekhar", "{{", "}}"), CoreMatchers.is(false));
        Assert.assertThat(isEnclosedBetween("*shekhar*", "*"), CoreMatchers.is(true));
        Assert.assertThat(isEnclosedBetween("shekhar", "*"), CoreMatchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isEnclosedBetween_shouldThrowIllegalArgumentExceptionWhenEncloserIsNull() throws Exception {
        Assert.assertThat(isEnclosedBetween("shekhar", null), CoreMatchers.is(false));
    }

    @Test
    public void words_shouldConvertTextToWords() throws Exception {
        final String line = "This is a string, with words!";
        Assert.assertThat(words(line), arrayContaining("This", "is", "a", "string,", "with", "words!"));
    }

    @Test
    public void upperFirst_shouldConvertFirstCharToUpperCase() throws Exception {
        Assert.assertThat(upperFirst("fred"), CoreMatchers.is("Fred"));
    }

    @Test
    public void upperFirst_shouldReturnSameStringIfFirstCharIsUpperCase() throws Exception {
        Assert.assertThat(upperFirst("FRED"), CoreMatchers.is("FRED"));
    }

    @Test
    public void trimStart_shouldRemoveAllWhitespaceAtStart() throws Exception {
        Assert.assertThat(trimStart("   abc   "), CoreMatchers.is(Optional.of("abc   ")));
        Assert.assertThat(trimStart("abc   "), CoreMatchers.is(Optional.of("abc   ")));
        Assert.assertThat(trimStart("abc"), CoreMatchers.is(Optional.of("abc")));
        Assert.assertThat(trimStart(""), CoreMatchers.is(Optional.empty()));
        Assert.assertThat(trimStart(null), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void trimStart_shouldRemoveSpecialCharactersAtStart() throws Exception {
        Assert.assertThat(trimStart("-_-abc-_-", "_", "-"), CoreMatchers.is(Optional.of("abc-_-")));
        Assert.assertThat(trimStart("-_-!abc-_-", "_", "-", "!"), CoreMatchers.is(Optional.of("abc-_-")));
        Assert.assertThat(trimStart("-_-#abc-_-", "_", "-", "!", "#"), CoreMatchers.is(Optional.of("abc-_-")));
    }

    @Test
    public void trimEnd_shouldRemoveAllTrailingWhitespace() throws Exception {
        Assert.assertThat(trimEnd("   abc   "), CoreMatchers.is(Optional.of("   abc")));
        Assert.assertThat(trimEnd("abc   "), CoreMatchers.is(Optional.of("abc")));
        Assert.assertThat(trimEnd("abc"), CoreMatchers.is(Optional.of("abc")));
        Assert.assertThat(trimEnd(""), CoreMatchers.is(Optional.empty()));
        Assert.assertThat(trimEnd(null), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void trimEnd_shouldRemoveAllTrailingSpecialCharacters() throws Exception {
        Assert.assertThat(trimEnd("-_-abc-_-", "_", "-"), CoreMatchers.is(Optional.of("-_-abc")));
        Assert.assertThat(trimEnd("-_-abc!-_-", "_", "-", "!"), CoreMatchers.is(Optional.of("-_-abc")));
        Assert.assertThat(trimEnd("-_-abc#-_-", "_", "-", "!", "#"), CoreMatchers.is(Optional.of("-_-abc")));
    }

    @Test
    public void charsCount_shouldReturnEmptyWhenInputStringIsNull() {
        Assert.assertThat(charsCount(null), CoreMatchers.equalTo(Collections.emptyMap()));
    }

    @Test
    public void charsCount_shouldReturnEmptyWhenInputStringIsEmpty() {
        Assert.assertThat(charsCount(""), CoreMatchers.equalTo(Collections.emptyMap()));
    }

    @Test
    public void charsCount_shouldReturnCharsCountWhenInputIsASimpleString() {
        Map<Character, Long> expectedOutput = new HashMap<Character, Long>() {
            {
                put('a', 1L);
                put('b', 1L);
                put('c', 1L);
            }
        };
        Assert.assertThat(charsCount("abc"), CoreMatchers.equalTo(expectedOutput));
    }

    @Test
    public void charsCount_shouldReturnCharsCountWhenInputIsAComplexString() {
        Map<Character, Long> expectedOutput = new HashMap<Character, Long>() {
            {
                put('a', 1L);
                put('b', 2L);
                put('c', 3L);
                put('A', 1L);
                put('B', 2L);
                put('C', 3L);
                put('-', 10L);
            }
        };
        Assert.assertThat(charsCount("-----abbcccCCCBBA-----"), CoreMatchers.equalTo(expectedOutput));
    }

    @Test
    public void isBlank_shouldReturnTrueIfNull() {
        Assert.assertTrue(isBlank(null));
    }

    @Test
    public void isBlank_shouldReturnTrueIfEmpty() {
        Assert.assertTrue(isBlank(""));
    }

    @Test
    public void isBlank_shouldReturnFalseIfNotEmpty() {
        Assert.assertFalse(isBlank("ac"));
    }

    @Test
    public void underscored_shouldReturnUnderscoredString() {
        Assert.assertThat(underscored("MozTransform"), CoreMatchers.equalTo("moz_transform"));
    }

    @Test
    public void underscored_shouldReturnEmptyStringIfEmptyStringPassedIn() {
        Assert.assertThat(underscored(""), CoreMatchers.equalTo(""));
    }

    @Test
    public void underscored_shouldReturnNullIfNullPassedIn() {
        Assert.assertThat(underscored(null), CoreMatchers.equalTo(""));
    }

    @Test
    public void underscored_shouldUnderscoreInputString() throws Exception {
        // assertThat(underscored("foo-bar-baz"), equalTo("foo_bar_baz"));
        Assert.assertThat(underscored("fooBarBaz"), CoreMatchers.equalTo("foo_bar_baz"));
        // assertThat(underscored("FooBarBaz"), equalTo("foo_bar_baz"));
        // assertThat(underscored(" foo   bar baz  "), equalTo("foo_bar_baz"));
    }

    @Test
    public void zip_shouldReturnEmptyList_whenNullOrEmptyIsPassedIn() {
        Assert.assertThat(zip("a", null), CoreMatchers.is(empty()));
        Assert.assertThat(zip("", "a"), CoreMatchers.is(empty()));
    }

    @Test
    public void zip_shouldReturnListOfOneElement_forSimplestValidInput() {
        Assert.assertThat(zip("a", "b"), CoreMatchers.equalTo(Arrays.asList("ab")));
    }

    @Test
    public void zip_shouldReturnExpectedListOfPairs_whenBothInputsHaveSameSize() {
        Assert.assertThat(zip("abc", "def"), CoreMatchers.equalTo(Arrays.asList("ad", "be", "cf")));
        Assert.assertThat(zip("ABC", "DEF"), CoreMatchers.equalTo(Arrays.asList("AD", "BE", "CF")));
    }

    @Test
    public void zip_shouldReturnExpectedListOfPairs_whenFirstInputIsBiggerThanSecond() {
        Assert.assertThat(zip("abc", "d"), CoreMatchers.equalTo(Arrays.asList("ad")));
        Assert.assertThat(zip("ABCDE", "FGH"), CoreMatchers.equalTo(Arrays.asList("AF", "BG", "CH")));
    }

    @Test
    public void zip_shouldReturnExpectedListOfPairs_whenSecondInputIsBiggerThanFirst() {
        Assert.assertThat(zip("d", "abc"), CoreMatchers.equalTo(Arrays.asList("da")));
        Assert.assertThat(zip("FGH", "ABCDE"), CoreMatchers.equalTo(Arrays.asList("FA", "GB", "HC")));
    }

    @Test
    public void zip_shouldReturnExpectedListOfTriplets_whenThreeInputs() {
        Assert.assertThat(zip("abc", "def", "ghi"), CoreMatchers.equalTo(Arrays.asList("adg", "beh", "cfi")));
    }

    @Test
    public void zip_shouldReturnExpectedListOfTuples_whenMoreThanThreeInputs() {
        Assert.assertThat(zip("abc", "def", "ghi", "123"), CoreMatchers.equalTo(Arrays.asList("adg1", "beh2", "cfi3")));
    }

    @Test
    public void zip_shouldReturnEmptyList_whenNotEnoughInputs() {
        Assert.assertThat(zip(), CoreMatchers.is(empty()));
    }

    @Test
    public void zip_shouldReturnInputInListForm_whenOnlyOneInput() {
        Assert.assertThat(zip("zip"), CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList("z", "i", "p"))));
        Assert.assertThat(zip("z"), CoreMatchers.is(CoreMatchers.equalTo(Collections.singletonList("z"))));
    }

    @Test
    public void lines_shouldReturnEmptyArrayWhenInputIsNull() throws Exception {
        Assert.assertThat(lines(null), emptyArray());
    }

    @Test
    public void lines_shouldReturnArrayWithOneEmptyElementWhenInputIsEmptyString() throws Exception {
        Assert.assertThat(lines(""), arrayWithSize(1));
        Assert.assertThat(lines(""), hasItemInArray(""));
    }

    @Test
    public void lines_shouldSplitToLines() throws Exception {
        Assert.assertThat(lines("Hello\r\nWorld").length, CoreMatchers.equalTo(2));
        Assert.assertThat(lines("Hello\rWorld").length, CoreMatchers.equalTo(2));
        Assert.assertThat(lines("Hello World").length, CoreMatchers.equalTo(1));
        Assert.assertThat(lines("\r\n\n\r ").length, CoreMatchers.equalTo(4));
        Assert.assertThat(lines("Hello\r\r\nWorld").length, CoreMatchers.equalTo(3));
        Assert.assertThat(lines("Hello\r\rWorld").length, CoreMatchers.equalTo(3));
    }

    @Test
    public void humanize_shouldHumanizeStrings() throws Exception {
        Assert.assertThat(humanize("the_humanize_method"), CoreMatchers.equalTo("The humanize method"));
        Assert.assertThat(humanize("ThehumanizeMethod"), CoreMatchers.equalTo("Thehumanize method"));
        Assert.assertThat(humanize("ThehumanizeMethod"), CoreMatchers.equalTo("Thehumanize method"));
        Assert.assertThat(humanize("the humanize  method"), CoreMatchers.equalTo("The humanize method"));
        Assert.assertThat(humanize("the humanize_id  method_id"), CoreMatchers.equalTo("The humanize id method id"));
        Assert.assertThat(humanize("the  humanize  method  "), CoreMatchers.equalTo("The humanize method"));
        Assert.assertThat(humanize("   capitalize dash-CamelCase_underscore trim  "), CoreMatchers.equalTo("Capitalize dash camel case underscore trim"));
        Assert.assertThat(humanize(""), CoreMatchers.equalTo(""));
        Assert.assertThat(humanize(null), CoreMatchers.equalTo(""));
    }

    @Test
    public void dasherize_shouldDasherizeInputString() throws Exception {
        Assert.assertThat(dasherize("the_dasherize_string_method"), CoreMatchers.equalTo("the-dasherize-string-method"));
        Assert.assertThat(dasherize("TheDasherizeStringMethod"), CoreMatchers.equalTo("the-dasherize-string-method"));
        Assert.assertThat(dasherize("thisIsATest"), CoreMatchers.equalTo("this-is-a-test"));
        Assert.assertThat(dasherize("this Is A Test"), CoreMatchers.equalTo("this-is-a-test"));
        Assert.assertThat(dasherize("thisIsATest123"), CoreMatchers.equalTo("this-is-a-test123"));
        Assert.assertThat(dasherize("123thisIsATest"), CoreMatchers.equalTo("123this-is-a-test"));
        Assert.assertThat(dasherize("the dasherize string method"), CoreMatchers.equalTo("the-dasherize-string-method"));
        Assert.assertThat(dasherize("the  dasherize string method  "), CoreMatchers.equalTo("the-dasherize-string-method"));
        Assert.assertThat(dasherize("input with a-dash"), CoreMatchers.equalTo("input-with-a-dash"));
        Assert.assertThat(dasherize(""), CoreMatchers.equalTo(""));
        Assert.assertThat(dasherize(null), CoreMatchers.equalTo(""));
    }

    @Test
    public void swapCase_shouldSwapCaseOfCharacters() throws Exception {
        Assert.assertThat(swapCase("AaBbCcDdEe"), CoreMatchers.equalTo("aAbBcCdDeE"));
        Assert.assertThat(swapCase("Hello World"), CoreMatchers.equalTo("hELLO wORLD"));
        Assert.assertThat(swapCase(""), CoreMatchers.equalTo(""));
        Assert.assertThat(swapCase(null), CoreMatchers.equalTo(""));
    }

    @Test
    public void chop_shouldChopStringByStep() throws Exception {
        Assert.assertThat(chop(null, 2).length, CoreMatchers.equalTo(0));
        Assert.assertThat(chop("whitespace", 2).length, CoreMatchers.equalTo(5));
        Assert.assertThat(chop("whitespace", 3).length, CoreMatchers.equalTo(4));
        Assert.assertThat(chop("whitespace", 0)[0].length(), CoreMatchers.equalTo(10));
    }

    @Test
    public void formatNumber_shouldFormatNumberWithCommaDelimiter() throws Exception {
        Assert.assertThat(formatNumber(1000), CoreMatchers.equalTo("1,000"));
        Assert.assertThat(formatNumber(100000), CoreMatchers.equalTo("100,000"));
        Assert.assertThat(formatNumber(10000000), CoreMatchers.equalTo("10,000,000"));
        Assert.assertThat(formatNumber(100000000), CoreMatchers.equalTo("100,000,000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void startCase_shouldThrowException() throws Exception {
        startCase(null);
    }

    @Test
    public void startCase_shouldStartCaseInputString() throws Exception {
        Assert.assertThat(startCase(""), CoreMatchers.equalTo(""));
        Assert.assertThat(startCase("ALL CAPS"), CoreMatchers.equalTo("All Caps"));
        Assert.assertThat(startCase("camelCase"), CoreMatchers.equalTo("Camel Case"));
        Assert.assertThat(startCase("kebab-case"), CoreMatchers.equalTo("Kebab Case"));
        Assert.assertThat(startCase("Snake_case"), CoreMatchers.equalTo("Snake Case"));
        Assert.assertThat(startCase("  spaces  "), CoreMatchers.equalTo("Spaces"));
        Assert.assertThat(startCase("spaces    between    words"), CoreMatchers.equalTo("Spaces Between Words"));
        Assert.assertThat(startCase("--dashes--"), CoreMatchers.equalTo("Dashes"));
        Assert.assertThat(startCase("dashes----between----words"), CoreMatchers.equalTo("Dashes Between Words"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void escapeRegExp_shouldThrowException() throws Exception {
        escapeRegExp(null);
    }

    @Test
    public void escapeRegExp_shouldEscapeRegExp() throws Exception {
        Assert.assertThat(escapeRegExp("\\"), CoreMatchers.equalTo("\\\\"));
        Assert.assertThat(escapeRegExp("^"), CoreMatchers.equalTo("\\^"));
        Assert.assertThat(escapeRegExp("$"), CoreMatchers.equalTo("\\$"));
        Assert.assertThat(escapeRegExp("*"), CoreMatchers.equalTo("\\*"));
        Assert.assertThat(escapeRegExp("+"), CoreMatchers.equalTo("\\+"));
        Assert.assertThat(escapeRegExp("-"), CoreMatchers.equalTo("\\-"));
        Assert.assertThat(escapeRegExp("?"), CoreMatchers.equalTo("\\?"));
        Assert.assertThat(escapeRegExp("."), CoreMatchers.equalTo("\\."));
        Assert.assertThat(escapeRegExp("|"), CoreMatchers.equalTo("\\|"));
        Assert.assertThat(escapeRegExp("("), CoreMatchers.equalTo("\\("));
        Assert.assertThat(escapeRegExp(")"), CoreMatchers.equalTo("\\)"));
        Assert.assertThat(escapeRegExp("{"), CoreMatchers.equalTo("\\{"));
        Assert.assertThat(escapeRegExp("}"), CoreMatchers.equalTo("\\}"));
        Assert.assertThat(escapeRegExp("["), CoreMatchers.equalTo("\\["));
        Assert.assertThat(escapeRegExp("]"), CoreMatchers.equalTo("\\]"));
        Assert.assertThat(escapeRegExp("How much is (2+3)? 5"), CoreMatchers.equalTo("How much is \\(2\\+3\\)\\? 5"));
        Assert.assertThat(escapeRegExp("\\s|_|-|(?<=[a-z])(?=[A-Z])"), CoreMatchers.equalTo("\\\\s\\|_\\|\\-\\|\\(\\?<=\\[a\\-z\\]\\)\\(\\?=\\[A\\-Z\\]\\)"));
    }
}

