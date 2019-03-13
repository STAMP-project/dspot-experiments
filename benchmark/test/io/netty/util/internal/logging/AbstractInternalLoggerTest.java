/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal.logging;


import InternalLogLevel.DEBUG;
import InternalLogLevel.ERROR;
import InternalLogLevel.INFO;
import InternalLogLevel.TRACE;
import InternalLogLevel.WARN;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * We only need to test methods defined by {@link InternaLogger}.
 */
public abstract class AbstractInternalLoggerTest<T> {
    protected String loggerName = "foo";

    protected T mockLog;

    protected InternalLogger logger;

    protected final Map<String, Object> result = new HashMap<String, Object>();

    @Test
    public void testName() {
        Assert.assertEquals(loggerName, logger.name());
    }

    @Test
    public void testAllLevel() throws Exception {
        testLevel(TRACE);
        testLevel(DEBUG);
        testLevel(INFO);
        testLevel(WARN);
        testLevel(ERROR);
    }
}

