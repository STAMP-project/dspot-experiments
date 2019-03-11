/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.monitoring;


import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.SdkClientException;
import org.junit.Assert;
import org.junit.Test;
import utils.EnvironmentVariableHelper;


/**
 * Tests for {@link EnvironmentVariableCsmConfigurationProvider}.
 */
public class EnvironmentVariableCsmConfigurationProviderTest {
    private static final EnvironmentVariableHelper environmentVariableHelper = new EnvironmentVariableHelper();

    private final EnvironmentVariableCsmConfigurationProvider provider = new EnvironmentVariableCsmConfigurationProvider();

    @Test
    public void testCorrectlyResolvesConfiguration() {
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_ENABLED_ENV_VAR, "true");
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_PORT_ENV_VAR, "1234");
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_CLIENT_ID_ENV_VAR, "foo");
        CsmConfiguration cfg = provider.getConfiguration();
        Assert.assertEquals(new CsmConfiguration(true, 1234, "foo"), cfg);
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenVariablesNotPresent() {
        provider.getConfiguration();
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenPortCannotBeParsed() {
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_ENABLED_ENV_VAR, "true");
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_PORT_ENV_VAR, "onetwothreefour");
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_CLIENT_ID_ENV_VAR, "foo");
        provider.getConfiguration();
    }

    @Test
    public void noPortClientIdSpecified_shouldUseDefaultValues() {
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.reset();
        EnvironmentVariableCsmConfigurationProviderTest.environmentVariableHelper.set(SDKGlobalConfiguration.AWS_CSM_ENABLED_ENV_VAR, "true");
        CsmConfiguration configuration = provider.getConfiguration();
        Assert.assertEquals(SDKGlobalConfiguration.DEFAULT_AWS_CSM_PORT, configuration.getPort());
    }
}

