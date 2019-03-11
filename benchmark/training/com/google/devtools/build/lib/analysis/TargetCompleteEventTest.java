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
package com.google.devtools.build.lib.analysis;


import ArtifactPathResolver.IDENTITY;
import RepositoryName.DEFAULT;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.TopLevelArtifactHelper.ArtifactsToBuild;
import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.buildeventstream.BuildEvent.LocalFile.LocalFileType;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OutputGroupInfo.DEFAULT_GROUPS;


/**
 * Tests for {@link TargetCompleteEvent}.
 */
@RunWith(JUnit4.class)
public class TargetCompleteEventTest extends AnalysisTestCase {
    /**
     * Regression test for b/111653523.
     */
    @Test
    public void testReferencedLocalFilesIncludesBaselineCoverage() throws Exception {
        scratch.file("java/a/BUILD", "java_test(name = 'Example', srcs = ['Example.java'])");
        useConfiguration("--collect_code_coverage");
        AnalysisResult result = update("//java/a:Example");
        ConfiguredTarget ct = Iterables.getOnlyElement(result.getTargetsToBuild());
        TargetAndConfiguration tac = Iterables.getOnlyElement(result.getTopLevelTargetsWithConfigs());
        ConfiguredTargetAndData ctAndData = new ConfiguredTargetAndData(ct, tac.getTarget(), tac.getConfiguration());
        TopLevelArtifactContext context = new TopLevelArtifactContext(false, DEFAULT_GROUPS);
        ArtifactsToBuild artifactsToBuild = TopLevelArtifactHelper.getAllArtifactsToBuild(ct, context);
        TargetCompleteEvent event = TargetCompleteEvent.successfulBuild(ctAndData, IDENTITY, artifactsToBuild.getAllArtifactsByOutputGroup());
        assertThat(event.referencedLocalFiles()).contains(new com.google.devtools.build.lib.buildeventstream.BuildEvent.LocalFile(tac.getConfiguration().getTestLogsDirectory(DEFAULT).getRoot().asPath().getRelative("java/a/Example/baseline_coverage.dat"), LocalFileType.OUTPUT));
    }
}

