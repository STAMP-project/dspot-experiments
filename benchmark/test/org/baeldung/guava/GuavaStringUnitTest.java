package org.baeldung.guava;


import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GuavaStringUnitTest {
    @Test
    public void whenConvertListToString_thenConverted() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final String result = Joiner.on(",").join(names);
        Assert.assertEquals(result, "John,Jane,Adam,Tom");
    }

    @Test
    public void whenConvertListToStringAndSkipNull_thenConverted() {
        final List<String> names = Lists.newArrayList("John", null, "Jane", "Adam", "Tom");
        final String result = Joiner.on(",").skipNulls().join(names);
        Assert.assertEquals(result, "John,Jane,Adam,Tom");
    }

    @Test
    public void whenConvertMapToString_thenConverted() {
        final Map<String, Integer> salary = Maps.newHashMap();
        salary.put("John", 1000);
        salary.put("Jane", 1500);
        final String result = Joiner.on(" , ").withKeyValueSeparator(" = ").join(salary);
        Assert.assertThat(result, Matchers.containsString("John = 1000"));
        Assert.assertThat(result, Matchers.containsString("Jane = 1500"));
    }

    @Test
    public void whenJoinNestedCollections_thenJoined() {
        final List<ArrayList<String>> nested = Lists.newArrayList(Lists.newArrayList("apple", "banana", "orange"), Lists.newArrayList("cat", "dog", "bird"), Lists.newArrayList("John", "Jane", "Adam"));
        final String result = Joiner.on(";").join(Iterables.transform(nested, new Function<List<String>, String>() {
            @Override
            public final String apply(final List<String> input) {
                return Joiner.on("-").join(input);
            }
        }));
        Assert.assertThat(result, Matchers.containsString("apple-banana-orange"));
        Assert.assertThat(result, Matchers.containsString("cat-dog-bird"));
        Assert.assertThat(result, Matchers.containsString("John-Jane-Adam"));
    }

    @Test
    public void whenUseForNull_thenUsed() {
        final List<String> names = Lists.newArrayList("John", null, "Jane", "Adam", "Tom");
        final String result = Joiner.on(",").useForNull("nameless").join(names);
        Assert.assertEquals(result, "John,nameless,Jane,Adam,Tom");
    }

    @Test
    public void whenCreateListFromString_thenCreated() {
        final String input = "apple - banana - orange";
        final List<String> result = Splitter.on("-").trimResults().splitToList(input);
        Assert.assertThat(result, Matchers.contains("apple", "banana", "orange"));
    }

    @Test
    public void whenCreateMapFromString_thenCreated() {
        final String input = "John=first,Adam=second";
        final Map<String, String> result = Splitter.on(",").withKeyValueSeparator("=").split(input);
        Assert.assertEquals("first", result.get("John"));
        Assert.assertEquals("second", result.get("Adam"));
    }

    @Test
    public void whenSplitStringOnMultipleSeparator_thenSplit() {
        final String input = "apple.banana,,orange,,.";
        final List<String> result = Splitter.onPattern("[.,]").omitEmptyStrings().splitToList(input);
        Assert.assertThat(result, Matchers.contains("apple", "banana", "orange"));
    }

    @Test
    public void whenSplitStringOnSpecificLength_thenSplit() {
        final String input = "Hello world";
        final List<String> result = Splitter.fixedLength(3).splitToList(input);
        Assert.assertThat(result, Matchers.contains("Hel", "lo ", "wor", "ld"));
    }

    @Test
    public void whenLimitSplitting_thenLimited() {
        final String input = "a,b,c,d,e";
        final List<String> result = Splitter.on(",").limit(4).splitToList(input);
        Assert.assertEquals(4, result.size());
        Assert.assertThat(result, Matchers.contains("a", "b", "c", "d,e"));
    }

    @Test
    public void whenRemoveSpecialCharacters_thenRemoved() {
        final String input = "H*el.lo,}12";
        final CharMatcher matcher = CharMatcher.JAVA_LETTER_OR_DIGIT;
        final String result = matcher.retainFrom(input);
        Assert.assertEquals("Hello12", result);
    }

    @Test
    public void whenRemoveNonASCIIChars_thenRemoved() {
        final String input = "?hello?";
        String result = CharMatcher.ASCII.retainFrom(input);
        Assert.assertEquals("hello", result);
        result = CharMatcher.inRange('0', 'z').retainFrom(input);
        Assert.assertEquals("hello", result);
    }

    @Test
    public void whenValidateString_thenValid() {
        final String input = "hello";
        boolean result = CharMatcher.JAVA_LOWER_CASE.matchesAllOf(input);
        Assert.assertTrue(result);
        result = CharMatcher.is('e').matchesAnyOf(input);
        Assert.assertTrue(result);
        result = CharMatcher.JAVA_DIGIT.matchesNoneOf(input);
        Assert.assertTrue(result);
    }

    @Test
    public void whenTrimString_thenTrimmed() {
        final String input = "---hello,,,";
        String result = CharMatcher.is('-').trimLeadingFrom(input);
        Assert.assertEquals("hello,,,", result);
        result = CharMatcher.is(',').trimTrailingFrom(input);
        Assert.assertEquals("---hello", result);
        result = CharMatcher.anyOf("-,").trimFrom(input);
        Assert.assertEquals("hello", result);
    }

    @Test
    public void whenCollapseFromString_thenCollapsed() {
        final String input = "       hel    lo      ";
        String result = CharMatcher.is(' ').collapseFrom(input, '-');
        Assert.assertEquals("-hel-lo-", result);
        result = CharMatcher.is(' ').trimAndCollapseFrom(input, '-');
        Assert.assertEquals("hel-lo", result);
    }

    @Test
    public void whenReplaceFromString_thenReplaced() {
        final String input = "apple-banana.";
        String result = CharMatcher.anyOf("-.").replaceFrom(input, '!');
        Assert.assertEquals("apple!banana!", result);
        result = CharMatcher.is('-').replaceFrom(input, " and ");
        Assert.assertEquals("apple and banana.", result);
    }

    @Test
    public void whenCountCharInString_thenCorrect() {
        final String input = "a, c, z, 1, 2";
        int result = CharMatcher.is(',').countIn(input);
        Assert.assertEquals(4, result);
        result = CharMatcher.inRange('a', 'h').countIn(input);
        Assert.assertEquals(2, result);
    }

    @Test
    public void whenRemoveCharsNotInCharset_thenRemoved() {
        final Charset charset = Charset.forName("cp437");
        final CharsetEncoder encoder = charset.newEncoder();
        final Predicate<Character> inRange = new Predicate<Character>() {
            @Override
            public boolean apply(final Character c) {
                return encoder.canEncode(c);
            }
        };
        final String result = CharMatcher.forPredicate(inRange).retainFrom("hello?");
        Assert.assertEquals("hello", result);
    }
}

