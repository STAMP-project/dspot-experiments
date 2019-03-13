/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis;


import BuildType.LABEL;
import BuildType.LABEL_LIST;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.packages.Rule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AspectAwareAttributeMapper}.
 */
@RunWith(JUnit4.class)
public class AspectAwareAttributeMapperTest extends BuildViewTestCase {
    private Rule rule;

    private ImmutableMap<String, Attribute> aspectAttributes;

    private AspectAwareAttributeMapper mapper;

    @Test
    public void getName() throws Exception {
        assertThat(mapper.getName()).isEqualTo(rule.getName());
    }

    @Test
    public void getLabel() throws Exception {
        assertThat(mapper.getLabel()).isEqualTo(rule.getLabel());
    }

    @Test
    public void getRuleAttributeValue() throws Exception {
        assertThat(mapper.get("srcs", LABEL_LIST)).containsExactly(Label.parseAbsolute("//foo:a.cc", ImmutableMap.of()));
    }

    @Test
    public void getAspectAttributeValue() throws Exception {
        try {
            mapper.get("fromaspect", LABEL);
            Assert.fail("Expected failure because value queries aren't supported for aspect attributes");
        } catch (UnsupportedOperationException e) {
            // Expected.
        }
    }

    @Test
    public void getAspectValueWrongType() throws Exception {
        try {
            mapper.get("fromaspect", LABEL_LIST);
            Assert.fail("Expected failure on wrong-typed attribute");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("attribute fromaspect has type label, not expected type list(label)");
        }
    }

    @Test
    public void getMissingAttributeValue() throws Exception {
        try {
            mapper.get("noexist", LABEL);
            Assert.fail("Expected failure on non-existent attribute");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("no attribute 'noexist' in either //foo:myrule or its aspects");
        }
    }

    @Test
    public void isConfigurable() throws Exception {
        assertThat(mapper.isConfigurable("linkstatic")).isTrue();
        assertThat(mapper.isConfigurable("fromaspect")).isFalse();
    }

    @Test
    public void getAttributeNames() throws Exception {
        assertThat(mapper.getAttributeNames()).containsAllOf("srcs", "linkstatic", "fromaspect");
    }

    @Test
    public void getAttributeType() throws Exception {
        assertThat(mapper.getAttributeType("srcs")).isEqualTo(LABEL_LIST);
        assertThat(mapper.getAttributeType("fromaspect")).isEqualTo(LABEL);
    }

    @Test
    public void getAttributeDefinition() throws Exception {
        assertThat(mapper.getAttributeDefinition("srcs").getName()).isEqualTo("srcs");
        assertThat(mapper.getAttributeDefinition("fromaspect").getName()).isEqualTo("fromaspect");
    }

    @Test
    public void has() throws Exception {
        assertThat(mapper.has("srcs")).isTrue();
        assertThat(mapper.has("fromaspect")).isTrue();
    }
}

