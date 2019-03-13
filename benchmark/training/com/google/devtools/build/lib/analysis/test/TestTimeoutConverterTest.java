/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis.test;


import TestTimeout.ETERNAL;
import TestTimeout.LONG;
import TestTimeout.MODERATE;
import TestTimeout.SHORT;
import com.google.devtools.build.lib.packages.TestTimeout;
import java.time.Duration;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link TestTimeoutConverter}.
 */
@RunWith(JUnit4.class)
public class TestTimeoutConverterTest {
    private Map<TestTimeout, Duration> timeouts;

    @Test
    public void testDefaultTimeout() throws Exception {
        setTimeouts("-1");
        assertDefaultTimeout(SHORT);
        assertDefaultTimeout(MODERATE);
        assertDefaultTimeout(LONG);
        assertDefaultTimeout(ETERNAL);
    }

    @Test
    public void testUniversalTimeout() throws Exception {
        setTimeouts("1");
        assertTimeout(SHORT, 1);
        assertTimeout(MODERATE, 1);
        assertTimeout(LONG, 1);
        assertTimeout(ETERNAL, 1);
        setTimeouts("2,");// comma at the end is ignored.

        assertTimeout(SHORT, 2);
        assertTimeout(MODERATE, 2);
        assertTimeout(LONG, 2);
        assertTimeout(ETERNAL, 2);
    }

    @Test
    public void testSeparateTimeouts() throws Exception {
        setTimeouts("1,0,-1,3");
        assertTimeout(SHORT, 1);
        assertDefaultTimeout(MODERATE);
        assertDefaultTimeout(LONG);
        assertTimeout(ETERNAL, 3);
        setTimeouts("0,-1,3,20");
        assertDefaultTimeout(SHORT);
        assertDefaultTimeout(MODERATE);
        assertTimeout(LONG, 3);
        assertTimeout(ETERNAL, 20);
    }

    @Test
    public void testIncorrectStrings() throws Exception {
        assertFailure("");
        assertFailure("1a");
        assertFailure("1 2 3 4");
        assertFailure("1:2:3:4");
        assertFailure("1,2,3");
        assertFailure("1,2,3,4,");
        assertFailure("1,2,,3,4");
        assertFailure("1,2,3 4");
        assertFailure("1,2,3,4,5");
    }
}

