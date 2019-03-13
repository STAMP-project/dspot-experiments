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
package com.google.devtools.build.lib.runtime.commands;


import BuildConfiguration.Options;
import ConfiguredRuleClassProvider.Builder;
import TestConfiguration.TestOptions;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.analysis.ServerDirectories;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.bazel.rules.DefaultBuildOptionsForDiffing;
import com.google.devtools.build.lib.runtime.BlazeCommandDispatcher;
import com.google.devtools.build.lib.runtime.BlazeModule;
import com.google.devtools.build.lib.runtime.BlazeRuntime;
import com.google.devtools.build.lib.runtime.BlazeServerStartupOptions;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.testutil.TestConstants;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.util.io.RecordingOutErr;
import com.google.devtools.common.options.OptionsParser;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests {@link CleanCommand}'s recommendation of the --async flag.
 */
@RunWith(Parameterized.class)
public class CleanCommandRecommendsAsyncTest {
    private final RecordingOutErr outErr = new RecordingOutErr();

    private final List<String> commandLine;

    private final OS os;

    private final boolean expectSuggestion;

    private static final String EXPECTED_SUGGESTION = "Consider using --async";

    public CleanCommandRecommendsAsyncTest(List<String> commandLine, OS os, boolean expectSuggestion) throws Exception {
        this.commandLine = commandLine;
        this.os = os;
        this.expectSuggestion = expectSuggestion;
    }

    @Test
    public void testCleanProvidesExpectedSuggestion() throws Exception {
        String productName = TestConstants.PRODUCT_NAME;
        Scratch scratch = new Scratch();
        ServerDirectories serverDirectories = new ServerDirectories(scratch.dir("install"), scratch.dir("output"), scratch.dir("user_root"));
        BlazeRuntime runtime = new BlazeRuntime.Builder().setFileSystem(scratch.getFileSystem()).setProductName(productName).setServerDirectories(serverDirectories).setStartupOptionsProvider(OptionsParser.newOptionsParser(BlazeServerStartupOptions.class)).addBlazeModule(new BlazeModule() {
            @Override
            public void initializeRuleClasses(ConfiguredRuleClassProvider.Builder builder) {
                // We must add these options so that the defaults package can be created.
                builder.addConfigurationOptions(Options.class);
                builder.addConfigurationOptions(TestOptions.class);
                // The tools repository is needed for createGlobals
                builder.setToolsRepository(TestConstants.TOOLS_REPOSITORY);
            }
        }).addBlazeModule(new BlazeModule() {
            @Override
            public BuildOptions getDefaultBuildOptions(BlazeRuntime runtime) {
                return DefaultBuildOptionsForDiffing.getDefaultBuildOptionsForFragments(runtime.getRuleClassProvider().getConfigurationOptions());
            }
        }).build();
        BlazeDirectories directories = /* defaultSystemJavabase= */
        new BlazeDirectories(serverDirectories, scratch.dir("workspace"), null, productName);
        /* binTools= */
        runtime.initWorkspace(directories, null);
        BlazeCommandDispatcher dispatcher = new BlazeCommandDispatcher(runtime, new CleanCommand(os));
        dispatcher.exec(commandLine, "test", outErr);
        String output = outErr.toString();
        if (expectSuggestion) {
            assertThat(output).contains(CleanCommandRecommendsAsyncTest.EXPECTED_SUGGESTION);
        } else {
            assertThat(output).doesNotContain(CleanCommandRecommendsAsyncTest.EXPECTED_SUGGESTION);
        }
    }
}

