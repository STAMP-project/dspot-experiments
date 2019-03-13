/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.data.input.influx;


import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.apache.druid.java.util.common.parsers.Parser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class InfluxParserTest {
    private String name;

    private String input;

    private Map<String, Object> expected;

    @Test
    public void testParseWhitelistPass() {
        Parser<String, Object> parser = new InfluxParser(Sets.newHashSet("cpu"));
        String input = "cpu,host=foo.bar.baz,region=us-east,application=echo pct_idle=99.3,pct_user=88.8,m1_load=2 1465839830100400200";
        Map<String, Object> parsed = parser.parseToMap(input);
        MatcherAssert.assertThat(parsed.get("measurement"), Matchers.equalTo("cpu"));
    }

    @Test
    public void testParseWhitelistFail() {
        Parser<String, Object> parser = new InfluxParser(Sets.newHashSet("mem"));
        String input = "cpu,host=foo.bar.baz,region=us-east,application=echo pct_idle=99.3,pct_user=88.8,m1_load=2 1465839830100400200";
        try {
            parser.parseToMap(input);
        } catch (ParseException t) {
            MatcherAssert.assertThat(t, Matchers.isA(ParseException.class));
            return;
        }
        Assert.fail("Exception not thrown");
    }

    private static class Parsed {
        private String measurement;

        private Long timestamp;

        private Map<String, Object> kv = new HashMap<>();

        public static InfluxParserTest.Parsed row(String measurement, Long timestamp) {
            InfluxParserTest.Parsed e = new InfluxParserTest.Parsed();
            e.measurement = measurement;
            e.timestamp = timestamp;
            return e;
        }

        public InfluxParserTest.Parsed with(String k, Object v) {
            kv.put(k, v);
            return this;
        }
    }
}

