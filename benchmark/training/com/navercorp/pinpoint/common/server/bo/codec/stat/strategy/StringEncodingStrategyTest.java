/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.common.server.bo.codec.stat.strategy;


import StringEncodingStrategy.ALWAYS_SAME_VALUE;
import StringEncodingStrategy.Analyzer.Builder;
import StringEncodingStrategy.NONE;
import StringEncodingStrategy.REPEAT_COUNT;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class StringEncodingStrategyTest {
    private static final String[] STRING_CANDIDATES = new String[]{ "aTest", "bTest", "cTest" };

    @Test
    public void noneTest() throws Exception {
        StringEncodingStrategy.Analyzer.Builder builder = new StringEncodingStrategy.Analyzer.Builder();
        for (String string : StringEncodingStrategyTest.STRING_CANDIDATES) {
            builder.addValue(string);
        }
        StrategyAnalyzer<String> build = builder.build();
        Assert.assertEquals(build.getBestStrategy(), NONE);
    }

    @Test
    public void repeatTest() throws Exception {
        StringEncodingStrategy.Analyzer.Builder builder = new StringEncodingStrategy.Analyzer.Builder();
        for (String string : StringEncodingStrategyTest.STRING_CANDIDATES) {
            builder.addValue(string);
            builder.addValue(string);
        }
        StrategyAnalyzer<String> build = builder.build();
        Assert.assertEquals(build.getBestStrategy(), REPEAT_COUNT);
    }

    @Test
    public void alwaysSameTest() throws Exception {
        StringEncodingStrategy.Analyzer.Builder builder = new StringEncodingStrategy.Analyzer.Builder();
        builder.addValue(StringEncodingStrategyTest.STRING_CANDIDATES[0]);
        builder.addValue(StringEncodingStrategyTest.STRING_CANDIDATES[0]);
        builder.addValue(StringEncodingStrategyTest.STRING_CANDIDATES[0]);
        builder.addValue(StringEncodingStrategyTest.STRING_CANDIDATES[0]);
        StrategyAnalyzer<String> build = builder.build();
        Assert.assertEquals(build.getBestStrategy(), ALWAYS_SAME_VALUE);
    }
}

