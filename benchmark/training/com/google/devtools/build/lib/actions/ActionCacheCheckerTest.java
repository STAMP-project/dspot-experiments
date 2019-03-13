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
package com.google.devtools.build.lib.actions;


import MissReason.DIFFERENT_ACTION_KEY;
import MissReason.DIFFERENT_DEPS;
import MissReason.DIFFERENT_ENVIRONMENT;
import MissReason.DIFFERENT_FILES;
import MissReason.NOT_CACHED;
import MissReason.UNCONDITIONAL_EXECUTION;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.cache.CompactPersistentActionCache;
import com.google.devtools.build.lib.actions.cache.Md5Digest;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.clock.Clock;
import com.google.devtools.build.lib.util.Fingerprint;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ActionCache.Entry.CORRUPTED;
import static MiddlemanType.RUNFILES_MIDDLEMAN;


@RunWith(JUnit4.class)
public class ActionCacheCheckerTest {
    private ActionCacheCheckerTest.CorruptibleCompactPersistentActionCache cache;

    private ActionCacheChecker cacheChecker;

    private Set<Path> filesToDelete;

    @Test
    public void testNoActivity() throws Exception {
        assertStatistics(0, new ActionsTestUtil.MissDetailsBuilder().build());
    }

    @Test
    public void testNotCached() throws Exception {
        doTestNotCached(new ActionsTestUtil.NullAction(), NOT_CACHED);
    }

    @Test
    public void testCached() throws Exception {
        doTestCached(new ActionsTestUtil.NullAction(), NOT_CACHED);
    }

    @Test
    public void testCorruptedCacheEntry() throws Exception {
        doTestCorruptedCacheEntry(new ActionsTestUtil.NullAction());
    }

    @Test
    public void testDifferentActionKey() throws Exception {
        Action action = new ActionsTestUtil.NullAction() {
            @Override
            protected void computeKey(ActionKeyContext actionKeyContext, Fingerprint fp) {
                fp.addString("key1");
            }
        };
        runAction(action);
        action = new ActionsTestUtil.NullAction() {
            @Override
            protected void computeKey(ActionKeyContext actionKeyContext, Fingerprint fp) {
                fp.addString("key2");
            }
        };
        runAction(action);
        assertStatistics(0, new ActionsTestUtil.MissDetailsBuilder().set(DIFFERENT_ACTION_KEY, 1).set(NOT_CACHED, 1).build());
    }

    @Test
    public void testDifferentEnvironment() throws Exception {
        Action action = new ActionsTestUtil.NullAction() {
            @Override
            public Iterable<String> getClientEnvironmentVariables() {
                return ImmutableList.of("used-var");
            }
        };
        Map<String, String> clientEnv = new HashMap<>();
        clientEnv.put("unused-var", "1");
        runAction(action, clientEnv);// Not cached.

        clientEnv.remove("unused-var");
        runAction(action, clientEnv);// Cache hit because we only modified uninteresting variables.

        clientEnv.put("used-var", "2");
        runAction(action, clientEnv);// Cache miss because of different environment.

        runAction(action, clientEnv);// Cache hit because we did not change anything.

        assertStatistics(2, new ActionsTestUtil.MissDetailsBuilder().set(DIFFERENT_ENVIRONMENT, 1).set(NOT_CACHED, 1).build());
    }

    @Test
    public void testDifferentFiles() throws Exception {
        Action action = new ActionsTestUtil.NullAction();
        runAction(action);// Not cached.

        FileSystemUtils.writeContentAsLatin1(action.getPrimaryOutput().getPath(), "modified");
        runAction(action);// Cache miss because output files were modified.

        assertStatistics(0, new ActionsTestUtil.MissDetailsBuilder().set(DIFFERENT_FILES, 1).set(NOT_CACHED, 1).build());
    }

    @Test
    public void testUnconditionalExecution() throws Exception {
        Action action = new ActionsTestUtil.NullAction() {
            @Override
            public boolean executeUnconditionally() {
                return true;
            }

            @Override
            public boolean isVolatile() {
                return true;
            }
        };
        int runs = 5;
        for (int i = 0; i < runs; i++) {
            runAction(action);
        }
        assertStatistics(0, new ActionsTestUtil.MissDetailsBuilder().set(UNCONDITIONAL_EXECUTION, runs).build());
    }

    @Test
    public void testMiddleman_NotCached() throws Exception {
        doTestNotCached(new ActionCacheCheckerTest.NullMiddlemanAction(), DIFFERENT_DEPS);
    }

    @Test
    public void testMiddleman_Cached() throws Exception {
        doTestCached(new ActionCacheCheckerTest.NullMiddlemanAction(), DIFFERENT_DEPS);
    }

    @Test
    public void testMiddleman_CorruptedCacheEntry() throws Exception {
        doTestCorruptedCacheEntry(new ActionCacheCheckerTest.NullMiddlemanAction());
    }

    @Test
    public void testMiddleman_DifferentFiles() throws Exception {
        Action action = new ActionCacheCheckerTest.NullMiddlemanAction() {
            @Override
            public synchronized Iterable<Artifact> getInputs() {
                FileSystem fileSystem = getPrimaryOutput().getPath().getFileSystem();
                Path path = fileSystem.getPath("/input");
                ArtifactRoot root = ArtifactRoot.asSourceRoot(Root.fromPath(fileSystem.getPath("/")));
                return ImmutableList.of(new Artifact(path, root));
            }
        };
        runAction(action);// Not cached so recorded as different deps.

        FileSystemUtils.writeContentAsLatin1(action.getPrimaryInput().getPath(), "modified");
        runAction(action);// Cache miss because input files were modified.

        FileSystemUtils.writeContentAsLatin1(action.getPrimaryOutput().getPath(), "modified");
        runAction(action);// Outputs are not considered for middleman actions, so this is a cache hit.

        runAction(action);// Outputs are not considered for middleman actions, so this is a cache hit.

        assertStatistics(2, new ActionsTestUtil.MissDetailsBuilder().set(DIFFERENT_DEPS, 1).set(DIFFERENT_FILES, 1).build());
    }

    /**
     * A {@link CompactPersistentActionCache} that allows injecting corruption for testing.
     */
    private static class CorruptibleCompactPersistentActionCache extends CompactPersistentActionCache {
        private boolean corrupted = false;

        CorruptibleCompactPersistentActionCache(Path cacheRoot, Clock clock) throws IOException {
            super(cacheRoot, clock);
        }

        void corruptAllEntries() {
            corrupted = true;
        }

        @Override
        public Entry get(String key) {
            if (corrupted) {
                return CORRUPTED;
            } else {
                return super.get(key);
            }
        }
    }

    /**
     * A null middleman action.
     */
    private static class NullMiddlemanAction extends ActionsTestUtil.NullAction {
        @Override
        public MiddlemanType getActionType() {
            return RUNFILES_MIDDLEMAN;
        }
    }

    /**
     * A fake metadata handler that is able to obtain metadata from the file system.
     */
    private static class FakeMetadataHandler extends ActionsTestUtil.FakeMetadataHandlerBase {
        @Override
        public FileArtifactValue getMetadata(ActionInput input) throws IOException {
            if (!(input instanceof Artifact)) {
                return null;
            }
            return FileArtifactValue.create(((Artifact) (input)));
        }

        @Override
        public void setDigestForVirtualArtifact(Artifact artifact, Md5Digest md5Digest) {
        }
    }
}

