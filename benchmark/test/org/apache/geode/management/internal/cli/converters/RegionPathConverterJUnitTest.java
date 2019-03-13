/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.converters;


import ConverterHint.REGION_PATH;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.apache.geode.test.junit.rules.GfshParserRule.CommandCandidate;
import org.junit.ClassRule;
import org.junit.Test;


public class RegionPathConverterJUnitTest {
    @ClassRule
    public static GfshParserRule parser = new GfshParserRule();

    private static RegionPathConverter converter;

    private static String[] allRegionPaths = new String[]{ "/region1", "/region2", "/rg3" };

    @Test
    public void testSupports() throws Exception {
        assertThat(RegionPathConverterJUnitTest.converter.supports(String.class, REGION_PATH)).isTrue();
    }

    @Test
    public void convert() throws Exception {
        assertThatThrownBy(() -> RegionPathConverterJUnitTest.converter.convertFromText("/", .class, "")).isInstanceOf(IllegalArgumentException.class).hasMessage("invalid region path: /");
        assertThat(RegionPathConverterJUnitTest.converter.convertFromText("region", String.class, "")).isEqualTo("/region");
        assertThat(RegionPathConverterJUnitTest.converter.convertFromText("/region/t", String.class, "")).isEqualTo("/region/t");
    }

    @Test
    public void complete() throws Exception {
        CommandCandidate candidate = RegionPathConverterJUnitTest.parser.complete("destroy region --name=");
        assertThat(candidate.size()).isEqualTo(RegionPathConverterJUnitTest.allRegionPaths.length);
        assertThat(candidate.getFirstCandidate()).isEqualTo("destroy region --name=/region1");
        candidate = RegionPathConverterJUnitTest.parser.complete("destroy region --name=/");
        assertThat(candidate.size()).isEqualTo(RegionPathConverterJUnitTest.allRegionPaths.length);
        assertThat(candidate.getFirstCandidate()).isEqualTo("destroy region --name=/region1");
        candidate = RegionPathConverterJUnitTest.parser.complete("destroy region --name=/region");
        assertThat(candidate.size()).isEqualTo(2);
        assertThat(candidate.getFirstCandidate()).isEqualTo("destroy region --name=/region1");
    }
}

