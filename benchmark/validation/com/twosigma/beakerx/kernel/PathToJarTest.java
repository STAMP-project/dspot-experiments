/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.kernel;


import com.twosigma.beakerx.KernelExecutionTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class PathToJarTest {
    @Test
    public void create() throws Exception {
        // given
        // when
        PathToJar pathToJar = new PathToJar(KernelExecutionTest.DEMO_JAR);
        // then
        assertThat(pathToJar.getPath()).contains(KernelExecutionTest.DEMO_JAR_NAME);
    }

    @Test
    public void shouldThrowErrorIfPathDoesNotExist() throws Exception {
        // given
        // when
        try {
            new PathToJar("./invalidPath");
            fail("Should not create PathToJar with invalid path.");
        } catch (Exception e) {
            // then
            Assertions.assertThat(e.getMessage()).contains("Path does not exist");
        }
    }
}

