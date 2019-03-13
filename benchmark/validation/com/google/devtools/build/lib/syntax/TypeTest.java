/**
 * Copyright 2006 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import BuildType.LABEL;
import BuildType.LABEL_LIST;
import BuildType.LICENSE;
import BuildType.NODEP_LABEL;
import BuildType.TRISTATE;
import License.NO_LICENSE;
import Order.STABLE_ORDER;
import TriState.AUTO;
import TriState.NO;
import TriState.YES;
import Type.BOOLEAN;
import Type.INTEGER;
import Type.STRING;
import Type.STRING_DICT;
import Type.STRING_LIST;
import Type.STRING_LIST_DICT;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.syntax.SkylarkList.MutableList;
import com.google.devtools.build.lib.syntax.SkylarkList.Tuple;
import com.google.devtools.build.lib.syntax.Type.ConversionException;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test of type-conversions using Type.
 */
@RunWith(JUnit4.class)
public class TypeTest {
    private Label currentRule;

    @Test
    public void testInteger() throws Exception {
        Object x = 3;
        assertThat(INTEGER.convert(x, null)).isEqualTo(x);
        assertThat(TypeTest.collectLabels(INTEGER, x)).isEmpty();
    }

    @Test
    public void testNonInteger() throws Exception {
        try {
            INTEGER.convert("foo", null);
            Assert.fail();
        } catch (Type e) {
            // This does not use assertMessageContainsWordsWithQuotes because at least
            // one test should test exact wording (but they all shouldn't to make
            // changing/improving the messages easy).
            assertThat(e).hasMessage("expected value of type \'int\', but got \"foo\" (string)");
        }
    }

    // Ensure that types are reported correctly.
    @Test
    public void testTypeErrorMessage() throws Exception {
        try {
            STRING_LIST.convert("[(1,2), 3, 4]", "myexpr", null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage(("expected value of type 'list(string)' for myexpr, " + "but got \"[(1,2), 3, 4]\" (string)"));
        }
    }

    @Test
    public void testString() throws Exception {
        Object s = "foo";
        assertThat(STRING.convert(s, null)).isEqualTo(s);
        assertThat(TypeTest.collectLabels(STRING, s)).isEmpty();
    }

    @Test
    public void testNonString() throws Exception {
        try {
            STRING.convert(3, null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'string', but got 3 (int)");
        }
    }

    @Test
    public void testBoolean() throws Exception {
        Object myTrue = true;
        Object myFalse = false;
        assertThat(BOOLEAN.convert(1, null)).isEqualTo(Boolean.TRUE);
        assertThat(BOOLEAN.convert(0, null)).isEqualTo(Boolean.FALSE);
        assertThat(BOOLEAN.convert(true, null)).isTrue();
        assertThat(BOOLEAN.convert(myTrue, null)).isTrue();
        assertThat(BOOLEAN.convert(false, null)).isFalse();
        assertThat(BOOLEAN.convert(myFalse, null)).isFalse();
        assertThat(TypeTest.collectLabels(BOOLEAN, myTrue)).isEmpty();
    }

    @Test
    public void testNonBoolean() throws Exception {
        try {
            BOOLEAN.convert("unexpected", null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type \'int\', but got \"unexpected\" (string)");
        }
        // Integers other than [0, 1] should fail.
        try {
            BOOLEAN.convert(2, null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessageThat().isEqualTo("boolean is not one of [0, 1]");
        }
        try {
            BOOLEAN.convert((-1), null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessageThat().isEqualTo("boolean is not one of [0, 1]");
        }
    }

    @Test
    public void testTriState() throws Exception {
        assertThat(TRISTATE.convert(1, null)).isEqualTo(YES);
        assertThat(TRISTATE.convert(0, null)).isEqualTo(NO);
        assertThat(TRISTATE.convert((-1), null)).isEqualTo(AUTO);
        assertThat(TRISTATE.convert(YES, null)).isEqualTo(YES);
        assertThat(TRISTATE.convert(NO, null)).isEqualTo(NO);
        assertThat(TRISTATE.convert(AUTO, null)).isEqualTo(AUTO);
        assertThat(TypeTest.collectLabels(TRISTATE, YES)).isEmpty();
        // deprecated:
        assertThat(TRISTATE.convert(true, null)).isEqualTo(YES);
        assertThat(TRISTATE.convert(false, null)).isEqualTo(NO);
    }

    @Test
    public void testTriStateDoesNotAcceptArbitraryIntegers() throws Exception {
        List<Integer> listOfCases = Lists.newArrayList(2, 3, (-5), (-2), 20);
        for (Object entry : listOfCases) {
            try {
                TRISTATE.convert(entry, null);
                Assert.fail();
            } catch (Type e) {
                // Expected.
            }
        }
    }

    @Test
    public void testTriStateDoesNotAcceptStrings() throws Exception {
        List<?> listOfCases = Lists.newArrayList("bad", "true", "auto", "false");
        // TODO(adonovan): add booleans true, false to this list; see b/116691720.
        for (Object entry : listOfCases) {
            try {
                TRISTATE.convert(entry, null);
                Assert.fail();
            } catch (Type e) {
                // Expected.
            }
        }
    }

    @Test
    public void testTagConversion() throws Exception {
        assertThat(BOOLEAN.toTagSet(true, "attribute")).containsExactlyElementsIn(Sets.newHashSet("attribute"));
        assertThat(BOOLEAN.toTagSet(false, "attribute")).containsExactlyElementsIn(Sets.newHashSet("noattribute"));
        assertThat(STRING.toTagSet("whiskey", "preferred_cocktail")).containsExactlyElementsIn(Sets.newHashSet("whiskey"));
        assertThat(STRING_LIST.toTagSet(Lists.newArrayList("cheddar", "ementaler", "gruyere"), "cheeses")).containsExactlyElementsIn(Sets.newHashSet("cheddar", "ementaler", "gruyere"));
    }

    @Test
    public void testIllegalTagConversionByType() throws Exception {
        try {
            TRISTATE.toTagSet(AUTO, "some_tristate");
            Assert.fail("Expect UnsuportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Success.
        }
        try {
            LICENSE.toTagSet(NO_LICENSE, "output_license");
            Assert.fail("Expect UnsuportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Success.
        }
    }

    @Test
    public void testIllegalTagConversIonFromNullOnSupportedType() throws Exception {
        try {
            BOOLEAN.toTagSet(null, "a_boolean");
            Assert.fail("Expect UnsuportedOperationException");
        } catch (IllegalStateException e) {
            // Success.
        }
    }

    @Test
    public void testLabel() throws Exception {
        Label label = Label.parseAbsolute("//foo:bar", ImmutableMap.of());
        assertThat(LABEL.convert("//foo:bar", null, currentRule)).isEqualTo(label);
        assertThat(TypeTest.collectLabels(LABEL, label)).containsExactly(label);
    }

    @Test
    public void testNodepLabel() throws Exception {
        Label label = Label.parseAbsolute("//foo:bar", ImmutableMap.of());
        assertThat(NODEP_LABEL.convert("//foo:bar", null, currentRule)).isEqualTo(label);
        assertThat(TypeTest.collectLabels(NODEP_LABEL, label)).containsExactly(label);
    }

    @Test
    public void testRelativeLabel() throws Exception {
        assertThat(LABEL.convert(":wiz", null, currentRule)).isEqualTo(Label.parseAbsolute("//quux:wiz", ImmutableMap.of()));
        assertThat(LABEL.convert("wiz", null, currentRule)).isEqualTo(Label.parseAbsolute("//quux:wiz", ImmutableMap.of()));
        try {
            LABEL.convert("wiz", null);
            Assert.fail();
        } catch (ConversionException e) {
            /* ok */
        }
    }

    @Test
    public void testInvalidLabel() throws Exception {
        try {
            LABEL.convert("not//a label", null, currentRule);
            Assert.fail();
        } catch (Type e) {
            MoreAsserts.assertContainsWordsWithQuotes(e.getMessage(), "not//a label");
        }
    }

    @Test
    public void testNonLabel() throws Exception {
        try {
            LABEL.convert(3, null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'string', but got 3 (int)");
        }
    }

    @Test
    public void testStringList() throws Exception {
        Object input = Arrays.asList("foo", "bar", "wiz");
        List<String> converted = STRING_LIST.convert(input, null);
        assertThat(converted).isEqualTo(input);
        assertThat(converted).isNotSameAs(input);
        assertThat(TypeTest.collectLabels(STRING_LIST, input)).isEmpty();
    }

    @Test
    public void testStringDict() throws Exception {
        Object input = ImmutableMap.of("foo", "bar", "wiz", "bang");
        Map<String, String> converted = STRING_DICT.convert(input, null);
        assertThat(converted).isEqualTo(input);
        assertThat(converted).isNotSameAs(input);
        assertThat(TypeTest.collectLabels(STRING_DICT, converted)).isEmpty();
    }

    @Test
    public void testStringDictBadElements() throws Exception {
        Object input = ImmutableMap.of("foo", MutableList.of(null, "bar", "baz"), "wiz", "bang");
        try {
            STRING_DICT.convert(input, null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage(("expected value of type 'string' for dict value element, " + "but got [\"bar\", \"baz\"] (list)"));
        }
    }

    @Test
    public void testNonStringList() throws Exception {
        try {
            STRING_LIST.convert(3, "blah");
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'list(string)' for blah, but got 3 (int)");
        }
    }

    @Test
    public void testStringListBadElements() throws Exception {
        Object input = Arrays.<Object>asList("foo", "bar", 1);
        try {
            STRING_LIST.convert(input, "argument quux");
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'string' for element 2 of argument quux, but got 1 (int)");
        }
    }

    @Test
    public void testListDepsetConversion() throws Exception {
        Object input = SkylarkNestedSet.of(String.class, NestedSetBuilder.create(STABLE_ORDER, "a", "b", "c"));
        STRING_LIST.convert(input, null);
    }

    @Test
    public void testLabelList() throws Exception {
        Object input = Arrays.asList("//foo:bar", ":wiz");
        List<Label> converted = LABEL_LIST.convert(input, null, currentRule);
        List<Label> expected = Arrays.asList(Label.parseAbsolute("//foo:bar", ImmutableMap.of()), Label.parseAbsolute("//quux:wiz", ImmutableMap.of()));
        assertThat(converted).isEqualTo(expected);
        assertThat(converted).isNotSameAs(expected);
        assertThat(TypeTest.collectLabels(LABEL_LIST, converted)).containsExactlyElementsIn(expected);
    }

    @Test
    public void testNonLabelList() throws Exception {
        try {
            LABEL_LIST.convert(3, "foo", currentRule);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'list(label)' for foo, but got 3 (int)");
        }
    }

    @Test
    public void testLabelListBadElements() throws Exception {
        Object list = Arrays.<Object>asList("//foo:bar", 2, "foo");
        try {
            LABEL_LIST.convert(list, null, currentRule);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'string' for element 1 of null, but got 2 (int)");
        }
    }

    @Test
    public void testLabelListSyntaxError() throws Exception {
        Object list = Arrays.<Object>asList("//foo:bar/..", "foo");
        try {
            LABEL_LIST.convert(list, "myexpr", currentRule);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage(("invalid label '//foo:bar/..' in element 0 of myexpr: " + ("invalid target name 'bar/..': " + "target names may not contain up-level references '..'")));
        }
    }

    @Test
    public void testStringListDict() throws Exception {
        Object input = ImmutableMap.of("foo", Arrays.asList("foo", "bar"), "wiz", Arrays.asList("bang"));
        Map<String, List<String>> converted = STRING_LIST_DICT.convert(input, null, currentRule);
        Map<?, ?> expected = ImmutableMap.<String, List<String>>of("foo", Arrays.asList("foo", "bar"), "wiz", Arrays.asList("bang"));
        assertThat(converted).isEqualTo(expected);
        assertThat(converted).isNotSameAs(expected);
        assertThat(TypeTest.collectLabels(STRING_LIST_DICT, converted)).isEmpty();
    }

    @Test
    public void testStringListDictBadFirstElement() throws Exception {
        Object input = ImmutableMap.of(2, Arrays.asList("foo", "bar"), "wiz", Arrays.asList("bang"));
        try {
            STRING_LIST_DICT.convert(input, null, currentRule);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage("expected value of type 'string' for dict key element, but got 2 (int)");
        }
    }

    @Test
    public void testStringListDictBadSecondElement() throws Exception {
        Object input = ImmutableMap.of("foo", "bar", "wiz", Arrays.asList("bang"));
        try {
            STRING_LIST_DICT.convert(input, null, currentRule);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage(("expected value of type 'list(string)' for dict value element, " + "but got \"bar\" (string)"));
        }
    }

    @Test
    public void testStringListDictBadElements1() throws Exception {
        Object input = ImmutableMap.of(Tuple.of("foo"), Tuple.of("bang"), "wiz", Tuple.of("bang"));
        try {
            STRING_LIST_DICT.convert(input, null);
            Assert.fail();
        } catch (Type e) {
            assertThat(e).hasMessage(("expected value of type 'string' for dict key element, but got " + "(\"foo\",) (tuple)"));
        }
    }

    @Test
    public void testStringDictThrowsConversionException() throws Exception {
        try {
            STRING_DICT.convert("some string", null);
            Assert.fail();
        } catch (ConversionException e) {
            assertThat(e).hasMessage("expected value of type \'dict(string, string)\', but got \"some string\" (string)");
        }
    }
}

