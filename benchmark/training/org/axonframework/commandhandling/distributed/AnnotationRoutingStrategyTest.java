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
package org.axonframework.commandhandling.distributed;


import org.axonframework.commandhandling.RoutingKey;
import org.junit.Assert;
import org.junit.Test;


public class AnnotationRoutingStrategyTest {
    private AnnotationRoutingStrategy testSubject;

    @Test
    public void testGetRoutingKeyFromField() {
        Assert.assertEquals("Target", testSubject.getRoutingKey(new org.axonframework.commandhandling.GenericCommandMessage(new AnnotationRoutingStrategyTest.SomeFieldAnnotatedCommand())));
        Assert.assertEquals("Target", testSubject.getRoutingKey(new org.axonframework.commandhandling.GenericCommandMessage(new AnnotationRoutingStrategyTest.SomeOtherFieldAnnotatedCommand())));
    }

    @Test
    public void testGetRoutingKeyFromMethod() {
        Assert.assertEquals("Target", testSubject.getRoutingKey(new org.axonframework.commandhandling.GenericCommandMessage(new AnnotationRoutingStrategyTest.SomeMethodAnnotatedCommand())));
        Assert.assertEquals("Target", testSubject.getRoutingKey(new org.axonframework.commandhandling.GenericCommandMessage(new AnnotationRoutingStrategyTest.SomeOtherMethodAnnotatedCommand())));
    }

    public static class SomeFieldAnnotatedCommand {
        @RoutingKey
        private final String target = "Target";
    }

    public static class SomeOtherFieldAnnotatedCommand {
        @RoutingKey
        private final AnnotationRoutingStrategyTest.SomeObject target = new AnnotationRoutingStrategyTest.SomeObject("Target");
    }

    public static class SomeMethodAnnotatedCommand {
        private final String target = "Target";

        @RoutingKey
        public String getTarget() {
            return target;
        }
    }

    public static class SomeOtherMethodAnnotatedCommand {
        private final AnnotationRoutingStrategyTest.SomeObject target = new AnnotationRoutingStrategyTest.SomeObject("Target");

        @RoutingKey
        public AnnotationRoutingStrategyTest.SomeObject getTarget() {
            return target;
        }
    }

    private static class SomeObject {
        private final String target;

        public SomeObject(String target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return target;
        }
    }
}

