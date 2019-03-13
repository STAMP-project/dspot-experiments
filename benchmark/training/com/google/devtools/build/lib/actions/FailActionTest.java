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
package com.google.devtools.build.lib.actions;


import com.google.devtools.build.lib.testutil.Scratch;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FailActionTest {
    private Scratch scratch = new Scratch();

    private String errorMessage;

    private Artifact anOutput;

    private Collection<Artifact> outputs;

    private FailAction failAction;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    protected MutableActionGraph actionGraph = new MapBasedActionGraph(actionKeyContext);

    @Test
    public void testExecutingItYieldsExceptionWithErrorMessage() {
        try {
            failAction.execute(null);
            Assert.fail();
        } catch (ActionExecutionException e) {
            assertThat(e).hasMessage(errorMessage);
        }
    }

    @Test
    public void testInputsAreEmptySet() {
        assertThat(failAction.getInputs()).containsExactlyElementsIn(Collections.emptySet());
    }

    @Test
    public void testRetainsItsOutputs() {
        assertThat(failAction.getOutputs()).containsExactlyElementsIn(outputs);
    }

    @Test
    public void testPrimaryOutput() {
        assertThat(failAction.getPrimaryOutput()).isSameAs(anOutput);
    }
}

