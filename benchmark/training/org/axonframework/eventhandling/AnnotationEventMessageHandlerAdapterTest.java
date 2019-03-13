/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AnnotationEventMessageHandlerAdapterTest {
    @Test
    public void testInvokeResetHandler() {
        AnnotationEventMessageHandlerAdapterTest.SomeHandler annotatedEventListener = new AnnotationEventMessageHandlerAdapterTest.SomeHandler();
        prepareReset();
        Assert.assertEquals(Collections.singletonList("reset"), annotatedEventListener.invocations);
    }

    public static class SomeHandler {
        private List<String> invocations = new ArrayList<>();

        @EventHandler
        public void handle(String event) {
            invocations.add(event);
        }

        @ResetHandler
        public void doReset(AnnotationEventMessageHandlerAdapterTest.SomeResource someResource) {
            invocations.add("reset");
        }
    }

    public static class SomeResource {}
}

