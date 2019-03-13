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
package org.apache.camel.component.log;


import java.io.StringWriter;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ResolveEndpointFailedException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


/**
 * Custom Logger test.
 */
public class LogCustomLoggerTest extends ContextTestSupport {
    // to capture the logs
    private static StringWriter sw1;

    // to capture the warnings from LogComponent
    private static StringWriter sw2;

    @Test
    public void testFallbackLogger() throws Exception {
        String endpointUri = "log:" + (LogCustomLoggerTest.class.getCanonicalName());
        template.requestBody(endpointUri, "hello");
        Assert.assertThat(LogCustomLoggerTest.sw1.toString(), CoreMatchers.equalTo(LogCustomLoggerTest.class.getCanonicalName()));
    }

    @Test
    public void testEndpointURIParametrizedLogger() throws Exception {
        context.getRegistry().bind("logger1", LoggerFactory.getLogger("provided.logger1.name"));
        context.getRegistry().bind("logger2", LoggerFactory.getLogger("provided.logger2.name"));
        template.requestBody("log:irrelevant.logger.name?logger=#logger2", "hello");
        Assert.assertThat(LogCustomLoggerTest.sw1.toString(), CoreMatchers.equalTo("provided.logger2.name"));
    }

    @Test
    public void testEndpointURIParametrizedNotResolvableLogger() {
        context.getRegistry().bind("logger1", LoggerFactory.getLogger("provided.logger1.name"));
        try {
            template.requestBody("log:irrelevant.logger.name?logger=#logger2", "hello");
        } catch (ResolveEndpointFailedException e) {
            // expected
        }
    }

    @Test
    public void testDefaultRegistryLogger() throws Exception {
        context.getRegistry().bind("logger", LoggerFactory.getLogger("provided.logger1.name"));
        template.requestBody("log:irrelevant.logger.name", "hello");
        Assert.assertThat(LogCustomLoggerTest.sw1.toString(), CoreMatchers.equalTo("provided.logger1.name"));
    }

    @Test
    public void testTwoRegistryLoggers() throws Exception {
        context.getRegistry().bind("logger1", LoggerFactory.getLogger("provided.logger1.name"));
        context.getRegistry().bind("logger2", LoggerFactory.getLogger("provided.logger2.name"));
        template.requestBody("log:irrelevant.logger.name", "hello");
        Assert.assertThat(LogCustomLoggerTest.sw1.toString(), CoreMatchers.equalTo("irrelevant.logger.name"));
        Assert.assertThat(LogCustomLoggerTest.sw2.toString(), CoreMatchers.equalTo(LogComponent.class.getName()));
    }
}

