/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.docgen;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.docgen.testutil.TestData;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.testutil.TestRuleClassProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test class for RuleDocumentation.
 */
@RunWith(JUnit4.class)
public class RuleDocumentationTest {
    private static final ImmutableSet<String> NO_FLAGS = ImmutableSet.<String>of();

    private static final ConfiguredRuleClassProvider provider = TestRuleClassProvider.getRuleClassProvider();

    @Test
    public void testVariableSubstitution() throws BuildEncyclopediaDocException {
        RuleDocumentation ruleDoc = new RuleDocumentation("rule", "OTHER", "FOO", Joiner.on("\n").join(new String[]{ "x", "${VAR}", "z" }), 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider);
        ruleDoc.addDocVariable("VAR", "y");
        assertThat(ruleDoc.getHtmlDocumentation()).isEqualTo("x\ny\nz");
    }

    @Test
    public void testSignatureContainsCommonAttribute() throws Exception {
        RuleDocumentationAttribute licensesAttr = RuleDocumentationAttribute.create("licenses", "common", "attribute doc");
        checkAttributeForRule(new RuleDocumentation("java_binary", "BINARY", "JAVA", "", 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider), licensesAttr, true);
    }

    @Test
    public void testInheritedAttributeGeneratesSignature() throws Exception {
        RuleDocumentationAttribute runtimeDepsAttr = RuleDocumentationAttribute.create(TestData.TestRule.class, "runtime_deps", "attribute doc", 0, "", RuleDocumentationTest.NO_FLAGS);
        checkAttributeForRule(new RuleDocumentation("java_binary", "BINARY", "JAVA", "", 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider), runtimeDepsAttr, false);
        checkAttributeForRule(new RuleDocumentation("java_library", "LIBRARY", "JAVA", "", 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider), runtimeDepsAttr, false);
    }

    @Test
    public void testRuleDocFlagSubstitution() throws BuildEncyclopediaDocException {
        RuleDocumentation ruleDoc = new RuleDocumentation("rule", "OTHER", "FOO", "x", 0, "", ImmutableSet.<String>of("DEPRECATED"), RuleDocumentationTest.provider);
        ruleDoc.addDocVariable("VAR", "y");
        assertThat(ruleDoc.getHtmlDocumentation()).isEqualTo("x");
    }

    @Test
    public void testCommandLineDocumentation() throws BuildEncyclopediaDocException {
        RuleDocumentation ruleDoc = new RuleDocumentation("foo_binary", "OTHER", "FOO", Joiner.on("\n").join(new String[]{ "x", "y", "z", "${VAR}" }), 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider);
        ruleDoc.addDocVariable("VAR", "w");
        RuleDocumentationAttribute attributeDoc = RuleDocumentationAttribute.create(TestData.TestRule.class, "srcs", "attribute doc", 0, "", RuleDocumentationTest.NO_FLAGS);
        ruleDoc.addAttribute(attributeDoc);
        assertThat(ruleDoc.getCommandLineDocumentation()).isEqualTo("\nx\ny\nz\n\n");
    }

    @Test
    public void testExtractExamples() throws BuildEncyclopediaDocException {
        RuleDocumentation ruleDoc = new RuleDocumentation("rule", "OTHER", "FOO", Joiner.on("\n").join(new String[]{ "x", "<!-- #BLAZE_RULE.EXAMPLE -->", "a", "<!-- #BLAZE_RULE.END_EXAMPLE -->", "y", "<!-- #BLAZE_RULE.EXAMPLE -->", "b", "<!-- #BLAZE_RULE.END_EXAMPLE -->", "z" }), 0, "", ImmutableSet.<String>of(), RuleDocumentationTest.provider);
        assertThat(ruleDoc.extractExamples()).isEqualTo(ImmutableSet.<String>of("a\n", "b\n"));
    }

    @Test
    public void testCreateExceptions() throws BuildEncyclopediaDocException {
        RuleDocumentation ruleDoc = new RuleDocumentation("foo_binary", "OTHER", "FOO", "", 10, "foo.txt", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider);
        BuildEncyclopediaDocException e = ruleDoc.createException("msg");
        assertThat(e).hasMessageThat().isEqualTo("Error in foo.txt:10: msg");
    }

    @Test
    public void testEquals() throws BuildEncyclopediaDocException {
        assertThat(new RuleDocumentation("rule", "OTHER", "FOO", "y", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider)).isEqualTo(new RuleDocumentation("rule", "OTHER", "FOO", "x", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider));
    }

    @Test
    public void testNotEquals() throws BuildEncyclopediaDocException {
        assertThat(new RuleDocumentation("rule1", "OTHER", "FOO", "x", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider).equals(new RuleDocumentation("rule2", "OTHER", "FOO", "y", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider))).isFalse();
    }

    @Test
    public void testCompareTo() throws BuildEncyclopediaDocException {
        assertThat(compareTo(new RuleDocumentation("rule2", "OTHER", "FOO", "x", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider))).isEqualTo((-1));
    }

    @Test
    public void testHashCode() throws BuildEncyclopediaDocException {
        assertThat(new RuleDocumentation("rule", "OTHER", "FOO", "y", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider).hashCode()).isEqualTo(new RuleDocumentation("rule", "OTHER", "FOO", "x", 0, "", RuleDocumentationTest.NO_FLAGS, RuleDocumentationTest.provider).hashCode());
    }
}

