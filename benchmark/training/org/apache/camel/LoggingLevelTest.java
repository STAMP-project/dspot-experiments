/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel;


import LoggingLevel.DEBUG;
import LoggingLevel.ERROR;
import LoggingLevel.INFO;
import LoggingLevel.OFF;
import LoggingLevel.TRACE;
import LoggingLevel.WARN;
import org.junit.Assert;
import org.junit.Test;


public class LoggingLevelTest extends Assert {
    @Test
    public void testLoggingLevelInfo() throws Exception {
        Assert.assertTrue(INFO.isEnabled(ERROR));
        Assert.assertTrue(INFO.isEnabled(WARN));
        Assert.assertTrue(INFO.isEnabled(INFO));
        Assert.assertFalse(INFO.isEnabled(DEBUG));
        Assert.assertFalse(INFO.isEnabled(TRACE));
        Assert.assertFalse(INFO.isEnabled(OFF));
    }

    @Test
    public void testLoggingLevelWARN() throws Exception {
        Assert.assertTrue(WARN.isEnabled(ERROR));
        Assert.assertTrue(WARN.isEnabled(WARN));
        Assert.assertFalse(WARN.isEnabled(INFO));
        Assert.assertFalse(WARN.isEnabled(DEBUG));
        Assert.assertFalse(WARN.isEnabled(TRACE));
        Assert.assertFalse(WARN.isEnabled(OFF));
    }
}

