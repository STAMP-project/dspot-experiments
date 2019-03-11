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


import BuildType.LABEL;
import BuildType.LABEL_LIST;
import CppFileTypes.CPP_HEADER;
import Type.INTEGER;
import Type.STRING;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.docgen.testutil.TestData;
import com.google.devtools.build.lib.packages.Attribute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test class for RuleDocumentationAttribute.
 */
@RunWith(JUnit4.class)
public class RuleDocumentationAttributeTest {
    private static final ImmutableSet<String> NO_FLAGS = ImmutableSet.<String>of();

    @Test
    public void testDirectChild() {
        RuleDocumentationAttribute attr1 = RuleDocumentationAttribute.create(TestData.IntermediateRule.class, "", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        assertThat(attr1.getDefinitionClassAncestryLevel(TestData.TestRule.class, null)).isEqualTo(1);
    }

    @Test
    public void testTransitiveChild() {
        RuleDocumentationAttribute attr2 = RuleDocumentationAttribute.create(TestData.BaseRule.class, "", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        assertThat(attr2.getDefinitionClassAncestryLevel(TestData.TestRule.class, null)).isEqualTo(2);
    }

    @Test
    public void testClassIsNotChild() {
        RuleDocumentationAttribute attr2 = RuleDocumentationAttribute.create(TestData.IntermediateRule.class, "", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        assertThat(attr2.getDefinitionClassAncestryLevel(TestData.BaseRule.class, null)).isEqualTo((-1));
    }

    @Test
    public void testClassIsSame() {
        RuleDocumentationAttribute attr3 = RuleDocumentationAttribute.create(TestData.TestRule.class, "", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        assertThat(attr3.getDefinitionClassAncestryLevel(TestData.TestRule.class, null)).isEqualTo(0);
    }

    @Test
    public void testHasFlags() {
        RuleDocumentationAttribute attr = RuleDocumentationAttribute.create(TestData.TestRule.class, "", "", 0, "", ImmutableSet.<String>of("SOME_FLAG"));
        assertThat(attr.hasFlag("SOME_FLAG")).isTrue();
    }

    @Test
    public void testCompareTo() {
        assertThat(RuleDocumentationAttribute.create(TestData.TestRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS).compareTo(RuleDocumentationAttribute.create(TestData.TestRule.class, "b", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS))).isEqualTo((-1));
    }

    @Test
    public void testCompareToWithPriorityAttributeName() {
        assertThat(RuleDocumentationAttribute.create(TestData.TestRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS).compareTo(RuleDocumentationAttribute.create(TestData.TestRule.class, "name", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS))).isEqualTo(1);
    }

    @Test
    public void testEquals() {
        assertThat(RuleDocumentationAttribute.create(TestData.IntermediateRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS)).isEqualTo(RuleDocumentationAttribute.create(TestData.TestRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS));
    }

    @Test
    public void testHashCode() {
        assertThat(RuleDocumentationAttribute.create(TestData.IntermediateRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS).hashCode()).isEqualTo(RuleDocumentationAttribute.create(TestData.TestRule.class, "a", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS).hashCode());
    }

    @Test
    public void testSynopsisForStringAttribute() {
        final String defaultValue = "9";
        Attribute attribute = Attribute.attr("foo_version", STRING).value(defaultValue).build();
        RuleDocumentationAttribute attributeDoc = RuleDocumentationAttribute.create(TestData.TestRule.class, "testrule", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        attributeDoc.setAttribute(attribute);
        String doc = attributeDoc.getSynopsis();
        assertThat(doc).isEqualTo((("String; optional; default is \"" + defaultValue) + "\""));
    }

    @Test
    public void testSynopsisForIntegerAttribute() {
        final int defaultValue = 384;
        Attribute attribute = Attribute.attr("bar_limit", INTEGER).value(defaultValue).build();
        RuleDocumentationAttribute attributeDoc = RuleDocumentationAttribute.create(TestData.TestRule.class, "testrule", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        attributeDoc.setAttribute(attribute);
        String doc = attributeDoc.getSynopsis();
        assertThat(doc).isEqualTo(("Integer; optional; default is " + defaultValue));
    }

    @Test
    public void testSynopsisForLabelListAttribute() {
        Attribute attribute = Attribute.attr("some_labels", LABEL_LIST).allowedRuleClasses("foo_rule").allowedFileTypes().build();
        RuleDocumentationAttribute attributeDoc = RuleDocumentationAttribute.create(TestData.TestRule.class, "testrule", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        attributeDoc.setAttribute(attribute);
        String doc = attributeDoc.getSynopsis();
        assertThat(doc).isEqualTo("List of <a href=\"../build-ref.html#labels\">labels</a>; optional");
    }

    @Test
    public void testSynopsisForMandatoryAttribute() {
        Attribute attribute = Attribute.attr("baz_labels", LABEL).mandatory().allowedFileTypes(CPP_HEADER).build();
        RuleDocumentationAttribute attributeDoc = RuleDocumentationAttribute.create(TestData.TestRule.class, "testrule", "", 0, "", RuleDocumentationAttributeTest.NO_FLAGS);
        attributeDoc.setAttribute(attribute);
        String doc = attributeDoc.getSynopsis();
        assertThat(doc).isEqualTo("<a href=\"../build-ref.html#labels\">Label</a>; required");
    }
}

