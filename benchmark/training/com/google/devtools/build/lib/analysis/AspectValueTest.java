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
package com.google.devtools.build.lib.analysis;


import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.analysis.util.TestAspects;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.AspectParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.skyframe.AspectValue}.
 */
@RunWith(JUnit4.class)
public class AspectValueTest extends AnalysisTestCase {
    @Test
    public void keyEquality() throws Exception {
        update();
        BuildConfiguration c1 = getTargetConfiguration();
        BuildConfiguration c2 = getHostConfiguration();
        Label l1 = Label.parseAbsolute("//a:l1", ImmutableMap.of());
        Label l1b = Label.parseAbsolute("//a:l1", ImmutableMap.of());
        Label l2 = Label.parseAbsolute("//a:l2", ImmutableMap.of());
        AspectParameters i1 = new AspectParameters.Builder().addAttribute("foo", "bar").build();
        AspectParameters i1b = new AspectParameters.Builder().addAttribute("foo", "bar").build();
        AspectParameters i2 = new AspectParameters.Builder().addAttribute("foo", "baz").build();
        TestAspects.AttributeAspect a1 = TestAspects.ATTRIBUTE_ASPECT;
        TestAspects.AttributeAspect a1b = TestAspects.ATTRIBUTE_ASPECT;
        TestAspects.ExtraAttributeAspect a2 = TestAspects.EXTRA_ATTRIBUTE_ASPECT;
        // label: //a:l1 or //a:l2
        // aspectConfiguration: target or host
        // baseConfiguration: target or host
        // aspect: Attribute or ExtraAttribute
        // parameters: bar or baz
        new EqualsTester().addEqualityGroup(AspectValueTest.createKey(l1, c1, a1, i1, c1), AspectValueTest.createKey(l1, c1, a1, i1b, c1), AspectValueTest.createKey(l1, c1, a1b, i1, c1), AspectValueTest.createKey(l1, c1, a1b, i1b, c1), AspectValueTest.createKey(l1b, c1, a1, i1, c1), AspectValueTest.createKey(l1b, c1, a1, i1b, c1), AspectValueTest.createKey(l1b, c1, a1b, i1, c1), AspectValueTest.createKey(l1b, c1, a1b, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a1, i2, c1), AspectValueTest.createKey(l1, c1, a1b, i2, c1), AspectValueTest.createKey(l1b, c1, a1, i2, c1), AspectValueTest.createKey(l1b, c1, a1b, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a2, i1, c1), AspectValueTest.createKey(l1, c1, a2, i1b, c1), AspectValueTest.createKey(l1b, c1, a2, i1, c1), AspectValueTest.createKey(l1b, c1, a2, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a2, i2, c1), AspectValueTest.createKey(l1b, c1, a2, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a1, i1, c1), AspectValueTest.createKey(l1, c2, a1, i1b, c1), AspectValueTest.createKey(l1, c2, a1b, i1, c1), AspectValueTest.createKey(l1, c2, a1b, i1b, c1), AspectValueTest.createKey(l1b, c2, a1, i1, c1), AspectValueTest.createKey(l1b, c2, a1, i1b, c1), AspectValueTest.createKey(l1b, c2, a1b, i1, c1), AspectValueTest.createKey(l1b, c2, a1b, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a1, i2, c1), AspectValueTest.createKey(l1, c2, a1b, i2, c1), AspectValueTest.createKey(l1b, c2, a1, i2, c1), AspectValueTest.createKey(l1b, c2, a1b, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a2, i1, c1), AspectValueTest.createKey(l1, c2, a2, i1b, c1), AspectValueTest.createKey(l1b, c2, a2, i1, c1), AspectValueTest.createKey(l1b, c2, a2, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a2, i2, c1), AspectValueTest.createKey(l1b, c2, a2, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a1, i1, c2), AspectValueTest.createKey(l1, c1, a1, i1b, c2), AspectValueTest.createKey(l1, c1, a1b, i1, c2), AspectValueTest.createKey(l1, c1, a1b, i1b, c2), AspectValueTest.createKey(l1b, c1, a1, i1, c2), AspectValueTest.createKey(l1b, c1, a1, i1b, c2), AspectValueTest.createKey(l1b, c1, a1b, i1, c2), AspectValueTest.createKey(l1b, c1, a1b, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a1, i2, c2), AspectValueTest.createKey(l1, c1, a1b, i2, c2), AspectValueTest.createKey(l1b, c1, a1, i2, c2), AspectValueTest.createKey(l1b, c1, a1b, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a2, i1, c2), AspectValueTest.createKey(l1, c1, a2, i1b, c2), AspectValueTest.createKey(l1b, c1, a2, i1, c2), AspectValueTest.createKey(l1b, c1, a2, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c1, a2, i2, c2), AspectValueTest.createKey(l1b, c1, a2, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a1, i1, c2), AspectValueTest.createKey(l1, c2, a1, i1b, c2), AspectValueTest.createKey(l1, c2, a1b, i1, c2), AspectValueTest.createKey(l1, c2, a1b, i1b, c2), AspectValueTest.createKey(l1b, c2, a1, i1, c2), AspectValueTest.createKey(l1b, c2, a1, i1b, c2), AspectValueTest.createKey(l1b, c2, a1b, i1, c2), AspectValueTest.createKey(l1b, c2, a1b, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a1, i2, c2), AspectValueTest.createKey(l1, c2, a1b, i2, c2), AspectValueTest.createKey(l1b, c2, a1, i2, c2), AspectValueTest.createKey(l1b, c2, a1b, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a2, i1, c2), AspectValueTest.createKey(l1, c2, a2, i1b, c2), AspectValueTest.createKey(l1b, c2, a2, i1, c2), AspectValueTest.createKey(l1b, c2, a2, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l1, c2, a2, i2, c2), AspectValueTest.createKey(l1b, c2, a2, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a1, i1, c1), AspectValueTest.createKey(l2, c1, a1, i1b, c1), AspectValueTest.createKey(l2, c1, a1b, i1, c1), AspectValueTest.createKey(l2, c1, a1b, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a1, i2, c1), AspectValueTest.createKey(l2, c1, a1b, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a2, i1, c1), AspectValueTest.createKey(l2, c1, a2, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a2, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a1, i1, c1), AspectValueTest.createKey(l2, c2, a1, i1b, c1), AspectValueTest.createKey(l2, c2, a1b, i1, c1), AspectValueTest.createKey(l2, c2, a1b, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a1, i2, c1), AspectValueTest.createKey(l2, c2, a1b, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a2, i1, c1), AspectValueTest.createKey(l2, c2, a2, i1b, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a2, i2, c1)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a1, i1, c2), AspectValueTest.createKey(l2, c1, a1, i1b, c2), AspectValueTest.createKey(l2, c1, a1b, i1, c2), AspectValueTest.createKey(l2, c1, a1b, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a1, i2, c2), AspectValueTest.createKey(l2, c1, a1b, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a2, i1, c2), AspectValueTest.createKey(l2, c1, a2, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c1, a2, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a1, i1, c2), AspectValueTest.createKey(l2, c2, a1, i1b, c2), AspectValueTest.createKey(l2, c2, a1b, i1, c2), AspectValueTest.createKey(l2, c2, a1b, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a1, i2, c2), AspectValueTest.createKey(l2, c2, a1b, i2, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a2, i1, c2), AspectValueTest.createKey(l2, c2, a2, i1b, c2)).addEqualityGroup(AspectValueTest.createKey(l2, c2, a2, i2, c2)).addEqualityGroup(AspectValueTest.createDerivedKey(l1, c1, a1, i1, c1, a2, i2, c2), AspectValueTest.createDerivedKey(l1, c1, a1, i1b, c1, a2, i2, c2)).addEqualityGroup(AspectValueTest.createDerivedKey(l1, c1, a2, i1, c1, a1, i2, c2), AspectValueTest.createDerivedKey(l1, c1, a2, i1b, c1, a1, i2, c2)).testEquals();
    }
}

