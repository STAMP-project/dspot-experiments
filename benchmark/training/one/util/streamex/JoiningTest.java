/**
 * Copyright 2015, 2017 StreamEx contributors
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
package one.util.streamex;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Tagir Valeev
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JoiningTest {
    @Test(expected = IllegalArgumentException.class)
    public void testMaxCharsRange() {
        Joining.with(",").maxChars((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxCodePointsRange() {
        Joining.with(",").maxCodePoints((-2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaxSymbolsRange() {
        Joining.with(",").maxGraphemes(Integer.MIN_VALUE);
    }

    @Test
    public void testSimple() {
        Supplier<Stream<String>> s = () -> IntStream.range(0, 100).mapToObj(String::valueOf);
        TestHelpers.checkCollector("joiningSimple", s.get().collect(Collectors.joining(", ")), s, Joining.with(", "));
        TestHelpers.checkCollector("joiningWrap", s.get().collect(Collectors.joining(", ", "[", "]")), s, Joining.with(", ").wrap("[", "]"));
        TestHelpers.checkCollector("joiningWrap2", s.get().collect(Collectors.joining(", ", "[(", ")]")), s, Joining.with(", ").wrap("(", ")").wrap("[", "]"));
    }

    @Test
    public void testCutSimple() {
        List<String> input = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");
        Assert.assertEquals("", StreamEx.of(input).peek(Assert::fail).collect(Joining.with(", ").maxChars(0).cutAnywhere()));
        Assert.assertEquals("", StreamEx.of(input).parallel().peek(Assert::fail).collect(Joining.with(", ").maxChars(0).cutAnywhere()));
        String expected = "one, two, three, four, five, six, seven, eight, nine, ten";
        for (int i = 3; i < ((expected.length()) + 5); i++) {
            String exp = expected;
            if ((exp.length()) > i) {
                exp = (exp.substring(0, (i - 3))) + "...";
            }
            String exp2 = expected;
            while ((exp2.length()) > i) {
                int pos = exp2.lastIndexOf(", ", (exp2.endsWith(", ...") ? (exp2.length()) - 6 : exp2.length()));
                exp2 = (pos >= 0) ? (exp2.substring(0, (pos + 2))) + "..." : "...";
            } 
            for (TestHelpers.StreamExSupplier<String> supplier : TestHelpers.streamEx(input::stream)) {
                Assert.assertEquals(((supplier + "/#") + i), exp, supplier.get().collect(Joining.with(", ").maxChars(i).cutAnywhere()));
                Assert.assertEquals(((supplier + "/#") + i), expected.substring(0, Math.min(i, expected.length())), supplier.get().collect(Joining.with(", ").ellipsis("").maxChars(i).cutAnywhere()));
                Assert.assertEquals(((supplier + "/#") + i), exp2, supplier.get().collect(Joining.with(", ").maxChars(i).cutAfterDelimiter()));
            }
        }
        byte[] data = new byte[]{ ((byte) (255)), 48, 64, 80, 112, 18, ((byte) (240)) };
        Assert.assertEquals("FF 30 40 50 ...", IntStreamEx.of(data).mapToObj(( b) -> String.format(Locale.ENGLISH, "%02X", (b & 255))).collect(Joining.with(" ").maxChars(15).cutAfterDelimiter()));
    }

    @Test
    public void testCuts() {
        List<String> input = Arrays.asList("one two", "three four", "five", "six seven");
        TestHelpers.checkShortCircuitCollector("cutBefore", "one two, three four...", 4, input::stream, Joining.with(", ").maxChars(25).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("cutBefore", "one two...", 2, input::stream, Joining.with(", ").maxChars(10).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("cutBefore", "...", 2, input::stream, Joining.with(", ").maxChars(9).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("cutAfter", "one two, three four, ...", 4, input::stream, Joining.with(", ").maxChars(25).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("cutAfter", "one two, ...", 2, input::stream, Joining.with(", ").maxChars(12).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("cutAfter", "...", 2, input::stream, Joining.with(", ").maxChars(11).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("cutWord", "one two, three four, ...", 4, input::stream, Joining.with(", ").maxChars(25).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "one two, ...", 2, input::stream, Joining.with(", ").maxChars(12).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "one two,...", 2, input::stream, Joining.with(", ").maxChars(11).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "one two...", 2, input::stream, Joining.with(", ").maxChars(10).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "one ...", 2, input::stream, Joining.with(", ").maxChars(9).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "one...", 1, input::stream, Joining.with(", ").maxChars(6).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one two, three four, f...", 4, input::stream, Joining.with(", ").maxChars(25));
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one two, ...", 2, input::stream, Joining.with(", ").maxChars(12));
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one two,...", 2, input::stream, Joining.with(", ").maxChars(11));
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one two...", 2, input::stream, Joining.with(", ").maxChars(10));
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one tw...", 2, input::stream, Joining.with(", ").maxChars(9));
        TestHelpers.checkShortCircuitCollector("cutCodePoint", "one...", 1, input::stream, Joining.with(", ").maxChars(6));
    }

    @Test
    public void testPrefixSuffix() {
        List<String> input = Arrays.asList("one two", "three four", "five", "six seven");
        TestHelpers.checkShortCircuitCollector("cutWord", "[one two, three four,...]", 3, input::stream, Joining.with(", ").wrap("[", "]").maxChars(25).cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[one two...]", 2, input::stream, Joining.with(", ").maxChars(12).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[one ...]", 2, input::stream, Joining.with(", ").maxChars(11).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[one ...]", 2, input::stream, Joining.with(", ").maxChars(10).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[one...]", 1, input::stream, Joining.with(", ").maxChars(8).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[...]", 1, input::stream, Joining.with(", ").maxChars(6).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[..]", 1, input::stream, Joining.with(", ").maxChars(4).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[.]", 1, input::stream, Joining.with(", ").maxChars(3).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[]", 0, input::stream, Joining.with(", ").maxChars(2).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "[", 0, input::stream, Joining.with(", ").maxChars(1).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "", 0, input::stream, Joining.with(", ").maxChars(0).wrap("[", "]").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "a prefix  a ", 0, input::stream, Joining.with(" ").maxChars(15).wrap("a prefix ", " a suffix").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "a prefix  ", 0, input::stream, Joining.with(" ").maxChars(10).wrap("a prefix ", " a suffix").cutAtWord());
        TestHelpers.checkShortCircuitCollector("cutWord", "a ", 0, input::stream, Joining.with(" ").maxChars(5).wrap("a prefix ", " a suffix").cutAtWord());
    }

    @Test
    public void testCodePoints() {
        String string = "\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28\ud801\udc49\ud801\udc2f\ud801\udc3b";
        List<CharSequence> input = Arrays.asList(string, new StringBuilder(string), new StringBuffer(string));
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f\ud801", 1, input::stream, Joining.with(",").ellipsis("").maxChars(5).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f", 1, input::stream, Joining.with(",").ellipsis("").maxChars(5).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f", 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f", 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f", 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f", 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxChars", "\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28", 1, input::stream, Joining.with(",").ellipsis("").maxChars(9));
        TestHelpers.checkShortCircuitCollector("maxCodePoints", "\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28\ud801\udc49", 1, input::stream, Joining.with(",").ellipsis("").maxCodePoints(5).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("maxCodePoints", "\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28\ud801\udc49", 1, input::stream, Joining.with(",").ellipsis("").maxCodePoints(5).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("maxCodePoints", "\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28\ud801\udc49", 1, input::stream, Joining.with(",").ellipsis("").maxCodePoints(5).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxCodePoints", string, 2, input::stream, Joining.with(",").ellipsis("").maxCodePoints(7));
        TestHelpers.checkShortCircuitCollector("maxCodePoints", (string + ","), 2, input::stream, Joining.with(",").ellipsis("").maxCodePoints(8));
        TestHelpers.checkShortCircuitCollector("maxCodePoints", (string + ",\ud801\udc14"), 2, input::stream, Joining.with(",").ellipsis("").maxCodePoints(9).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("maxCodePoints", ((((string + ",") + string) + ",") + string), 3, input::stream, Joining.with(",").ellipsis("").maxCodePoints(23));
        TestHelpers.checkShortCircuitCollector("maxCodePoints", (((string + ",") + string) + ",\ud801\udc14\ud801\udc2f\ud801\udc45\ud801\udc28\ud801\udc49\ud801\udc2f"), 3, input::stream, Joining.with(",").ellipsis("").maxCodePoints(22));
        TestHelpers.checkShortCircuitCollector("maxCodePointsPrefix", string, 0, input::stream, Joining.with(",").wrap(string, string).maxCodePoints(7).cutAnywhere());
    }

    @Test
    public void testSurrogates() {
        String string = "\ud801\ud801\ud801\ud801\ud801\udc14\udc14\udc14\udc14\udc14";
        List<String> input = Collections.nCopies(3, string);
        TestHelpers.checkShortCircuitCollector("highSurr", string.substring(0, 4), 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("highSurr", string.substring(0, 4), 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("highSurr", string.substring(0, 4), 1, input::stream, Joining.with(",").ellipsis("").maxCodePoints(4).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("lowSurr", string.substring(0, 7), 1, input::stream, Joining.with(",").ellipsis("").maxChars(7).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("lowSurr", string.substring(0, 7), 1, input::stream, Joining.with(",").ellipsis("").maxChars(7).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("lowSurr", string.substring(0, 8), 1, input::stream, Joining.with(",").ellipsis("").maxCodePoints(7).cutAnywhere());
    }

    @Test
    public void testGraphemes() {
        String string = "aa\u0300\u0321e\u0300a\u0321a\u0300\u0321a";
        List<String> input = Collections.nCopies(3, string);
        TestHelpers.checkShortCircuitCollector("maxChars", "aa\u0300\u0321e", 1, input::stream, Joining.with(",").ellipsis("").maxChars(5).cutAnywhere());
        TestHelpers.checkShortCircuitCollector("maxChars", "aa\u0300\u0321e", 1, input::stream, Joining.with(",").ellipsis("").maxChars(5).cutAtCodePoint());
        TestHelpers.checkShortCircuitCollector("maxChars", "aa\u0300\u0321", 1, input::stream, Joining.with(",").ellipsis("").maxChars(5).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxChars", "aa\u0300\u0321e\u0300", 1, input::stream, Joining.with(",").ellipsis("").maxChars(6).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxChars", "aa\u0300\u0321", 1, input::stream, Joining.with(",").ellipsis("").maxChars(4).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxChars", "a", 1, input::stream, Joining.with(",").ellipsis("").maxChars(3).cutAtGrapheme());
        TestHelpers.checkShortCircuitCollector("maxSymbols", "aa\u0300\u0321e\u0300", 1, input::stream, Joining.with(",").ellipsis("").maxGraphemes(3));
        TestHelpers.checkShortCircuitCollector("maxSymbols", "aa\u0300\u0321e\u0300a\u0321a\u0300\u0321", 1, input::stream, Joining.with(",").ellipsis("").maxGraphemes(5));
        TestHelpers.checkShortCircuitCollector("maxSymbols", string, 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(6));
        TestHelpers.checkShortCircuitCollector("maxSymbols", (string + ","), 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(7));
        TestHelpers.checkShortCircuitCollector("maxSymbols", (string + ",a"), 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(8));
        TestHelpers.checkShortCircuitCollector("maxSymbols", (string + ",aa\u0300\u0321"), 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(9));
        TestHelpers.checkShortCircuitCollector("maxSymbolsBeforeDelimiter", "", 1, input::stream, Joining.with(",").ellipsis("").maxGraphemes(5).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsBeforeDelimiter", string, 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(6).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsBeforeDelimiter", string, 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(7).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsBeforeDelimiter", string, 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(8).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsAfterDelimiter", "", 1, input::stream, Joining.with(",").ellipsis("").maxGraphemes(5).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsAfterDelimiter", "", 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(6).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsAfterDelimiter", (string + ","), 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(7).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsAfterDelimiter", (string + ","), 2, input::stream, Joining.with(",").ellipsis("").maxGraphemes(8).cutAfterDelimiter());
        TestHelpers.checkShortCircuitCollector("maxSymbolsBeforeDelimiterPrefix", string, 0, input::stream, Joining.with(",").wrap(string, string).maxGraphemes(8).cutBeforeDelimiter());
    }

    @Test
    public void testMaxElements() {
        List<String> input = Arrays.asList("one", "two", "three", "four");
        TestHelpers.checkShortCircuitCollector("maxElements", "", 0, StreamEx::empty, Joining.with(", ").maxElements(0));
        TestHelpers.checkShortCircuitCollector("maxElements", "...", 1, input::stream, Joining.with(", ").maxElements(0));
        TestHelpers.checkShortCircuitCollector("maxElements", "one, ...", 2, input::stream, Joining.with(", ").maxElements(1));
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two, ...", 3, input::stream, Joining.with(", ").maxElements(2));
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two, three, ...", 4, input::stream, Joining.with(", ").maxElements(3));
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two, three, four", 4, input::stream, Joining.with(", ").maxElements(4));
        TestHelpers.checkShortCircuitCollector("maxElements", "...", 1, input::stream, Joining.with(", ").maxElements(0).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxElements", "one...", 2, input::stream, Joining.with(", ").maxElements(1).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two...", 3, input::stream, Joining.with(", ").maxElements(2).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two, three...", 4, input::stream, Joining.with(", ").maxElements(3).cutBeforeDelimiter());
        TestHelpers.checkShortCircuitCollector("maxElements", "one, two, three, four", 4, input::stream, Joining.with(", ").maxElements(4).cutBeforeDelimiter());
    }
}

