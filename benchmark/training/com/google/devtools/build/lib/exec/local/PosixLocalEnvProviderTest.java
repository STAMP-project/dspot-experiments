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
package com.google.devtools.build.lib.exec.local;


import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link PosixLocalEnvProvider}.
 */
@RunWith(JUnit4.class)
public final class PosixLocalEnvProviderTest {
    /**
     * Should use the client environment's TMPDIR envvar if specified.
     */
    @Test
    public void testRewriteEnvWithClientTmpdir() throws Exception {
        PosixLocalEnvProvider p = new PosixLocalEnvProvider(ImmutableMap.of("TMPDIR", "client-env/tmp"));
        assertThat(PosixLocalEnvProviderTest.rewriteEnv(p, ImmutableMap.of("key1", "value1"))).isEqualTo(ImmutableMap.of("key1", "value1", "TMPDIR", "client-env/tmp"));
        assertThat(PosixLocalEnvProviderTest.rewriteEnv(p, ImmutableMap.of("key1", "value1", "TMPDIR", "ignored"))).isEqualTo(ImmutableMap.of("key1", "value1", "TMPDIR", "client-env/tmp"));
    }

    /**
     * Should use the default temp dir when the client env doesn't define TMPDIR.
     */
    @Test
    public void testRewriteEnvWithDefaultTmpdir() throws Exception {
        PosixLocalEnvProvider p = new PosixLocalEnvProvider(ImmutableMap.<String, String>of());
        assertThat(PosixLocalEnvProviderTest.rewriteEnv(p, ImmutableMap.of("key1", "value1"))).isEqualTo(ImmutableMap.of("key1", "value1", "TMPDIR", "/tmp"));
        assertThat(PosixLocalEnvProviderTest.rewriteEnv(p, ImmutableMap.of("key1", "value1", "TMPDIR", "ignored"))).isEqualTo(ImmutableMap.of("key1", "value1", "TMPDIR", "/tmp"));
    }
}

