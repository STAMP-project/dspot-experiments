/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.common;


import java.time.ZoneOffset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringFormatterTest {
    private static final Logger log = LoggerFactory.getLogger(StringFormatterTest.class);

    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testDateString() {
        Assert.assertEquals("2019-01-08T20:59:29.85Z", StringFormatter.dateString(1546981169850L, ZoneOffset.UTC));
    }

    @Test
    public void testDurationString() {
        Assert.assertEquals("1m", StringFormatter.durationString(60000));
        Assert.assertEquals("1m1s", StringFormatter.durationString(61000));
        Assert.assertEquals("1m1s", StringFormatter.durationString(61200));
        Assert.assertEquals("5s", StringFormatter.durationString(5000));
        Assert.assertEquals("2h", StringFormatter.durationString(7200000));
        Assert.assertEquals("2h1s", StringFormatter.durationString(7201000));
        Assert.assertEquals("2h5m3s", StringFormatter.durationString(7503000));
    }

    @Test
    public void testPrettyPrintGrid() {
        Assert.assertEquals(String.format(("ANIMAL  NUMBER INDEX %n" + ("lion    1      12345 %n" + "manatee 50     1     %n"))), StringFormatter.prettyPrintGrid(Arrays.asList(Arrays.asList("ANIMAL", "NUMBER", "INDEX"), Arrays.asList("lion", "1", "12345"), Arrays.asList("manatee", "50", "1"))));
    }
}

