/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit.tls;


import ExitCode.INVALID_ARGS;
import ExitCode.SERVICE_ERROR;
import TlsToolkitMain.UNABLE_TO_GET_DESCRIPTION;
import org.apache.nifi.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class TlsToolkitMainTest {
    private TlsToolkitMain tlsToolkitMain;

    private SystemExitCapturer systemExitCapturer;

    @Test
    public void testAllMainClassesHaveDescription() {
        tlsToolkitMain.getMainMap().values().forEach(( mainClass) -> {
            String description = tlsToolkitMain.getDescription(mainClass);
            assertFalse(StringUtils.isEmpty(description));
            assertFalse(description.contains(TlsToolkitMain.UNABLE_TO_GET_DESCRIPTION));
        });
    }

    @Test
    public void testGetDescriptionClassWithNoDescription() {
        Assert.assertTrue(tlsToolkitMain.getDescription(TlsToolkitMainTest.class).startsWith(UNABLE_TO_GET_DESCRIPTION));
    }

    @Test
    public void testAllMainClassesHaveMain() {
        tlsToolkitMain.getMainMap().keySet().stream().map(String::toLowerCase).forEach(( service) -> {
            assertNotNull(tlsToolkitMain.getMain(service));
        });
    }

    @Test
    public void testWrongServiceName() {
        systemExitCapturer.runAndAssertExitCode(() -> tlsToolkitMain.doMain(new String[]{ "fakeService" }), INVALID_ARGS);
    }

    @Test
    public void testNoArguments() {
        systemExitCapturer.runAndAssertExitCode(() -> tlsToolkitMain.doMain(new String[0]), INVALID_ARGS);
    }

    @Test
    public void testInaccessibleMain() {
        String privateMain = "privatemain";
        tlsToolkitMain.getMainMap().put(privateMain, TlsToolkitMainTest.PrivateMain.class);
        systemExitCapturer.runAndAssertExitCode(() -> tlsToolkitMain.doMain(new String[]{ privateMain }), SERVICE_ERROR);
    }

    @Test
    public void testInvocationTargetException() {
        String throwingMain = "throwingmain";
        tlsToolkitMain.getMainMap().put(throwingMain, TlsToolkitMainTest.ThrowingMain.class);
        systemExitCapturer.runAndAssertExitCode(() -> tlsToolkitMain.doMain(new String[]{ throwingMain }), SERVICE_ERROR);
    }

    @Test
    public void testNoMain() {
        String noMain = "nomain";
        tlsToolkitMain.getMainMap().put(noMain, TlsToolkitMainTest.NoMain.class);
        systemExitCapturer.runAndAssertExitCode(() -> tlsToolkitMain.doMain(new String[]{ noMain }), SERVICE_ERROR);
    }

    @Test
    public void testRemovesServiceArg() {
        String storingMain = "storingmain";
        tlsToolkitMain.getMainMap().put(storingMain, TlsToolkitMainTest.StoringMain.class);
        tlsToolkitMain.doMain(new String[]{ storingMain, "-h" });
        Assert.assertArrayEquals(new String[]{ "-h" }, TlsToolkitMainTest.StoringMain.args);
    }

    private static class PrivateMain {
        private static void main(String[] args) {
        }
    }

    private static class ThrowingMain {
        public static void main(String[] args) {
            throw new IllegalArgumentException();
        }
    }

    private static class NoMain {}

    private static class StoringMain {
        private static String[] args;

        public static void main(String[] args) {
            TlsToolkitMainTest.StoringMain.args = args;
        }
    }
}

