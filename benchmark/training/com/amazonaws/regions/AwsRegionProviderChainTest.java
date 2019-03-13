/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.regions;


import com.amazonaws.AmazonClientException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AwsRegionProviderChainTest {
    @Test
    public void firstProviderInChainGivesRegionInformation_DoesNotConsultOtherProviders() {
        AwsRegionProvider providerOne = Mockito.mock(AwsRegionProvider.class);
        AwsRegionProvider providerTwo = Mockito.mock(AwsRegionProvider.class);
        AwsRegionProvider providerThree = Mockito.mock(AwsRegionProvider.class);
        AwsRegionProviderChain chain = new AwsRegionProviderChain(providerOne, providerTwo, providerThree);
        final String expectedRegion = "some-region-string";
        Mockito.when(providerOne.getRegion()).thenReturn(expectedRegion);
        Assert.assertEquals(expectedRegion, chain.getRegion());
        Mockito.verify(providerTwo, Mockito.never()).getRegion();
        Mockito.verify(providerThree, Mockito.never()).getRegion();
    }

    @Test
    public void lastProviderInChainGivesRegionInformation() {
        final String expectedRegion = "some-region-string";
        AwsRegionProviderChain chain = new AwsRegionProviderChain(new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.StaticAwsRegionProvider(expectedRegion));
        Assert.assertEquals(expectedRegion, chain.getRegion());
    }

    @Test
    public void providerThrowsException_ContinuesToNextInChain() {
        final String expectedRegion = "some-region-string";
        AwsRegionProviderChain chain = new AwsRegionProviderChain(new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.FaultyAwsRegionProvider(), new AwsRegionProviderChainTest.StaticAwsRegionProvider(expectedRegion));
        Assert.assertEquals(expectedRegion, chain.getRegion());
    }

    /**
     * Only Exceptions should be caught and continued, Errors should propagate to caller and short
     * circuit the chain.
     */
    @Test(expected = Error.class)
    public void providerThrowsError_DoesNotContinueChain() {
        final String expectedRegion = "some-region-string";
        AwsRegionProviderChain chain = new AwsRegionProviderChain(new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.FatalAwsRegionProvider(), new AwsRegionProviderChainTest.StaticAwsRegionProvider(expectedRegion));
        Assert.assertEquals(expectedRegion, chain.getRegion());
    }

    @Test(expected = AmazonClientException.class)
    public void noProviderGivesRegion_ThrowsAmazonClientException() {
        AwsRegionProviderChain chain = new AwsRegionProviderChain(new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.NeverAwsRegionProvider(), new AwsRegionProviderChainTest.NeverAwsRegionProvider());
        chain.getRegion();
    }

    private static class NeverAwsRegionProvider extends AwsRegionProvider {
        @Override
        public String getRegion() throws AmazonClientException {
            return null;
        }
    }

    private static class StaticAwsRegionProvider extends AwsRegionProvider {
        private final String region;

        public StaticAwsRegionProvider(String region) {
            this.region = region;
        }

        @Override
        public String getRegion() {
            return region;
        }
    }

    private static class FaultyAwsRegionProvider extends AwsRegionProvider {
        @Override
        public String getRegion() throws AmazonClientException {
            throw new AmazonClientException("Unable to fetch region info");
        }
    }

    private static class FatalAwsRegionProvider extends AwsRegionProvider {
        @Override
        public String getRegion() throws AmazonClientException {
            throw new Error("Something really bad happened");
        }
    }
}

