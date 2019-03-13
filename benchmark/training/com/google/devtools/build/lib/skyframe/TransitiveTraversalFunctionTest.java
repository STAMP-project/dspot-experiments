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
package com.google.devtools.build.lib.skyframe;


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.skyframe.TransitiveBaseTraversalFunction.TargetAndErrorIfAnyImpl;
import com.google.devtools.build.lib.util.GroupedList;
import com.google.devtools.build.lib.util.GroupedList.GroupedListHelper;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyKey;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Test for {@link TransitiveTraversalFunction}.
 */
@RunWith(JUnit4.class)
public class TransitiveTraversalFunctionTest extends BuildViewTestCase {
    @Test
    public void noRepeatedLabelVisitationForTransitiveTraversalFunction() throws Exception {
        // Create a basic package with a target //foo:foo.
        Label label = Label.parseAbsolute("//foo:foo", ImmutableMap.of());
        Package pkg = scratchPackage("workspace", label.getPackageIdentifier(), (("sh_library(name = '" + (label.getName())) + "')"));
        TargetAndErrorIfAnyImpl targetAndErrorIfAny = /* packageLoadedSuccessfully= */
        /* errorLoadingTarget= */
        new TargetAndErrorIfAnyImpl(true, null, pkg.getTarget(label.getName()));
        TransitiveTraversalFunction function = new TransitiveTraversalFunction() {
            @Override
            LoadTargetResults loadTarget(Environment env, Label label) {
                return targetAndErrorIfAny;
            }
        };
        // Create the GroupedList saying we had already requested two targets the last time we called
        // #compute.
        GroupedListHelper<SkyKey> helper = new GroupedListHelper();
        SkyKey fakeDep1 = function.getKey(Label.parseAbsolute("//foo:bar", ImmutableMap.of()));
        SkyKey fakeDep2 = function.getKey(Label.parseAbsolute("//foo:baz", ImmutableMap.of()));
        helper.add(TargetMarkerValue.key(label));
        helper.add(PackageValue.key(label.getPackageIdentifier()));
        helper.startGroup();
        // Note that these targets don't actually exist in the package we created initially. It doesn't
        // matter for the purpose of this test, the original package was just to create some objects
        // that we needed.
        helper.add(fakeDep1);
        helper.add(fakeDep2);
        helper.endGroup();
        GroupedList<SkyKey> groupedList = new GroupedList();
        groupedList.append(helper);
        AtomicBoolean wasOptimizationUsed = new AtomicBoolean(false);
        SkyFunction.Environment mockEnv = Mockito.mock(SkyFunction.Environment.class);
        Mockito.when(mockEnv.getTemporaryDirectDeps()).thenReturn(groupedList);
        Mockito.when(mockEnv.getValuesOrThrow(groupedList.get(2), NoSuchPackageException.class, NoSuchTargetException.class)).thenAnswer(( invocationOnMock) -> {
            wasOptimizationUsed.set(true);
            // It doesn't matter what this map is, we'll return false in the valuesMissing() call.
            return ImmutableMap.of();
        });
        Mockito.when(mockEnv.valuesMissing()).thenReturn(true);
        // Run the compute function and check that we returned null.
        assertThat(function.compute(function.getKey(label), mockEnv)).isNull();
        // Verify that the mock was called with the arguments we expected.
        assertThat(wasOptimizationUsed.get()).isTrue();
    }
}

