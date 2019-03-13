/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.signavio.feel.runtime;


import FEELEvent.Severity;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.signavio.KieDMNSignavioProfile;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public abstract class ExtendedFunctionsBaseFEELTest {
    private final FEEL feel = FEEL.newInstance(Arrays.asList(new KieDMNSignavioProfile()));

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Object result;

    @Parameterized.Parameter(2)
    public Severity severity;

    @Test
    public void testExpression() {
        FEELEventListener listener = Mockito.mock(FEELEventListener.class);
        feel.addListener(listener);
        feel.addListener(( evt) -> {
            System.out.println(evt);
        });
        assertResult(expression, result);
        if ((severity) != null) {
            ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            Mockito.verify(listener, Mockito.atLeastOnce()).onEvent(captor.capture());
            Assert.assertThat(captor.getValue().getSeverity(), Matchers.is(severity));
        } else {
            Mockito.verify(listener, Mockito.never()).onEvent(ArgumentMatchers.any(FEELEvent.class));
        }
    }
}

