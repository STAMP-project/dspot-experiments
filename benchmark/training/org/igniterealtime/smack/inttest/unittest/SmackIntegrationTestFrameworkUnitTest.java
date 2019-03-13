/**
 * Copyright 2015-2019 Florian Schmaus
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
package org.igniterealtime.smack.inttest.unittest;


import StanzaError.Condition.bad_request;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.DummySmackIntegrationTestFramework;
import org.igniterealtime.smack.inttest.FailedTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.SmackIntegrationTestFramework.TestRunResult;
import org.igniterealtime.smack.inttest.SmackIntegrationTestUnitTestUtil;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaError;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SmackIntegrationTestFrameworkUnitTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static boolean beforeClassInvoked;

    private static boolean afterClassInvoked;

    @Test
    public void throwsRuntimeExceptionsTest() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, KeyManagementException, NoSuchAlgorithmException, SmackException, XMPPException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(SmackIntegrationTestFrameworkUnitTest.ThrowsRuntimeExceptionDummyTest.RUNTIME_EXCEPTION_MESSAGE);
        DummySmackIntegrationTestFramework sinttest = SmackIntegrationTestUnitTestUtil.getFrameworkForUnitTest(SmackIntegrationTestFrameworkUnitTest.ThrowsRuntimeExceptionDummyTest.class);
        run();
    }

    public static class ThrowsRuntimeExceptionDummyTest extends AbstractSmackIntegrationTest {
        public ThrowsRuntimeExceptionDummyTest(SmackIntegrationTestEnvironment<?> environment) {
            super(environment);
        }

        public static final String RUNTIME_EXCEPTION_MESSAGE = "Dummy RuntimeException";

        @SmackIntegrationTest
        public void throwRuntimeExceptionTest() {
            throw new RuntimeException(SmackIntegrationTestFrameworkUnitTest.ThrowsRuntimeExceptionDummyTest.RUNTIME_EXCEPTION_MESSAGE);
        }
    }

    @Test
    public void logsNonFatalExceptionTest() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, KeyManagementException, NoSuchAlgorithmException, SmackException, XMPPException {
        DummySmackIntegrationTestFramework sinttest = SmackIntegrationTestUnitTestUtil.getFrameworkForUnitTest(SmackIntegrationTestFrameworkUnitTest.ThrowsNonFatalExceptionDummyTest.class);
        TestRunResult testRunResult = sinttest.run();
        List<FailedTest> failedTests = testRunResult.getFailedTests();
        Assert.assertEquals(1, failedTests.size());
        FailedTest failedTest = failedTests.get(0);
        Assert.assertTrue(((failedTest.failureReason) instanceof XMPPErrorException));
        XMPPErrorException ex = ((XMPPErrorException) (failedTest.failureReason));
        Assert.assertEquals(bad_request, ex.getStanzaError().getCondition());
        Assert.assertEquals(SmackIntegrationTestFrameworkUnitTest.ThrowsNonFatalExceptionDummyTest.DESCRIPTIVE_TEXT, ex.getStanzaError().getDescriptiveText());
    }

    public static class ThrowsNonFatalExceptionDummyTest extends AbstractSmackIntegrationTest {
        public static final String DESCRIPTIVE_TEXT = "I'm not fatal";

        public ThrowsNonFatalExceptionDummyTest(SmackIntegrationTestEnvironment<?> environment) {
            super(environment);
        }

        @SmackIntegrationTest
        public void throwRuntimeExceptionTest() throws XMPPErrorException {
            Message message = new Message();
            throw new XMPPException.XMPPErrorException(message, StanzaError.from(bad_request, SmackIntegrationTestFrameworkUnitTest.ThrowsNonFatalExceptionDummyTest.DESCRIPTIVE_TEXT).build());
        }
    }

    @Test
    public void testInvoking() throws IOException, IllegalAccessException, IllegalArgumentException, InstantiationException, InterruptedException, InvocationTargetException, KeyManagementException, NoSuchAlgorithmException, SmackException, XMPPException {
        SmackIntegrationTestFrameworkUnitTest.beforeClassInvoked = false;
        SmackIntegrationTestFrameworkUnitTest.afterClassInvoked = false;
        DummySmackIntegrationTestFramework sinttest = SmackIntegrationTestUnitTestUtil.getFrameworkForUnitTest(SmackIntegrationTestFrameworkUnitTest.BeforeAfterClassTest.class);
        run();
        Assert.assertTrue("A before class method should have been executed to this time", SmackIntegrationTestFrameworkUnitTest.beforeClassInvoked);
        Assert.assertTrue("A after class method should have been executed to this time", SmackIntegrationTestFrameworkUnitTest.afterClassInvoked);
    }

    public static class BeforeAfterClassTest extends AbstractSmackIntegrationTest {
        public BeforeAfterClassTest(SmackIntegrationTestEnvironment<?> environment) {
            super(environment);
        }

        @BeforeClass
        public void setUp() {
            SmackIntegrationTestFrameworkUnitTest.beforeClassInvoked = true;
        }

        @AfterClass
        public void tearDown() {
            SmackIntegrationTestFrameworkUnitTest.afterClassInvoked = true;
        }

        @SmackIntegrationTest
        public void test() {
            Assert.assertTrue("A before class method should have been executed to this time", SmackIntegrationTestFrameworkUnitTest.beforeClassInvoked);
            Assert.assertFalse("A after class method shouldn't have been executed to this time", SmackIntegrationTestFrameworkUnitTest.afterClassInvoked);
        }
    }
}

