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
package com.twosigma.beakerx.scala.kernel;


import com.twosigma.beakerx.KernelExecutionTest;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.junit.Test;


public class ScalaKernelTest extends KernelExecutionTest {
    @Test
    public void inputOutputProblem() throws Exception {
        // given
        // when
        runInputOutputStatement("line 1", "first input");
        addDemoJar();
        runInputOutputStatement("line 3", "third input");
        runInputOutputStatement("line 4", "fourth input");
        // then
    }

    @Test
    public void noKeepVariablesWhenAddJar() throws Exception {
        // given
        // when
        runStatement("val aaa  = 1 ");
        addDemoJar();
        runStatement("aaa");
        // then
        Optional<Message> result = waitForErrorMessage(getKernelSocketsService().getKernelSockets());
        assertThat(result).isPresent();
        assertThat(((String) (result.get().getContent().get("text")))).contains("not found");
    }
}

