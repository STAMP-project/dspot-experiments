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
package com.google.devtools.build.lib.analysis.platform;


import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link ConstraintValueInfo}.
 */
@RunWith(JUnit4.class)
public class ConstraintValueInfoTest extends BuildViewTestCase {
    @Test
    public void constraintValue_equalsTester() {
        ConstraintSettingInfo setting1 = ConstraintSettingInfo.create(BuildViewTestCase.makeLabel("//constraint:basic"));
        ConstraintSettingInfo setting2 = ConstraintSettingInfo.create(BuildViewTestCase.makeLabel("//constraint:other"));
        // Different setting.
        // Different label.
        // Base case.
        new EqualsTester().addEqualityGroup(ConstraintValueInfo.create(setting1, BuildViewTestCase.makeLabel("//constraint:value")), ConstraintValueInfo.create(setting1, BuildViewTestCase.makeLabel("//constraint:value"))).addEqualityGroup(ConstraintValueInfo.create(setting1, BuildViewTestCase.makeLabel("//constraint:otherValue"))).addEqualityGroup(ConstraintValueInfo.create(setting2, BuildViewTestCase.makeLabel("//constraint:otherValue"))).testEquals();
    }
}

