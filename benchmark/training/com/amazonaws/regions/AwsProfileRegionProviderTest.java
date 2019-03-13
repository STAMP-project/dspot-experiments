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


import ProfileKeyConstants.REGION;
import com.amazonaws.auth.profile.internal.AllProfiles;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.auth.profile.internal.BasicProfileConfigLoader;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import com.amazonaws.util.ImmutableMapParameter;
import java.io.File;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AwsProfileRegionProviderTest {
    private static final String PROFILE = "test_profile";

    @Mock
    private BasicProfileConfigLoader configLoader;

    @Mock
    private AwsProfileFileLocationProvider locationProvider;

    private AwsRegionProvider regionProvider;

    @Test
    public void nullConfigFileLocation_ProvidesNullRegion() {
        Mockito.when(locationProvider.getLocation()).thenReturn(null);
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void nonExistentConfigFile_ProvidesNullRegion() {
        Mockito.when(locationProvider.getLocation()).thenReturn(new File("/var/tmp/this/is/invalid.txt"));
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void profilesAreEmpty_ProvidesNullRegion() {
        Mockito.when(configLoader.loadProfiles(ArgumentMatchers.any(File.class))).thenReturn(new AllProfiles(new HashMap<String, BasicProfile>()));
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void profilesNonEmptyButGivenProfileNotPresent_ProvidesNullRegion() {
        final String otherProfileName = "other_profile";
        final BasicProfile other_profile = new BasicProfile(otherProfileName, ImmutableMapParameter.of(REGION, "us-east-8"));
        final AllProfiles profiles = new AllProfiles(ImmutableMapParameter.of(otherProfileName, other_profile));
        stubLoadProfile(profiles);
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void profilePresentButRegionIsNotSet_ProvidesNullRegion() {
        final BasicProfile profile = new BasicProfile(AwsProfileRegionProviderTest.PROFILE, new HashMap<String, String>());
        final AllProfiles profiles = new AllProfiles(ImmutableMapParameter.of(AwsProfileRegionProviderTest.PROFILE, profile));
        stubLoadProfile(profiles);
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void profilePresentButRegionIsEmpty_ProvidesNullRegion() {
        final BasicProfile profile = new BasicProfile(AwsProfileRegionProviderTest.PROFILE, ImmutableMapParameter.of(REGION, ""));
        final AllProfiles profiles = new AllProfiles(ImmutableMapParameter.of(AwsProfileRegionProviderTest.PROFILE, profile));
        stubLoadProfile(profiles);
        Assert.assertNull(regionProvider.getRegion());
    }

    @Test
    public void profilePresentAndRegionIsSet_ProvidesCorrectRegion() {
        final String expectedRegion = "us-east-8";
        final BasicProfile profile = new BasicProfile(AwsProfileRegionProviderTest.PROFILE, ImmutableMapParameter.of(REGION, expectedRegion));
        final AllProfiles profiles = new AllProfiles(ImmutableMapParameter.of(AwsProfileRegionProviderTest.PROFILE, profile));
        stubLoadProfile(profiles);
        Assert.assertEquals(expectedRegion, regionProvider.getRegion());
    }
}

