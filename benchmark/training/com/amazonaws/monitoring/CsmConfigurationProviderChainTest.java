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


import com.amazonaws.SdkClientException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link CsmConfigurationProviderChain}.
 */
public class CsmConfigurationProviderChainTest {
    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionIfEndOfChainReached() {
        CsmConfigurationProvider provider = Mockito.mock(CsmConfigurationProvider.class);
        Mockito.when(provider.getConfiguration()).thenThrow(new SdkClientException(""));
        CsmConfigurationProviderChain chain = new CsmConfigurationProviderChain(provider);
        chain.getConfiguration();
    }

    @Test
    public void testReturnsTheFirstResult() {
        CsmConfigurationProvider provider1 = Mockito.mock(CsmConfigurationProvider.class);
        CsmConfigurationProvider provider2 = Mockito.mock(CsmConfigurationProvider.class);
        CsmConfiguration cfg1 = new CsmConfiguration(true, 1234, "foo");
        CsmConfiguration cfg2 = new CsmConfiguration(false, 5678, "bar");
        Mockito.when(provider1.getConfiguration()).thenReturn(cfg1);
        Mockito.when(provider2.getConfiguration()).thenReturn(cfg2);
        CsmConfigurationProviderChain chain = new CsmConfigurationProviderChain(provider1, provider2);
        Assert.assertEquals(cfg1, chain.getConfiguration());
    }

    @Test
    public void testContinuesUntilAProviderReturnsAConfiguration() {
        CsmConfigurationProvider provider1 = Mockito.mock(CsmConfigurationProvider.class);
        CsmConfigurationProvider provider2 = Mockito.mock(CsmConfigurationProvider.class);
        CsmConfigurationProvider provider3 = Mockito.mock(CsmConfigurationProvider.class);
        CsmConfiguration cfg = new CsmConfiguration(true, 1234, "foo");
        Mockito.when(provider1.getConfiguration()).thenThrow(new SdkClientException(""));
        Mockito.when(provider2.getConfiguration()).thenThrow(new SdkClientException(""));
        Mockito.when(provider3.getConfiguration()).thenReturn(cfg);
        CsmConfigurationProviderChain chain = new CsmConfigurationProviderChain(provider1, provider2, provider3);
        Assert.assertEquals(cfg, chain.getConfiguration());
    }
}

