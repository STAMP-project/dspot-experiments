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
import com.amazonaws.auth.profile.ProfileResourceLoader;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ProfileCsmConfigurationProvider}.
 */
public class ProfileCsmConfigurationProviderTest {
    private static final String TEST_PROFILE_NAME = "aws_csm";

    private static final String COMPLETE_CONFIG = "CompleteCsmProperties.tst";

    private static final String NO_PROPERTIES_CONFIG = "ProfileWithNoCsmProperties.tst";

    private static final String EMPTY_CONFIG_FILE = "EmptyConfigFile.tst";

    private static final String UNPARSEABLE_PORT_CONFIG = "ProfileWithUnparseableCsmPort.tst";

    private static final String PROFILE_WITH_ONLY_ENABLED_PROPERTY = "ProfileWithOnlyCsmEnabledProperty.tst";

    private static final String MULTIPLE_PROFILES_WITH_CSM_PROPERTIES_CONFIG = "MultipleProfilesWithCsmProperties.tst";

    @Test
    public void testCorrectlyResolvesConfiguration() {
        ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider(ProfileCsmConfigurationProviderTest.TEST_PROFILE_NAME, makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.COMPLETE_CONFIG));
        Assert.assertEquals(new CsmConfiguration(true, 1234, "foo"), provider.getConfiguration());
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenPropertiesNotSet() {
        getConfiguration();
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenCsmPropertiesNotPresent() {
        getConfiguration();
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenPortCannotBeParsed() {
        getConfiguration();
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenConfigNotFound() {
        getConfiguration();
    }

    @Test(expected = SdkClientException.class)
    public void testThrowsSdkClientExceptionWhenProfileNotFound() {
        ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider("my_other_profile", makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.COMPLETE_CONFIG));
        provider.getConfiguration();
    }

    @Test
    public void testSourcesConfigurationFromCorrectProfile() {
        ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider("aws_csm_2", makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.MULTIPLE_PROFILES_WITH_CSM_PROPERTIES_CONFIG));
        Assert.assertEquals(new CsmConfiguration(false, 5678, "bar"), provider.getConfiguration());
    }

    @Test
    public void testLocatesProfileNameUsingAwsProfileNameLoaderWhenNull_ProfileExists() {
        try {
            System.setProperty(AWS_PROFILE_SYSTEM_PROPERTY, ProfileCsmConfigurationProviderTest.TEST_PROFILE_NAME);
            ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider(null, makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.COMPLETE_CONFIG));
            Assert.assertEquals(new CsmConfiguration(true, 1234, "foo"), provider.getConfiguration());
        } finally {
            System.clearProperty(AWS_PROFILE_SYSTEM_PROPERTY);
        }
    }

    @Test(expected = SdkClientException.class)
    public void testLocatesProfileNameUsingAwsProfileNameLoaderWhenNull_ProfileNotExists() {
        try {
            System.setProperty(AWS_PROFILE_SYSTEM_PROPERTY, "my_other_profile");
            ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider(null, makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.COMPLETE_CONFIG));
            provider.getConfiguration();
        } finally {
            System.clearProperty(AWS_PROFILE_SYSTEM_PROPERTY);
        }
    }

    @Test
    public void profileNullPortClientId_shouldUseDefaultValues() {
        ProfileCsmConfigurationProvider provider = new ProfileCsmConfigurationProvider(ProfileCsmConfigurationProviderTest.TEST_PROFILE_NAME, makeConfigLocationProvider(ProfileCsmConfigurationProviderTest.PROFILE_WITH_ONLY_ENABLED_PROPERTY));
        Assert.assertEquals(CsmConfiguration.builder().withEnabled(true).build(), provider.getConfiguration());
    }

    private static class TestConfigLocationProvider implements AwsProfileFileLocationProvider {
        private final ProfileResourceLoader profileResourceLoader;

        public TestConfigLocationProvider(String fileName) {
            this.profileResourceLoader = new ProfileResourceLoader(fileName);
        }

        @Override
        public File getLocation() {
            return profileResourceLoader.asFile();
        }
    }
}

