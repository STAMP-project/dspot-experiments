/**
 * Copyright (C) 2014 simpligility technologies inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.simpligility.maven.plugins.android.sample;


import com.simpligility.maven.plugins.android.PluginInfo;
import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({ "3.3.9" })
public class Support4DemosSampleIT {
    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime mavenRuntime;

    public Support4DemosSampleIT(MavenRuntimeBuilder builder) throws Exception {
        this.mavenRuntime = builder.build();
    }

    @Test
    public void buildDeployAndRun() throws Exception {
        File basedir = resources.getBasedir("support4demos");
        MavenExecutionResult result = mavenRuntime.forProject(basedir).execute("clean", PluginInfo.getQualifiedGoal("undeploy"), "install", PluginInfo.getQualifiedGoal("deploy"), PluginInfo.getQualifiedGoal("run"));
        result.assertErrorFreeLog();
        result.assertLogText("Successfully installed");
        result.assertLogText("Attempting to start com.example.android.supportv4/com.example.android.supportv4.Support4Demos");
    }
}

