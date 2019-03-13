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
package com.google.devtools.build.lib.vfs;


import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests different {@link DigestHashFunction} for consistency between the MessageDigests and the
 * HashFunctions that it exposes.
 */
@RunWith(Parameterized.class)
public class DigestHashFunctionsTest {
    @Parameterized.Parameter
    public DigestHashFunction digestHashFunction;

    @Test
    public void emptyDigestIsConsistent() {
        assertHashFunctionAndMessageDigestEquivalentForInput(new byte[]{  });
    }

    @Test
    public void shortDigestIsConsistent() {
        assertHashFunctionAndMessageDigestEquivalentForInput("Bazel".getBytes(StandardCharsets.UTF_8));
    }
}

