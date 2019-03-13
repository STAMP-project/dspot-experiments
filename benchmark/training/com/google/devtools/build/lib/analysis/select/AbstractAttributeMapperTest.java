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
package com.google.devtools.build.lib.analysis.select;


import BuildType.LABEL_LIST;
import Type.BOOLEAN;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.AbstractAttributeMapper;
import com.google.devtools.build.lib.packages.AttributeContainer;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.packages.RuleClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AbstractAttributeMapper}.
 */
@RunWith(JUnit4.class)
public class AbstractAttributeMapperTest extends BuildViewTestCase {
    protected Rule rule;

    protected AbstractAttributeMapper mapper;

    private static class TestMapper extends AbstractAttributeMapper {
        public TestMapper(Package pkg, RuleClass ruleClass, Label ruleLabel, AttributeContainer attributes) {
            super(pkg, ruleClass, ruleLabel, attributes);
        }
    }

    @Test
    public void testRuleProperties() throws Exception {
        assertThat(mapper.getName()).isEqualTo(rule.getName());
        assertThat(mapper.getLabel()).isEqualTo(rule.getLabel());
    }

    @Test
    public void testPackageDefaultProperties() throws Exception {
        rule = scratchRule("a", "myrule", "cc_binary(name = 'myrule',", "          srcs = ['a', 'b', 'c'])");
        Package pkg = rule.getPackage();
        assertThat(mapper.getPackageDefaultHdrsCheck()).isEqualTo(getDefaultHdrsCheck());
        assertThat(mapper.getPackageDefaultTestOnly()).isEqualTo(getDefaultTestOnly());
        assertThat(mapper.getPackageDefaultDeprecation()).isEqualTo(getDefaultDeprecation());
    }

    @Test
    public void testAttributeTypeChecking() throws Exception {
        // Good typing:
        mapper.get("srcs", LABEL_LIST);
        // Bad typing:
        try {
            mapper.get("srcs", BOOLEAN);
            Assert.fail("Expected type mismatch to trigger an exception");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        // Unknown attribute:
        try {
            mapper.get("nonsense", BOOLEAN);
            Assert.fail("Expected non-existent type to trigger an exception");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testGetAttributeType() throws Exception {
        assertThat(mapper.getAttributeType("srcs")).isEqualTo(LABEL_LIST);
        assertThat(mapper.getAttributeType("nonsense")).isNull();
    }

    @Test
    public void testGetAttributeDefinition() {
        assertThat(mapper.getAttributeDefinition("srcs").getName()).isEqualTo("srcs");
        assertThat(mapper.getAttributeDefinition("nonsense")).isNull();
    }

    @Test
    public void testIsAttributeExplicitlySpecified() throws Exception {
        assertThat(mapper.isAttributeValueExplicitlySpecified("srcs")).isTrue();
        assertThat(mapper.isAttributeValueExplicitlySpecified("deps")).isFalse();
        assertThat(mapper.isAttributeValueExplicitlySpecified("nonsense")).isFalse();
    }

    @Test
    public void testVisitation() throws Exception {
        assertThat(AbstractAttributeMapperTest.getLabelsForAttribute(mapper, "srcs")).containsExactly("//p:a", "//p:b", "//p:c");
    }
}

