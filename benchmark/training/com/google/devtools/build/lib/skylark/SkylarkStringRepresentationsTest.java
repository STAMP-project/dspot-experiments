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
package com.google.devtools.build.lib.skylark;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.skylark.util.SkylarkTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for string representations of Skylark objects.
 */
@RunWith(JUnit4.class)
public class SkylarkStringRepresentationsTest extends SkylarkTestCase {
    // Different ways to format objects, these suffixes are used in the `prepare_params` function
    private static final ImmutableList<String> SUFFIXES = ImmutableList.of("_str", "_repr", "_format", "_str_perc", "_repr_perc");

    @Test
    public void testStringRepresentations_Strings() throws Exception {
        assertThat(skylarkLoadingEval("str('foo')")).isEqualTo("foo");
        assertThat(skylarkLoadingEval("'%s' % 'foo'")).isEqualTo("foo");
        assertThat(skylarkLoadingEval("'{}'.format('foo')")).isEqualTo("foo");
        assertThat(skylarkLoadingEval("repr('foo')")).isEqualTo("\"foo\"");
        assertThat(skylarkLoadingEval("'%r' % 'foo'")).isEqualTo("\"foo\"");
    }

    @Test
    public void testStringRepresentations_Labels() throws Exception {
        assertThat(skylarkLoadingEval("str(Label('//foo:bar'))")).isEqualTo("//foo:bar");
        assertThat(skylarkLoadingEval("'%s' % Label('//foo:bar')")).isEqualTo("//foo:bar");
        assertThat(skylarkLoadingEval("'{}'.format(Label('//foo:bar'))")).isEqualTo("//foo:bar");
        assertThat(skylarkLoadingEval("repr(Label('//foo:bar'))")).isEqualTo("Label(\"//foo:bar\")");
        assertThat(skylarkLoadingEval("'%r' % Label('//foo:bar')")).isEqualTo("Label(\"//foo:bar\")");
        assertThat(skylarkLoadingEval("'{}'.format([Label('//foo:bar')])")).isEqualTo("[Label(\"//foo:bar\")]");
    }

    @Test
    public void testStringRepresentations_Primitives() throws Exception {
        // Strings are tested in a separate test case as they have different str and repr values.
        assertStringRepresentation("1543", "1543");
        assertStringRepresentation("True", "True");
        assertStringRepresentation("False", "False");
    }

    @Test
    public void testStringRepresentations_Containers() throws Exception {
        assertStringRepresentation("['a', 'b']", "[\"a\", \"b\"]");
        assertStringRepresentation("('a', 'b')", "(\"a\", \"b\")");
        assertStringRepresentation("{'a': 'b', 'c': 'd'}", "{\"a\": \"b\", \"c\": \"d\"}");
        assertStringRepresentation("struct(d = 4, c = 3)", "struct(c = 3, d = 4)");
    }

    @Test
    public void testStringRepresentations_Functions() throws Exception {
        assertStringRepresentation("all", "<built-in function all>");
        assertStringRepresentation("def f(): pass", "f", "<function f from //eval:eval.bzl>");
    }

    @Test
    public void testStringRepresentations_Rules() throws Exception {
        assertStringRepresentation("native.cc_library", "<built-in rule cc_library>");
        assertStringRepresentation("rule(implementation=str)", "<rule>");
    }

    @Test
    public void testStringRepresentations_Aspects() throws Exception {
        assertStringRepresentation("aspect(implementation=str)", "<aspect>");
    }

    @Test
    public void testStringRepresentations_Providers() throws Exception {
        assertStringRepresentation("provider()", "<provider>");
        assertStringRepresentation("p = provider()", "p(b = 'foo', a = 1)", "struct(a = 1, b = \"foo\")");
    }

    @Test
    public void testStringRepresentations_Select() throws Exception {
        assertStringRepresentation("select({'//foo': ['//bar']}) + select({'//foo2': ['//bar2']})", "select({\"//foo\": [\"//bar\"]}) + select({\"//foo2\": [\"//bar2\"]})");
    }

    @Test
    public void testStringRepresentations_RuleContext() throws Exception {
        generateFilesToTestStrings();
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:check");
        for (String suffix : SkylarkStringRepresentationsTest.SUFFIXES) {
            assertThat(target.get(("rule_ctx" + suffix))).isEqualTo("<rule context for //test/skylark:check>");
            assertThat(target.get(("aspect_ctx" + suffix))).isEqualTo("<aspect context for //test/skylark:bar>");
            assertThat(target.get(("aspect_ctx.rule" + suffix))).isEqualTo("<rule collection for //test/skylark:bar>");
        }
    }

    @Test
    public void testStringRepresentations_Files() throws Exception {
        generateFilesToTestStrings();
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:check");
        for (String suffix : SkylarkStringRepresentationsTest.SUFFIXES) {
            assertThat(target.get(("source_file" + suffix))).isEqualTo("<source file test/skylark/input.txt>");
            assertThat(target.get(("generated_file" + suffix))).isEqualTo("<generated file test/skylark/output.txt>");
        }
    }

    @Test
    public void testStringRepresentations_Root() throws Exception {
        generateFilesToTestStrings();
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:check");
        for (String suffix : SkylarkStringRepresentationsTest.SUFFIXES) {
            assertThat(target.get(("source_root" + suffix))).isEqualTo("<source root>");
            assertThat(target.get(("generated_root" + suffix))).isEqualTo("<derived root>");
        }
    }

    @Test
    public void testStringRepresentations_Glob() throws Exception {
        scratch.file("eval/one.txt");
        scratch.file("eval/two.txt");
        scratch.file("eval/three.txt");
        assertStringRepresentationInBuildFile("glob(['*.txt'])", "[\"one.txt\", \"three.txt\", \"two.txt\"]");
    }

    @Test
    public void testStringRepresentations_Attr() throws Exception {
        assertStringRepresentation("attr", "<attr>");
        assertStringRepresentation("attr.int()", "<attr.int>");
        assertStringRepresentation("attr.string()", "<attr.string>");
        assertStringRepresentation("attr.label()", "<attr.label>");
        assertStringRepresentation("attr.string_list()", "<attr.string_list>");
        assertStringRepresentation("attr.int_list()", "<attr.int_list>");
        assertStringRepresentation("attr.label_list()", "<attr.label_list>");
        assertStringRepresentation("attr.label_keyed_string_dict()", "<attr.label_keyed_string_dict>");
        assertStringRepresentation("attr.bool()", "<attr.bool>");
        assertStringRepresentation("attr.output()", "<attr.output>");
        assertStringRepresentation("attr.output_list()", "<attr.output_list>");
        assertStringRepresentation("attr.string_dict()", "<attr.string_dict>");
        assertStringRepresentation("attr.string_list_dict()", "<attr.string_list_dict>");
        assertStringRepresentation("attr.license()", "<attr.license>");
    }

    @Test
    public void testStringRepresentations_Targets() throws Exception {
        generateFilesToTestStrings();
        ConfiguredTarget target = getConfiguredTarget("//test/skylark:check");
        for (String suffix : SkylarkStringRepresentationsTest.SUFFIXES) {
            assertThat(target.get(("target" + suffix))).isEqualTo("<target //test/skylark:foo>");
            assertThat(target.get(("input_target" + suffix))).isEqualTo("<input file target //test/skylark:input.txt>");
            assertThat(target.get(("output_target" + suffix))).isEqualTo("<output file target //test/skylark:output.txt>");
            assertThat(target.get(("alias_target" + suffix))).isEqualTo("<alias target //test/skylark:foobar of //test/skylark:foo>");
            assertThat(target.get(("aspect_target" + suffix))).isEqualTo("<merged target //test/skylark:bar>");
        }
    }

    @Test
    public void testStringRepresentationsOfUnknownObjects() throws Exception {
        update("mock", new Object());
        assertThat(eval("str(mock)")).isEqualTo("<unknown object java.lang.Object>");
        assertThat(eval("repr(mock)")).isEqualTo("<unknown object java.lang.Object>");
        assertThat(eval("'{}'.format(mock)")).isEqualTo("<unknown object java.lang.Object>");
        assertThat(eval("'%s' % mock")).isEqualTo("<unknown object java.lang.Object>");
        assertThat(eval("'%r' % mock")).isEqualTo("<unknown object java.lang.Object>");
    }
}

