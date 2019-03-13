/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.common.server.util;


import com.navercorp.pinpoint.common.PinpointConstants;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author emeroad
 */
public class SpanUtilsTest {
    @Test
    public void testGetTraceIndexRowKeyWhiteSpace() {
        String applicationName = "test test";
        long time = System.currentTimeMillis();
        check(applicationName, time);
    }

    @Test
    public void testGetTraceIndexRowKey1() {
        String applicationName = "test";
        long time = System.currentTimeMillis();
        check(applicationName, time);
    }

    @Test
    public void testGetTraceIndexRowKey2() {
        String applicationName = "";
        for (int i = 0; i < (PinpointConstants.APPLICATION_NAME_MAX_LEN); i++) {
            applicationName += "1";
        }
        long time = System.currentTimeMillis();
        check(applicationName, time);
    }

    @Test
    public void testGetTraceIndexRowKey3() {
        String applicationName = "";
        for (int i = 0; i < ((PinpointConstants.APPLICATION_NAME_MAX_LEN) + 1); i++) {
            applicationName += "1";
        }
        long time = System.currentTimeMillis();
        try {
            check(applicationName, time);
            Assert.fail("error");
        } catch (IndexOutOfBoundsException ignore) {
        }
    }
}

