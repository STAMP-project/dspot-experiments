/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.common.options;


import com.google.common.collect.ImmutableList;
import com.google.common.truth.Correspondence;
import com.google.devtools.common.options.OptionsParser.ConstructionException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * Tests for {@link IsolatedOptionsData} and {@link OptionsData}.
 */
@RunWith(JUnit4.class)
public class OptionsDataTest {
    /**
     * Dummy options class.
     */
    public static class ExampleNameConflictOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "1")
        public int foo;

        @Option(name = "foo", documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "I should conflict with foo")
        public String anotherFoo;
    }

    @Test
    public void testNameConflictInSingleClass() {
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleNameConflictOptions.class);
            Assert.fail("foo should conflict with the previous flag foo");
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to option name collision: --foo");
        }
    }

    /**
     * Dummy options class.
     */
    public static class ExampleIntegerFooOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "5")
        public int foo;
    }

    /**
     * Dummy options class.
     */
    public static class ExampleBooleanFooOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean foo;
    }

    @Test
    public void testNameConflictInTwoClasses() {
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleIntegerFooOptions.class, OptionsDataTest.ExampleBooleanFooOptions.class);
            Assert.fail("foo should conflict with the previous flag foo");
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to option name collision: --foo");
        }
    }

    /**
     * Dummy options class.
     */
    public static class ExamplePrefixedFooOptions extends OptionsBase {
        @Option(name = "nofoo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean noFoo;
    }

    @Test
    public void testBooleanPrefixNameConflict() {
        // Try the same test in both orders, the parser should fail if the overlapping flag is defined
        // before or after the boolean flag introduces the alias.
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleBooleanFooOptions.class, OptionsDataTest.ExamplePrefixedFooOptions.class);
            Assert.fail(("nofoo should conflict with the previous flag foo, " + "since foo, as a boolean flag, can be written as --nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains(("Duplicate option name, due to option --nofoo, it " + "conflicts with a negating alias for boolean flag --foo"));
        }
        try {
            OptionsDataTest.construct(OptionsDataTest.ExamplePrefixedFooOptions.class, OptionsDataTest.ExampleBooleanFooOptions.class);
            Assert.fail(("option nofoo should conflict with the previous flag foo, " + "since foo, as a boolean flag, can be written as --nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to boolean option alias: --nofoo");
        }
    }

    /**
     * Dummy options class.
     */
    public static class ExampleBarWasNamedFooOption extends OptionsBase {
        @Option(name = "bar", oldName = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean bar;
    }

    @Test
    public void testBooleanAliasWithOldNameConflict() {
        // Try the same test in both orders, the parser should fail if the overlapping flag is defined
        // before or after the boolean flag introduces the alias.
        try {
            OptionsDataTest.construct(OptionsDataTest.ExamplePrefixedFooOptions.class, OptionsDataTest.ExampleBarWasNamedFooOption.class);
            Assert.fail(("bar has old name foo, which is a boolean flag and can be named as nofoo, so it " + "should conflict with the previous option --nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to boolean option alias: --nofoo");
        }
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleBarWasNamedFooOption.class, OptionsDataTest.ExamplePrefixedFooOptions.class);
            Assert.fail(("nofoo should conflict with the previous flag bar that has old name foo, " + "since foo, as a boolean flag, can be written as --nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains(("Duplicate option name, due to option --nofoo, it conflicts with a negating " + "alias for boolean flag --foo"));
        }
    }

    /**
     * Dummy options class.
     */
    public static class ExampleBarWasNamedNoFooOption extends OptionsBase {
        @Option(name = "bar", oldName = "nofoo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean bar;
    }

    @Test
    public void testBooleanWithOldNameAsAliasOfBooleanConflict() {
        // Try the same test in both orders, the parser should fail if the overlapping flag is defined
        // before or after the boolean flag introduces the alias.
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleBooleanFooOptions.class, OptionsDataTest.ExampleBarWasNamedNoFooOption.class);
            Assert.fail(("nofoo, the old name for bar, should conflict with the previous flag foo, " + "since foo, as a boolean flag, can be written as --nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains(("Duplicate option name, due to old option name --nofoo, it conflicts with a " + "negating alias for boolean flag --foo"));
        }
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleBarWasNamedNoFooOption.class, OptionsDataTest.ExampleBooleanFooOptions.class);
            Assert.fail(("foo, as a boolean flag, can be written as --nofoo and should conflict with the " + "previous option bar that has old name nofoo"));
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to boolean option alias: --nofoo");
        }
    }

    /**
     * Dummy options class.
     */
    public static class ExampleFooBooleanConflictsWithOwnOldName extends OptionsBase {
        @Option(name = "nofoo", oldName = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean foo;
    }

    @Test
    public void testSelfConflictBooleanAliases() {
        // Try the same test in both orders, the parser should fail if the overlapping flag is defined
        // before or after the boolean flag introduces the alias.
        try {
            OptionsDataTest.construct(OptionsDataTest.ExampleFooBooleanConflictsWithOwnOldName.class);
            Assert.fail("foo, the old name for boolean option nofoo, should conflict with its own new name.");
        } catch (ConstructionException e) {
            assertThat(e).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(e).hasMessageThat().contains("Duplicate option name, due to boolean option alias: --nofoo");
        }
    }

    /**
     * Dummy options class.
     */
    public static class OldNameToCanonicalNameConflictExample extends OptionsBase {
        @Option(name = "new_name", oldName = "old_name", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "defaultValue")
        public String flag1;

        @Option(name = "old_name", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "defaultValue")
        public String flag2;
    }

    @Test
    public void testOldNameToCanonicalNameConflict() {
        try {
            OptionsDataTest.construct(OptionsDataTest.OldNameToCanonicalNameConflictExample.class);
            Assert.fail("old_name should conflict with the flag already named old_name");
        } catch (ConstructionException expected) {
            assertThat(expected).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(expected).hasMessageThat().contains(("Duplicate option name, due to option name collision with another option's old name:" + " --old_name"));
        }
    }

    /**
     * Dummy options class.
     */
    public static class OldNameConflictExample extends OptionsBase {
        @Option(name = "new_name", oldName = "old_name", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "defaultValue")
        public String flag1;

        @Option(name = "another_name", oldName = "old_name", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "defaultValue")
        public String flag2;
    }

    @Test
    public void testOldNameToOldNameConflict() {
        try {
            OptionsDataTest.construct(OptionsDataTest.OldNameConflictExample.class);
            Assert.fail("old_name should conflict with the flag already named old_name");
        } catch (ConstructionException expected) {
            assertThat(expected).hasCauseThat().isInstanceOf(DuplicateOptionDeclarationException.class);
            assertThat(expected).hasMessageThat().contains(("Duplicate option name, due to old option name collision with another " + "old option name: --old_name"));
        }
    }

    /**
     * Dummy options class.
     */
    public static class StringConverter implements Converter<String> {
        @Override
        public String convert(String input) {
            return input;
        }

        @Override
        public String getTypeDescription() {
            return "a string";
        }
    }

    /**
     * Dummy options class.
     *
     * <p>Option name order is different from field name order.
     *
     * <p>There are four fields to increase the likelihood of a non-deterministic order being noticed.
     */
    public static class FieldNamesDifferOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int aFoo;

        @Option(name = "bar", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int bBar;

        @Option(name = "baz", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int cBaz;

        @Option(name = "qux", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int dQux;
    }

    /**
     * Dummy options class.
     */
    public static class EndOfAlphabetOptions extends OptionsBase {
        @Option(name = "X", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int x;

        @Option(name = "Y", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int y;
    }

    /**
     * Dummy options class.
     */
    public static class ReverseOrderedOptions extends OptionsBase {
        @Option(name = "C", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int c;

        @Option(name = "B", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int b;

        @Option(name = "A", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int a;
    }

    @Test
    public void optionsClassesIsOrdered() throws Exception {
        IsolatedOptionsData data = OptionsDataTest.construct(OptionsDataTest.FieldNamesDifferOptions.class, OptionsDataTest.EndOfAlphabetOptions.class, OptionsDataTest.ReverseOrderedOptions.class);
        assertThat(data.getOptionsClasses()).containsExactly(OptionsDataTest.FieldNamesDifferOptions.class, OptionsDataTest.EndOfAlphabetOptions.class, OptionsDataTest.ReverseOrderedOptions.class).inOrder();
    }

    @Test
    public void getAllNamedFieldsIsOrdered() throws Exception {
        IsolatedOptionsData data = OptionsDataTest.construct(OptionsDataTest.FieldNamesDifferOptions.class, OptionsDataTest.EndOfAlphabetOptions.class, OptionsDataTest.ReverseOrderedOptions.class);
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, OptionDefinition> entry : data.getAllOptionDefinitions()) {
            names.add(entry.getKey());
        }
        assertThat(names).containsExactly("bar", "baz", "foo", "qux", "X", "Y", "A", "B", "C").inOrder();
    }

    @Test
    public void getFieldsForClassIsOrdered() throws Exception {
        assertThat(getOptionNames(OptionsDataTest.FieldNamesDifferOptions.class)).containsExactly("bar", "baz", "foo", "qux").inOrder();
        assertThat(getOptionNames(OptionsDataTest.EndOfAlphabetOptions.class)).containsExactly("X", "Y").inOrder();
        assertThat(getOptionNames(OptionsDataTest.ReverseOrderedOptions.class)).containsExactly("A", "B", "C").inOrder();
    }

    private static class ReferenceEqualityCorrespondence extends Correspondence<Object, Object> {
        @Override
        public boolean compare(Object obj1, Object obj2) {
            return obj1 == obj2;
        }

        @Override
        public String toString() {
            return "is the same object as";
        }
    }

    @Test
    public void optionsDefinitionsAreSharedBetweenOptionsBases() throws Exception {
        Class<OptionsDataTest.FieldNamesDifferOptions> class1 = OptionsDataTest.FieldNamesDifferOptions.class;
        Class<OptionsDataTest.EndOfAlphabetOptions> class2 = OptionsDataTest.EndOfAlphabetOptions.class;
        Class<OptionsDataTest.ReverseOrderedOptions> class3 = OptionsDataTest.ReverseOrderedOptions.class;
        // Construct the definitions once and accumulate them so we can test that these are not
        // recomputed during the construction of the options data.
        ImmutableList<OptionDefinition> optionDefinitions = new ImmutableList.Builder<OptionDefinition>().addAll(OptionsData.getAllOptionDefinitionsForClass(class1)).addAll(OptionsData.getAllOptionDefinitionsForClass(class2)).addAll(OptionsData.getAllOptionDefinitionsForClass(class3)).build();
        // Construct the data all together.
        IsolatedOptionsData data = OptionsDataTest.construct(class1, class2, class3);
        ArrayList<OptionDefinition> optionDefinitionsFromData = new ArrayList(optionDefinitions.size());
        data.getAllOptionDefinitions().forEach(( entry) -> optionDefinitionsFromData.add(entry.getValue()));
        OptionsDataTest.ReferenceEqualityCorrespondence referenceEquality = new OptionsDataTest.ReferenceEqualityCorrespondence();
        assertThat(optionDefinitionsFromData).comparingElementsUsing(referenceEquality).containsAllIn(optionDefinitions);
        // Construct options data for each class separately, and check again.
        IsolatedOptionsData data1 = OptionsDataTest.construct(class1);
        IsolatedOptionsData data2 = OptionsDataTest.construct(class2);
        IsolatedOptionsData data3 = OptionsDataTest.construct(class3);
        ArrayList<OptionDefinition> optionDefinitionsFromGroupedData = new ArrayList(optionDefinitions.size());
        data1.getAllOptionDefinitions().forEach(( entry) -> optionDefinitionsFromGroupedData.add(entry.getValue()));
        data2.getAllOptionDefinitions().forEach(( entry) -> optionDefinitionsFromGroupedData.add(entry.getValue()));
        data3.getAllOptionDefinitions().forEach(( entry) -> optionDefinitionsFromGroupedData.add(entry.getValue()));
        assertThat(optionDefinitionsFromGroupedData).comparingElementsUsing(referenceEquality).containsAllIn(optionDefinitions);
    }

    /**
     * Dummy options class.
     */
    public static class ValidExpansionOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "1")
        public int foo;

        @Option(name = "bar", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "null", expansion = { "--foo=42" })
        public Void bar;
    }

    @Test
    public void staticExpansionOptionsCanBeVoidType() {
        OptionsDataTest.construct(OptionsDataTest.ValidExpansionOptions.class);
    }
}

