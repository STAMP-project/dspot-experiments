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
package org.apache.camel.impl;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.TypeConverterRegistry;
import org.junit.Assert;
import org.junit.Test;


public class TypeConverterRegistryStatisticsEnabledTest extends ContextTestSupport {
    @Test
    public void testTypeConverterRegistry() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(2);
        template.sendBody("direct:start", "3");
        template.sendBody("direct:start", "7");
        assertMockEndpointsSatisfied();
        TypeConverterRegistry reg = context.getTypeConverterRegistry();
        Assert.assertTrue("Should be enabled", reg.getStatistics().isStatisticsEnabled());
        Long failed = reg.getStatistics().getFailedCounter();
        Assert.assertEquals(0, failed.intValue());
        Long miss = reg.getStatistics().getMissCounter();
        Assert.assertEquals(0, miss.intValue());
        try {
            template.sendBody("direct:start", "foo");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // expected
        }
        // should now have a failed
        failed = reg.getStatistics().getFailedCounter();
        Assert.assertEquals(1, failed.intValue());
        miss = reg.getStatistics().getMissCounter();
        Assert.assertEquals(0, miss.intValue());
        // reset
        reg.getStatistics().reset();
        failed = reg.getStatistics().getFailedCounter();
        Assert.assertEquals(0, failed.intValue());
        miss = reg.getStatistics().getMissCounter();
        Assert.assertEquals(0, miss.intValue());
    }
}

