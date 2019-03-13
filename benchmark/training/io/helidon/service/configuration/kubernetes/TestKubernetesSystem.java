/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.service.configuration.kubernetes;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestKubernetesSystem {
    public TestKubernetesSystem() {
        super();
    }

    @Test
    public void testEnabled() throws IOException {
        final KubernetesSystem system = new KubernetesSystem();
        final Path proc1CpuSet = Paths.get("/proc/1/cpuset");
        if (Files.exists(proc1CpuSet)) {
            final String singleLine = Files.lines(proc1CpuSet).findAny().get();
            Assertions.assertNotNull(singleLine);
            if (singleLine.startsWith("/kubepods/")) {
                Assertions.assertTrue(system.isEnabled());
            } else {
                Assertions.assertFalse(system.isEnabled());
            }
        } else {
            Assertions.assertFalse(system.isEnabled());
        }
    }
}

