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


import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OptionDocumentationCategory.UNCATEGORIZED;
import static OptionEffectTag.NO_OP;


/**
 * Tests for converting {@link OptionsBase} subclass instances to and from maps.
 */
@RunWith(JUnit4.class)
public class OptionsMapConversionTest {
    /**
     * Dummy options base class.
     */
    public static class FooOptions extends OptionsBase {
        @Option(name = "foo", documentationCategory = UNCATEGORIZED, effectTags = { NO_OP }, defaultValue = "false")
        public boolean foo;
    }

    /**
     * Dummy options derived class.
     */
    public static class BazOptions extends OptionsMapConversionTest.FooOptions {
        @Option(name = "bar", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "true")
        public boolean bar;

        @Option(name = "baz", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "5")
        public int baz;
    }

    @Test
    public void toMap_Basic() {
        OptionsMapConversionTest.FooOptions foo = Options.getDefaults(OptionsMapConversionTest.FooOptions.class);
        assertThat(OptionsMapConversionTest.keysToStrings(OptionsParser.toMap(OptionsMapConversionTest.FooOptions.class, foo))).containsExactly("foo", false);
    }

    @Test
    public void asMap_Basic() {
        OptionsMapConversionTest.FooOptions foo = Options.getDefaults(OptionsMapConversionTest.FooOptions.class);
        assertThat(asMap()).containsExactly("foo", false);
    }

    @Test
    public void toMap_Inheritance() {
        OptionsMapConversionTest.BazOptions baz = Options.getDefaults(OptionsMapConversionTest.BazOptions.class);
        assertThat(OptionsMapConversionTest.keysToStrings(OptionsParser.toMap(OptionsMapConversionTest.BazOptions.class, baz))).containsExactly("foo", false, "bar", true, "baz", 5);
    }

    @Test
    public void asMap_Inheritance() {
        // Static type is base class, dynamic type is derived. We still get the derived fields.
        OptionsMapConversionTest.FooOptions foo = Options.getDefaults(OptionsMapConversionTest.BazOptions.class);
        assertThat(asMap()).containsExactly("foo", false, "bar", true, "baz", 5);
    }

    @Test
    public void toMap_InheritanceBaseFieldsOnly() {
        OptionsMapConversionTest.BazOptions baz = Options.getDefaults(OptionsMapConversionTest.BazOptions.class);
        assertThat(OptionsMapConversionTest.keysToStrings(OptionsParser.toMap(OptionsMapConversionTest.FooOptions.class, baz))).containsExactly("foo", false);
    }

    /**
     * Dummy options class for checking alphabetizing.
     *
     * <p>Note that field name order differs from option name order.
     */
    public static class AlphaOptions extends OptionsBase {
        @Option(name = "c", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int v;

        @Option(name = "d", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int w;

        @Option(name = "a", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int x;

        @Option(name = "e", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int y;

        @Option(name = "b", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "0")
        public int z;
    }

    @Test
    public void toMap_AlphabeticalOrder() {
        OptionsMapConversionTest.AlphaOptions alpha = Options.getDefaults(OptionsMapConversionTest.AlphaOptions.class);
        assertThat(OptionsMapConversionTest.keysToStrings(OptionsParser.toMap(OptionsMapConversionTest.AlphaOptions.class, alpha))).containsExactly("a", 0, "b", 0, "c", 0, "d", 0, "e", 0).inOrder();
    }

    @Test
    public void asMap_AlphabeticalOrder() {
        OptionsMapConversionTest.AlphaOptions alpha = Options.getDefaults(OptionsMapConversionTest.AlphaOptions.class);
        assertThat(asMap()).containsExactly("a", 0, "b", 0, "c", 0, "d", 0, "e", 0).inOrder();
    }

    @Test
    public void fromMap_Basic() {
        Map<String, Object> map = ImmutableMap.<String, Object>of("foo", true);
        Map<Field, Object> fieldMap = OptionsMapConversionTest.keysToFields(OptionsMapConversionTest.FooOptions.class, map);
        OptionsMapConversionTest.FooOptions foo = OptionsParser.fromMap(OptionsMapConversionTest.FooOptions.class, fieldMap);
        assertThat(foo.foo).isTrue();
    }

    /**
     * Dummy subclass of foo.
     */
    public static class SubFooAOptions extends OptionsMapConversionTest.FooOptions {
        @Option(name = "a", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean a;
    }

    /**
     * Dummy subclass of foo.
     */
    public static class SubFooBOptions extends OptionsMapConversionTest.FooOptions {
        @Option(name = "b1", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean b1;

        @Option(name = "b2", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean b2;
    }

    @Test
    public void fromMap_FailsOnWrongKeys() {
        Map<String, Object> map = ImmutableMap.<String, Object>of("foo", true, "a", false);
        Map<Field, Object> fieldMap = OptionsMapConversionTest.keysToFields(OptionsMapConversionTest.SubFooAOptions.class, map);
        try {
            OptionsParser.fromMap(OptionsMapConversionTest.SubFooBOptions.class, fieldMap);
            Assert.fail("Should have failed due to the given map's fields not matching the ones on the class");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains(("Map keys do not match fields of options class; extra map keys: {'a'}; " + "extra options class options: {'b1', 'b2'}"));
        }
    }

    @Test
    public void fromMap_FailsOnWrongTypes() {
        Map<String, Object> map = ImmutableMap.<String, Object>of("foo", 5);
        Map<Field, Object> fieldMap = OptionsMapConversionTest.keysToFields(OptionsMapConversionTest.SubFooAOptions.class, map);
        try {
            OptionsParser.fromMap(OptionsMapConversionTest.FooOptions.class, fieldMap);
            Assert.fail("Should have failed due to trying to assign a field value with the wrong type");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().matches("Can not set boolean field .*\\.foo to java\\.lang\\.Integer");
        }
    }

    @Test
    public void fromMap_Inheritance() {
        Map<String, Object> map = ImmutableMap.<String, Object>of("foo", true, "bar", true, "baz", 3);
        Map<Field, Object> fieldMap = OptionsMapConversionTest.keysToFields(OptionsMapConversionTest.BazOptions.class, map);
        OptionsMapConversionTest.BazOptions baz = OptionsParser.fromMap(OptionsMapConversionTest.BazOptions.class, fieldMap);
        assertThat(baz.foo).isTrue();
        assertThat(baz.bar).isTrue();
        assertThat(baz.baz).isEqualTo(3);
    }
}

