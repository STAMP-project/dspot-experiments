/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit;


import java.math.BigDecimal;
import org.apache.nifi.toolkit.flowanalyzer.FlowAnalyzerDriver;
import org.junit.Assert;
import org.junit.Test;


public class FlowAnalyzerDriverTest {
    @Test
    public void testConvertSizeToValue() {
        String gbTest = "13 GB";
        String kbTest = "103 KB";
        String mbTest = "20 MB";
        BigDecimal gbBigDecimal = FlowAnalyzerDriver.convertSizeToByteValue(gbTest);
        Assert.assertEquals(gbBigDecimal.toPlainString(), "13000000000");
        BigDecimal kbBigDecimal = FlowAnalyzerDriver.convertSizeToByteValue(kbTest);
        Assert.assertEquals(kbBigDecimal.toPlainString(), "103000");
        BigDecimal mbBigDecimal = FlowAnalyzerDriver.convertSizeToByteValue(mbTest);
        Assert.assertEquals(mbBigDecimal.toPlainString(), "20000000");
    }

    @Test
    public void convertBytesToGB() {
        BigDecimal gbBigDecimal = new BigDecimal("13000000000");
        BigDecimal kbBigDecimal = new BigDecimal("103000");
        BigDecimal mbBigDecimal = new BigDecimal("20000000");
        BigDecimal result = FlowAnalyzerDriver.convertBytesToGB(gbBigDecimal);
        Assert.assertEquals("13", result.stripTrailingZeros().toPlainString());
        result = FlowAnalyzerDriver.convertBytesToGB(mbBigDecimal);
        Assert.assertEquals("0.02", result.stripTrailingZeros().toPlainString());
        result = FlowAnalyzerDriver.convertBytesToGB(kbBigDecimal);
        Assert.assertEquals("0.000103", result.stripTrailingZeros().toPlainString());
    }
}

