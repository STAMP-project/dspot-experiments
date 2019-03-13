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
package com.google.devtools.build.lib.standalone;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.ArtifactExpander;
import com.google.devtools.build.lib.actions.ExecException;
import com.google.devtools.build.lib.actions.ResourceSet;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnActionContext;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.events.PrintingEventHandler;
import com.google.devtools.build.lib.events.Reporter;
import com.google.devtools.build.lib.exec.BlazeExecutor;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.FileSystem;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test StandaloneSpawnStrategy.
 */
@RunWith(JUnit4.class)
public class StandaloneSpawnStrategyTest {
    private static final ArtifactExpander SIMPLE_ARTIFACT_EXPANDER = new ArtifactExpander() {
        @Override
        public void expand(Artifact artifact, Collection<? super Artifact> output) {
            output.add(artifact);
        }
    };

    private Reporter reporter = new Reporter(new EventBus(), PrintingEventHandler.ERRORS_AND_WARNINGS_TO_STDERR);

    private BlazeExecutor executor;

    private FileSystem fileSystem;

    private FileOutErr outErr;

    @Test
    public void testBinTrueExecutesFine() throws Exception {
        Spawn spawn = createSpawn(StandaloneSpawnStrategyTest.getTrueCommand());
        executor.getContext(SpawnActionContext.class).exec(spawn, createContext());
        assertThat(out()).isEmpty();
        assertThat(err()).isEmpty();
    }

    @Test
    public void testBinFalseYieldsException() throws Exception {
        try {
            run(createSpawn(StandaloneSpawnStrategyTest.getFalseCommand()));
            Assert.fail();
        } catch (ExecException e) {
            assertWithMessage(("got: " + (e.getMessage()))).that(e.getMessage().startsWith("false failed: error executing command")).isTrue();
        }
    }

    @Test
    public void testBinEchoPrintsArguments() throws Exception {
        Spawn spawn = createSpawn("/bin/echo", "Hello,", "world.");
        run(spawn);
        assertThat(out()).isEqualTo("Hello, world.\n");
        assertThat(err()).isEmpty();
    }

    @Test
    public void testCommandRunsInWorkingDir() throws Exception {
        Spawn spawn = createSpawn("/bin/pwd");
        run(spawn);
        assertThat(out()).isEqualTo(((executor.getExecRoot()) + "\n"));
    }

    @Test
    public void testCommandHonorsEnvironment() throws Exception {
        if ((OS.getCurrent()) == (OS.DARWIN)) {
            // // TODO(#)3795: For some reason, we get __CF_USER_TEXT_ENCODING into the env in some
            // configurations of MacOS machines. I have been unable to reproduce on my Mac, or to track
            // down where that env var is coming from.
            return;
        }
        Spawn spawn = /* environment= */
        /* executionInfo= */
        /* inputs= */
        /* outputs= */
        new com.google.devtools.build.lib.actions.SimpleSpawn(new ActionsTestUtil.NullAction(), ImmutableList.of("/usr/bin/env"), ImmutableMap.of("foo", "bar", "baz", "boo"), ImmutableMap.of(), ImmutableList.of(), ImmutableList.of(), ResourceSet.ZERO);
        run(spawn);
        assertThat(Sets.newHashSet(out().split("\n"))).isEqualTo(Sets.newHashSet("foo=bar", "baz=boo"));
    }

    @Test
    public void testStandardError() throws Exception {
        Spawn spawn = createSpawn("/bin/sh", "-c", "echo Oops! >&2");
        run(spawn);
        assertThat(err()).isEqualTo("Oops!\n");
        assertThat(out()).isEmpty();
    }
}

