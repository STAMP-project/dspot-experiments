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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import org.junit.Assert;
import org.junit.Test;

import static FairSchedulerUtilities.trimQueueName;


/**
 * Tests for {@link FairSchedulerUtilities}.
 */
public class TestFairSchedulerUtilities {
    @Test
    public void testTrimQueueNameEquals() throws Exception {
        final String[] equalsStrings = new String[]{ // no spaces
        "a", // leading spaces
        " a", " \u3000a", "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000a", "\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680a", "\t \u2006\u2001\u202f\u00a0\f\u2009a", "\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000a", // trailing spaces
        "a\u200a", "a  \u0085 ", // spaces on both sides
        " a ", "  a\u00a0", "\t \u2006\u2001\u202f\u00a0\f\u2009a" + "\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000" };
        for (String s : equalsStrings) {
            Assert.assertEquals("a", trimQueueName(s));
        }
    }

    @Test
    public void testTrimQueueNamesEmpty() throws Exception {
        Assert.assertNull(FairSchedulerUtilities.trimQueueName(null));
        final String spaces = "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000" + (("\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680" + "\t \u2006\u2001\u202f\u00a0\f\u2009") + "\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000");
        Assert.assertTrue(trimQueueName(spaces).isEmpty());
    }
}

