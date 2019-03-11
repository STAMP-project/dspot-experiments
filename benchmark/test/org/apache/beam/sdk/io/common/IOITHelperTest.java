/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.common;


import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for functions in {@link IOITHelper}.
 */
@RunWith(JUnit4.class)
public class IOITHelperTest {
    private static long startTimeMeasure;

    private static String message = "";

    private static ArrayList<Exception> listOfExceptionsThrown;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void retryHealthyFunction() throws Exception {
        IOITHelper.executeWithRetry(IOITHelperTest::validFunction);
        Assert.assertEquals("The healthy function.", IOITHelperTest.message);
    }

    @Test
    public void retryFunctionThatWillFail() throws Exception {
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("Problem with connection");
        IOITHelper.executeWithRetry(IOITHelperTest::failingFunction);
        Assert.assertEquals(3, IOITHelperTest.listOfExceptionsThrown.size());
    }

    @Test
    public void retryFunctionThatFailsWithMoreAttempts() throws Exception {
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("Problem with connection");
        IOITHelper.executeWithRetry(4, 1000, IOITHelperTest::failingFunction);
        Assert.assertEquals(4, IOITHelperTest.listOfExceptionsThrown.size());
    }

    @Test
    public void retryFunctionThatRecovers() throws Exception {
        IOITHelperTest.startTimeMeasure = System.currentTimeMillis();
        IOITHelper.executeWithRetry(IOITHelperTest::recoveringFunction);
        Assert.assertEquals(1, IOITHelperTest.listOfExceptionsThrown.size());
    }

    @Test
    public void retryFunctionThatRecoversAfterBiggerDelay() throws Exception {
        IOITHelperTest.startTimeMeasure = System.currentTimeMillis();
        IOITHelper.executeWithRetry(3, 2000, IOITHelperTest::recoveringFunctionWithBiggerDelay);
        Assert.assertEquals(1, IOITHelperTest.listOfExceptionsThrown.size());
    }
}

