/**
 * Copyright 2019 Florian Schmaus
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
package org.igniterealtime.smack.inttest;


import java.lang.reflect.Method;
import java.util.List;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.junit.Assert;
import org.junit.Test;


public class SmackIntegrationTestFrameWorkTest {
    private static class ValidLowLevelList {
        @SuppressWarnings("unused")
        public void test(List<AbstractXMPPConnection> connections) {
        }
    }

    private static class InvalidLowLevelList {
        @SuppressWarnings("unused")
        public void test(List<AbstractXMPPConnection> connections, boolean invalid) {
        }
    }

    private static class ValidLowLevelVarargs {
        @SuppressWarnings("unused")
        public void test(AbstractXMPPConnection connectionOne, AbstractXMPPConnection connectionTwo, AbstractXMPPConnection connectionThree) {
        }
    }

    private static class InvalidLowLevelVarargs {
        @SuppressWarnings("unused")
        public void test(AbstractXMPPConnection connectionOne, Integer invalid, AbstractXMPPConnection connectionTwo, AbstractXMPPConnection connectionThree) {
        }
    }

    @Test
    public void testValidLowLevelList() {
        Method testMethod = SmackIntegrationTestFrameWorkTest.getTestMethod(SmackIntegrationTestFrameWorkTest.ValidLowLevelList.class);
        Assert.assertTrue(SmackIntegrationTestFramework.testMethodParametersIsListOfConnections(testMethod, AbstractXMPPConnection.class));
    }

    @Test
    public void testInvalidLowLevelList() {
        Method testMethod = SmackIntegrationTestFrameWorkTest.getTestMethod(SmackIntegrationTestFrameWorkTest.InvalidLowLevelList.class);
        Assert.assertFalse(SmackIntegrationTestFramework.testMethodParametersIsListOfConnections(testMethod, AbstractXMPPConnection.class));
    }

    @Test
    public void testValidLowLevelVarargs() {
        Method testMethod = SmackIntegrationTestFrameWorkTest.getTestMethod(SmackIntegrationTestFrameWorkTest.ValidLowLevelVarargs.class);
        Assert.assertTrue(SmackIntegrationTestFramework.testMethodParametersVarargsConnections(testMethod, AbstractXMPPConnection.class));
    }

    @Test
    public void testInvalidLowLevelVargs() {
        Method testMethod = SmackIntegrationTestFrameWorkTest.getTestMethod(SmackIntegrationTestFrameWorkTest.InvalidLowLevelVarargs.class);
        Assert.assertFalse(SmackIntegrationTestFramework.testMethodParametersVarargsConnections(testMethod, AbstractXMPPConnection.class));
    }
}

