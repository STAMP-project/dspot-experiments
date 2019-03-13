/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules;


import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration.Fragment;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.analysis.config.ConfigurationFragmentFactory;
import com.google.devtools.build.lib.analysis.config.FragmentOptions;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.LabelSyntaxException;
import com.google.devtools.build.lib.packages.Attribute.LabelLateBoundDefault;
import com.google.devtools.build.lib.packages.AttributeMap;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.rules.LateBoundAlias.CommonAliasRule;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.io.IOException;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that LateBoundAlias can resolve null actual reference.
 */
@RunWith(JUnit4.class)
public class LateBoundAliasTest extends BuildViewTestCase {
    private static final class TestFragment extends Fragment {}

    private static final class TestFragmentOptionFactory implements ConfigurationFragmentFactory {
        @Override
        public Class<? extends Fragment> creates() {
            return LateBoundAliasTest.TestFragment.class;
        }

        @Override
        public ImmutableSet<Class<? extends FragmentOptions>> requiredOptions() {
            return ImmutableSet.of();
        }

        @Nullable
        @Override
        public Fragment create(BuildOptions buildOptions) {
            return new LateBoundAliasTest.TestFragment();
        }
    }

    private static final class TestLateBoundDefault extends LabelLateBoundDefault<LateBoundAliasTest.TestFragment> {
        private TestLateBoundDefault() {
            super(false, LateBoundAliasTest.TestFragment.class, null, null);
        }

        @Override
        public Label resolve(Rule rule, AttributeMap attributes, LateBoundAliasTest.TestFragment input) {
            return null;
        }
    }

    private static final class MyTestRule extends CommonAliasRule<LateBoundAliasTest.TestFragment> {
        public MyTestRule() {
            super("test_rule_name", ( env) -> new com.google.devtools.build.lib.rules.TestLateBoundDefault(), LateBoundAliasTest.TestFragment.class);
        }
    }

    @Test
    public void testResolveNullTarget() throws LabelSyntaxException, IOException {
        scratch.file("a/BUILD", "test_rule_name(name='alias')");
        ConfiguredTarget alias = getConfiguredTarget("//a:alias");
        assertThat(alias).isNotNull();
    }

    @Test
    public void testNullTargetCanBeDependant() throws LabelSyntaxException, IOException {
        scratch.file("a/BUILD", "test_rule_name(name='alias')", "filegroup(", "    name = 'my_filegroup',", "    srcs = [':alias']", ")");
        ConfiguredTarget myFilegroup = getConfiguredTarget("//a:my_filegroup");
        assertThat(myFilegroup).isNotNull();
    }
}

