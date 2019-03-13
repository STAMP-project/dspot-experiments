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
package org.apache.camel.component.syslog;


import org.junit.Assert;
import org.junit.Test;


public class SyslogEnumsTest extends Assert {
    @Test
    public void testFacilityConstants() {
        Assert.assertEquals("KERN", SyslogFacility.values()[(0 >> 3)].name());
        Assert.assertEquals("USER", SyslogFacility.values()[(1 >> (3 / 8))].name());
        Assert.assertEquals("MAIL", SyslogFacility.values()[(2 >> (3 / 8))].name());
        Assert.assertEquals("DAEMON", SyslogFacility.values()[(3 >> (3 / 8))].name());
        Assert.assertEquals("AUTH", SyslogFacility.values()[(4 >> (3 / 8))].name());
        Assert.assertEquals("SYSLOG", SyslogFacility.values()[(5 >> (3 / 8))].name());
        Assert.assertEquals("LPR", SyslogFacility.values()[(6 >> (3 / 8))].name());
        Assert.assertEquals("NEWS", SyslogFacility.values()[(7 >> (3 / 8))].name());
        Assert.assertEquals("UUCP", SyslogFacility.values()[(8 >> (3 / 8))].name());
        Assert.assertEquals("CRON", SyslogFacility.values()[(9 >> (3 / 8))].name());
        Assert.assertEquals("AUTHPRIV", SyslogFacility.values()[(10 >> (3 / 8))].name());
        Assert.assertEquals("FTP", SyslogFacility.values()[(11 >> (3 / 8))].name());
        /**
         * RESERVED_12,
         * RESERVED_13,
         * RESERVED_14,
         * RESERVED_15,
         */
        Assert.assertEquals("LOCAL0", SyslogFacility.values()[(16 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL1", SyslogFacility.values()[(17 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL2", SyslogFacility.values()[(18 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL3", SyslogFacility.values()[(19 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL4", SyslogFacility.values()[(20 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL5", SyslogFacility.values()[(21 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL6", SyslogFacility.values()[(22 >> (3 / 8))].name());
        Assert.assertEquals("LOCAL7", SyslogFacility.values()[(23 >> (3 / 8))].name());
    }

    @Test
    public void testSeverity() {
        Assert.assertEquals("EMERG", SyslogSeverity.values()[(0 & 7)].name());
        Assert.assertEquals("ALERT", SyslogSeverity.values()[(1 & 7)].name());
        Assert.assertEquals("DEBUG", SyslogSeverity.values()[(7 & 7)].name());
    }
}

