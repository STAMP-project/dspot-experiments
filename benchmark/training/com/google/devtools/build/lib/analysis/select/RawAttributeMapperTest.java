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
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.RawAttributeMapper;
import com.google.devtools.build.lib.packages.Rule;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link RawAttributeMapper}.
 */
@RunWith(JUnit4.class)
public class RawAttributeMapperTest extends AbstractAttributeMapperTest {
    @Test
    public void testGetAttribute() throws Exception {
        RawAttributeMapper rawMapper = RawAttributeMapper.of(setupGenRule());
        List<Label> value = rawMapper.get("data", LABEL_LIST);
        assertThat(value).isNotNull();
        assertThat(value).containsExactly(Label.create("@//x", "data_a"), Label.create("@//x", "data_b"));
        // Configurable attribute: trying to directly access from a RawAttributeMapper throws a
        // type mismatch exception.
        try {
            rawMapper.get("srcs", LABEL_LIST);
            Assert.fail("Expected srcs lookup to fail since the returned type is a SelectorList and not a list");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasCauseThat().hasMessageThat().containsMatch(".*SelectorList cannot be cast to .*java\\.util\\.List");
        }
    }

    @Override
    @Test
    public void testGetAttributeType() throws Exception {
        RawAttributeMapper rawMapper = RawAttributeMapper.of(setupGenRule());
        assertThat(rawMapper.getAttributeType("data")).isEqualTo(LABEL_LIST);// not configurable

        assertThat(rawMapper.getAttributeType("srcs")).isEqualTo(LABEL_LIST);// configurable

    }

    @Test
    public void testConfigurabilityCheck() throws Exception {
        RawAttributeMapper rawMapper = RawAttributeMapper.of(setupGenRule());
        assertThat(rawMapper.isConfigurable("data")).isFalse();
        assertThat(rawMapper.isConfigurable("srcs")).isTrue();
    }

    /**
     * Tests that RawAttributeMapper can't handle label visitation with configurable attributes.
     */
    @Test
    public void testVisitLabels() throws Exception {
        RawAttributeMapper rawMapper = RawAttributeMapper.of(setupGenRule());
        try {
            rawMapper.visitLabels();
            Assert.fail("Expected label visitation to fail since one attribute is configurable");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasCauseThat().hasMessageThat().containsMatch(".*SelectorList cannot be cast to .*java\\.util\\.List");
        }
    }

    @Test
    public void testGetConfigurabilityKeys() throws Exception {
        RawAttributeMapper rawMapper = RawAttributeMapper.of(setupGenRule());
        assertThat(rawMapper.getConfigurabilityKeys("srcs", LABEL_LIST)).containsExactly(Label.parseAbsolute("//conditions:a", ImmutableMap.of()), Label.parseAbsolute("//conditions:b", ImmutableMap.of()), Label.parseAbsolute("//conditions:default", ImmutableMap.of()));
        assertThat(rawMapper.getConfigurabilityKeys("data", LABEL_LIST)).isEmpty();
    }

    @Test
    public void testGetMergedValues() throws Exception {
        Rule rule = scratchRule("x", "myrule", "sh_binary(", "    name = 'myrule',", "    srcs = select({", "        '//conditions:a': ['a.sh', 'b.sh'],", "        '//conditions:b': ['b.sh', 'c.sh'],", "    }))");
        RawAttributeMapper rawMapper = RawAttributeMapper.of(rule);
        assertThat(rawMapper.getMergedValues("srcs", LABEL_LIST)).containsExactly(Label.parseAbsolute("//x:a.sh", ImmutableMap.of()), Label.parseAbsolute("//x:b.sh", ImmutableMap.of()), Label.parseAbsolute("//x:c.sh", ImmutableMap.of())).inOrder();
    }

    @Test
    public void testMergedValuesWithConcatenatedSelects() throws Exception {
        Rule rule = scratchRule("x", "myrule", "sh_binary(", "    name = 'myrule',", "    srcs = select({", "            '//conditions:a1': ['a1.sh'],", "            '//conditions:b1': ['b1.sh', 'another_b1.sh']})", "        + select({", "            '//conditions:a2': ['a2.sh'],", "            '//conditions:b2': ['b2.sh']})", "    )");
        RawAttributeMapper rawMapper = RawAttributeMapper.of(rule);
        assertThat(rawMapper.getMergedValues("srcs", LABEL_LIST)).containsExactly(Label.parseAbsolute("//x:a1.sh", ImmutableMap.of()), Label.parseAbsolute("//x:b1.sh", ImmutableMap.of()), Label.parseAbsolute("//x:another_b1.sh", ImmutableMap.of()), Label.parseAbsolute("//x:a2.sh", ImmutableMap.of()), Label.parseAbsolute("//x:b2.sh", ImmutableMap.of())).inOrder();
    }
}

