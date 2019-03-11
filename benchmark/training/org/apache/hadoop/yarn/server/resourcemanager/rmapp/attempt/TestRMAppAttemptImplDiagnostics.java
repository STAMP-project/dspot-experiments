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
package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;


import BoundedAppender.TRUNCATED_MESSAGES_TEMPLATE;
import YarnConfiguration.APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC;
import YarnConfiguration.DEFAULT_APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Testing {@link RMAppAttemptImpl#diagnostics} scenarios.
 */
public class TestRMAppAttemptImplDiagnostics {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenCreatedWithDefaultConfigurationSuccess() {
        final Configuration configuration = new Configuration();
        configuration.setInt(APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC, DEFAULT_APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC);
        createRMAppAttemptImpl(configuration);
    }

    @Test
    public void whenCreatedWithWrongConfigurationError() {
        final Configuration configuration = new Configuration();
        configuration.setInt(APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC, 0);
        expectedException.expect(YarnRuntimeException.class);
        createRMAppAttemptImpl(configuration);
    }

    @Test
    public void whenAppendedWithinLimitMessagesArePreserved() {
        final Configuration configuration = new Configuration();
        configuration.setInt(APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC, 1);
        final RMAppAttemptImpl appAttempt = createRMAppAttemptImpl(configuration);
        final String withinLimit = RandomStringUtils.random(1024);
        appAttempt.appendDiagnostics(withinLimit);
        Assert.assertEquals("messages within limit should be preserved", withinLimit, appAttempt.getDiagnostics());
    }

    @Test
    public void whenAppendedBeyondLimitMessagesAreTruncated() {
        final Configuration configuration = new Configuration();
        configuration.setInt(APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC, 1);
        final RMAppAttemptImpl appAttempt = createRMAppAttemptImpl(configuration);
        final String beyondLimit = RandomStringUtils.random(1025);
        appAttempt.appendDiagnostics(beyondLimit);
        final String truncated = String.format(TRUNCATED_MESSAGES_TEMPLATE, 1024, 1025, beyondLimit.substring(1));
        Assert.assertEquals("messages beyond limit should be truncated", truncated, appAttempt.getDiagnostics());
    }
}

