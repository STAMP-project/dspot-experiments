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
package org.apache.flink.runtime.rest.handler.util;


import MessageParameterRequisiteness.OPTIONAL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.MessageParameters;
import org.apache.flink.runtime.rest.messages.MessagePathParameter;
import org.apache.flink.runtime.rest.messages.MessageQueryParameter;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link HandlerRequestUtils}.
 */
public class HandlerRequestUtilsTest extends TestLogger {
    @Test
    public void testGetQueryParameter() throws Exception {
        final Boolean queryParameter = HandlerRequestUtils.getQueryParameter(new org.apache.flink.runtime.rest.handler.HandlerRequest(EmptyRequestBody.getInstance(), new HandlerRequestUtilsTest.TestMessageParameters(), Collections.emptyMap(), Collections.singletonMap("key", Collections.singletonList("true"))), HandlerRequestUtilsTest.TestBooleanQueryParameter.class);
        Assert.assertThat(queryParameter, Matchers.equalTo(true));
    }

    @Test
    public void testGetQueryParameterRepeated() throws Exception {
        try {
            HandlerRequestUtils.getQueryParameter(new org.apache.flink.runtime.rest.handler.HandlerRequest(EmptyRequestBody.getInstance(), new HandlerRequestUtilsTest.TestMessageParameters(), Collections.emptyMap(), Collections.singletonMap("key", Arrays.asList("true", "false"))), HandlerRequestUtilsTest.TestBooleanQueryParameter.class);
        } catch (final RestHandlerException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected only one value"));
        }
    }

    @Test
    public void testGetQueryParameterDefaultValue() throws Exception {
        final Boolean allowNonRestoredState = HandlerRequestUtils.getQueryParameter(new org.apache.flink.runtime.rest.handler.HandlerRequest(EmptyRequestBody.getInstance(), new HandlerRequestUtilsTest.TestMessageParameters(), Collections.emptyMap(), Collections.singletonMap("key", Collections.emptyList())), HandlerRequestUtilsTest.TestBooleanQueryParameter.class, true);
        Assert.assertThat(allowNonRestoredState, Matchers.equalTo(true));
    }

    private static class TestMessageParameters extends MessageParameters {
        private HandlerRequestUtilsTest.TestBooleanQueryParameter testBooleanQueryParameter;

        @Override
        public Collection<MessagePathParameter<?>> getPathParameters() {
            return Collections.emptyList();
        }

        @Override
        public Collection<MessageQueryParameter<?>> getQueryParameters() {
            testBooleanQueryParameter = new HandlerRequestUtilsTest.TestBooleanQueryParameter();
            return Collections.singletonList(testBooleanQueryParameter);
        }
    }

    private static class TestBooleanQueryParameter extends MessageQueryParameter<Boolean> {
        private TestBooleanQueryParameter() {
            super("key", OPTIONAL);
        }

        @Override
        public Boolean convertStringToValue(final String value) {
            return Boolean.parseBoolean(value);
        }

        @Override
        public String convertValueToString(final Boolean value) {
            return value.toString();
        }

        @Override
        public String getDescription() {
            return "boolean query parameter";
        }
    }
}

